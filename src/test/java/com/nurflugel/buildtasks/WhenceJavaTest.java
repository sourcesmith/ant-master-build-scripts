package com.nurflugel.buildtasks;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import static org.testng.Assert.assertEquals;

@Test(groups = "whencejava")
public class WhenceJavaTest
{
  File                dibbleFile      = new File("dibble");
  private PrintStream origionalStream;
  private final String[] usageOutput = new String[]{
    "usage: build whenceJava -Dpath=xxxx -Dclass=SomeClass",                                                                        //
    "       path = comma delimited list of dirs or archives to search.",                                                            //
    "       class = name of class to search for.",                                                                                  //
    "Example: whencejava -Dclass=Vector -Dpath=lib,loaderLib/struts  - finds the jar and packages structure for the Vector class",  //
    "Example: whencejava -Dclass=*Vector -Dpath=lib  - finds the jar and packages structure for any class ending with Vector",      //
    "Example: whencejava -Dclass=Vector* -Dpath=lib  - finds the jar and packages structure for any class beginning with Vector",   //
    "Example: whencejava -Dclass=*Vector* -Dpath=lib - finds the jar and packages structure for any class with Vector as part of it's name"
  };

  // -------------------------- OTHER METHODS --------------------------
  @AfterTest
  public void doAfter()
  {
    System.setOut(origionalStream);
    dibbleFile.delete();
  }

  @BeforeTest
  public void setup() throws FileNotFoundException
  {
    origionalStream = System.out;

    PrintStream newStream = new PrintStream(dibbleFile);

    System.setOut(newStream);
  }

  /** Stupid test, but shows how we can measure the output. */
  @Test
  public void testExecuteNoArgs() throws Exception
  {
    WhenceJava whenceJava = new WhenceJava();

    whenceJava.run();

    validateExpectedOutput(usageOutput);
  }

  private void validateExpectedOutput(String[] expectedOutput) throws IOException
  {
    List<String> lines = FileUtils.readLines(dibbleFile);
    int          index = 0;

    for (String line : expectedOutput)
    {
      assertEquals(lines.get(index++), line);
    }
  }

  /** Stupid test, but shows how we can measure the output. */
  @Test
  public void testExecuteNoArgs2() throws Exception
  {
    WhenceJava whenceJava = new WhenceJava();

    whenceJava.setClassToFind("*Vector*");
    whenceJava.run();

   

    validateExpectedOutput(usageOutput);
  }
  //todo test no valid dir
  //todo test no files in dir
  //todo test find known class in dir
  
}
