package com.nurflugel.buildtasks.todo;

import java.io.File;

/** Representation of a t odo item. */
public class TodoItem
{
  private final String comment;
  private final File   file;
  private final int    lineNumber;

  public TodoItem(String comment, File file, int lineNumber)
  {
    this.comment    = comment;
    this.file       = file;
    this.lineNumber = lineNumber;
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }

    if ((o == null) || (getClass() != o.getClass()))
    {
      return false;
    }

    TodoItem todo = (TodoItem) o;

    if (lineNumber != todo.lineNumber)
    {
      return false;
    }

    if ((comment != null) ? (!comment.equals(todo.comment))
                          : (todo.comment != null))
    {
      return false;
    }

    if ((file != null) ? (!file.equals(todo.file))
                       : (todo.file != null))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = 31 * lineNumber;

    result = (31 * result) + comment.hashCode();
    result = (31 * result) + file.getAbsolutePath().hashCode();

    return result;
  }

  @Override
  public String toString()
  {
    return "Todo{"
             + "file=" + file.getName() + ", lineNumber=" + lineNumber + ", comment='" + comment + '\'' + '}';
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public String getComment()
  {
    return comment;
  }

  public File getFile()
  {
    return file;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }
}
