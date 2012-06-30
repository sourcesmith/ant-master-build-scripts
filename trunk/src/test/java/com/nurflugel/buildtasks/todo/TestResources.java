package com.nurflugel.buildtasks.todo;

import java.io.File;
import static org.apache.commons.lang.BooleanUtils.toBooleanObject;

/** Util class to help with test resources. */
public class TestResources
{
  private static final String SOURCE_PATH_IDEA   = "build/resources/test/";
  private static final String SOURCE_PATH_GRADLE = "resources/test/";

  private TestResources() {}

  /** We do this because when unit tests run in the IDE the base file path is different than when running under Gradle, so we have to adjust it. */
  public static String getTestFilePath(String fileName)
  {
    String  property            = System.getProperty("running.in.gradle");
    boolean isGradleEnvironment = toBooleanObject(property, "yes", null, "dibble");

    return isGradleEnvironment ? new File(SOURCE_PATH_GRADLE + fileName).getAbsolutePath()
                               : new File(SOURCE_PATH_IDEA + fileName).getAbsolutePath();
  }
}
