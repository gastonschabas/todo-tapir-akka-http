package com.gaston.todo.tapir.server.route

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gaston.todo.tapir.api.spec.OpenAPISpec
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ToDoResponse
}
import com.gaston.todo.tapir.endpoint.Endpoints
import com.gaston.todo.tapir.server.repository.{ToDoVO, ToDosRepository}
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Routes(toDosRepository: ToDosRepository) {

  private val indexEndpoint = pathSingleSlash(
    getFromResource("html/index.html", ContentTypes.`text/html(UTF-8)`)
  )

  private val assetsPath =
    path("helium" / Remaining)(file => getFromResource(s"html/helium/$file"))

  private val openAPISpec = Endpoints.openAPISpec.serverLogic[Future](_ =>
    Future.successful(Right(OpenAPISpec.yaml))
  )

  private val todoDescriptionEndpoint =
    Endpoints.todoDescriptionEndpoint.serverLogic[Future](_ =>
      Future.successful(Right(s"""
                                 | A simple API to create a list of pending ToDos.
                                 | The spec can be found in `/api/${Endpoints.version}/spec`
                                 |""".stripMargin))
    )

  private val getToDosEndpoint =
    Endpoints.getToDosEndpoint.serverLogic[Future] {
      case Some(limit) =>
        Future.successful(
          Right(
            (if (limit < 1) toDosRepository.takeToDos(1)
             else toDosRepository.takeToDos(limit)).map(row =>
              ToDoResponse(row.id, row.title, row.description)
            )
          )
        )
      case None =>
        Future.successful(
          Right(
            toDosRepository
              .takeToDos(5)
              .map(row => ToDoResponse(row.id, row.title, row.description))
          )
        )
    }

  private val getTodoEndpoint =
    Endpoints.getTodoEndpoint.serverLogic[Future] { id =>
      toDosRepository.getToDo(id) match {
        case Some(todo) => Future(Right(todo))
        case None => Future(Left(s"ToDo $id was not found"))
      }
    }

  val addToDoEndpoint =
    Endpoints.addToDoEndpoint.serverLogic[Future] { toDo =>
      val id =
        toDosRepository.addToDo(ToDoVO(toDo.title, toDo.description))
      Future(Right(CreateToDoResponse(id)))
    }

  val deleteToDoEndpoint =
    Endpoints.deleteTodoEndpoint.serverLogic[Future] { id =>
      val wasDeleted = toDosRepository.deleteToDo(id)
      if (wasDeleted) Future(Right(s"ToDo $id was deleted"))
      else Future(Right(s"ToDo $id was not deleted"))
    }

  lazy val routes: Route =
    AkkaHttpServerInterpreter().toRoute(
      List(
        openAPISpec,
        todoDescriptionEndpoint,
        getToDosEndpoint,
        getTodoEndpoint,
        addToDoEndpoint,
        deleteToDoEndpoint
      )
    ) ~ indexEndpoint ~ assetsPath

}
