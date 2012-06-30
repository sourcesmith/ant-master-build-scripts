package com.nurflugel.buildtasks;

import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/30/12 Time: 14:55 To change this template use File | Settings | File Templates. */
@Test(groups = "ValidateProperties")
public class ValidatePropertiesTaskTest
{
  @Test(expectedExceptions = BuildException.class)
  public void testDoWork() throws Exception
  {
    ValidatePropertiesTask task = new ValidatePropertiesTask();

    task.setBuildFile("master-build/master-build.xml");
    task.doWork();
  }
}
