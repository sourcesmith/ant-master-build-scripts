package com.nurflugel.buildtasks;

import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;
import static org.testng.Assert.assertTrue;

/** Created with IntelliJ IDEA. User: douglas_bullard Date: 6/30/12 Time: 14:55 To change this template use File | Settings | File Templates. */
@Test(groups = "validateProperties")
public class ValidatePropertiesTaskTest
{
  @Test(expectedExceptions = BuildException.class)
  public void testDoWork() throws Exception
  {
    ValidatePropertiesTask task = new ValidatePropertiesTask();

    task.setBuildFile("master-build/master-build.xml");
    task.doWork();
  }

  @Test
  public void testParseLineForProp()
  {
    ValidatePropertiesTask task  = new ValidatePropertiesTask();
    Set<String>            props = new HashSet<String>();

    task.parseLineForProps(props, "dibble dabble ${tribble}");
    assertTrue(props.contains("tribble"));
  }

  @Test
  public void testParseLineForProps()
  {
    ValidatePropertiesTask task  = new ValidatePropertiesTask();
    Set<String>            props = new HashSet<String>();

    task.parseLineForProps(props, "dibble dabble ${tribble} ${travel} dididlksjlksjls ${me}");
    assertTrue(props.contains("tribble"));
    assertTrue(props.contains("travel"));
    assertTrue(props.contains("me"));
  }
}
