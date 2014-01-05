package com.ryangrier.ant;

import org.apache.commons.io.FileUtils;

import org.apache.tools.ant.taskdefs.Manifest;

import java.io.File;
import java.io.FileReader;

/**
 * A task definition which replaces an old manifest attribute value with a new value.
 *
 * @author   Ryan Grier
 * @version  1.0
 * @since    version_tool 1.1.4
 */
public class ManifestFile
{
  /** The manifest file file. */
  private File file;

  /** The variable to replace. */
  private String variable;

  /**
   * Sets the file attribute of the ManifestFile object.
   *
   * @param  file  The new file value
   */
  public void setFile(File file)
  {
    this.file = file;
  }

  /**
   * Sets the variable attribute of the ManifestFile object.
   *
   * @param  variable  The new variable value
   */
  public void setVariable(String variable)
  {
    this.variable = variable;
  }

  /**
   * Replaces the old variable name value with the new one. In this case, it is replacing a version/build string.
   *
   * @param      replacementValue  The new value
   *
   * @exception  Exception  Something went wrong.
   */
  public void execute(String replacementValue) throws Exception
  {
    if (null == file)
    {
      throw new Exception("The manifest file must be provided.");
    }

    if (!file.exists())
    {
      throw new Exception("The manifest file could not be found.");
    }

    if (null == variable)
    {
      throw new Exception("The manifest variable must be provided.");
    }

    Manifest           manifest  = new Manifest(new FileReader(file));
    Manifest.Section   section   = manifest.getMainSection();
    Manifest.Attribute attribute = section.getAttribute(variable);

    if (null == attribute)
    {
      throw new Exception("The " + variable + " attribute does not exist.");
    }

    attribute.setValue(replacementValue);
    FileUtils.writeByteArrayToFile(file, manifest.toString().getBytes());
  }
}
