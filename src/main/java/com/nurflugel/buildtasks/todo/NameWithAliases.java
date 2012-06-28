package com.nurflugel.buildtasks.todo;

import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.*;
import static org.apache.commons.lang.StringUtils.containsAny;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.apache.tools.ant.Project.MSG_ERR;

/** Created with IntelliJ IDEA. User: dbulla Date: 6/28/12 Time: 2:23 PM To change this template use File | Settings | File Templates. */
public class NameWithAliases
{
  /** The user id - this should be RACF, as it's always unique. */
  protected String name;

  /** This can be anything the user is called by - in my case, doug, dbulla, dgb, douglas, etc. */
  protected final Set<String> aliases = new HashSet<String>();

  public NameWithAliases(String name)
  {
    this.name = name;
  }

  public NameWithAliases(String name, String[] aliasList)
  {
    this(name);

    for (String alias : aliasList)
    {
      if (!isEmpty(alias))
      {
        aliases.add(alias);
      }
    }
  }

  static NameWithAliases[] parseNames(String text)
  {
    return null;
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public Set<String> getAliases()
  {
    return aliases;
  }

  public String getName()
  {
    return name;
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

    NameWithAliases user = (NameWithAliases) o;

    if ((name != null) ? (!name.equals(user.name))
                       : (user.name != null))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return (name != null) ? name.hashCode()
                          : 0;
  }
}
