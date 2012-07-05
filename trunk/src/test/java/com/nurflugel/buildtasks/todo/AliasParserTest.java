package com.nurflugel.buildtasks.todo;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;

/** Created with IntelliJ IDEA. User: dbulla Date: 7/5/12 Time: 11:08 AM To change this template use File | Settings | File Templates. */
public class AliasParserTest
{
  @Test
  public void testGetSimpleText() throws Exception
  {
    List<String> strings = AliasParser.getAliases("dbulla");

    findAllAliases(strings, "dbulla");
  }

  private void findAllAliases(List<String> strings, String... text)
  {
    Assert.assertEquals(strings.size(), text.length);
    for (String item : text)
    {
      
      Assert.assertTrue(strings.contains(item));
    }
  }

  
  
  @Test
  public void testGetOneAlias() throws Exception
  {
    List<String> strings = AliasParser.getAliases("dbulla(dgb)");

    findAllAliases(strings, "dbulla", "dgb");
  }

  @Test
  public void testGetTwoAlias() throws Exception
  {
    List<String> strings = AliasParser.getAliases("dbulla(dgb,doug)");

    findAllAliases(strings, "dbulla", "dgb", "doug");
  }
  @Test
  public void testGetEmptyComma() throws Exception
  {
    List<String> strings = AliasParser.getAliases("dbulla(dgb,,doug)");

    findAllAliases(strings, "dbulla", "dgb", "doug");
  }
  @Test
  public void testGetEmptyParens() throws Exception
  {
    List<String> strings = AliasParser.getAliases("dbulla()");

    findAllAliases(strings, "dbulla");
  }
}
