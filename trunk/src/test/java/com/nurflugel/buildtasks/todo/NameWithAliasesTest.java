package com.nurflugel.buildtasks.todo;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

@Test(groups = "todos")
public class NameWithAliasesTest
{
  @Test
  public void testEquals() throws Exception
  {
    NameWithAliases nameWithAliases1 = new NameWithAliases("dbulla", new String[] { "doug is great", "doug is badass", "doug need a new job" });
    NameWithAliases nameWithAliases2 = new NameWithAliases("dbulla", new String[] { "doug is NOT great", "doug is lame", "doug has a day job" });

    assertEquals(nameWithAliases1, nameWithAliases2);
  }

  @Test
  public void testEquals2() throws Exception
  {
    NameWithAliases nameWithAliases1 = new NameWithAliases("dbulla");
    NameWithAliases nameWithAliases2 = new NameWithAliases("dbulla");

    assertEquals(nameWithAliases1, nameWithAliases2);
  }
}
