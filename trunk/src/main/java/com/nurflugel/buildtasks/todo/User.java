package com.nurflugel.buildtasks.todo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import static java.util.Arrays.asList;

/** This represents a user and all their aliases. */
public class User extends NameWithAliases
{
  static final User           ALL     = new User("all");
  static final User           UNKNOWN = new User("unknown");
  private final Set<TodoItem> todos   = new LinkedHashSet<TodoItem>();

  public User(String... names)
  {
    super(names);
  }

  public User(List<String> aliases)
  {
    super(aliases);
  }

  // -------------------------- OTHER METHODS --------------------------
  public void addTodo(TodoItem todo)
  {
    if (todos.contains(todo)) {}
    else
    {
      todos.add(todo);
    }
  }

  @Override
  public String toString()
  {
    return "User{"
             + "aliases=" + ((aliases == null) ? null
                                               : asList(aliases)) + ", id='" + name + '\'' + ", todos count=" + todos.size() + '}';
  }

  public List<TodoItem> getTodos()
  {
    return new ArrayList<TodoItem>(todos);
  }
}
