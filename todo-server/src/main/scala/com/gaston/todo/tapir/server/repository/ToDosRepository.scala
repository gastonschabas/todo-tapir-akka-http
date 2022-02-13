package com.gaston.todo.tapir.server.repository

import java.util.UUID
import scala.collection.mutable.ListBuffer

class ToDosRepository {
  private val todos = ListBuffer(
    ToDoRow(UUID.randomUUID(), "1 ToDo title", "1 ToDo description"),
    ToDoRow(UUID.randomUUID(), "2 ToDo title", "2 ToDo description"),
    ToDoRow(UUID.randomUUID(), "3 ToDo title", "3 ToDo description"),
    ToDoRow(UUID.randomUUID(), "4 ToDo title", "4 ToDo description"),
    ToDoRow(UUID.randomUUID(), "5 ToDo title", "5 ToDo description"),
    ToDoRow(UUID.randomUUID(), "6 ToDo title", "6 ToDo description"),
    ToDoRow(UUID.randomUUID(), "7 ToDo title", "7 ToDo description")
  )

  def getToDos: List[ToDoRow] = todos.toList

  def takeToDos(n: Int): List[ToDoRow] = todos.take(n).toList

  def addToDo(toDo: ToDoVO): UUID = {
    val uuid = UUID.randomUUID()
    todos += ToDoRow(uuid, toDo.title, toDo.description)
    uuid
  }

  def removeToDo(uuid: UUID): Boolean = {
    todos.find(_.id == uuid) match {
      case Some(toDo) =>
        todos -= toDo
        true
      case None =>
        false
    }
  }
}
