package com.nurflugel.buildtasks.todo;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** This represents a user and all their aliases. */
public class User extends NameWithAliases
{
  private final Set<TodoItem> todos = new LinkedHashSet<>();

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

  public List<TodoItem> getTodos()
  {
    return new ArrayList<>(todos);
  }

  // ------------------------ CANONICAL METHODS ------------------------
  @Override
  public String toString()
  {
    return "User{"
             + "aliases=" + ((aliases == null) ? null
                                               : asList(aliases)) + ", id='" + name + '\'' + ", todos count=" + todos.size() + '}';
  }
}
