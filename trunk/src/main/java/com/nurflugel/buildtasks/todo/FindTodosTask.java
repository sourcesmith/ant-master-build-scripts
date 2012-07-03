package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import static com.nurflugel.buildtasks.todo.User.*;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.io.FileUtils.writeLines;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.tools.ant.Project.MSG_INFO;
import static org.apache.tools.ant.Project.MSG_VERBOSE;

/**
 * This task goes through the baseDir and finds any t*dos in there. It will try to assign them to a user if there's a user ID or alias in the same
 * line. It genrates a text report, and reports the output to TeamCity for tracking.
 */
@SuppressWarnings({ "CloneableClassWithoutClone", "TodoComment" })
public class FindTodosTask extends Task
{
  private static final String VALUE        = "' value='";
  private static final String END_TAG      = "']";
  private static final String TEAMCITY_TAG = "##teamcity[buildStatisticValue key='numberOf";

  /** The dir to be scanned for t*dos. */
  private File baseDir;

  /** Where the report goes. */
  private File reportDir;

  /**
   * The phrase to search for - i.e., "t*do", "CODEREVIEW" - actually, a comma-delimited list of terms ("codereview,codereviewresult"). Optional, if
   * blank, "t*do" is used.
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
  @Override
  public void execute() throws BuildException
  {
    validateProperties();

    String[]   tokens = LineSplitter.splitLine(namePattern);
    List<User> users  = null;  // User.parseUsers();

    users.add(all);
    users.add(unknown);

    try
    {
      long start = new Date().getTime();

      findTodos(baseDir, users);

      long stop  = new Date().getTime() - start;
      int  total = all.getTodos().size();

      writeReportOutput(users);
      outputToTeamCity(users, total);
      log("Duration, in ms = " + stop + ", found " + total + " todos", MSG_VERBOSE);
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }
  }

  /** Give a nice error message if required properties are missing. */
  private void validateProperties()
  {
    if (baseDir == null)
    {
      throw new BuildException("You must specify a baseDir attribute");
    }

    if (reportDir == null)
    {
      throw new BuildException("You must specify a reportDir attribute");
    }
  }

  /**
   * Get all the t*dos from the files in the directory.
   *
   * @param  dir    the file or dir to examine for t*dos
   * @param  users  the list of users
   */
  void findTodos(File dir, List<User> users) throws IOException
  {
    List<TodoItem> todos = new ArrayList<TodoItem>();

    findTodosInDir(dir, todos);
    assembleUserTodos(users, todos);
  }

  /**
   * Recursive method to delve into the file or dir to find all the t*dos.
   *
   * @param  file   the file or dir to examine
   * @param  todos  the list of t*dos to add to
   */
  private void findTodosInDir(File file, List<TodoItem> todos) throws IOException
  {
    // Avoid Subversion or OS X dirs
    if (file.getName().endsWith(".svn") || file.getName().endsWith(".DS_Store"))
    {
      return;
    }

    if (file.isDirectory())
    {
      File[] files = file.listFiles();

      for (File theFile : files)
      {
        findTodosInDir(theFile, todos);
      }
    }
    else
    {
      if (!file.getName().endsWith(".properties") && !file.getName().endsWith("es.html") && !file.getName().endsWith("pt.html"))  // ignore properties files
      {
        findTodosInFile(file, todos);
      }
    }
  }

  /**
   * Finds the t*dos in a particular file.
   *
   * @param  file   the file to examine
   * @param  todos  the list of t*dos to add to
   */
  @SuppressWarnings({ "unchecked" })
  private void findTodosInFile(File file, List<TodoItem> todos) throws IOException
  {
    System.out.println("file = " + file.getAbsolutePath());

    List<String> lines      = readLines(file);
    int          lineNumber = 0;

    for (String line : lines)
    {
      lineNumber++;

      // if (containsIgnoreCase(line, searchPhrase+" ") || containsIgnoreCase(line, " "+searchPhrase) || containsIgnoreCase(line, "//"+searchPhrase))
      if (containsIgnoreCase(line, searchPhrase))
      {
        todos.add(new TodoItem(trim(line), file, lineNumber));
      }
    }
  }

  /**
   * go through all of the t*dos, assign them to the User. If you can't find which user it belongs to, put it in "unknown".
   *
   * @param  users     the list of Users
   * @param  allTodos  all of the t*dos found
   */
  private void assembleUserTodos(List<User> users, List<TodoItem> allTodos)
  {
    for (TodoItem todo : allTodos)
    {
      String comment = todo.getComment().toUpperCase();

      all.addTodo(todo);  // add to all t*dos

      boolean isUnknownUser = true;

      for (User user : users)
      {
        Set<String> aliases = user.getAliases();

        for (String alias : aliases)
        {
          if (comment.contains(alias.toUpperCase()))
          {
            user.addTodo(todo);
            isUnknownUser = false;

            break;
          }
        }
      }

      if (isUnknownUser)  // didn't find a matching user?  Then add it to the "unknown" user collection
      {
        unknown.addTodo(todo);
      }
    }
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
        writeOutputForUser(user, lines, maxLength);
      }
    }

    lines.add("      </font>");
    lines.add("    </pre>");
    lines.add("  </body>");
    lines.add("</html>");

    File reportFile = new File(reportDir, searchPhrase + "_list.html");

    log("Writing report to " + reportFile, MSG_INFO);
    writeLines(reportFile, lines);
  }

  /**
   * Writes the output for a particular user.
   *
   * @param  maxLength  the maximum length of all the file names - used for spacing the output nicely
   */
  private void writeOutputForUser(User user, List<String> lines, int maxLength)
  {
    List<TodoItem> todos = user.getTodos();

    if (!todos.isEmpty())
    {
      lines.add("Report for user " + user.getName() + ':');
      log("   Writing output for user " + user.getName(), MSG_INFO);

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

  /** Writes the output to Ant's output so that TeamCity will pick up the data. */
  private void outputToTeamCity(List<User> users, int total)
  {
    String tag = TEAMCITY_TAG + capitalize(searchPhrase) + 's';

    log(tag + VALUE + total + END_TAG);

    for (User user : users)
    {
      if (!user.equals(all))
      {
        if (!user.getTodos().isEmpty())
        {
          log(tag + '_' + user.getName() + VALUE + user.getTodos().size() + END_TAG);
        }
      }
    }
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

  public void setSearchPhrase(String searchPhrase)
  {
    this.searchPhrase = searchPhrase;
  }

  public String getSearchPhrase()
  {
    return searchPhrase;
  }
}
