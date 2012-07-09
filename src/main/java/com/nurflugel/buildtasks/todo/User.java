package com.nurflugel.buildtasks.todo;

import java.util.ArrayList;
import java.util.List;
import static java.util.Arrays.asList;

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
      this.name = name;                              // add the ID, so that's always in the list
    }
  }

  public User(List<String> aliases)
  {
    super(aliases);
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
