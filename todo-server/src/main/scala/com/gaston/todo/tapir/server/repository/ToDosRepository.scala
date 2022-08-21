package com.gaston.todo.tapir.server.repository

import com.gaston.todo.tapir.contract.response.ToDoResponse

import java.util.UUID
import scala.concurrent.Future

trait ToDosRepository {
  def getToDo(user: String, id: UUID): Future[Option[ToDoResponse]]
  def takeToDos(user: String, n: Int): Future[List[ToDoRow]]
  def addToDo(user: String, toDo: ToDoVO): Future[UUID]
  def deleteToDo(user: String, uuid: UUID): Future[Boolean]
}
