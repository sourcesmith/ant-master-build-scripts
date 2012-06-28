package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import static com.nurflugel.buildtasks.todo.TestResources.getFilePath;
import static com.nurflugel.buildtasks.todo.User.ALL;
import static com.nurflugel.buildtasks.todo.User.UNKNOWN;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Tests for the FindTodosTask task. */
@Test(groups = "unit")
public class TodosTest
{
  // -------------------------- OTHER METHODS --------------------------
  @Test(groups = "unit")
  public void testFiles() throws IOException
  {
    FindTodosTask task = new FindTodosTask();
    File          dir  = new File(getFilePath("dir"));

    task.setBaseDir(dir);
    task.setReportDir(new File("build/reports"));
    task.setNamePattern("dbulla(dgb;doug),snara(snara3;sunitha),bren");

    List<User> users = task.parseUsers();

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

  @Test(groups             = "unit",
        expectedExceptions = BuildException.class)
  public void testParseBadUsers1()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla{dgb;doug;dbulla),snara(snara;sunitha)");
    task.parseUsers();
  }

  @Test(groups             = "unit",
        expectedExceptions = BuildException.class)
  public void testParseBadUsers2()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla(dgb;doug;dbulla]:snara(snara;sunitha)");
    task.parseUsers();
  }

  @Test(groups = "unit")
  public void testParseEmptyUsers()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("");

    List<User> users = task.parseUsers();

    assertEquals(users.size(), 2);  // all and unknown
    task.setNamePattern("");
    users = task.parseUsers();
    assertEquals(users.size(), 2);
    task.setNamePattern(null);
    users = task.parseUsers();
    assertEquals(users.size(), 2);
  }

  @Test(groups = "unit")
  public void testParseGoodUsers()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla(dgb;doug),snara(sunitha),bob");

    List<User> users = task.parseUsers();

    assertEquals(users.size(), 5);

    User all     = users.get(0);
    User unknown = users.get(1);

    assertEquals(all.getId(), ALL);
    assertTrue(all.getAliases().isEmpty());
    assertEquals(unknown.getId(), UNKNOWN);

    User dbulla = users.get(2);

    assertEquals(dbulla.getId(), "dbulla");

    Set<String> aliases = dbulla.getAliases();

    assertTrue(aliases.contains("dbulla"));
    assertTrue(aliases.contains("doug"));
    assertTrue(aliases.contains("dgb"));

    User snara = users.get(3);

    assertEquals(snara.getId(), "snara");
    aliases = snara.getAliases();
    assertTrue(aliases.contains("snara"));
    assertTrue(aliases.contains("sunitha"));

    User bob = users.get(4);

    assertEquals(bob.getId(), "bob");
    aliases = bob.getAliases();
    assertTrue(aliases.contains("bob"));
  }

  // todo validate todos searched for if searchphrase not used
  // todo validate multiple searchphrase
}
