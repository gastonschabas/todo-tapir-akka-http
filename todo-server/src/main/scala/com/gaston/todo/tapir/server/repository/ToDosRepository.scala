package com.gaston.todo.tapir.server.repository

import com.gaston.todo.tapir.contract.response.ToDoResponse

import java.util.UUID

trait ToDosRepository {
  def getToDo(user: String, id: UUID): Option[ToDoResponse]
  def takeToDos(user: String, n: Int): List[ToDoRow]
  def addToDo(user: String, toDo: ToDoVO): UUID
  def deleteToDo(user: String, uuid: UUID): Boolean
}
