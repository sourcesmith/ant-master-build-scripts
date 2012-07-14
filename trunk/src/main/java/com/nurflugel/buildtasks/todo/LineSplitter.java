package com.nurflugel.buildtasks.todo;

import com.nurflugel.buildtasks.todo.exceptions.BadParsingException;
import org.apache.tools.ant.BuildException;
import static com.nurflugel.buildtasks.todo.exceptions.BadParsingException.EXPECTED_FORMAT;
import static org.apache.commons.lang.StringUtils.containsAny;
import static org.apache.commons.lang.StringUtils.isEmpty;

/** Splitter to take list of users/terms and split them for further processing. */
public class LineSplitter
{
  /**
   * Take the name pattern and parse it into a list of users. The pattern template looks like this:
   *
   * <p>dbulla(dgb;doug;dbulla),snara(snara;sunitha),bren,dlabar,mkshir</p>
   *
   * @return  a list of User. If none are specified, then the list is empty.
   *
   * @throws  BuildException  if the line isn't in the expected format
   */
  public static String[] splitLine(String namePattern) throws BadParsingException
  {
    // if nothing is listed, return an empty list
    if (isEmpty(namePattern) || "${namePattern}".equals(namePattern))
    {
      return new String[0];
    }

    // reject wrong parenthesis types
    if (containsAny(namePattern, "{}[]"))
    {
      // First, validate that the line starts with the word "users"
      BadParsingException buildException = new BadParsingException("Name pattern " + namePattern + " was not in the expected pattern of "
                                                                     + EXPECTED_FORMAT);

      throw buildException;
    }

    // break the line up into users
    String[] tokens = namePattern.split(";");

    if (tokens.length == 0)
    {
      throw new BuildException("No names found in the string " + namePattern);
    }

    return tokens;
  }

  private LineSplitter() {}
}
