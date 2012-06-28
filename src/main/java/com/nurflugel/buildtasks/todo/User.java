package com.nurflugel.buildtasks.todo;

import java.util.*;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

/** This represents a user and all their aliases. */
public class User extends NameWithAliases
{
  static final String          ALL     = "all";
  static final String          UNKNOWN = "unknown";
  private final List<TodoItem> todos   = new ArrayList<TodoItem>();

  public User(String name)
  {
    super(name);

    if (!name.equals(ALL) && !name.equals(UNKNOWN))  // these guys don't get default aliases
    {
      aliases.add(name);                             // add the ID, so that's always in the list
    }
  }

  /** Get the user from the token and add it to the list. */
  void getUser(List<NameWithAliases> users, String[] tokens)
  {
    // for (String token : tokens)
    // {
    // String id          = substringBefore(token, "(");
    // String aliasesText = substringAfter(token, "(");
    //
    // aliasesText = substringBefore(aliasesText, ")");
    //
    // String[] aliases = aliasesText.split(";");
    // User     user    = new User(id, aliases);
    //
    // users.add(user);
    //
    // }
  }

  // -------------------------- OTHER METHODS --------------------------
  public void addTodo(TodoItem todo)
  {
    todos.add(todo);
  }

  @Override
  public String toString()
  {
    return "User{"
             + "aliases=" + ((aliases == null) ? null
                                               : asList(aliases)) + ", id='" + name + '\'' + ", todos=" + todos + '}';
  }

  public List<TodoItem> getTodos()
  {
    return todos;
  }
}
