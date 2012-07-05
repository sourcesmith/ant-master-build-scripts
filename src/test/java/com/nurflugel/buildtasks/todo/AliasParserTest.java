package com.nurflugel.buildtasks.todo;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import static com.nurflugel.buildtasks.todo.AliasParser.getAliases;

/** Created with IntelliJ IDEA. User: dbulla Date: 7/5/12 Time: 11:08 AM To change this template use File | Settings | File Templates. */
public class AliasParserTest
{
  @Test
  public void testGetSimpleText() throws Exception
  {
    List<String> strings = getAliases("dbulla");

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
    List<String> strings = getAliases("dbulla(dgb)");

    findAllAliases(strings, "dbulla", "dgb");
  }

  @Test
  public void testGetTwoAlias() throws Exception
  {
    List<String> strings = getAliases("dbulla(dgb,doug)");

    findAllAliases(strings, "dbulla", "dgb", "doug");
  }

  @Test
  public void testGetEmptyComma() throws Exception
  {
    List<String> strings = getAliases("dbulla(dgb,,doug)");

    findAllAliases(strings, "dbulla", "dgb", "doug");
  }

  @Test
  public void testGetEmptyParens() throws Exception
  {
    List<String> strings = getAliases("dbulla()");

    findAllAliases(strings, "dbulla");
  }

  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxNoCloseParens() throws Exception
  {
    getAliases("dbulla(");
  }

  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxNoOpenParens() throws Exception
  {
    getAliases("dbulla)");
  }

  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxWrongParens1() throws Exception
  {
    getAliases("dbulla{");
  }
  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxWrongParens2() throws Exception
  {
    getAliases("dbulla[");
  }
  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxWrongParens3() throws Exception
  {
    getAliases("dbulla]");
  }
  @Test(expectedExceptions = BadParsingException.class)
  public void testBadSyntaxWrongParens4() throws Exception
  {
    getAliases("dbulla}");
  }
}
