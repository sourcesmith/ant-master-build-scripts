package com.nurflugel.buildtasks.todo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.nurflugel.buildtasks.todo.User.ALL;
import static com.nurflugel.buildtasks.todo.User.UNKNOWN;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.replace;

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
  private String searchPhrase = "todo";

  /**
   * A list of names and aliases - the format is like this:
   *
   * <p>users[dbulla(dgb;doug;dbulla),snara(snara;sunitha),mkshir,dlabar].</p>
   *
   * <p>The users are separated by commas, and the optional aliases separated by semicolons. Note that the alias list for each user is optional.</p>
   *
   * <p>Optional, if not specified, then no users will show in the graphs.</p>
   */
  private String namePattern;

  /** User to catch all unknown assignments. */
  private User unknown = new User(UNKNOWN);

  /** User to catch everything. */
  private User all = new User(ALL);
  // -------------------------- OTHER METHODS --------------------------

  /** Writes the output to Ant's output so that TeamCity will pick up the data. */
  private void outputToTeamCity(List<User> users, int total)
  {
    String tag = TEAMCITY_TAG + capitalize(searchPhrase) + 's';

    // log(tag + VALUE + total + END_TAG);
    System.out.println(tag + VALUE + total + END_TAG);

    for (User user : users)
    {
      if (!user.equals(all))
      {
        if (!user.getTodos().isEmpty())
        {
          // log(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
          System.out.println(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
        }
      }
    }
  }

  public Map<User, List<TodoItem>> parseLines(String[] lines, List<User> users)
  {
    return null;
  }

  /** Writes the output to a file for artifacts. */
  private void writeReportOutput(List<User> users) throws IOException
  {
    List<String> lines     = new ArrayList<String>();
    int          maxLength = getMaxLength();

    lines.add("<html>");
    lines.add("  <body>");
    lines.add("    <pre>");
    lines.add("      <font face=\"Courier New\">");
    lines.add("Results for  analysis of " + baseDir);
    lines.add("");

    for (User user : users)
    {
      if (!user.equals(all))
      {
        addLinesForUser(user, lines, maxLength);
      }
    }

    lines.add("      </font>");
    lines.add("    </pre>");
    lines.add("  </body>");
    lines.add("</html>");

    File reportFile = new File(reportDir, searchPhrase + "_list.html");

    System.out.println("Writing report to " + reportFile);
    writeLines(reportFile, lines);
  }

  /**
   * Writes the output for a particular user.
   *
   * @param  maxLength  the maximum length of all the file names - used for spacing the output nicely
   */
  private static void addLinesForUser(User user, List<String> lines, int maxLength)
  {
    List<TodoItem> todos = user.getTodos();

    if (!todos.isEmpty())
    {
      lines.add("Report for user " + user.getName() + ':');

      // log("   Writing output for user " + user.getName(), MSG_INFO);
      System.out.println("   Writing output for user " + user.getName());

      for (TodoItem todo : todos)
      {
        String line = String.format("     %1$" + maxLength + "s  %2$-100s  ", todo.getFile().getName(), todo.getComment());

        line = replace(line, "<!--", "");  // if we don't replace this, it will ruin the HTML output if there are any lines which contain it in the
                                           // code
        lines.add(line);
      }
    }

    lines.add("");
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  private int getMaxLength()
  {
    int maxLength = 0;

    for (TodoItem todo : all.getTodos())
    {
      maxLength = Math.max(maxLength, todo.getFile().getName().length());
    }

    return maxLength;
  }

  public String getSearchPhrase()
  {
    return searchPhrase;
  }

  public void setSearchPhrase(String searchPhrase)
  {
    this.searchPhrase = searchPhrase;
  }

  public List<User> getUsers()
  {
    String[]   names = LineSplitter.splitLine(namePattern);
    List<User> users = new ArrayList<User>();

    for (String name : names)
    {
      try
      {
        List<String> aliases = AliasParser.getAliases(name);
        User         user    = new User(aliases);

        users.add(user);
      }
      catch (BadParsingException e)
      {
        e.printStackTrace();  // todo something better
      }
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
