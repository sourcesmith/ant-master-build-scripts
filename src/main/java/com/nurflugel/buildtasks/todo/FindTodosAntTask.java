package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import java.io.File;
import java.util.List;

/**
 * This task goes through the baseDir and finds any t*dos in there. It will try to assign them to a user if there's a user ID or alias in the same
 * line. It genrates a text report, and reports the output to TeamCity for tracking.
 */
@SuppressWarnings({ "CloneableClassWithoutClone", "TodoComment" })
public class FindTodosAntTask extends Task
{
  private File   baseDir;
  private String namePattern;
  private File   reportDir;
  private String searchPhrase;

  // -------------------------- OTHER METHODS --------------------------
  @Override
  public void execute() throws BuildException
  {
    validateProperties();

    // String[]   tokens = LineSplitter.splitLine(namePattern);
    // List<User> users  = null;  // User.parseUsers();
    //
    // users.add(all);
    // users.add(unknown);
    //
    // try
    // {
    // long start = new Date().getTime();
    //
    // findTodos(baseDir, users);
    //
    // long stop  = new Date().getTime() - start;
    // int  total = all.getTodos().size();
    //
    // writeReportOutput(users);
    // outputToTeamCity(users, total);
    // log("Duration, in ms = " + stop + ", found " + total + " todos", MSG_VERBOSE);
    // }
    // catch (IOException e)
    // {
    // throw new BuildException(e);
    // }
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
  // void findTodos(File dir, List<User> users) throws IOException { List<TodoItem> todos = new ArrayList<TodoItem>();
  //
  // findTodosInDir(dir, todos); assembleUserTodos(users, todos); }
  /**
   * Recursive method to delve into the file or dir to find all the t*dos.
   *
   * @param  file   the file or dir to examine
   * @param  todos  the list of t*dos to add to
   */
  // private void findTodosInDir(File file, List<TodoItem> todos) throws IOException { // Avoid Subversion or OS X dirs if
  // (file.getName().endsWith(".svn") || file.getName().endsWith(".DS_Store")) { return; }
  //
  // if (file.isDirectory()) { File[] files = file.listFiles();
  //
  // for (File theFile : files) { findTodosInDir(theFile, todos); } } else { if (!file.getName().endsWith(".properties") &&
  // !file.getName().endsWith("es.html") && !file.getName().endsWith("pt.html"))  // ignore properties files { findTodosInFile(file, todos); } } }
  /**
   * Finds the t*dos in a particular file.
   *
   * @param  file   the file to examine
   * @param  todos  the list of t*dos to add to
   */
  @SuppressWarnings({ "unchecked" })
  // private void findTodosInFile(File file, List<TodoItem> todos) throws IOException { System.out.println("file = " + file.getAbsolutePath());
  //
  // List<String> lines      = readLines(file); int          lineNumber = 0;
  //
  // for (String line : lines) { lineNumber++;
  //
  // if (containsIgnoreCase(line, searchPhrase+" ") || containsIgnoreCase(line, " "+searchPhrase) || containsIgnoreCase(line, "//"+searchPhrase)) if
  // (containsIgnoreCase(line, searchPhrase)) { todos.add(new TodoItem(trim(line), file, lineNumber)); } } }
  /**
   * go through all of the t*dos, assign them to the User. If you can't find which user it belongs to, put it in "unknown".
   *
   * @param  users     the list of Users
   * @param  allTodos  all of the t*dos found
   */
  // private void assembleUserTodos(List<User> users, List<TodoItem> allTodos)
  // {
  // for (TodoItem todo : allTodos)
  // {
  // String comment = todo.getComment().toUpperCase();
  //
  // all.addTodo(todo);  // add to all t*dos
  //
  // boolean isUnknownUser = true;
  //
  // for (User user : users)
  // {
  // Set<String> aliases = user.getAliases();
  //
  // for (String alias : aliases)
  // {
  // if (comment.contains(alias.toUpperCase()))
  // {
  // user.addTodo(todo);
  // isUnknownUser = false;
  //
  // break;
  // }
  // }
  // }
  //
  // if (isUnknownUser)  // didn't find a matching user?  Then add it to the "unknown" user collection
  // {
  // unknown.addTodo(todo);
  // }
  // }
  // }
  // --------------------- GETTER / SETTER METHODS ---------------------
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

  public void findTodos(File dir, List<User> users) {}
}
