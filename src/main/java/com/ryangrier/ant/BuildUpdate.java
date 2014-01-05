package com.ryangrier.ant;

import static com.ryangrier.ant.TaskTools.findVariableNameValueInClassFile;
import static com.ryangrier.ant.TaskTools.getFilePathFromClassName;
import static com.ryangrier.ant.TaskTools.replaceOldVersion;

import org.apache.commons.io.FileUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class will be able to change a build number in a java file. This assumes that the build number is located in a static variable (hopefully
 * final) and does not get changed elsewhere.
 *
 * <p>For this class, your build number needs to have the following format:<br>
 * 2003010301 (Original Example)<br>
 * 2003 - 01 - 03 - 01 (Broken Down Further)<br>
 * year - month - day - build of day (This is what I really mean).</p>
 *
 * <p>In future versions, I hope to be able to have the user define a pattern to determine the build number pattern. But that'll have to wait.</p>
 *
 * @author   Ryan Grier <a href="http://www.ryangrier.com">http://www.ryangrier.com</a>
 * @version  1.3.0
 */
@SuppressWarnings("CloneableClassWithoutClone")
public class BuildUpdate extends Task
{
  /** Current Version of BuildUpdate. */
  private static final String BUILD_UPDATE_VERSION = "1.3.0";

  /** Source Directory of Class to be modified. */
  private File srcdir;

  /** The variable which holds the build number. */
  private String variablename = "build";

  /** The ant property which holds the build number. */
  private String antPropertyName = "VersionTool_Build";

  /** Class name of Class to be modified. */
  private String classname;

  /** The Class File to be modified. */
  private File classFile;

  /** Nested ManifestFiles. */
  private List<ManifestFile> manifestFiles = new ArrayList<>();

  /** The contents of the Java file. */
  private List<String> fileContents;

  /** Custom property name. */
  private String propertyName;

  /** Whether to print debug information. */
  private boolean debug;

  /**
   * Adds a ManifestFile to the task.
   *
   * @param  manifestFile  A nested ManifestFile.
   */
  public void addManifestFile(ManifestFile manifestFile)
  {
    manifestFiles.add(manifestFile);
  }

  /**
   * The method executing the task.
   *
   * @throws  BuildException  If srcdir or classname are null. This exception is also thrown if the variable cannot be found, The java file does not
   *                          exist,
   */
  @Override
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

        if (currentVersion.length() < 1)
        {
          throw new BuildException("Cannot find the variable : " + variablename);
        }

        String newVersion = getNewVersion(currentVersion);

        fileContents = replaceOldVersion(fileContents, currentVersion, newVersion);

        if (debug)
        {
          System.out.println("Updating " + classFile.getName() + " from build " + currentVersion + " to build " + newVersion);
        }

        FileUtils.writeLines(classFile, fileContents);
        getProject().setNewProperty(antPropertyName, newVersion);

        if (null != propertyName)
        {
          getProject().setNewProperty(propertyName, newVersion);
        }

        updateManifestFiles(newVersion);
      }
      catch (Exception ex)
      {
        throw new BuildException(ex.toString(), ex);
      }
    }
    else
    {
      throw new BuildException("The File: " + classFile.getPath() + " does not exist.");
    }
  }

  /**
   * Gets the new version.
   *
   * @param   currentVersion  The current build version.
   *
   * @return  The new build version.
   */
  private String getNewVersion(String currentVersion)
  {
    StringBuilder    sb           = new StringBuilder();
    SimpleDateFormat formatter    = new SimpleDateFormat("yyyyMMdd");
    Date             today        = new Date();
    String           newBuildDate = formatter.format(today);
    int              version      = Integer.parseInt(currentVersion.substring(currentVersion.length() - 2));
    String           oldBuildDate = currentVersion.substring(0, currentVersion.length() - 2);

    if (oldBuildDate.equals(newBuildDate))
    {
      sb.append(newBuildDate);

      String versionString = String.valueOf(version + 1);

      if (versionString.length() < 2)
      {
        sb.append("0");
      }

      sb.append(versionString);
    }
    else
    {
      sb.append(newBuildDate);
      sb.append("01");
    }

    return sb.toString();
  }

  /**
   * Updates the manifest files.
   *
   * @param   newVersion  the new Version number.
   *
   * @throws  Exception  Something has gone wrong.
   */
  private void updateManifestFiles(String newVersion) throws Exception
  {
    for (ManifestFile manifest : manifestFiles)
    {
      manifest.execute(newVersion);
    }
  }
  // --------------------------- main() method ---------------------------

  /**
   * The main program for VersionUpdate. It just prints out the current version number.
   *
   * @param  args  Arguments passed to the program - ignored.
   */
  public static void main(String[] args)
  {
    System.out.println("BuildUpdate Version: " + BUILD_UPDATE_VERSION);
  }
  // --------------------- GETTER / SETTER METHODS ---------------------

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
   * Sets the debug to update the build version.
   *
   * @param  debug  Runs as debug.
   */
  public void setDebug(boolean debug)
  {
    this.debug = debug;
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
   * Sets the variablename to update the build version.
   *
   * @param  variablename  The Variable Name.
   */
  public void setVariablename(String variablename)
  {
    this.variablename = variablename;
  }
}
