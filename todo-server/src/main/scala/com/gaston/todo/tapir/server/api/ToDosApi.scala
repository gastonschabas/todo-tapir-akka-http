package com.gaston.todo.tapir.server.api

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gaston.todo.tapir.api.spec.OpenAPISpec
import com.gaston.todo.tapir.contract.auth.{BearerToken, UserAuthenticated}
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ErrorInfo,
  ToDoResponse
}
import com.gaston.todo.tapir.endpoint.Endpoints
import com.gaston.todo.tapir.server.auth.Authentication
import com.gaston.todo.tapir.server.repository.{ToDoVO, ToDosRepository}
import sttp.tapir.Endpoint
import sttp.tapir.server.PartialServerEndpoint
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ToDosApi(
  toDosRepository: ToDosRepository,
  authentication: Authentication
) {

  private val indexEndpoint = pathSingleSlash(
    getFromResource("html/index.html", ContentTypes.`text/html(UTF-8)`)
  )

  private val assetsPath =
    path("helium" / Remaining)(file => getFromResource(s"html/helium/$file"))

  private val openAPISpec = Endpoints.openAPISpec.serverLogic[Future](_ =>
    Future.successful(Right(OpenAPISpec.yaml))
  )

  private val todoDescriptionLogic: Unit => Future[String] = (_: Unit) =>
    Future.successful(s"""
       | A simple API to create a list of pending ToDos.
       | The spec can be found in `/api/${Endpoints.version}/spec`
       |""".stripMargin)

  private val todoDescriptionEndpoint =
    Endpoints.todoDescriptionEndpoint.serverLogicSuccess[Future](
      todoDescriptionLogic
    )

  private val getToDosEndpoint =
    secureEndpoint(Endpoints.getToDosEndpoint)
      .serverLogic { token =>
        {
          case Some(limit) =>
            (if (limit < 1) toDosRepository.takeToDos(token.subject, 1)
             else toDosRepository.takeToDos(token.subject, limit))
              .map(listToDoRows =>
                Right(
                  listToDoRows.map(toDo =>
                    ToDoResponse(toDo.id, toDo.title, toDo.description)
                  )
                )
              )
          case None =>
            toDosRepository
              .takeToDos(token.subject, 5)
              .map(listToDoRow =>
                Right(
                  listToDoRow.map(row =>
                    ToDoResponse(row.id, row.title, row.description)
                  )
                )
              )
        }
      }

  private val getTodoEndpoint =
    secureEndpoint(Endpoints.getTodoEndpoint).serverLogic { token => id =>
      toDosRepository.getToDo(token.subject, id).map { toDoResponse =>
        toDoResponse match {
          case Some(todo) => Right(todo)
          case None => Left(ErrorInfo.toDoIdNotFound(id))
        }
      }
    }

  private val addToDoEndpoint =
    secureEndpoint(Endpoints.addToDoEndpoint).serverLogic { token => toDo =>
      toDosRepository
        .addToDo(token.subject, ToDoVO(toDo.title, toDo.description))
        .map(id => Right(CreateToDoResponse(id)))
    }

  private val deleteToDoEndpoint =
    secureEndpoint(Endpoints.deleteTodoEndpoint).serverLogic { token => id =>
      toDosRepository.deleteToDo(token.subject, id).map { wasDeleted =>
        if (wasDeleted) Right(s"ToDo $id was deleted")
        else Left(ErrorInfo.toDoIdNotFound(id))
      }

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

  def secureEndpoint[INPUT, OUTPUT](
    endpointDefinition: Endpoint[BearerToken, INPUT, ErrorInfo, OUTPUT, Any]
  ): PartialServerEndpoint[
    BearerToken,
    UserAuthenticated,
    INPUT,
    ErrorInfo,
    OUTPUT,
    Any,
    Future
  ] =
    endpointDefinition
      .serverSecurityLogic[UserAuthenticated, Future] { bearerToken =>
        Future {
          authentication.validateToken(bearerToken) match {
            case Failure(_) => Left(ErrorInfo.AccessDenied)
            case Success(userAuthenticated)
                if userAuthenticated.permissions.nonEmpty =>
              Right(userAuthenticated)
            case Success(_) => Left(ErrorInfo.AccessForbidden)
          }
        }
      }

}
