package com.nurflugel.buildtasks.todo;

import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.assertEquals;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 7/2/12 Time: 21:53 To change this template use File | Settings | File Templates. */
@Test(groups = "todos")
public class FindTodosCoreTaskTest
{
  @Test
  public void testDefaultSearchPhrase() throws Exception
  {
    FindTodosCoreTask task         = new FindTodosCoreTask();
    String            searchPhrase = task.getSearchPhrase();

    assertEquals(searchPhrase, "todo");
  }

  @Test
  public void testSimpleParse() throws Exception
  {
    FindTodosCoreTask task = new FindTodosCoreTask();

    task.setNamePattern("dbulla(doug;dgb),dlabar");

    List<User> users = task.getUsers();

    assertEquals(users.get(0), new User("dbulla"));
  }

  @Test
  public void testSimpleParseLines() throws Exception
  {
    FindTodosCoreTask task  = new FindTodosCoreTask();
    String[]          lines = { "dkdjlksjlskslkj lksdj ls todo dbulla test1" };

    task.setNamePattern("dbulla(doug;dgb),dlabar");

    List<User>                users = task.getUsers();
    Map<User, List<TodoItem>> items = task.parseLines(lines, users);
  }
}
