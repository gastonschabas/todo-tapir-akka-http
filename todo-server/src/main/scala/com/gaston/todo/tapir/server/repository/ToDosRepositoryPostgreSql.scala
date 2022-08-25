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

  class ToDoTable(tag: Tag) extends Table[ToDoRow](tag, "todos") {
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def description = column[String]("description")
    def user = column[String]("user")

    override def * : ProvenShape[ToDoRow] =
      (id, title, description, user) <> (ToDoRow.tupled, ToDoRow.unapply)
  }

  class ToDoTableForInsert(tag: Tag) extends Table[ToDoVO](tag, "todos") {
    def title = column[String]("title")
    def description = column[String]("description")
    def user = column[String]("user")

    override def * : ProvenShape[ToDoVO] =
      (title, description, user) <> (ToDoVO.tupled, ToDoVO.unapply)
  }

  class ToDoTableResponse(tag: Tag) extends Table[ToDoResponse](tag, "todos") {
    def id = column[UUID]("id", O.PrimaryKey)
    def title = column[String]("title")
    def description = column[String]("description")

    override def * : ProvenShape[ToDoResponse] =
      (
        id,
        title,
        description
      ) <> ((ToDoResponse.apply _).tupled, ToDoResponse.unapply)
  }

  lazy val toDoTableQuery: TableQuery[ToDoTable] = TableQuery[ToDoTable]
  lazy val toDoTableForInsert: TableQuery[ToDoTableForInsert] =
    TableQuery[ToDoTableForInsert]

  lazy val toDoTableResponseQuery: TableQuery[ToDoTableResponse] =
    TableQuery[ToDoTableResponse]

  lazy val insertToDoTable = toDoTableQuery
    .flatMap(_ => toDoTableForInsert)
    .returning(toDoTableQuery.map(_.id))
    .into((t, id) => ToDoResponse(id, t.title, t.description))

  override def getToDo(user: String, id: UUID): Future[Option[ToDoResponse]] =
    database.run(toDoTableResponseQuery.filter(_.id === id).result.headOption)

  override def takeToDos(user: String, n: Int): Future[List[ToDoRow]] = ???

  override def addToDo(user: String, toDo: ToDoVO): Future[ToDoResponse] =
    database.run(insertToDoTable += toDo)

  override def deleteToDo(user: String, uuid: UUID): Future[Boolean] = ???
}
