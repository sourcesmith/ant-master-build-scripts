package com.nurflugel.buildtasks.todo;

import com.nurflugel.buildtasks.todo.exceptions.BadParsingException;
import java.util.ArrayList;
import java.util.List;
import static com.nurflugel.buildtasks.todo.exceptions.BadParsingException.EXPECTED_FORMAT;
import static org.apache.commons.lang.StringUtils.*;

/** Parser to break up a name with aliases. dbulla(doug,dgb,douglas). */
public class AliasParser
{
  public static final char   OPEN_CURLY_BRACE     = '{';
  public static final char   OPEN_SQUARE_BRACKET  = '[';
  public static final char   CLOSE_SQUARE_BRACKET = ']';
  public static final char   CLOSE_CURLY_BRACE    = '}';
  public static final char   OPEN_PAREN           = '(';
  public static final char   CLOSE_PAREN          = ')';
  public static final String COMMA                = ",";

  public static List<String> getAliases(String textToParse) throws BadParsingException
  {
    List<String> terms = new ArrayList<String>();

    if (containsAny(textToParse, new char[] { OPEN_CURLY_BRACE, OPEN_SQUARE_BRACKET, CLOSE_CURLY_BRACE, CLOSE_SQUARE_BRACKET }))
    {
      throw new BadParsingException("Wrong type of parenthesis used!  Only \"()\" is supported - format should be like " + EXPECTED_FORMAT);
    }

    if (contains(textToParse, OPEN_PAREN))
    {
      if (!contains(textToParse, CLOSE_PAREN))
      {
        throw new BadParsingException("Opening paren without closing paren - format should be like " + EXPECTED_FORMAT);
      }

      String textBeforeParens = substringBefore(textToParse, OPEN_PAREN + "");

      terms.add(textBeforeParens);

      String   textAfterParens = substringAfter(textToParse, OPEN_PAREN + "");
      String   middleText      = substringBefore(textAfterParens, CLOSE_PAREN + "");
      String[] split           = middleText.split(COMMA);

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
      if (contains(textToParse, CLOSE_PAREN))
      {
        throw new BadParsingException("Closing paren without opening paren - format should be like " + EXPECTED_FORMAT);
      }

      terms.add(textToParse);
    }

    return terms;
  }

  private AliasParser() {}
}
