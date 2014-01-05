package com.ryangrier.ant;

import static com.ryangrier.ant.TaskTools.findVariableNameValueInClassFile;
import static com.ryangrier.ant.TaskTools.getFilePathFromClassName;
import static com.ryangrier.ant.TaskTools.replaceOldVersion;

import org.apache.commons.io.FileUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// import org.apache.tools.ant.taskdefs.Manifest;
// import org.apache.tools.ant.taskdefs.Manifest;
// import org.apache.tools.ant.taskdefs.Manifest;
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
public class VersionUpdate extends Task
{
  /** Application Version change options. */
  public static final String[] versionTypes = { "Major", "Point", "Minor" };

  /** Current Version of VersionUpdate. */
  private static final String VERSION_UPDATE_VERSION = "1.3.0";

  /** int value to perform a Major Update. */
  private static final int MAJOR_VERSION = 0;

  /** int value to perform a Point Update. */
  private static final int POINT_VERSION = 1;

  /** int value to perform a Minor Update. */
  private static final int MINOR_VERSION = 2;

  /** Source Directory of Class to be modified. */
  private File srcdir;

  /** The variable which holds the version number. */
  private String variablename = "version";

  /** The ant property which holds the version number. */
  private String antPropertyName = "VersionTool_Version";

  /** Class name of Class to be modified. */
  private String classname;

  /** The Class File to be modified. */
  private File classFile;

  /** Nested ManifestFiles. */
  private List<ManifestFile> manifestFiles = new ArrayList<>();

  /** The type of update to perform. */
  private int versionupdatetype = MINOR_VERSION;

  /** The contents of the Java file. */
  private List<String> fileContents;

  /** Custom property name. */
  private String propertyName;

  /**
   * Adds a ManifestFile to the task.
   *
   * @param  manifestFile  A nested ManifestFile.
   */
  public void addManifestFile(ManifestFile manifestFile)
  {
    manifestFiles.add(manifestFile);
  }
  // --------------------------- main() method ---------------------------

  /**
   * The main program for VersionUpdate. It just prints out the current version number.
   *
   * @param  args  Arguments passed to the program - ignored.
   */
  public static void main(String[] args)
  {
    System.out.println("VersionUpdate Version: " + VERSION_UPDATE_VERSION);

    VersionUpdate vu = new VersionUpdate();

    vu.setClassname("com.jazzautomation.Version");

    // vu.setSrcdir(new File("/Users/douglasbullard/Documents/JavaStuff/Google_Code/IvyTools/trunk/source/java/src/"));
    vu.setSrcdir(new File("/googleDev/jazzautomation_source_FRESH/src/main/java"));
    vu.setVariablename("VERSION");
    vu.setVersionupdatetype("minor");
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
   * Sets the variablename to update the build version.
   *
   * @param  versionupdatetype  The new versionupdatetype value
   */
  public void setVersionupdatetype(String versionupdatetype)
  {
    if (versionupdatetype.equals(versionTypes[POINT_VERSION]))
    {
      this.versionupdatetype = POINT_VERSION;
    }
    else if (versionupdatetype.equals(versionTypes[MAJOR_VERSION]))
    {
      this.versionupdatetype = MAJOR_VERSION;
    }
    else
    {
      this.versionupdatetype = MINOR_VERSION;
    }
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

        if (currentVersion.length() < 1)
        {
          throw new BuildException("Cannot find the variable : " + variablename);
        }

        String  newVersion = getNewVersion(currentVersion);
        Project project1   = getProject();

        if (project1 != null)
        {
          project1.setNewProperty("NewVersionName", newVersion);  // does this get passed back up to Gradle?
        }

        fileContents = replaceOldVersion(fileContents, currentVersion, newVersion);
        System.out.println("Updating " + classFile.getName() + " from v. " + currentVersion + " to v. " + newVersion);
        System.out.println("Setting project property " + propertyName + " to " + newVersion);
        FileUtils.writeLines(classFile, fileContents);

        if (null != propertyName)
        {
          getProject().setNewProperty(propertyName, newVersion);
        }

        updateManifestFiles(newVersion);
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
   * Gets the new version.
   *
   * @param   currentVersion  The current version.
   *
   * @return  The new version.
   */
  private String getNewVersion(String currentVersion)
  {
    StringTokenizer st      = new StringTokenizer(currentVersion, ".");
    StringBuilder   sb      = new StringBuilder();
    int             tokens  = st.countTokens();
    int             i       = 0;
    boolean         zeroOut = false;

    while (st.hasMoreTokens())
    {
      String smallChunkOfVersion = st.nextToken();

      if (versionupdatetype == i)
      {
        int version = Integer.parseInt(smallChunkOfVersion);

        version++;
        sb.append(version);
        zeroOut = true;
      }
      else if (zeroOut)
      {
        sb.append('0');
      }
      else
      {
        sb.append(smallChunkOfVersion);
      }

      i++;

      if (st.hasMoreTokens())
      {
        sb.append('.');
      }
    }

    while (tokens < 3)
    {
      sb.append('.' + '0');
      tokens++;
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
  // --------------------- GETTER / SETTER METHODS ---------------------

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
