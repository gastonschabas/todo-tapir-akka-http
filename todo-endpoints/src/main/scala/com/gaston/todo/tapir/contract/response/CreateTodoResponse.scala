package com.gaston.todo.tapir.contract.response

import java.util.UUID

case class CreateToDoResponse(id: UUID)

object CreateToDoResponse {
  val example1 = CreateToDoResponse(
    UUID.fromString("65c6bb19-b576-4019-b569-388ac3f92ce2")
  )
}
