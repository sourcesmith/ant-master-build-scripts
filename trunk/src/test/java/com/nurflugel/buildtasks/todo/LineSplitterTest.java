package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.testng.Assert;
import org.testng.annotations.Test;
import static com.nurflugel.buildtasks.todo.LineSplitter.splitLine;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Created with IntelliJ IDEA. User: dbulla Date: 6/28/12 Time: 4:19 PM To change this template use File | Settings | File Templates. */
@Test(groups = "todos")
public class LineSplitterTest
{
  @Test
  public void testSplitLine() throws Exception
  {
    String[] strings = splitLine("dbulla(dgb;douglas;doug),dlabar(derek),dduddl(dave;david)");

    for (String text : strings)
    {
      System.out.println("string = " + text);
    }

    assertEquals(strings.length, 3);  // should have dbulla, dlabar, dduddl
    assertTrue(strings[0].startsWith("dbulla"));
    assertTrue(strings[1].startsWith("dlabar"));
    assertTrue(strings[2].startsWith("dduddl"));
  }

  @Test(expectedExceptions = BuildException.class)
  public void testBadChars1()
  {
    splitLine("dbulla(dgb;douglas;doug],dlabar(derek),dduddl(dave;david)");
  }

  @Test(expectedExceptions = BuildException.class)
  public void testBadChars2()
  {
    splitLine("dbulla(dgb;douglas;doug[,dlabar(derek),dduddl(dave;david)");
  }

  @Test(expectedExceptions = BuildException.class)
  public void testBadChars3()
  {
    splitLine("dbulla(dgb;douglas;doug{,dlabar(derek),dduddl(dave;david)");
  }

  @Test(expectedExceptions = BuildException.class)
  public void testBadChars4()
  {
    splitLine("dbulla(dgb;douglas;doug},dlabar(derek),dduddl(dave;david)");
  }

  @Test
  public void testNoNames()
  {
    String   namePattern = "didkdksksks slkjlskjslks";
    String[] strings     = splitLine(namePattern);

    assertEquals(strings.length, 1);
    assertEquals(strings[0], namePattern);
  }

  @Test
  public void testNoText()
  {
    String[] strings = splitLine("");

    assertEquals(strings.length, 0);
  }
}
