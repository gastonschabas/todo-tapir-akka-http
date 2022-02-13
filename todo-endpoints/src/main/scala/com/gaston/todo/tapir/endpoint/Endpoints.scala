package com.gaston.todo.tapir.endpoint

import com.gaston.todo.tapir.contract.request.CreateToDoRequest
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ToDoResponse
}
import io.circe.generic.auto._
import tapir._
import tapir.json.circe._

import java.util.UUID

object Endpoints {

  val version = "v0.0"

  val baseEndpointV0 = endpoint.in("api" / version)

  val todoBaseEndpoint = baseEndpointV0.in("todo")

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

  val getToDosEndpoint = todoBaseEndpoint.get
    .description("The list of ToDos")
    .in(
      query[Option[Int]]("limit").description("Set the limit of ToDos returned")
    )
    .out(
      jsonBody[List[ToDoResponse]]
        .example(ToDoResponse.exampleList)
    )

  val getTodoEndpoint = todoBaseEndpoint.get
    .description("The Todo")
    .in(path[UUID]("id").description("The ID of the ToDo"))
    .out(
      jsonBody[ToDoResponse]
        .example(ToDoResponse.example1)
    )

  val addToDoEndpoint = todoBaseEndpoint.post
    .description("creates a new ToDo")
    .in(
      jsonBody[CreateToDoRequest]
        .description("The ToDo to be saved")
        .example(CreateToDoRequest.example1)
    )
    .out(
      jsonBody[CreateToDoResponse]
        .description("The ID generated of the saved ToDo")
        .example(CreateToDoResponse.example1)
    )

  val exposedEndpoints =
    List(openAPISpec, todoDescriptionEndpoint, getToDosEndpoint)

}
