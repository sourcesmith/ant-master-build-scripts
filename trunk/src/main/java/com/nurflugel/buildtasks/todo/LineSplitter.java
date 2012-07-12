package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import static org.apache.commons.lang.StringUtils.containsAny;
import static org.apache.commons.lang.StringUtils.isEmpty;

/** Created with IntelliJ IDEA. User: dbulla Date: 6/28/12 Time: 4:17 PM To change this template use File | Settings | File Templates. */
public class LineSplitter
{
  private LineSplitter() {}

  /**
   * Take the name pattern and parse it into a list of users. The pattern template looks like this:
   *
   * <p>dbulla(dgb;doug;dbulla),snara(snara;sunitha),bren,dlabar,mkshir</p>
   *
   * @return  a list of User. If none are specified, then the list is empty.
   *
   * @throws  BuildException  if the line isn't in the expected format
   */
  public static String[] splitLine(String namePattern)
  {
    // if nothing is listed, return an empty list
    if (isEmpty(namePattern) || "${namePattern}".equals(namePattern))
    {
      return new String[0];
    }

    // reject wrong parenthesis types
    if (containsAny(namePattern, "{}[]"))
    {
      // todo move away from Ant exceptions
      // First, validate that the line starts with the word "users"
      BuildException buildException = new BuildException("Name pattern " + namePattern
                                                           + " was not in the expected pattern of 'name1(alias1,alias2...);name2(alias1...);name3...");

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
}
