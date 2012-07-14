package com.nurflugel.buildtasks;

import com.nurflugel.buildtasks.ant.ValidatePropertiesTask;
import org.apache.tools.ant.BuildException;
import org.testng.annotations.Test;
import java.util.HashSet;
import java.util.Set;
import static com.nurflugel.buildtasks.ant.ValidatePropertiesTask.parseLineForDefinitions;
import static com.nurflugel.buildtasks.ant.ValidatePropertiesTask.parseLineForProps;
import static org.testng.Assert.assertTrue;

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
    Set<String> props = new HashSet<String>();

    parseLineForProps(props, "dibble dabble ${tribble}");
    assertTrue(props.contains("tribble"));
  }

  @Test
  public void testParseLineForProps()
  {
    Set<String> props = new HashSet<String>();

    parseLineForProps(props, "dibble dabble ${tribble} ${travel} dididlksjlksjls ${me}");
    assertTrue(props.contains("tribble"));
    assertTrue(props.contains("travel"));
    assertTrue(props.contains("me"));
  }

  @Test
  public void testLinesForDefinitions()
  {
    String[]    lines = { "<available property=\"dibble\" and such", "<setPropertyFromEnvstore propertyName=\"travel\" to a fro" };
    Set<String> props = new HashSet<String>();

    for (String line : lines)
    {
      parseLineForDefinitions(props, line);
    }

    assertTrue(props.contains("dibble"));
    assertTrue(props.contains("travel"));
  }
}
