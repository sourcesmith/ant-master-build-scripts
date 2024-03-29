package com.nurflugel.buildtasks.todo;

import com.nurflugel.buildtasks.todo.exceptions.BadParsingException;
import org.testng.annotations.Test;
import java.util.List;
import static com.nurflugel.buildtasks.todo.AliasParser.getAliases;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
    assertEquals(strings.size(), text.length);

    for (String item : text)
    {
      assertTrue(strings.contains(item));
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
