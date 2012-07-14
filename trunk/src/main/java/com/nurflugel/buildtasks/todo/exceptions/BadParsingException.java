package com.nurflugel.buildtasks.todo.exceptions;

/** Thrown when we can't parse some input the user has given us. Stupid user! */
public class BadParsingException extends Exception
{
  private static final long  serialVersionUID = 1359466178008188147L;
  public static final String EXPECTED_FORMAT  = "name1(alias1,alias2,alias3);name2,name3(alias)";

  public BadParsingException(String message)
  {
    super(message);
  }
}
