package com.ryangrier.ant;

import static com.ryangrier.ant.TaskTools.findVariableNameValueInClassFile;
import static com.ryangrier.ant.TaskTools.getFilePathFromClassName;

import org.apache.commons.io.FileUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

import java.util.List;

/**
 * This class will be able to change a version number in a java file. This assumes that the version number is located in a static variable (hopefully
 * final) and does not get changed elsewhere.
 *
 * <p>There are three different kinds of version updates, Major (0.6.11 -> 1.0.0), Point (0.6.11 -> 0.7.0) and Minor (0.6.11 -> 0.6.12). These types
 * are all located in <code>com.ryangrier.ant.VersionUpdate#versionTypes</code>.</p>
 *
 * @author   Ryan Grier <a href="http://www.ryangrier.com">http://www.ryangrier.com</a>
 * @version  1.3.0
 */
@SuppressWarnings("CloneableClassWithoutClone")
public class VersionFinder extends Task
{
  /** Source Directory of Class to be modified. */
  private File srcdir;

  /** The variable which holds the version number. */
  private String variablename = "version";

  /** Class name of Class to be modified. */
  private String classname;

  /** The Class File to be modified. */
  private File classFile;

  /** The contents of the Java file. */
  private List<String> fileContents;

  /** Custom property name. */
  private String propertyName;

  // --------------------------- main() method ---------------------------
  /**
   * The main program for VersionUpdate. It just prints out the current version number.
   *
   * @param  args  Arguments passed to the program - ignored.
   */
  public static void main(String[] args)
  {
    VersionFinder vu = new VersionFinder();

    vu.setClassname("com.jazzautomation.Version");

    // vu.setSrcdir(new File("/Users/douglasbullard/Documents/JavaStuff/Google_Code/IvyTools/trunk/source/java/src/"));
    vu.setSrcdir(new File("/googleDev/jazzautomation_source_FRESH/src/main/java"));
    vu.setVariablename("VERSION");
    vu.execute();
  }

  /**
   * Sets the classname to update the build version.
   *
   * @param  classname  The classname.
   */
  public void setClassname(String classname)
  {
    this.classname = classname;
  }

  /**
   * Set the source directories to find the source Java files.
   *
   * @param  srcdir  The source file directory.
   */
  public void setSrcdir(File srcdir)
  {
    this.srcdir = srcdir;
  }

  /**
   * The setter for the "variablename" attribute.
   *
   * @param  variablename  The variableName.
   */
  public void setVariablename(String variablename)
  {
    this.variablename = variablename;
  }

  /**
   * The method executing the task.
   *
   * @throws  org.apache.tools.ant.BuildException  If srcdir or classname are null. This exception is also thrown if the variable cannot be found, The
   *                                               java file does not exist,
   */
  public void execute() throws BuildException
  {
    if (null == srcdir)
    {
      throw new BuildException("The srcdir variable cannot be null.");
    }

    if (null == classname)
    {
      throw new BuildException("The classname variable cannot be null.");
    }

    classFile = getFilePathFromClassName(srcdir, classname);

    if (classFile.exists())
    {
      try
      {
        fileContents = FileUtils.readLines(classFile);

        String currentVersion = findVariableNameValueInClassFile(fileContents, variablename);

        if (null != propertyName)
        {
          getProject().setNewProperty(propertyName, currentVersion);
        }
      }
      catch (Exception ex)
      {
        throw new BuildException("Error doing version update", ex);
      }
    }
    else
    {
      throw new BuildException("The File: " + classFile.getPath() + " does not exist.");
    }
  }

  /**
   * Sets the ant property name to store the new version number in..
   *
   * @param  propertyName  The Ant Property name.
   */
  public void setPropertyName(String propertyName)
  {
    this.propertyName = propertyName;
  }
}
