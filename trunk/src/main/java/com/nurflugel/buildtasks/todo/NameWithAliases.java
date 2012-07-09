package com.nurflugel.buildtasks.todo;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Representation of something identified by a name or ID, but has a list of alternate identities. The main name is one of those. */
public class NameWithAliases
{
  /** The user id - this should be RACF, as it's always unique. */
  protected String name;

  /** This can be anything the user is called by - in my case, doug, dbulla, dgb, douglas, etc. */
  protected final Set<String> aliases = new HashSet<String>();

  public NameWithAliases(List<String> names)
  {
    this(names.toArray(new String[names.size()]));
  }

  public NameWithAliases(String... names)
  {
    name = names[0];
    Collections.addAll(aliases, names);
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
