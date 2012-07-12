package com.nurflugel.buildtasks.todo;

import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import static com.nurflugel.buildtasks.todo.TestResources.getTestFilePath;
import static com.nurflugel.buildtasks.todo.User.ALL;
import static com.nurflugel.buildtasks.todo.User.UNKNOWN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 7/2/12 Time: 21:53 To change this template use File | Settings | File Templates. */
@Test(groups = "todos")
public class FindTodosCoreTaskTest
{
  @Test
  public void testDefaultSearchPhrase() throws Exception
  {
    FindTodosCoreTask task         = new FindTodosCoreTask();
    String[]          searchPhrase = task.getSearchPhrases();

    assertEquals(searchPhrase[0], "todo");
  }

  @Test
  public void testSimpleParse() throws Exception
  {
    FindTodosCoreTask task = new FindTodosCoreTask();

    task.setNamePattern("dbulla(doug,dgb);dlabar");

    List<User> users = task.findUsers();
    User       doug  = users.get(0);

    assertEquals(doug.getName(), "dbulla");

    Set<String> aliases = doug.getAliases();

    assertTrue(aliases.contains("dbulla"));
    assertTrue(aliases.contains("doug"));
    assertTrue(aliases.contains("dgb"));

    User derek = users.get(1);

    assertEquals(derek.getName(), "dlabar");
  }

  @Test
  public void testSimpleParseLinesOneUserOneLine() throws Exception
  {
    FindTodosCoreTask task = new FindTodosCoreTask();

    task.setNamePattern("dbulla(doug,dgb);dlabar");

    List<User> users = task.findUsers();
    String     line  = "dkdjlksjlskslkj lksdj ls todo dbulla test1";

    task.parseLines(users, new File("someFileName.java"), line);

    User           user  = users.get(0);
    List<TodoItem> todos = user.getTodos();

    assertEquals(todos.size(), 1);
  }

  @Test
  public void testSimpleParseLinesOneUserAliasOneLine() throws Exception
  {
    FindTodosCoreTask task = new FindTodosCoreTask();

    task.setNamePattern("dbulla(doug,dgb);dlabar");

    List<User> users = task.findUsers();
    String     line  = "dkdjlksjlskslkj lksdj ls todo dgb test1";

    task.parseLines(users, new File("someFileName.java"), line);

    User           user  = users.get(0);
    List<TodoItem> todos = user.getTodos();

    assertEquals(todos.size(), 1);
    assertEquals(todos.get(0).getComment(), line);
  }

  @Test
  public void testSimpleParseLinesTwoUsersOneLine() throws Exception
  {
    FindTodosCoreTask task = new FindTodosCoreTask();

    task.setNamePattern("dbulla(doug,dgb);dlabar");

    List<User> users = task.findUsers();
    String     line  = "dkdjlksjlskslkj lksdj ls todo dbulla  dlabar test1";

    task.parseLines(users, new File("someFileName.java"), line);

    User           user  = users.get(0);
    List<TodoItem> todos = user.getTodos();

    assertEquals(todos.size(), 1);

    TodoItem todoItem = todos.get(0);

    assertEquals(todoItem.getComment(), line);
    user = users.get(1);

    List<TodoItem> todos2 = user.getTodos();

    assertEquals(todos2.size(), 1);

    TodoItem todoItem2 = todos2.get(0);

    assertEquals(todoItem2.getComment(), line);
    assertEquals(todoItem2.getComment(), todoItem.getComment());
  }

  @Test
  public void testFileOneUserOneComment() throws IOException
  {
    FindTodosCoreTask task = new FindTodosCoreTask();
    File              file = new File(getTestFilePath("dir/Dummy.java"));

    task.setBaseDir(file);
    task.setReportDir(new File("build/reports/dir"));
    task.setNamePattern("dbulla(dgb,doug);snara(snara3,sunitha);bren");

    List<User> users = task.findUsers();

    task.findTodosInFile(file, users);

    User           user  = users.get(2);
    List<TodoItem> todos = user.getTodos();

    assertEquals(todos.size(), 1);
  }

  @Test
  public void testSmallFileOneUserManyComments() throws IOException
  {
    FindTodosCoreTask task = new FindTodosCoreTask();
    File              file = new File(getTestFilePath("dir/otherDir/SmallComments.java"));

    task.setBaseDir(file);
    task.setReportDir(new File("build/reports/dir"));
    task.setNamePattern("dbulla(dgb,doug)");

    List<User> users = task.findUsers();

    task.findTodosInFile(file, users);
    assertEquals(users.get(0).getTodos().size(), 6);
  }

  @Test
  public void testFileOneUserManyComments() throws IOException
  {
    FindTodosCoreTask task = new FindTodosCoreTask();
    File              file = new File(getTestFilePath("dir/otherDir/Comments.java"));

    task.setBaseDir(file);
    task.setReportDir(new File("build/reports/dir"));
    task.setNamePattern("dbulla(dgb,doug);snara(snara3,sunitha);bren");

    List<User> users = task.findUsers();

    task.findTodosInFile(file, users);
    assertEquals(users.get(0).getTodos().size(), 19);
    assertEquals(users.get(1).getTodos().size(), 2);
    assertEquals(users.get(2).getTodos().size(), 0);
  }

  @Test
  public void testDirsManyCommentsManyUsers() throws IOException
  {
    FindTodosCoreTask task = new FindTodosCoreTask();
    File              file = new File(getTestFilePath("dir"));

    task.setNamePattern("dbulla(dgb,doug);snara(snara3,sunitha);bren");

    List<User> users = task.findUsers();

    task.findTodosInDir(file, users);
    assertEquals(users.get(0).getTodos().size(), 25);
    assertEquals(users.get(1).getTodos().size(), 2);
    assertEquals(users.get(2).getTodos().size(), 1);
  }

  @Test
  public void testTodos() throws IOException
  {
    FindTodosCoreTask task = new FindTodosCoreTask();
    File              file = new File(getTestFilePath("dir"));

    task.setBaseDir(file);
    task.setReportDir(new File("build/reports/dir"));
    task.setNamePattern("dbulla(dgb,doug);snara(snara3,sunitha);bren");
    task.findTodos();

    List<User> users = task.getUsers();

    assertEquals(users.get(0).getTodos().size(), 25);
    assertEquals(users.get(1).getTodos().size(), 2);
    assertEquals(users.get(2).getTodos().size(), 1);
    assertEquals(ALL.getTodos().size(), 34);
    assertEquals(UNKNOWN.getTodos().size(), 3);
  }

  // todo find all tasks, tasks for unknown users
}
