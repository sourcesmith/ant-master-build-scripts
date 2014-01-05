package com.ryangrier.ant;

/**
 * @author   Ryan Grier <a href="http://www.ryangrier.com">http://www.ryangrier.com</a>
 * @version  1.1.4 build # 2003012806
 */
public class Version
{
  /** Current version of the framework. */
  private static final String version = "1.1.4";

  /** Current build number of the framework. */
  private static final String build = "2003012806";

  /**
   * Gets the version of the framework.
   *
   * @return  The framework version.
   */
  public static String getVersion()
  {
    return version;
  }

  /**
   * Gets the build number of the framwork.
   *
   * @return  The framework build number.
   */
  public static String getBuild()
  {
    return build;
  }

  /**
   * Gets the version of the framework.
   *
   * @return  The framework version.
   */
  public static String getFrameworkVersion()
  {
    return version;
  }

  /**
   * Gets the build number of the framwork.
   *
   * @return  The framework build number.
   */
  public static String getFrameworkBuild()
  {
    return build;
  }
  // --------------------------- main() method ---------------------------

  /**
   * The main program for the Version class.
   *
   * @param  args  The command line arguments
   */
  public static void main(String[] args)
  {
    System.out.println(new Version().toString());
  }

  /**
   * Gets the version and the build number of the framework.
   *
   * @return  The framework version and build number.
   */
  @Override
  public String toString()
  {
    return getString();
  }

  public static String getString()
  {
    return "ant_version_tool Version: " + version + " build # " + build;
  }
}
