package com.gaston.todo.tapir.server.repository
import com.gaston.todo.tapir.contract.response.ToDoResponse
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.PostgresProfile.backend.Database
import slick.lifted.ProvenShape

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class ToDosRepositoryPostgreSql(database: Database)(implicit
  ec: ExecutionContext
) extends ToDosRepository {

  private final val TABLE_NAME = "todos"

  class ToDoTable(tag: Tag) extends Table[ToDoRow](tag, TABLE_NAME) {
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def description = column[String]("description")
    def user = column[String]("user")

    override def * : ProvenShape[ToDoRow] =
      (id, title, description, user) <> (ToDoRow.tupled, ToDoRow.unapply)
  }

  lazy val toDoTableQuery: TableQuery[ToDoTable] = TableQuery[ToDoTable]

  override def getToDo(user: String, id: UUID): Future[Option[ToDoResponse]] =
    database
      .run(
        toDoTableQuery
          .filter(_.id === id)
          .filter(_.user === user)
          .result
          .headOption
      )
      .map(maybeToDoRow =>
        maybeToDoRow.map(toDoRow =>
          ToDoResponse(toDoRow.id, toDoRow.title, toDoRow.description)
        )
      )

  override def takeToDos(user: String, n: Int): Future[List[ToDoResponse]] =
    database
      .run(
        toDoTableQuery
          .filter(_.user === user)
          .take(n)
          .result
      )
      .map(toDoRows =>
        toDoRows
          .map(toDoRow =>
            ToDoResponse(toDoRow.id, toDoRow.title, toDoRow.description)
          )
          .toList
      )

  override def addToDo(user: String, toDo: ToDoVO): Future[ToDoResponse] = {
    val uuid = UUID.randomUUID()
    val toDoRow = ToDoRow(uuid, toDo.title, toDo.description, user)
    database
      .run(toDoTableQuery += toDoRow)
      .map(_ => ToDoResponse(uuid, toDoRow.title, toDo.description))
  }

  override def deleteToDo(user: String, uuid: UUID): Future[Boolean] =
    database
      .run(toDoTableQuery.filter(_.id === uuid).filter(_.user === user).delete)
      .map(_ > 0)
}
