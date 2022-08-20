package com.gaston.todo.tapir.contract.response

import play.api.libs.json.Json

case class ErrorMessage(id: String, description: String, path: String)
object ErrorMessage {
  implicit val format = Json.format[ErrorMessage]
}

case class ErrorInfo(id: String, status: Int, errors: List[ErrorMessage])
object ErrorInfo {
  implicit val format = Json.format[ErrorInfo]

  val AccessDenied = ErrorInfo(
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

  val AccessForbidden = ErrorInfo(
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
}
