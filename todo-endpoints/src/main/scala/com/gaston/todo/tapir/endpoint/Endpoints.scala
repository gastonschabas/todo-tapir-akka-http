package com.gaston.todo.tapir.endpoint

import com.gaston.todo.tapir.contract.auth.BearerToken
import com.gaston.todo.tapir.contract.request.CreateToDoRequest
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ErrorInfo,
  ToDoResponse
}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.play.jsonBody

import java.util.UUID

object Endpoints {

  val version = "v0.0"

  val indexEndpoint = endpoint
    .out(htmlBodyUtf8)
    .description("The index page with some details")

  val baseEndpointV0 = endpoint.in("api" / version)

  val todoBaseEndpoint = baseEndpointV0.in("todo")

  val todosSecuredBaseEndpoint =
    todoBaseEndpoint
      .securityIn(auth.bearer[String]().mapTo[BearerToken])
      .errorOut(jsonBody[ErrorInfo])

  val todoDescriptionEndpoint = baseEndpointV0.get
    .description("Give details about the purpose of this API")
    .out(
      stringBody
        .example("An API to create a list of ToDos")
    )

  val openAPISpec = baseEndpointV0
    .in("spec")
    .get
    .description("It shows the Open API spec of this API")
    .out(stringBody)

  val getToDosEndpoint = todosSecuredBaseEndpoint.get
    .description("The list of ToDos")
    .in(
      query[Option[Int]]("limit").description("Set the limit of ToDos returned")
    )
    .out(
      jsonBody[List[ToDoResponse]]
        .example(ToDoResponse.exampleList)
    )

  val getTodoEndpoint = todosSecuredBaseEndpoint.get
    .description("The Todo")
    .in(path[UUID]("id").description("The ID of the ToDo"))
    .out(
      jsonBody[ToDoResponse]
        .example(ToDoResponse.example1)
    )

  val addToDoEndpoint = todosSecuredBaseEndpoint.post
    .in(
      jsonBody[CreateToDoRequest]
        .description("The ToDo to be saved")
        .example(CreateToDoRequest.example1)
    )
    .description("creates a new ToDo")
    .out(
      jsonBody[CreateToDoResponse]
        .description("The ID generated of the saved ToDo")
        .example(CreateToDoResponse.example1)
    )

  val deleteTodoEndpoint =
    todosSecuredBaseEndpoint
      .in(path[UUID]("id"))
      .delete
      .out(plainBody[String])

  val exposedEndpoints =
    List(
      openAPISpec,
      getTodoEndpoint,
      getToDosEndpoint,
      addToDoEndpoint,
      deleteTodoEndpoint
    )

}
