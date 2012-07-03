package com.nurflugel.buildtasks.todo;

import org.testng.Assert;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 7/2/12 Time: 21:53 To change this template use File | Settings | File Templates. */
@Test(groups = "todos")
public class FindTodosTaskTest
{
  @Test
  public void testExecute() throws Exception
  {
    FindTodosTask task = new FindTodosTask();

    task.setNamePattern("dbulla");

    String searchPhrase = task.getSearchPhrase();

    assertEquals(searchPhrase, "todo");
  }
}
