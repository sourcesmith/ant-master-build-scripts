package com.nurflugel.buildtasks.whencejava;

public class SearchResult
{
  private String filePath;
  private String fullClassPathName;

  SearchResult(String filePath, String fullClassPathName)
  {
    this.filePath          = filePath;
    this.fullClassPathName = fullClassPathName;
  }

  // -------------------------- OTHER METHODS --------------------------
  public void addToOutput(int maxLength, WhenceJava whenceJava)
  {
    int           numberOfNeededSpaces = maxLength + 4 - filePath.length();
    StringBuilder buffer               = new StringBuilder();

    for (int j = 0; j < numberOfNeededSpaces; j++)  // todo stringtuils or word utils
    {
      buffer.append(' ');
    }

    whenceJava.addToOutput("\t====>" + filePath + buffer + fullClassPathName);
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public String getFilePath()
  {
    return filePath;
  }

  public String getFullClassPathName()
  {
    return fullClassPathName;
  }
}
