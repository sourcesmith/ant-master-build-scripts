package com.nurflugel.buildtasks.ant;

import static org.apache.commons.io.FileUtils.readLines;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Goes through Ant files and looks for missing properties. todo - make it ignore comments */
public class ValidatePropertiesTask extends Task
{
  private static final String OPEN_PROPERTY  = "${";
  private static final String CLOSE_PROPERTY = "}";
  private String              exceptions     = "";
  private Set<String>         buildFiles     = new HashSet<>();
  private String              errorText;

  /** parse the given line for any properties defined - if found, add them to the set. */
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

  // -------------------------- OTHER METHODS --------------------------
  @Override
  /** This just calls doWork so we can unit test it nicely without having to run in Ant
   *
   */
  public void execute() throws BuildException
  {
    buildFiles = getBuildFiles();
    doWork();
  }

  /** Get the list of build files that should have been passed in. */
  private Set<String> getBuildFiles()
  {
    Map         projectProperties   = getProjectProperties();
    Set         projectPropertyKeys = projectProperties.keySet();
    Set<String> buildFileNames      = new HashSet<>();

    for (Object o : projectPropertyKeys)
    {
      String propertyName = (String) o;

      if (propertyName.startsWith("ant.file"))
      {
        buildFileNames.add((String) projectProperties.get(propertyName));
      }
    }

    return buildFileNames;
  }

  /** Returns a safe map (if the project is null, an empty one). */
  private Map<String, String> getProjectProperties()
  {
    Map<String, String> results    = new HashMap<>();
    Project             theProject = getProject();

    if (theProject != null)
    {
      Map properties = theProject.getProperties();

      for (Object key : properties.keySet())
      {
        Object value = properties.get(key);

        results.put((String) key, (String) value);
      }
    }

    return results;
  }

  /** Do the actual work - make sure the properties are all defined. */
  public void doWork()
  {
    Set<String> properties           = new HashSet<>();
    Set<String> allDefinedProperties = new HashSet<>();
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

  private Set<String> parseExceptions()
  {
    Set<String> notMissingProperties = new HashSet<>();
    String[]    strings              = exceptions.split(",");

    for (String text : strings)
    {
      notMissingProperties.add(text.trim());
    }

    return notMissingProperties;
  }

  private static void processBuildFileLines(Set<String> properties, Set<String> allDefinedProperties, List<String> lines)
  {
    for (String line : lines)
    {
      parseLineForProps(properties, line);
      parseLineForDefinitions(allDefinedProperties, line);
    }
  }

  /** parse the given line for any properties used - if found, add them to the set. */
  public static void parseLineForProps(Set<String> properties, String line)
  {
    String trimmedLine = line.trim();

    while (trimmedLine.contains(OPEN_PROPERTY) && !trimmedLine.startsWith("<!"))  // there's at least one property in here...
    {
      String property = substringAfter(trimmedLine, OPEN_PROPERTY);

      property = substringBefore(property, CLOSE_PROPERTY);

      if (!property.contains("@{"))                                               // don't do property names with attributes (yet)
      {
        properties.add(property);
      }

      trimmedLine = substringAfter(trimmedLine, CLOSE_PROPERTY);
    }
  }

  /** Go through any custom ways of setting properties outside of property files. */
  public static void parseLineForDefinitions(Set<String> definedProperties, String line)
  {
    String[] customDefs = { "<available property=\"", "<setPropertyFromEnvstore propertyName=\"" };

    for (String def : customDefs)
    {
      parseLineForDefinitions(definedProperties, line, def);
    }
  }

  void validateProject(Set<String> properties, Set<String> allDefinedProperties, Set<String> notMissingProperties)
  {
    Map<String, String> projectProperties = getProjectProperties();
    Set<String>         keys              = projectProperties.keySet();

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

  // ------------------------ CANONICAL METHODS ------------------------
  public ValidatePropertiesTask clone() throws CloneNotSupportedException
  {
    return (ValidatePropertiesTask) super.clone();
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public String getErrorText()
  {
    return errorText;
  }

  public void setExceptions(String exceptions)
  {
    this.exceptions = exceptions;
  }
}
