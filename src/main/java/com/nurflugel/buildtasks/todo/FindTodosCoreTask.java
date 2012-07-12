package com.nurflugel.buildtasks.todo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import static com.nurflugel.buildtasks.todo.AliasParser.getAliases;
import static com.nurflugel.buildtasks.todo.LineSplitter.splitLine;
import static com.nurflugel.buildtasks.todo.User.ALL;
import static com.nurflugel.buildtasks.todo.User.UNKNOWN;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.lang.ArrayUtils.indexOf;
import static org.apache.commons.lang.StringUtils.*;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 7/7/12 Time: 22:48 To change this template use File | Settings | File Templates. */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class FindTodosCoreTask
{
  private static final String VALUE        = "' value='";
  private static final String END_TAG      = "']";
  private static final String TEAMCITY_TAG = "##teamcity[buildStatisticValue key='numberOf";

  /** The dir to be scanned for t*dos. */
  private File baseDir;

  /** Where the report goes. */
  private File reportDir;

  /**
   * The phrase to search for - i.e., "t*do", "C ODEREVIEW" - actually, a comma-delimited list of terms ("c odereview,c odereviewresult"). Optional,
   * if blank, "t*do" is used.
   */
  private String[] searchPhrases = { "todo" };

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
  // -------------------------- OTHER METHODS --------------------------

  /** Writes the output to Ant's output so that TeamCity will pick up the data. */
  private void outputToTeamCity(List<User> users, int total)
  {
    String tag = TEAMCITY_TAG + capitalize(searchPhrases[0]) + 's';  // todo how to deal with this???

    // log(tag + VALUE + total + END_TAG);
    System.out.println(tag + VALUE + total + END_TAG);

    for (User user : users)
    {
      if (!user.equals(ALL))
      {
        if (!user.getTodos().isEmpty())
        {
          // log(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
          System.out.println(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
        }
      }
    }
  }

  public void parseLines(List<User> users, File file, String... lines)
  {
    for (int lineNumber = 0; lineNumber < lines.length; lineNumber++)
    {
      String line = lines[lineNumber];

      for (String phrase : searchPhrases)
      {
        if (containsIgnoreCase(line, phrase))
        {
          TodoItem todoItem              = new TodoItem(line, file, lineNumber);
          boolean  noUserFoundForTodoYet = true;

          for (User user : users)
          {
            for (String alias : user.aliases)
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

  void findTodos() throws IOException
  {
    List<User> users = getUsers();

    findTodosInDir(baseDir, users);
    writeReportOutput(users);
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

    File reportFile = new File(reportDir, searchPhrases[0] + "_list.html");

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
  // --------------------- GETTER / SETTER METHODS ---------------------
  public String[] getSearchPhrases()
  {
    return searchPhrases;
  }

  public void setSearchPhrases(String... searchPhrases)
  {
    this.searchPhrases = searchPhrases;
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

    // users.add(all);
    // users.add(unknown);
    this.users = users;

    return users;
  }

  public List<User> getUsers()
  {
    if (users.isEmpty())
    {
      findUsers();
    }

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
}
