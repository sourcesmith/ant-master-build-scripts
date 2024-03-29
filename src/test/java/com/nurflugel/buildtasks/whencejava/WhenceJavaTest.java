package com.nurflugel.buildtasks.whencejava;

import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.nurflugel.buildtasks.todo.TestResources.getTestFilePath;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.testng.Assert.assertEquals;

@Test(groups = "whencejava")
public class WhenceJavaTest
{
  private static final String[] USAGE_OUTPUT =
  {
    "usage: build whenceJava -Dpath=xxxx -Dclass=SomeClass",                                                                        //
    "       path = comma delimited list of dirs or archives to search.",                                                            //
    "       class = name of class to search for.",                                                                                  //
    "Example: whencejava -Dclass=Vector -Dpath=lib,loaderLib/struts  - finds the jar and packages structure for the Vector class",  //
    "Example: whencejava -Dclass=*Vector -Dpath=lib  - finds the jar and packages structure for any class ending with Vector",      //
    "Example: whencejava -Dclass=Vector* -Dpath=lib  - finds the jar and packages structure for any class beginning with Vector",   //
    "Example: whencejava -Dclass=*Vector* -Dpath=lib - finds the jar and packages structure for any class with Vector as part of it's name"
  };
  // -------------------------- OTHER METHODS --------------------------

  /** Stupid test, but shows how we can measure the output. */
  @Test
  public void testExecuteNoArgs() throws Exception
  {
    WhenceJava whenceJava = new WhenceJava();

    whenceJava.run();
    validateExpectedOutput(whenceJava.getOutputLines(), USAGE_OUTPUT);
  }

  /**
   * Take the collections, strip out any empty lines, then compare them.
   *
   * <p>Note that this doesn't deal well with extra spaces in the middle of lines if they differ...</p>
   */
  private static void validateExpectedOutput(List<String> actualOutput, String... expectedOutput)
  {
    List<String> cleanActual   = getCleanList(actualOutput);
    List<String> cleanExpected = getCleanList(Arrays.asList(expectedOutput));

    assertEquals(cleanActual, cleanExpected);
  }

  private static List<String> getCleanList(List<String> actualOutput)
  {
    List<String> cleanList = new ArrayList<String>();

    for (String line : actualOutput)
    {
      if (!isBlank(line))
      {
        cleanList.add(line);
      }
    }

    return cleanList;
  }

  @Test
  public void testExecuteNoArgs2()
  {
    WhenceJava whenceJava = new WhenceJava();

    whenceJava.setClassToFind("*Vector*");
    whenceJava.run();
    validateExpectedOutput(whenceJava.getOutputLines(), USAGE_OUTPUT);
  }

  /** test find a known class(es) in test dir with known jars. */
  @Test
  public void testFindKnownClasses()
  {
    String   filePath       = getTestFilePath("whencejava_libs");
    String[] expectedOutput =
    {
      "Class to find: BeanUtils",  //
      "Searching " + filePath,     //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/BeanUtils.class"
    };

    validateTest(filePath, "BeanUtils", expectedOutput);
  }

  /** test find a known class(es) in test dir with known jars. */
  @Test
  public void testFindWithLeadingWildCard()
  {
    String   filePath       = getTestFilePath("whencejava_libs");
    String[] expectedOutput =
    {
      "Class to find: *BeanUtils",                                                                                   //
      "Searching " + filePath,                                                                                       //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/locale/LocaleBeanUtils.class",  //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/BeanUtils.class"
    };

    validateTest(filePath, "*BeanUtils", expectedOutput);
  }

  /** test find a known class(es) in test dir with known jars. */
  @Test
  public void testFindWithTrailingWildCard()
  {
    String   filePath       = getTestFilePath("whencejava_libs");
    String[] expectedOutput =
    {
      "Class to find: BeanU*",                                                                               //
      "Searching " + filePath,                                                                               //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/BeanUtilsBean.class",   //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/BeanUtils.class",       //
      "\t====>" + filePath + "/commons-beanutils.jar    org/apache/commons/beanutils/BeanUtilsBean$1.class"  //
    };

    validateTest(filePath, "BeanU*", expectedOutput);
  }

