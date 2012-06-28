package com.nurflugel.buildtasks.todo;

import java.util.List;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

/** Created with IntelliJ IDEA. User: dbulla Date: 6/28/12 Time: 2:21 PM To change this template use File | Settings | File Templates. */
public class SearchPhrase extends NameWithAliases
{
  public SearchPhrase(String name)
  {
    super(name);
  }

  public SearchPhrase(String id, String[] aliases)
  {
    super(id, aliases);
  }

  /** Get the user from the token and add it to the list. */
  void getUser(List<NameWithAliases> searchPhrases, String[] tokens)
  {
    for (String token : tokens)
    {
      String id           = substringBefore(token, "(");
      String aliasesText  = substringAfter(token, "(");

      aliasesText = substringBefore(aliasesText, ")");

      String[]     aliases      = aliasesText.split(";");
      SearchPhrase searchPhrase = new SearchPhrase(id, aliases);

      searchPhrases.add(searchPhrase);
    }
  }
}
