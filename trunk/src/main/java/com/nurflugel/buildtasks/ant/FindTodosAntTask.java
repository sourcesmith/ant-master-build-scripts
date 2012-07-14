package com.nurflugel.buildtasks.ant;

import com.nurflugel.buildtasks.todo.FindTodosCoreTask;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import java.io.File;

/**
 * This task goes through the baseDir and finds any t*dos in there. It will try to assign them to a user if there's a user ID or alias in the same
 * line. It generates a text report, and reports the output to TeamCity for tracking.
 */
@SuppressWarnings({ "CloneableClassWithoutClone", "TodoComment" })
public class FindTodosAntTask extends Task
{
  private File    baseDir;
  private String  namePattern;
  private File    reportDir;
  private String  searchPhrase;
  private boolean outputToTeamCity = false;

  // -------------------------- OTHER METHODS --------------------------
  @Override
  public void execute() throws BuildException
  {
    try
    {
      validateProperties();

      FindTodosCoreTask coreTask = new FindTodosCoreTask();

      coreTask.setBaseDir(baseDir);
      coreTask.setShouldOutputToTeamCity(outputToTeamCity);
      coreTask.setNamePattern(namePattern);

      if (searchPhrase != null)
      {
        coreTask.setSearchPhrase(searchPhrase);
      }

      coreTask.findTodos();
    }
    catch (Exception e)  // throw the exception into something Ant wil like..
    {
      throw new BuildException(e.getMessage(), e);
    }
  }

  /** Give a nice error message if required properties are missing. */
  private void validateProperties()
  {
    validateProperty("You must specify a baseDir attribute", baseDir);
    validateProperty("You must specify a reportDir attribute", reportDir);
    validateProperty("You must specify a namePattern attribute", namePattern);
  }

  private void validateProperty(String message, Object object)
  {
    if (object == null)
    {
      throw new BuildException(message);
    }
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public void setBaseDir(File dir)
  {
    baseDir = dir;
  }

  public void setNamePattern(String namePattern)
  {
    this.namePattern = namePattern;
  }

  public void setOutputToTeamCity(boolean outputToTeamCity)
  {
    this.outputToTeamCity = outputToTeamCity;
  }

  public void setReportDir(File dir)
  {
    reportDir = dir;
  }

  public void setSearchPhrase(String searchPhrase)
  {
    this.searchPhrase = searchPhrase;
  }
}
