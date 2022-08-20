package com.gaston.todo.tapir.server.api

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gaston.todo.tapir.api.spec.OpenAPISpec
import com.gaston.todo.tapir.contract.auth.{BearerToken, UserAuthenticated}
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ErrorInfo,
  ErrorMessage,
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
            Future.successful(
              Right(
                (if (limit < 1) toDosRepository.takeToDos(token.subject, 1)
                 else toDosRepository.takeToDos(token.subject, limit))
                  .map(row => ToDoResponse(row.id, row.title, row.description))
              )
            )
          case None =>
            Future.successful(
              Right(
                toDosRepository
                  .takeToDos(token.subject, 5)
                  .map(row => ToDoResponse(row.id, row.title, row.description))
              )
            )
        }
      }

  private val getTodoEndpoint =
    secureEndpoint(Endpoints.getTodoEndpoint).serverLogic { token => id =>
      toDosRepository.getToDo(token.subject, id) match {
        case Some(todo) => Future(Right(todo))
        case None =>
          Future(
            Left(
              ErrorInfo(
                "todo.not.found",
                404,
                List(
                  ErrorMessage(
                    "todo.id.not.exist",
                    s"ToDo $id was not found",
                    ""
                  )
                )
              )
            )
          )
      }
    }

  private val addToDoEndpoint =
    secureEndpoint(Endpoints.addToDoEndpoint).serverLogic { token => toDo =>
      val id =
        toDosRepository.addToDo(
          token.subject,
          ToDoVO(toDo.title, toDo.description)
        )
      Future(Right(CreateToDoResponse(id)))
    }

  private val deleteToDoEndpoint =
    secureEndpoint(Endpoints.deleteTodoEndpoint).serverLogic { token => id =>
      val wasDeleted = toDosRepository.deleteToDo(token.subject, id)
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
            case Failure(_) =>
              Left[ErrorInfo, UserAuthenticated](
                ErrorInfo(
                  "access.denied",
                  401,
                  List(
                    ErrorMessage(
                      "token.invalid",
                      "bearer token provided is invalid",
                      "header.authentication"
                    )
                  )
                )
              )
            case Success(userAuthenticated)
                if userAuthenticated.permissions.nonEmpty =>
              Right(userAuthenticated)
            case Success(_) =>
              Left(
                ErrorInfo(
                  "access.forbidden",
                  403,
                  List(
                    ErrorMessage(
                      "token.forbidden",
                      s"permission read required",
                      "header.authorization"
                    )
                  )
                )
              )
          }
        }
      }

}
