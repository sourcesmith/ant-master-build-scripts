package com.nurflugel.buildtasks;

import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfterLast;
import static org.apache.commons.lang.StringUtils.substringBefore;

/**
 * Class to find which jars contain the class you're looking for. Run with no args to get usage.
 *
 * @author  Douglas Bullard
 */
public class WhenceJava
{
  public static final String  VERSION           = "1.0.0";
  private static final String EXTENSION_JAR     = ".jar";
  private static final String EXTENSION_ZIP     = ".zip";
  private static final String SLASH             = "/";
  private static final String EXTENSION_SVN     = ".svn";
  private static final String EXTENSION_CLASS   = ".class";
  private static final String WILDCARD_CHAR     = "*";
  private boolean             wildcardEnd;
  private boolean             wildcardFront;
  private char                fileSep           = File.separatorChar;
  private String[]            entrySeparator    = { ",", ":" };
  private String              classToFind;
  private String              libPath;
  private int                 numberOfJarsFound = 0;
  private List<String>        outputLines;

  public WhenceJava()
  {
    wildcardEnd = false;
  }

  // -------------------------- OTHER METHODS --------------------------
  // todo this is too long - break up!!
  public void run()
  {
    outputLines = new ArrayList<String>();

    boolean shouldExit = usage();

    if (!shouldExit)
    {
      List<String> allList   = new ArrayList<String>();
      String       separator = null;

      if (libPath.contains(","))
      {
        separator = ",";
      }
      else if (libPath.contains(":"))
      {
        separator = ":";
      }
      else if (libPath.contains(";"))
      {
        separator = ";";
      }

      if (separator != null)
      {
        String[] dirsToParse = libPath.split(separator);

        allList.addAll(asList(dirsToParse));
      }
      else
      {
        allList.add(libPath);
      }

      String[] allPaths = allList.toArray(new String[allList.size()]);

      if (classToFind.startsWith(WILDCARD_CHAR))
      {
        wildcardFront = true;
        classToFind   = StringUtils.substringAfter(classToFind, WILDCARD_CHAR);
      }

      if (classToFind.endsWith(WILDCARD_CHAR))
      {
        wildcardEnd = true;
        classToFind = substringBefore(classToFind, WILDCARD_CHAR);
      }

      String text = "Class to find: " + classToFind;

      addToOutput(text);

      List<SearchResult> results = findClasses(allPaths, classToFind);

      displayResults(results);
    }
  }

  /** @noinspection  CallToSystemExit */
  private boolean usage()
  {
    if ((classToFind == null) || (libPath == null))
    {
      addToOutput("usage: build whenceJava -Dpath=xxxx -Dclass=SomeClass");
      addToOutput("       path = comma delimited list of dirs or archives to search.");
      addToOutput("       class = name of class to search for.");
      addToOutput("Example: whencejava -Dclass=Vector -Dpath=lib,loaderLib/struts  - finds the jar and packages structure for the Vector class");
      addToOutput("Example: whencejava -Dclass=*Vector -Dpath=lib  - finds the jar and packages structure for any class ending with Vector");
      addToOutput("Example: whencejava -Dclass=Vector* -Dpath=lib  - finds the jar and packages structure for any class beginning with Vector");
      addToOutput("Example: whencejava -Dclass=*Vector* -Dpath=lib - finds the jar and packages structure for any class with Vector as part of it's name");

      return true;
    }

    return false;
  }

  private void addToOutput(String text)
  {
    System.out.println(text);
    outputLines.add(text);
  }

  /** Find all of matching classes in the class path elements. */
  private List<SearchResult> findClasses(String[] classPathElements, String classToFind)
  {
    List<SearchResult> results = new ArrayList<SearchResult>();

    for (String classpathElement : classPathElements)
    {
      File classpathFile = new File(classpathElement);

      if (!classpathFile.exists())
      {
        addToOutput("       *** file or directory does not exist: " + classpathFile.getAbsolutePath());
      }
      else if (classpathFile.isDirectory())
      {
        parseDirectoryClasspath(classToFind, results, classpathElement, classpathFile);
      }
      else if (classpathFile.isFile())  // is is is a zip or a .jar?
      {
        parseFileClasspath(classToFind, results, classpathElement, classpathFile);
      }
    }

    return results;
  }

  private void parseDirectoryClasspath(String classToFind, List<SearchResult> results, String classpathElement, File classpathFile)
  {
    String[] filesInDir = classpathFile.list();

    if (!classpathElement.endsWith(EXTENSION_SVN))
    {
      addToOutput("Searching " + classpathElement);

      for (String fileName : filesInDir)
      {
        findClassInFile(results, classToFind, classpathElement, fileName);
      }
    }
  }

  private void findClassInFile(List<SearchResult> results, String className, String currentDir, String fileName)
  {
    try
    {
      File fileNameWithDir = new File(currentDir + fileSep + fileName);

      if (fileName.endsWith(".class"))
      {
        boolean doDisplay = shouldDisplay(fileName, className, true);

        if (doDisplay)
        {
          results.add(new SearchResult(currentDir, trimClassExtension(fileName)));
        }
      }
      else if (isArchiveFile(fileName))
      {
        numberOfJarsFound++;
        findClassInZipEntries(results, className, fileNameWithDir);
      }
      else if (fileNameWithDir.isDirectory())
      {
        parseDirectoryClasspath(className, results, currentDir + fileSep + fileName, fileNameWithDir);
      }
    }
    catch (IOException e)
    {
      addToOutput("Encountered an error, skipping file " + fileName + ": " + e.getMessage());
    }
  }

