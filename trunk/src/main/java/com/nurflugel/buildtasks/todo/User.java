package com.nurflugel.buildtasks.todo;

import org.apache.commons.lang.StringUtils;
import java.util.*;

/** This represents a user and all their aliases. */
public class User
{
  static final String ALL     = "all";
  static final String UNKNOWN = "unknown";

  /** The user id - this should be RACF, as it's always unique. */
  private String id;

  /** This can be anything the user is called by - in my case, doug, dbulla, dgb, douglas, etc. */
  private final Set<String>    aliases = new HashSet<String>();
  private final List<TodoItem> todos   = new ArrayList<TodoItem>();

  public User(String id)
  {
    this.id = id;

    if (!id.equals(ALL) && !id.equals(UNKNOWN))  // these guys don't get default aliases
    {
      aliases.add(id);                           // add the ID, so that's always in the list
    }
  }

  public User(String id, String[] aliasList)
  {
    this(id);

    for (String alias : aliasList)
    {
      if (!StringUtils.isEmpty(alias))
      {
        aliases.add(alias);
      }
    }
  }

  // -------------------------- OTHER METHODS --------------------------
  public void addTodo(TodoItem todo)
  {
    todos.add(todo);
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

    User user = (User) o;

    if ((id != null) ? (!id.equals(user.id))
                     : (user.id != null))
    {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode()
  {
    return (id != null) ? id.hashCode()
                        : 0;
  }

  @Override
  public String toString()
  {
    return "User{"
             + "aliases=" + ((aliases == null) ? null
                                               : Arrays.asList(aliases)) + ", id='" + id + '\'' + ", todos=" + todos + '}';
  }

  // --------------------- GETTER / SETTER METHODS ---------------------
  public Set<String> getAliases()
  {
    return aliases;
  }

  public String getId()
  {
    return id;
  }

  public List<TodoItem> getTodos()
  {
    return todos;
  }
}
