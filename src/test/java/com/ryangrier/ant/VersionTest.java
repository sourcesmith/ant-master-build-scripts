package com.ryangrier.ant;

/**
 * This class is a Test for the VersionTracker ant task.
 *
 * @author   Ryan Grier <a href="http://www.ryangrier.com">http://www.ryangrier.com</a>
 * @version  55.0.0
 */
public class VersionTest
{
  /** Current Version of VersionTracker. */
  private static final String TEST_VERSION = "55.0.0";

  /** Current Version of VersionTracker. */
  private static final String TEST_VERSION_TWO = "10.61.0";

  /** Current Version of VersionTracker. */
  private static final String version = "101.0.27";

  /** Current build of VersionTracker. */
  private static final String build = "2003012811";

  private VersionTest() {}

  /**
   * The main program for VersionTracker. It just prints out the current version number.
   *
   * @param  args  Arguments passed to the program - ignored.
   */
  public static void main(String[] args)
  {
    System.out.println("TEST_VERSION Version: " + TEST_VERSION);
  }
}
