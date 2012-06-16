package com.nurflugel.buildtasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static java.util.Arrays.asList;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Class to find which jars contain the class you're looking for. Run with no args to get usage.
 *
 * @author        Douglas Bullard
 * @noinspection  UseOfSystemOutOrSystemErr,ClassWithoutPackageStatement
 */
public class WhenceJava  // extends Task
{
  private boolean wildcardEnd;
  private boolean wildcardFront;
  private char    fileSep        = File.separatorChar;
  private String  entrySeperator = ",";
  private String  classToFind;
  private String  libPath;

  public WhenceJava()
  {
    wildcardEnd = false;
  }

  // --------------------------- main() method ---------------------------
  public static void main(String[] args)
  {
    WhenceJava wj = new WhenceJava();

    if (args.length == 0)
    {
      wj.setClassToFind("Task*");
      wj.setLibPath("/Users/douglasbullard/Documents/JavaStuff/Google_Code/ant-master-build-scripts/trunk/unversioned/lib/build");
    }
    else if (args.length == 2)
    {
      wj.setClassToFind(args[0]);
      wj.setLibPath(args[1]);
    }

    wj.execute();
  }

  public void execute() throws BuildException
  {
    // String[] dibble={"Vector","/snapshots/admin/trunk/unversioned/lib/source"};
    // wj.doit(dibble);
    usage();

    String[]     dirsToParse = libPath.split(entrySeperator);
    List<String> allList     = new ArrayList<String>();

    allList.addAll(asList(dirsToParse));

    String[] allPaths = allList.toArray(new String[allList.size()]);

    if (classToFind.startsWith("*"))
    {
      wildcardFront = true;
      classToFind   = classToFind.substring(1);
    }

    if (classToFind.endsWith("*"))
    {
      wildcardEnd = true;
      classToFind = classToFind.substring(0, classToFind.length() - 1);
    }

    System.out.println("Class to find: " + classToFind);

    List<SearchResult> results = findClasses(allPaths, classToFind);

    displayResults(results);
  }

  /** @noinspection  CallToSystemExit */
  private void usage()
  {
    if ((classToFind == null) || (libPath == null))
    {
      System.out.println("usage: build whenceJava -Dpath=xxxx -Dclass=SomeClass");
      System.out.println("       path = comma delimited list of dirs or archives to search.");
      System.out.println("       class = name of class to search for.");
      System.out.println("Example: whencejava -Dclass=Vector -Dpath=lib,loaderLib/struts  - finds the jar and packages structure for the Vector class");
      System.out.println("Example: whencejava -Dclass=*Vector -Dpath=lib  - finds the jar and packages structure for any class ending with Vector");
      System.out.println("Example: whencejava -Dclass=Vector* -Dpath=lib  - finds the jar and packages structure for any class beginning with Vector");
      System.out.println("Example: whencejava -Dclass=*Vector* -Dpath=lib - finds the jar and packages structure for any class with Vector as part of it's name");
      System.exit(0);
    }
  }

  /** Find all of matching classes in the class path elements. */
  private List<SearchResult> findClasses(String[] classPathElements, String classToFind)
  {
    int                arrayLength = classPathElements.length;
    List<SearchResult> results     = new ArrayList<SearchResult>();

    for (int i = 0; i < arrayLength; i++)
    {
      String classpathElement = classPathElements[i];
      File   classpathFile    = new File(classpathElement);

      if (!classpathFile.exists())
      {
        System.out.println("       *** file or directory does not exist: " + classpathElement);
      }
      else if (classpathFile.isDirectory())
      {
        String[] filesInDir = classpathFile.list();

        findClassInDirectory(results, classToFind, classpathElement, filesInDir);
      }
      else if (classpathFile.isFile())  // is is is a zip or a .jar?
      {
        if (isArchiveFile(classpathElement))
        {
          try
          {
            findClassInZipEntries(results, classToFind, classpathFile);
          }
          catch (ZipException zipex)
          {
            System.out.println("      *** file not a zip file: " + classpathElement);
          }
          catch (IOException ioex)
          {
            System.out.println("      *** io error opening file: " + classpathElement);
          }
        }
        else
        {
          System.out.println("File " + classpathElement + " isn't a .jar or .zip file, can't read classes from it.");
        }
      }
    }                                   // end for

    return results;
  }

  /** if the class path element is a directory, see if there are any matches here. */
  private void findClassInDirectory(List<SearchResult> results, String className, String currentDir, String[] filesInDir)
  {
    if (!currentDir.endsWith(".svn"))
    {
      System.out.println("Searching " + currentDir);

      int numberOfFiles = filesInDir.length;

      for (int i = 0; i < numberOfFiles; i++)
      {
        String fileName = filesInDir[i];

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
            findClassInZipEntries(results, className, fileNameWithDir);
          }
          else if (fileNameWithDir.isDirectory())
          {
            String[] dirList = fileNameWithDir.list();

            findClassInDirectory(results, className, currentDir + fileSep + fileName, dirList);
          }
        }
        catch (IOException e)
        {
          System.out.println("Encountered an error, skipping file " + fileName + ": " + e.getMessage());
        }
      }
    }  // end if
  }

  /** Tests the prospective class name the the desired matching name - if it matches, display it. */
  private boolean shouldDisplay(String nameToTest, String rawClassName, boolean isDir)
  {
    String nextTrimmedClassName = trimClassExtension(nameToTest);
    int    lastSlash            = -1;

    if (!isDir)
    {
      lastSlash = nextTrimmedClassName.lastIndexOf("/");
    }

    if (lastSlash >= 0)
    {
      nextTrimmedClassName = nextTrimmedClassName.substring(lastSlash + 1, nextTrimmedClassName.length());
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

  private String trimClassExtension(String className)
  {
    String trimmedClassName = className;
    int    dotStart         = trimmedClassName.indexOf(".class");

    if (dotStart >= 0)
    {
      trimmedClassName = trimmedClassName.substring(0, dotStart);
    }

    return trimmedClassName;
  }

  /**
   * @param   classpathElement  the file name to examine
   *
   * @return  returns true if this is a java archive (zip or jar), false if otherwise.
   */
  private boolean isArchiveFile(String classpathElement)
  {
    return classpathElement.endsWith(".jar") || classpathElement.endsWith(".zip");
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
    System.out.println("\n");

    // find the longest key, add 4 to that, pad the rest
    int maxLength = 0;

    for (SearchResult result : results)
    {
      maxLength = Math.max(maxLength, result.getFilePath().length());
    }

    for (SearchResult result : results)
    {
      int          numberOfNeededSpaces = maxLength + 4 - result.getFilePath().length();
      StringBuffer buffer               = new StringBuffer();

      for (int j = 0; j < numberOfNeededSpaces; j++)
      {
        buffer.append(" ");
      }

      System.out.println("\t====>" + result.getFilePath() + buffer + result.getFullClassPathName());
    }
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
