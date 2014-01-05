package com.ryangrier.ant;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * This class has some helper methods for the VersionTracker and BuildTracker classes.
 *
 * @author   Ryan Grier <a href="http://www.ryangrier.com">http://www.ryangrier.com</a>
 * @version  1.1
 */
public class TaskTools
{
  /**
   * This class takes the sourcePath file and The classname given to the ant task and figures out the filename of the class.
   *
   * <p>Ex: <code>com.ryangrier.ant.test.VersionTest</code> becomes <code>com/ryangrier/ant/test/VersionTest.java</code></p>
   *
   * @param   sourceDirectory  Description of the Parameter
   * @param   className        Description of the Parameter
   *
   * @return  The filePathFromClassName value
   */
  public static File getFilePathFromClassName(File sourceDirectory, String className)
  {
    String fileName = StringUtils.replace(className, ".", System.getProperty("file.separator", "/"));

    fileName += ".java";

    return new File(sourceDirectory, fileName);
  }

  /**
   * Finds the value of the version/build number in the given class file contents.
   *
   * @param      fileContents  The file to look for.
   * @param      variableName  The variable name to look for.
   *
   * @return     The variable value.
   *
   * @exception  Exception  Description of the Exception
   * @throws     Exception  An Exception has occurred.
   */
  public static String findVariableNameValueInClassFile(List<String> fileContents, String variableName) throws Exception
  {
    for (String line : fileContents)
    {
      if (line.contains(variableName) && line.contains("="))
      {
        line = StringUtils.substringAfter(line, "=");

        if (line.contains(";"))
        {
          line = StringUtils.substringBefore(line, ";");
        }

        line = StringUtils.replace(line, "\"", "");
        line = line.trim();

        return line;
      }
    }

    return null;
  }

  /**
   * Finds the old version in the fileContents param and replaces with new version.
   *
   * @param   fileContents  The file contents to be searched and replaced.
   * @param   oldString     The old string to find (to be replaced).
   * @param   newString     Replace the old String with this.
   *
   * @return  String The file contents with the newString instead of the old.
   */
  public static List<String> replaceOldVersion(List<String> fileContents, String oldString, String newString)
  {
    List<String> newLines = new ArrayList<>();

    for (String line : fileContents)
    {
      if (line.contains(oldString) && line.contains("="))
      {
        String newLine = StringUtils.substringBefore(line, oldString) + newString + StringUtils.substringAfter(line, oldString);

        newLines.add(newLine);
      }
      else
      {
        newLines.add(line);
      }
    }

    return newLines;
  }

  private TaskTools() {}
}
