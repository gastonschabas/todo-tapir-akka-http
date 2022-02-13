package com.gaston.todo.tapir.contract.response

import java.util.UUID

case class ToDoResponse(id: UUID, title: String, description: String)

object ToDoResponse {
  val example1 = ToDoResponse(
    UUID.fromString("a09cac78-56c5-418c-a132-9e8c51b7785b"),
    "Learn Scala 3",
    "Learn the new syntax and components that come with scala 3"
  )
  val example2 = ToDoResponse(
    UUID.fromString("6b13e1b8-1062-4e50-b8e3-25aa29042613"),
    "Learn how to use Tapir with Akka",
    ""
  )
  val exampleList = List(example1, example2)
}
