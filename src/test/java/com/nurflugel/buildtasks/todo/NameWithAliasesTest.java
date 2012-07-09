package com.nurflugel.buildtasks.todo;

import org.testng.annotations.Test;
import java.util.Set;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "todos")
public class NameWithAliasesTest
{
  @Test
  public void testSimpleParsing()
  {
    NameWithAliases nameWithAliases = new NameWithAliases("dbulla");

    assertEquals(nameWithAliases.getName(), "dbulla");
  }

  @Test
  public void testSimpleParsingWithAlias()
  {
    NameWithAliases nameWithAliases = new NameWithAliases("dbulla", "doug is great", "doug is badass", "doug need a new job");

    assertEquals(nameWithAliases.getName(), "dbulla");

    Set<String> aliases = nameWithAliases.getAliases();

    assertTrue(aliases.contains("dbulla"));
    assertTrue(aliases.contains("doug is great"));
    assertTrue(aliases.contains("doug is badass"));
    assertTrue(aliases.contains("doug need a new job"));
  }

  @Test
  public void testEquals() throws Exception
  {
    NameWithAliases nameWithAliases1 = new NameWithAliases("dbulla", "doug is great", "doug is badass", "doug need a new job");
    NameWithAliases nameWithAliases2 = new NameWithAliases("dbulla", "doug is NOT great", "doug is lame", "doug has a day job");

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
