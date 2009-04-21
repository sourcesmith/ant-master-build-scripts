package com.nurflugel.buildtasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import java.io.*;
import java.util.*;


/** Goes through Ant files and looks for missing properties.
 * todo - make it ignore comments 
  */
public class ValidatePropertiesTask

        extends Task
{
    private String exceptions="";

    public void execute() throws BuildException
    {

        Project theProject = getProject();

        Set<String> buildFiles = getBuildFiles(theProject);
        Set<String> properties = new HashSet<String>();
        Set<String> allDefinedProperties = new HashSet<String>();
        Set<String> notMissingProperties = parseExceptions();
        try
        {
            for (String buildFile : buildFiles)
            {
                System.out.println("buildFile = " + buildFile);
                FileReader fileReader = new FileReader(buildFile);
                BufferedReader reader = new BufferedReader(fileReader);
                String line = reader.readLine();

                while (line != null)
                {
                    parseLineForProps(properties, line);
                    parseLineForDefinations(allDefinedProperties, line);
                    line = reader.readLine();
                }

                reader.close();

            }
        }
        catch (IOException e)
        {
            throw new BuildException(e);
        }

        validateProject(theProject, properties, allDefinedProperties, notMissingProperties);

    }

    private Set<String> getBuildFiles(Project project)
    {
        Hashtable projectProperties = project.getProperties();
        Set set = projectProperties.keySet();
        Set<String> buildFileNames = new HashSet<String>();
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

    private Set<String> parseExceptions()
    {
        Set<String> notMissingProperties = new HashSet<String>();
        String[] strings = exceptions.split(",");
        for (String text : strings)
        {
            notMissingProperties.add(text.trim());
        }
        return notMissingProperties;
    }

    private void parseLineForProps(Set<String> properties, String line)
    {
        while (line.contains("${") && !line.trim().startsWith("<!"))
        {
            int firstIndex = line.indexOf("${");
            int secondIndex = line.indexOf("}", firstIndex);
            String property = line.substring(firstIndex + 2, secondIndex);
            if (!property.contains("@{"))
            {
                properties.add(property);
            }

            line = line.substring(secondIndex + 1);
        }
    }


    /** Go through any custom ways of setting properties outside of property files */
    private void parseLineForDefinations(Set<String> definedproperties, String line)
    {
        String [] defs = {"<available property=\"","<setPropertyFromEnvstore propertyName=\""};
        for (String def : defs)
        {
            parseLineForDefinations(definedproperties, line, def);
        }
    }

    private void parseLineForDefinations(Set<String> definedproperties, String line, String def)
    {
        while (line.contains(def))
        {
            int firstIndex = line.indexOf(def);
            line = line.substring(firstIndex + def.length());
            int secondIndex = line.indexOf("\"");
            String property = line.substring(0, secondIndex);

            definedproperties.add(property);

            line = line.substring(secondIndex + 1);
        }
    }

    private void validateProject(Project project, Set<String> properties, Set<String> allDefinedProperties, Set<String> notMissingProperties)
    {
        if (project != null)
        {
            Map projectProperties = project.getProperties();
            Set keys = projectProperties.keySet();
            allDefinedProperties.addAll(keys);
            boolean isFailed = false;
            StringBuffer buffer = new StringBuffer();
            for (String property : properties)
            {
                if (!allDefinedProperties.contains(property) && !notMissingProperties.contains(property))
                {
                    isFailed = true;
                    buffer.append("\n").append(property);
                }
            }
            if (isFailed)
            {
                throw new BuildException("Missing required properties in build file " /*+ buildFileName*/ + ":" + buffer.toString());
            }
        }
    }

//    public void setBuildFile(String buildFileName)
//    {
//        this.buildFileName = buildFileName;
//    }

    public static void main(String[] args)
    {
        ValidatePropertiesTask task = new ValidatePropertiesTask();
//        task.setBuildFile("build/master-build/master-build.xml");
        task.execute();
    }

    public void setExceptions(String exceptions)
    {
        this.exceptions = exceptions;
    }
}
