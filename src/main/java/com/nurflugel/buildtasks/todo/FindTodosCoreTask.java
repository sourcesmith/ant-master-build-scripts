package com.nurflugel.buildtasks.todo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static com.nurflugel.buildtasks.todo.AliasParser.getAliases;
import static com.nurflugel.buildtasks.todo.LineSplitter.splitLine;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.lang.StringUtils.*;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 7/7/12 Time: 22:48 To change this template use File | Settings | File Templates. */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class FindTodosCoreTask
{
  private static final String VALUE        = "' value='";
  private static final String END_TAG      = "']";
  private static final String TEAMCITY_TAG = "##teamcity[buildStatisticValue key='numberOf";
  private final User          ALL          = new User("all");
  private final User          UNKNOWN      = new User("unknown");

  /** The dir to be scanned for t*dos. */
  private File baseDir;

  /** Where the report goes. */
  private File    reportDir;
  private boolean shouldOutputToTeamCity = false;

  /**
   * The phrase to search for - i.e., "t*do", "C ODEREVIEW" - actually, a comma-delimited list of terms ("c odereview,c odereviewresult"). Optional,
   * if blank, "t*do" is used.
   */
  private List<SearchPhrase> searchPhrases;

  /**
   * A list of names and aliases - the format is like this:
   *
   * <p>users[dbulla(dgb;doug;dbulla),snara(snara;sunitha),mkshir,dlabar].</p>
   *
   * <p>The users are separated by commas, and the optional aliases separated by semicolons. Note that the alias list for each user is optional.</p>
   *
   * <p>Optional, if not specified, then no users will show in the graphs.</p>
   */
  private String     namePattern;
  private List<User> users = new ArrayList<User>();

  /** used to store output to TeamCity for unit testing, as scraping System.out is a pain. */
  private List<String> outputLines = new ArrayList<String>();

  public FindTodosCoreTask()
  {
    searchPhrases = new ArrayList<SearchPhrase>();

    List<String> dibble = new ArrayList<String>();

    dibble.add("todo");
    searchPhrases.add(new SearchPhrase(dibble));
  }

  // -------------------------- OTHER METHODS --------------------------
  void findTodos() throws IOException
  {
    List<User> users = getUsers();

    findTodosInDir(baseDir, users);
    writeReportOutput(users);
    outputToTeamCity(users);
  }

  void findTodosInDir(File dir, List<User> users) throws IOException
  {
    if (dir.isFile())
    {
      findTodosInFile(dir, users);
    }
    else
    {
      File[] files = dir.listFiles();

      for (File file : files)
      {
        findTodosInDir(file, users);
      }
    }
  }

  void findTodosInFile(File file, List<User> users) throws IOException
  {
    List<String> lines   = readLines(file);
    String[]     strings = lines.toArray(new String[lines.size()]);

    parseLines(users, file, strings);
  }

  public void parseLines(List<User> users, File file, String... lines)
  {
    for (int lineNumber = 0; lineNumber < lines.length; lineNumber++)
    {
      String line = lines[lineNumber];

      for (SearchPhrase phrase : searchPhrases)
      {
        for (String searchAlias : phrase.getAliases())
        {
          if (containsIgnoreCase(line, searchAlias))
          {
            TodoItem todoItem              = new TodoItem(line, file, lineNumber);
            boolean  noUserFoundForTodoYet = true;

            for (User user : users)
            {
              for (String alias : user.getAliases())
              {
                if (contains(line, alias))
                {
                  user.addTodo(todoItem);
                  noUserFoundForTodoYet = false;

                  break;
                }
              }
            }

            // if no user found, add to unknown
            if (noUserFoundForTodoYet)
            {
              UNKNOWN.addTodo(todoItem);
            }

            // now, add to all t odos found
            ALL.addTodo(todoItem);
          }
        }
      }
    }
  }

  /** Writes the output to a file for artifacts. */
  private void writeReportOutput(List<User> users) throws IOException
  {
    List<String> lines = new ArrayList<String>();

    lines.add("<html>");
    lines.add("<title>Todo Analysis</title>");
    lines.add("  <body>");
    lines.add("<h1>Results for analysis of " + baseDir.getName() + "</h1>");
    lines.add("");

    for (User user : users)
    {
      if (!user.equals(ALL) && !user.equals(UNKNOWN))
      {
        addLinesForUser(user, lines);
      }
    }

    addLinesForUser(UNKNOWN, lines);
    lines.add("  </body>");
    lines.add("</html>");

    File reportFile = new File(reportDir, searchPhrases.get(0) + "_list.html");

    System.out.println("Writing report to " + reportFile);
    writeLines(reportFile, lines);
  }

  /** Writes the output for a particular user. */
  private static void addLinesForUser(User user, List<String> lines)
  {
    List<TodoItem> todos = user.getTodos();

    if (!todos.isEmpty())
    {
      lines.add("<p><b>Report for user " + user.getName() + ":</b>");

      // log("   Writing output for user " + user.getName(), MSG_INFO);
      System.out.println("   Writing output for user " + user.getName());
      lines.add("<table>");

      for (TodoItem todo : todos)
      {
        String line = "  <tr><td>" + todo.getFile().getName() + ":" + todo.getLineNumber() + "</td><td width=\"20px\">  </td><td>" + todo.getComment()
                        + "</td></tr>";

        line = replace(line, "<!--", "");  // if we don't replace this, it will ruin the HTML output if there are any lines which contain it in the

        // code
        lines.add(line);
      }
    }

    lines.add("</table>\n");
  }

  /** Writes the output to Ant's output so that TeamCity will pick up the data. */
  private void outputToTeamCity(List<User> users)
  {
    if (shouldOutputToTeamCity)
    {
      String tag = TEAMCITY_TAG + capitalize(searchPhrases.get(0).getName()) + 's';  // todo how to deal with this???

      // log(tag + VALUE + total + END_TAG);
      int    total = ALL.getTodos().size();
      String line  = tag + VALUE + total + END_TAG;

      System.out.println(line);
      outputLines.add(line);

      for (User user : users)
      {
        if (!user.equals(ALL))
        {
          if (!user.getTodos().isEmpty())
          {
            // log(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
            line = tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG;
            System.out.println(line);
            outputLines.add(line);
          }
        }
      }
    }
  }

  public List<TodoItem> getTodosForUser(String name)
  {
    for (User user : users)
    {
      if (user.getName().equalsIgnoreCase(name))
      {
        return user.getTodos();
      }
    }

    if (name.equals("all"))
    {
      return ALL.getTodos();
    }

    if (name.equals("unknown"))
    {
      return UNKNOWN.getTodos();
    }

    return new ArrayList<TodoItem>();
  }

  public void setSearchPhrase(String text)
  {
    String[]           names   = splitLine(text);
    List<SearchPhrase> phrases = new ArrayList<SearchPhrase>();

    for (String name : names)
    {
      try
      {
        List<String> aliases = getAliases(name);
        SearchPhrase phrase  = new SearchPhrase(aliases);

        phrases.add(phrase);
      }
      catch (BadParsingException e)
      {
        e.printStackTrace();
      }

      searchPhrases = phrases;
    }
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public List<SearchPhrase> getSearchPhrases()
  {
    return searchPhrases;
  }

  public List<User> getUsers()
  {
    if (users.isEmpty())
    {
      findUsers();
    }

    return users;
  }

  public List<User> findUsers()
  {
    String[]   names = splitLine(namePattern);
    List<User> users = new ArrayList<User>();

    for (String name : names)
    {
      try
      {
        List<String> aliases = getAliases(name);
        User         user    = new User(aliases);

        users.add(user);
      }
      catch (BadParsingException e)
      {
        e.printStackTrace();  // todo something better
      }
    }

    this.users = users;

    return users;
  }

  public void setBaseDir(File dir)
  {
    baseDir = dir;
  }

  public void setNamePattern(String namePattern)
  {
    this.namePattern = namePattern;
  }

  public void setReportDir(File dir)
  {
    reportDir = dir;
  }

  public void setShouldOutputToTeamCity(boolean shouldOutputToTeamCity)
  {
    this.shouldOutputToTeamCity = shouldOutputToTeamCity;
  }

  public List<String> getOutput()
  {
    return outputLines;
  }
}
