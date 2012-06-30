package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static com.nurflugel.buildtasks.todo.TestResources.getTestFilePath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/** Tests for the FindTodosTask task. */
@Test(groups = { "todos", "failed" })
// @Test(groups = {"todos"})
public class TodosTest
{
  // -------------------------- OTHER METHODS --------------------------
  @Test
  public void testFiles() throws IOException
  {
    FindTodosTask task = new FindTodosTask();
    File          dir  = new File(getTestFilePath("dir"));

    task.setBaseDir(dir);
    task.setReportDir(new File("build/reports"));
    task.setNamePattern("dbulla(dgb;doug),snara(snara3;sunitha),bren");

    List<User> users = null;  // task.parseUsers();

    task.findTodos(dir, users);

    User           user  = users.get(0);
    List<TodoItem> todos = user.getTodos();

    assertEquals(todos.size(), 25);
    todos = users.get(1).getTodos();
    assertEquals(todos.size(), 3);
    todos = users.get(2).getTodos();
    assertEquals(todos.size(), 19);
    todos = users.get(3).getTodos();
    assertEquals(todos.size(), 2);
    todos = users.get(4).getTodos();
    assertEquals(todos.size(), 1);
  }

  @Test(expectedExceptions = BuildException.class)
  public void testParseBadUsers1()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla{dgb;doug;dbulla),snara(snara;sunitha)");

    // task.parseUsers();
  }

  @Test(expectedExceptions = BuildException.class)
  public void testParseBadUsers2()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla(dgb;doug;dbulla]:snara(snara;sunitha)");

    // task.parseUsers();
  }

  @Test
  public void testParseEmptyUsers()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("");
    fail();
    // List<NameWithAliases> users = task.parseUsers();
    //
    // assertEquals(users.size(), 2);  // all and unknown
    // task.setNamePattern("");
    // users = task.parseUsers();
    // assertEquals(users.size(), 2);
    // task.setNamePattern(null);
    // users = task.parseUsers();
    // assertEquals(users.size(), 2);
  }

  @Test
  public void testParseGoodUsers()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla(dgb;doug),snara(sunitha),bob");
    fail();
    // List<NameWithAliases> users = task.parseUsers();
    //
    // assertEquals(users.size(), 5);
    //
    // NameWithAliases all     = users.get(0);
    // NameWithAliases unknown = users.get(1);
    //
    // assertEquals(all.getName(), ALL);
    // assertTrue(all.getAliases().isEmpty());
    // assertEquals(unknown.getName(), UNKNOWN);
    //
    // NameWithAliases dbulla = users.get(2);
    //
    // assertEquals(dbulla.getName(), "dbulla");
    //
    // Set<String> aliases = dbulla.getAliases();
    //
    // assertTrue(aliases.contains("dbulla"));
    // assertTrue(aliases.contains("doug"));
    // assertTrue(aliases.contains("dgb"));
    //
    // NameWithAliases snara = users.get(3);
    //
    // assertEquals(snara.getName(), "snara");
    // aliases = snara.getAliases();
    // assertTrue(aliases.contains("snara"));
    // assertTrue(aliases.contains("sunitha"));
    //
    // NameWithAliases bob = users.get(4);
    //
    // assertEquals(bob.getName(), "bob");
    // aliases = bob.getAliases();
    // assertTrue(aliases.contains("bob"));
  }

  @Test
  public void testDefaultSearchPhrase()
  {
    FindTodosTask task = new FindTodosTask();

    assertEquals(task.getSearchPhrase(), "todo");
  }

  /** Similar to the multiple user aliases with a single name, search for mulitiple variants of a phrase. */
  @Test
  public void testMultipleValueSearchPhrase() throws IOException
  {
    FindTodosTask task = new FindTodosTask();

    task.setSearchPhrase("codereview(Code Review;code_review)");

    File dir = new File(getTestFilePath("dir"));

    task.setBaseDir(dir);
    task.setReportDir(new File("build/reports"));
    task.setNamePattern("dbulla(dgb;doug),snara(snara3;sunitha),bren");
    fail();
    // List<NameWithAliases> users = task.parseUsers();
    //
    // task.findTodos(dir, users);
    // User user = users.get(0);
    // assertEquals(user.getName(),"dbulla");
    // List<TodoItem> todos = user.getTodos();
    // assertEquals(todos,3);
  }

  // todo validate todos searched for if searchphrase not used
  // todo validate multiple searchphrase
}
