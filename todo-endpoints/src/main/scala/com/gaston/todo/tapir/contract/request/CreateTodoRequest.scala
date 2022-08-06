package com.gaston.todo.tapir.contract.request

import play.api.libs.json.Json

case class CreateToDoRequest(title: String, description: String)

object CreateToDoRequest {
  val example1 = CreateToDoRequest(
    "Read DDD by Eric Evans",
    "Book that shows how to implement DDD in a real project"
  )

  implicit val format = Json.format[CreateToDoRequest]
}
