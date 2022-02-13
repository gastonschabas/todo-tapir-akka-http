package com.gaston.todo.tapir.contract.response

case class ErrorMessage(id: String, description: String, path: String)

case class ErrorInfo(id: String, status: Int, errors: List[ErrorMessage])
