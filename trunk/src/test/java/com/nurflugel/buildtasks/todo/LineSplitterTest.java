package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;
import static com.nurflugel.buildtasks.todo.LineSplitter.splitLine;

/** Created with IntelliJ IDEA. User: dbulla Date: 6/28/12 Time: 4:19 PM To change this template use File | Settings | File Templates. */
public class LineSplitterTest
{
  @Test
  public void testSplitLine() throws Exception
  {
    String[] strings = splitLine("dbulla(dgb;douglas;doug),dlabar(derek),dduddl(dave;david)");

    for (String string : strings)
    {
      System.out.println("string = " + string);
    }

    // assertEquals(strings.length, 3);  // should have dbulla, dlabar, dduddl
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
}