  /** test find a known class(es) in test dir with known jars. */
  @Test
  public void testFindWithLeadingAndTrailingWildCard()
  {
    String   filePath       = getTestFilePath("whencejava_libs");
    String[] expectedOutput =
    {
      "Class to find: *eanU*",                                                                                                       //
      "Searching " + filePath,                                                                                                       //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/locale/LocaleBeanUtils$Descriptor.class",      //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/locale/LocaleBeanUtilsBean$Descriptor.class",  //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/BeanUtilsBean.class",                          //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/locale/LocaleBeanUtilsBean.class",             //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/locale/LocaleBeanUtils.class",                 //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/BeanUtils.class",                              //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/locale/LocaleBeanUtilsBean$1.class",           //
      "\t====>" + filePath + "/commons-beanutils.jar     org/apache/commons/beanutils/BeanUtilsBean$1.class",                        //
      "\t====>" + filePath + "/commons-fileupload.jar    org/apache/commons/fileupload/servlet/FileCleanerCleanup.class",            //
      "\t====>" + filePath + "/commons-lang.jar          org/apache/commons/lang/BooleanUtils.class"
    };

    validateTest(filePath, "*eanU*", expectedOutput);
  }

  /** We should tell the user that no jar or zip files were found in the dir. */
  @Test
  public void testJarCount()
  {
    WhenceJava wj = new WhenceJava();

    wj.setClassToFind("*BeanUtils");
    wj.setLibPath(getTestFilePath("whencejava_libs"));
    wj.run();

    int numberOfJars = wj.getNumberOfJarsFound();

    assertEquals(numberOfJars, 14);
  }

  /** Make sure recursive counts are correct. */
  @Test
  public void testJarsInSubDirsCount()
  {
    WhenceJava wj = new WhenceJava();

    wj.setClassToFind("*BeanUtils");
    wj.setLibPath(getTestFilePath("jarsInSubDirs"));
    wj.run();

    int numberOfJars = wj.getNumberOfJarsFound();

    assertEquals(numberOfJars, 14);
  }

  /** Make sure we count zips and jars, but not other files. */
  @Test
  public void testMixedJarZipCount()
  {
    WhenceJava wj = new WhenceJava();

    wj.setClassToFind("*BeanUtils");
    wj.setLibPath(getTestFilePath("mixedJarsZipsText"));
    wj.run();

    int numberOfJars = wj.getNumberOfJarsFound();

    assertEquals(numberOfJars, 6);
  }

  /** We should tell the user that no jar or zip files were found in the dir. */
  @Test
  public void testNoJarFilesInDir()
  {
    String   emptyDir       = getTestFilePath("emptyDir");
    String[] expectedOutput =
    {
      "Class to find: *BeanUtils",                            //
      "Searching " + emptyDir,                                //
      "No jar or zip files found in search path " + emptyDir
    };

    validateTest(emptyDir, "*BeanUtils", expectedOutput);
  }

  @Test
  public void testMultipleLibPaths()
  {
    String[] separatorChars = { ",", ":", ";" };

    for (String separatorChar : separatorChars)
    {
      WhenceJava wj = new WhenceJava();

      wj.setClassToFind("*BeanUtils");

      String filePath1 = getTestFilePath("whencejava_libs");
      String filePath2 = getTestFilePath("mixedJarsZipsText");
      String filePath  = filePath1 + separatorChar + filePath2;

      wj.setLibPath(filePath);
      wj.run();

      String[] expectedOutput =
      {
        "Class to find: *BeanUtils",                                                                                      //
        "Searching " + filePath1,                                                                                         //
        "Searching " + filePath2,                                                                                         //
        "\t====>" + filePath1 + "/commons-beanutils.jar      org/apache/commons/beanutils/locale/LocaleBeanUtils.class",  //
        "\t====>" + filePath1 + "/commons-beanutils.jar      org/apache/commons/beanutils/BeanUtils.class",               //
        "\t====>" + filePath2 + "/commons-beanutils.zip    org/apache/commons/beanutils/locale/LocaleBeanUtils.class",    //
        "\t====>" + filePath2 + "/commons-beanutils.zip    org/apache/commons/beanutils/BeanUtils.class"
      };

      validateExpectedOutput(wj.getOutputLines(), expectedOutput);
    }
  }

  @Test
  public void testJarFilePaths()
  {
    String   filePath       = getTestFilePath("whencejava_libs/commons-beanutils.jar");
    String[] expectedOutput =
    {
      "Class to find: *BeanUtils",                                                             //
      "Searching " + filePath,                                                                 //
      "\t====>" + filePath + "    org/apache/commons/beanutils/locale/LocaleBeanUtils.class",  //
      "\t====>" + filePath + "    org/apache/commons/beanutils/BeanUtils.class"                //
    };

    validateTest(filePath, "*BeanUtils", expectedOutput);
  }

  private void validateTest(String filePath, String classToFind, String[] expectedOutput)
  {
    WhenceJava wj = new WhenceJava();

    wj.setClassToFind(classToFind);
    wj.setLibPath(filePath);
    wj.run();
    validateExpectedOutput(wj.getOutputLines(), expectedOutput);
  }
}
