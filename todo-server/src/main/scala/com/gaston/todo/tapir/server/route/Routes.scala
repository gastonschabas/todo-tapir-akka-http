package com.gaston.todo.tapir.server.route

import akka.http.scaladsl.server.Directives._
import com.gaston.todo.tapir.api.spec.OpenAPISpec
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ToDoResponse
}
import com.gaston.todo.tapir.endpoint.Endpoints
import com.gaston.todo.tapir.server.repository.{ToDoVO, ToDosRepository}
import com.stackstate.pac4j.AkkaHttpSecurity
import tapir.server.akkahttp._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Routes(
  toDosRepository: ToDosRepository,
  akkaHttpSecurity: AkkaHttpSecurity
) {

  private val openAPISpec = Endpoints.openAPISpec.toRoute { _ =>
    Future.successful(Right(OpenAPISpec.yaml))
  }

  private val todoDescriptionEndpoint =
    Endpoints.todoDescriptionEndpoint.toRoute { _ =>
      Future.successful(Right(s"""
                                 | A simple API to create a list of pending ToDos.
                                 | The spec can be found in `/api/${Endpoints.version}/spec`
                                 |""".stripMargin))
    }

  private val getToDosEndpoint = Endpoints.getToDosEndpoint.toRoute {
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

  val getTodoEndpoint = Endpoints.getTodoEndpoint.toRoute { id =>
    toDosRepository.getToDo(id) match {
      case Some(todo) => Future(Right(todo))
      case None => Future(Left(s"ToDo $id was not found"))
    }
  }

  val addToDoEndpoint =
    akkaHttpSecurity.withAuthentication("DirectBearerAuthClient") { _ =>
      Endpoints.addToDoEndpoint.toRoute { case (_, toDo) =>
        val id =
          toDosRepository.addToDo(ToDoVO(toDo.title, toDo.description))
        Future(Right(CreateToDoResponse(id)))
      }
    }

  val deleteToDoEndpoint =
    akkaHttpSecurity.withAuthentication("DirectBearerAuthClient") { _ =>
      Endpoints.deleteTodoEndpoint.toRoute { case (_, id) =>
        val wasDeleted = toDosRepository.deleteToDo(id)
        if (wasDeleted) Future(Right(s"ToDo $id was deleted"))
        else Future(Right(s"ToDo $id was not deleted"))
      }
    }

  val routes =
    openAPISpec ~ todoDescriptionEndpoint ~ getTodoEndpoint ~ getToDosEndpoint ~ addToDoEndpoint ~ deleteToDoEndpoint

}
