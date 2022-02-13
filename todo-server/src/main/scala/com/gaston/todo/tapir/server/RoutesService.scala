package com.gaston.todo.tapir.server

import akka.http.scaladsl.server.Directives._
import com.gaston.todo.tapir.api.spec.OpenAPISpec
import com.gaston.todo.tapir.contract.response.ToDoResponse
import com.gaston.todo.tapir.endpoint.Endpoints
import com.gaston.todo.tapir.server.repository.ToDosRepository
import tapir.server.akkahttp._

import scala.concurrent.Future

class RoutesService(toDosRepository: ToDosRepository) {

  private val openAPISpec = Endpoints.openAPISpec.toRoute { _ =>
    Future.successful(Right(OpenAPISpec.yaml))
  }

  private val todoDescriptionEndpoint =
    Endpoints.todoDescriptionEndpoint.toRoute { _ =>
      Future.successful(Right(
        s"""
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

//  val getTodoEndpoint = ???

//  val addToDoEndpoint = ???

  val routes = openAPISpec ~ todoDescriptionEndpoint ~ getToDosEndpoint

}
