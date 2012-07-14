package com.nurflugel.buildtasks.todo;

import java.util.List;

/** Class to represent a search phrase - t odo, c odereview, etc - each with an alias.. */
public class SearchPhrase extends NameWithAliases
{
  public SearchPhrase(List<String> phrase)
  {
    super(phrase);
  }
}
