package com.nurflugel.buildtasks;

// import org.apache.tools.ant.BuildException;
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

/**
 * Class to find which jars contain the class you're looking for. Run with no args to get usage.
 *
 * @author        Douglas Bullard
 * @noinspection  UseOfSystemOutOrSystemErr, ClassWithoutPackageStatement
 */
public class WhenceJavaText
{
  private boolean            wildcardEnd;
  private boolean            wildcardFront;
  private char               fileSep        = File.separatorChar;
  private String[]           entrySeparator = { ",", ":" };
  private String             classToFind;
  private String             libPath;
  public static final String VERSION        = "1.0.0";

  public WhenceJavaText()
  {
    wildcardEnd = false;
  }

  // --------------------------- main() method ---------------------------
  // public static void main(String[] args)
  // {
  // WhenceJavaText wj = new WhenceJavaText();
  //
  // if (args.length == 0)
  // {
  // wj.setClassToFind("Vector*");
  // wj.setLibPath(
  // "/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.jdom/jdom/1.0/jar/a2ac1cd690ab4c80defe7f9bce14d35934c35cec/jdom-1.0.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/ca.odell.glazedlists/jdk_1.5/1.8.0/jar/4c7fef54a2e3fe2e0fb2c97068ff618d685adb92/glazedlists-1.8.0.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.codehaus.xstream/xstream/1.2/jar/3e6ab37c97d87c1f985b7d3c1d269f23023406e9/xstream-1.2.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.tmatesoft.svnkit/svnkit/1.7.4-v1/jar/c959638a63b4bd5fc5e939707ce196b849ce0c11/svnkit-1.7.4-v1.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/w3c/jtidy/04aug2000r7-dev/jar/ae599a07138b346ef69383f22cfc4a15e670fe36/jtidy-04aug2000r7-dev.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/com.ryangrier.ant/version_tool/1.1.4_fixed/jar/88ae12976a331f583ef50342683c6ddd9ae79558/version_tool-1.1.4_fixed.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/com.nurflugel/buildtasks/1.0-SNAPSHOT/jar/587d7f176cd866ad8a9ab4fd4e17ddfd300091b6/buildtasks-1.0-SNAPSHOT.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.apache/commons-logging/1.0.4/jar/f029a2aefe2b3e1517573c580f948caac31b1056/commons-logging-1.0.4.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.apache/commons-lang/2.4/jar/16313e02a793435009f1e458fa4af5d879f6fb11/commons-lang-2.4.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.apache/commons-collections/3.2.1/jar/761ea405b9b37ced573d2df0d1e3a4e0f9edc668/commons-collections-3.2.1.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.apache/commons-io/2.2/jar/83b5b8a7ba1c08f9e8c8ff2373724e33d3c1e22a/commons-io-2.2.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/com.intellij/forms_rt/11.0.3/jar/1ad4b8c9fc4b32a5e98c09e57f9253002362d960/forms_rt-11.0.3.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.apache/log4j/1.2.15/jar/f0a0d2e29ed910808c33135a3a5a51bba6358f7b/log4j-1.2.15.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.testng/testng/6.4/jar/780e5804ff5e2bcfa262c02ca9401456f682bbc5/testng-6.4.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.objectweb.asm/asm/3.1/jar/c157def142714c544bdea2e6144645702adf7097/asm-3.1.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/javax.help/jhall/2.0.6/jar/fe38b9072f87184c1b50f9dc86a89b3722c3a2d3/jhall-2.0.6.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/de.regnis.q.sequence/sequence-library/1.0.2/jar/531ae7f7a4eac050dbce729edae139c2e546464/sequence-library-1.0.2.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/net.java.dev.jna/jna/3.4.0/jar/803ff252fedbd395baffd43b37341dc4a150a554/jna-3.4.0.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.antlr/antlr-runtime/3.4/jar/8f011408269a8e42b8548687e137d8eeb56df4b4/antlr-runtime-3.4.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.tmatesoft.sqljet/sqljet/1.1.1/jar/100d749ead85d747c0fc30c62d650e084251426d/sqljet-1.1.1.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/com.trilead/trilead-ssh2/2.1.0-build215/jar/efcd626068f633b4676b16a62b84e5dabac82d71/trilead-ssh2-2.1.0-build215.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/junit/junit/3.8.1/jar/99129f16442844f6a4a11ae22fbbee40b14d774f/junit-3.8.1.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.beanshell/bsh/2.0b4/jar/a05f0a0feefa8d8467ac80e16e7de071489f0d9c/bsh-2.0b4.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/com.beust/jcommander/1.12/jar/7409692b48022f9eca7445861defbcdb9ee3c2a8/jcommander-1.12.jar:/Users/douglas_bullard/.gradle/caches/artifacts-13/filestore/org.yaml/snakeyaml/1.6/jar/a1e23e31c424d566ee27382e373d73a28fdabd88/snakeyaml-1.6.jar");
  // }
  // else if (args.length == 2)
  // {
  // wj.setClassToFind(args[0]);
  // wj.setLibPath(args[1]);
  // }
  //
  // wj.run();
  // }
  public void run()  // throws BuildException
  {
    // String[] dibble={"Vector","/snapshots/admin/trunk/unversioned/lib/source"};
    // wj.doit(dibble);
    usage();

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
    List<SearchResult> results = new ArrayList<SearchResult>();

    for (String classpathElement : classPathElements)
    {
      File classpathFile = new File(classpathElement);

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

      for (String fileName : filesInDir)
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

    if (!isDir)
    {
      if (nextTrimmedClassName.contains("/"))
      {
        nextTrimmedClassName = StringUtils.substringAfterLast(nextTrimmedClassName, "/");
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
