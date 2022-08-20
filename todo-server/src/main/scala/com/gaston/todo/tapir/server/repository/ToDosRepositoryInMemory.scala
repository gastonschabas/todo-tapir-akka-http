package com.gaston.todo.tapir.server.repository

import com.gaston.todo.tapir.contract.response.ToDoResponse

import java.util.UUID
import scala.collection.mutable.{ListBuffer, Map}

class ToDosRepositoryInMemory extends ToDosRepository {

  private val toDosRepo: Map[String, ListBuffer[ToDoRow]] = Map.empty

  def getToDo(user: String, id: UUID): Option[ToDoResponse] = {
    toDosRepo
      .get(user)
      .flatMap(
        _.find(_.id == id)
          .map(todo => ToDoResponse(todo.id, todo.title, todo.description))
      )
  }

  def takeToDos(user: String, n: Int): List[ToDoRow] =
    toDosRepo.get(user).map(_.take(n).toList).getOrElse(List.empty)

  def addToDo(user: String, toDo: ToDoVO): UUID = {
    val uuid = UUID.randomUUID()
    val toDoRow = ToDoRow(uuid, toDo.title, toDo.description)
    toDosRepo.get(user) match {
      case Some(value) => value += toDoRow
      case None => toDosRepo.put(user, ListBuffer(toDoRow))
    }
    uuid
  }

  def deleteToDo(user: String, uuid: UUID): Boolean = {
    toDosRepo.get(user) match {
      case Some(value) =>
        value.find(_.id == uuid) match {
          case Some(toDo) =>
            value -= toDo
            true
          case None =>
            false
        }
      case None => false
    }
  }
}
