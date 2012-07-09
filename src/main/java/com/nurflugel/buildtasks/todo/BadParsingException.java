package com.nurflugel.buildtasks.todo;

/** Created with IntelliJ IDEA. User: dbulla Date: 7/5/12 Time: 2:51 PM To change this template use File | Settings | File Templates. */
public class BadParsingException extends Exception
{
  private static final long serialVersionUID = 1359466178008188147L;

  public BadParsingException(String message)
  {
    super(message);
  }
}
