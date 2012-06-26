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

    assertEquals(aliases.size(), 3);
    assertTrue(aliases.contains("dbulla"));
    assertTrue(aliases.contains("doug"));
    assertTrue(aliases.contains("dgb"));

    User snara = users.get(3);

    assertEquals(snara.getId(), "snara");
    aliases = snara.getAliases();
    assertEquals(aliases.size(), 2);
    assertTrue(aliases.contains("snara"));
    assertTrue(aliases.contains("sunitha"));

    User bob = users.get(4);

    assertEquals(bob.getId(), "bob");
    aliases = bob.getAliases();
    assertEquals(aliases.size(), 1);
    assertTrue(aliases.contains("bob"));
  }

  @Test(groups             = "unit",
        expectedExceptions = BuildException.class)
  public void testParseBadUsers1()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla{dgb;doug;dbulla),snara(snara;sunitha)");
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

  @Test(groups             = "unit",
        expectedExceptions = BuildException.class)
  public void testParseBadUsers2()
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla(dgb;doug;dbulla]:snara(snara;sunitha)");
    task.parseUsers();
  }

  @Test(groups = "unit")
  public void testFiles() throws IOException
  {
    FindTodosTask task = new FindTodosTask();
    File          dir  = new File(getFilePath(""));

    task.setBaseDir(dir);
    task.setReportDir(new File("build/reports"));
    task.setNamePattern("dbulla(dgb;doug),snara(snara3;sunitha),bren");

    List<User> users = task.parseUsers();

    task.findTodos(dir, users);

    List<TodoItem> todos = users.get(0).getTodos();

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

  // public static void main(String[] args)
  // {
  // FindTodosTask task = new FindTodosTask();
  //
  // task.setBaseDir(new File("/nikeDev/ecommerce/ordercapture/trunk/source"));
  //
  // // task.setBaseDir(new File("unversioned/config"));
  // task.setNamePattern("dbulla(dgb;doug),snara(sunitha),bren,dboyd2,bcohen,tzela(tomek)");
  //
  // // task.setBaseDir(new File("/nikeDev/ecommerce/productsearch/productsearch/trunk/source"));
  // // task.setBaseDir(new File("/nikeDev/other/cdmII/trunk/source"));
  // task.setReportDir(new File("unversioned/reports"));
  // task.execute();
  // task = new FindTodosTask();
  // task.setBaseDir(new File("/nikeDev/ecommerce/ordercapture/trunk/source"));
  // task.setNamePattern("dbulla(dgb;doug),snara(sunitha),bren,dboyd2,cohen,tzela(tomek)");
  // task.setSearchPhrase("codereview");
  // task.setReportDir(new File("unversioned/reports"));
  // task.execute();
  // }
}
