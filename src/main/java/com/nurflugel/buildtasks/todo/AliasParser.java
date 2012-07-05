package com.nurflugel.buildtasks.todo;

import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang.StringUtils.*;

/** dbulla(doug,dgb,douglas). */
public class AliasParser
{
  public static List<String> getAliases(String textToParse) throws BadParsingException
  {
    List<String> terms = new ArrayList<String>();
    if (textToParse.contains("{")||textToParse.contains("[")||textToParse.contains("]")||textToParse.contains("}"))
          {
            throw new BadParsingException("Wrong type of parenthesis used!");
          }
    

    if (textToParse.contains("("))
    {
      if (!textToParse.contains(")"))
      {
        throw new BadParsingException("Opening paren without closing paren");
      }

      String textBeforeParens = substringBefore(textToParse, "(");

      terms.add(textBeforeParens);

      String   textAfterParens = substringAfter(textToParse, "(");
      String   middleText      = substringBefore(textAfterParens, ")");
      String[] split           = middleText.split(",");

      for (String item : split)
      {
        if (isNotBlank(item))
        {
          terms.add(item);
        }
      }
    }
    else
    {
      if (textToParse.contains(")"))
      {
        throw new BadParsingException("Closing paren without opening paren");
      }

      terms.add(textToParse);
    }

    return terms;
  }
}
