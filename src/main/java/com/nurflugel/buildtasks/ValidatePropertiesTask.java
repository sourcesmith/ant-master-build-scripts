package com.nurflugel.buildtasks;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.io.*;
import java.util.*;
import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

/** Goes through Ant files and looks for missing properties. todo - make it ignore comments */
public class ValidatePropertiesTask extends Task
{
  private static final String OPEN_PROPERTY  = "${";
  private static final String CLOSE_PROPERTY = "}";
  private String              exceptions     = "";
  private Set<String>         buildFiles     = new HashSet<String>();
  private String              errorText;

  @Override
  public void execute() throws BuildException
  {
    buildFiles = getBuildFiles();
    doWork();
  }

  public void doWork()
  {
    Set<String> properties           = new HashSet<String>();
    Set<String> allDefinedProperties = new HashSet<String>();
    Set<String> notMissingProperties = parseExceptions();

    try
    {
      for (String buildFile : buildFiles)
      {
        System.out.println("buildFile = " + buildFile);

        List<String> lines = readLines(new File(buildFile));

        processBuildFileLines(properties, allDefinedProperties, lines);
      }
    }
    catch (IOException e)
    {
      throw new BuildException(e);
    }

    validateProject(properties, allDefinedProperties, notMissingProperties);
  }

  private void processBuildFileLines(Set<String> properties, Set<String> allDefinedProperties, List<String> lines)
  {
    for (String line : lines)
    {
      parseLineForProps(properties, line);
      parseLineForDefinations(allDefinedProperties, line);
    }
  }

  private Set<String> getBuildFiles()
  {
    Map         projectProperties = getProjectProperties();
    Set         set               = projectProperties.keySet();
    Set<String> buildFileNames    = new HashSet<String>();

    for (Object o : set)
    {
      String propertyName = (String) o;

      if (propertyName.startsWith("ant.file"))
      {
        buildFileNames.add((String) projectProperties.get(propertyName));
      }
    }

    return buildFileNames;
  }

  private Map getProjectProperties()
  {
    Project theProject = getProject();

    return (theProject != null) ? theProject.getProperties()
                                : new Hashtable();
  }

  private Set<String> parseExceptions()
  {
    Set<String> notMissingProperties = new HashSet<String>();
    String[]    strings              = exceptions.split(",");

    for (String text : strings)
    {
      notMissingProperties.add(text.trim());
    }

    return notMissingProperties;
  }

  static void parseLineForProps(Set<String> properties, String line)
  {
    String line1 = line.trim();

    while (line1.contains(OPEN_PROPERTY) && !line1.startsWith("<!"))  // there's at least one property in here...
    {
      String property = substringAfter(line1, OPEN_PROPERTY);

      property = substringBefore(property, CLOSE_PROPERTY);

      if (!property.contains("@{"))                                   // don't do property names with attributes (yet)
      {
        properties.add(property);
      }

      line1 = substringAfter(line1, CLOSE_PROPERTY);
    }
  }

  /** Go through any custom ways of setting properties outside of property files. */
  static void parseLineForDefinations(Set<String> definedProperties, String line)
  {
    String[] customDefs = { "<available property=\"", "<setPropertyFromEnvstore propertyName=\"" };

    for (String def : customDefs)
    {
      parseLineForDefinitions(definedProperties, line, def);
    }
  }

  private static void parseLineForDefinitions(Set<String> definedProperties, String theLine, String def)
  {
    String line = theLine;

    while (line.contains(def))
    {
      line = substringAfter(line, def);

      String property = substringBefore(line, "\"");

      definedProperties.add(property);
      line = substringAfter(line, "\"");
    }
  }

  void validateProject(Set<String> properties, Set<String> allDefinedProperties, Set<String> notMissingProperties)
  {
    Map projectProperties = getProjectProperties();
    Set keys              = projectProperties.keySet();

    allDefinedProperties.addAll(keys);

    boolean       isFailed = false;
    StringBuilder buffer   = new StringBuilder();

    for (String property : properties)
    {
      if (!allDefinedProperties.contains(property) && !notMissingProperties.contains(property))
      {
        isFailed = true;
        buffer.append("\n\t").append(property);
      }
    }

    errorText = buffer.toString();

    if (isFailed)
    {
      throw new BuildException("Missing required properties in build file "  /*+ buildFileName*/ + ':' + errorText);
    }
  }

  public void setBuildFile(String buildFileName)
  {
    buildFiles.add(buildFileName);
  }

  public void setExceptions(String exceptions)
  {
    this.exceptions = exceptions;
  }

  public String getErrorText()
  {
    return errorText;
  }
}