  /** Tests the prospective class name the the desired matching name - if it matches, display it. */
  private boolean shouldDisplay(String nameToTest, String rawClassName, boolean isDir)
  {
    String nextTrimmedClassName = trimClassExtension(nameToTest);

    if (!isDir)
    {
      if (nextTrimmedClassName.contains(SLASH))
      {
        nextTrimmedClassName = substringAfterLast(nextTrimmedClassName, SLASH);
      }
    }

    String className = rawClassName.toLowerCase();

    nextTrimmedClassName = nextTrimmedClassName.toLowerCase();

    boolean shouldDisplay = false;

    if (wildcardFront && wildcardEnd)
    {
      if (nextTrimmedClassName.contains(className))
      {
        shouldDisplay = true;
      }
    }
    else if (wildcardFront && !shouldDisplay)
    {
      if (nextTrimmedClassName.endsWith(className))
      {
        shouldDisplay = true;
      }
    }
    else if (wildcardEnd && !shouldDisplay)
    {
      if (nextTrimmedClassName.startsWith(className))
      {
        shouldDisplay = true;
      }
    }
    else if (nextTrimmedClassName.equals(className))
    {
      shouldDisplay = true;
    }

    return shouldDisplay;
  }

  private static String trimClassExtension(String className)
  {
    String trimmedClassName = className;

    if (trimmedClassName.contains(EXTENSION_CLASS))
    {
      trimmedClassName = substringBefore(trimmedClassName, EXTENSION_CLASS);
    }

    return trimmedClassName;
  }

  private void parseFileClasspath(String classToFind, List<SearchResult> results, String classpathElement, File classpathFile)
  {
    if (isArchiveFile(classpathElement))
    {
      try
      {
        numberOfJarsFound++;
        findClassInZipEntries(results, classToFind, classpathFile);
      }
      catch (ZipException zipex)
      {
        addToOutput("      *** file not a zip file: " + classpathElement);
      }
      catch (IOException ioex)
      {
        addToOutput("      *** io error opening file: " + classpathElement);
      }
    }
    else
    {
      addToOutput("File " + classpathElement + " isn't a .jar or .zip file, can't read classes from it.");
    }
  }

  /**
   * @param   classpathElement  the file name to examine
   *
   * @return  returns true if this is a java archive (zip or jar), false if otherwise.
   */
  private boolean isArchiveFile(String classpathElement)
  {
    return classpathElement.endsWith(EXTENSION_JAR) || classpathElement.endsWith(EXTENSION_ZIP);
  }

  /** If the class path element is a .zip or .jar, look in there. */
  private void findClassInZipEntries(List<SearchResult> results, String className, File classpathElement) throws IOException
  {
    // if(classpathElement.getPath().equals("lib/oracle/classes12.zip")){
    ZipFile     zipFile    = new ZipFile(classpathElement);
    Enumeration zipEntries = zipFile.entries();

    while (zipEntries.hasMoreElements())
    {
      String  nextFullClassName = zipEntries.nextElement().toString();
      boolean doDisplay         = shouldDisplay(nextFullClassName, className, false);

      if (doDisplay)  // todo - here is the problem - this only allows one instance of the class per file...
      {
        results.add(new SearchResult(classpathElement.getAbsolutePath(), nextFullClassName));
      }
      // }
    }
  }

  /** Display any results that were found. */
  private void displayResults(List<SearchResult> results)
  {
    addToOutput("\n");

    // find the longest key, add 4 to that, pad the rest
    if (results.isEmpty())
    {
      addToOutput("No jar or zip files found in search path " + libPath);
    }
    else
    {
      int maxLength = 0;

      for (SearchResult result : results)
      {
        maxLength = max(maxLength, result.getFilePath().length());
      }

      for (SearchResult result : results)
      {
        int           numberOfNeededSpaces = maxLength + 4 - result.getFilePath().length();
        StringBuilder buffer               = new StringBuilder();

        for (int j = 0; j < numberOfNeededSpaces; j++)
        {
          buffer.append(' ');
        }

        addToOutput("\t====>" + result.getFilePath() + buffer + result.getFullClassPathName());
      }
    }
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public int getNumberOfJarsFound()
  {
    return numberOfJarsFound;
  }

  public List<String> getOutputLines()
  {
    return outputLines;
  }

  public void setClassToFind(String classToFind)
  {
    this.classToFind = classToFind;
  }

  public void setLibPath(String libPath)
  {
    this.libPath = libPath;
  }

  // -------------------------- INNER CLASSES --------------------------
  private class SearchResult
  {
    private String filePath;
    private String fullClassPathName;

    SearchResult(String filePath, String fullClassPathName)
    {
      this.filePath          = filePath;
      this.fullClassPathName = fullClassPathName;
    }

    public String getFilePath()
    {
      return filePath;
    }

    public String getFullClassPathName()
    {
      return fullClassPathName;
    }
  }
}
