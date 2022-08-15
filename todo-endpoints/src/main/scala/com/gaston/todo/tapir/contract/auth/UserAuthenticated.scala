package com.gaston.todo.tapir.contract.auth

case class UserAuthenticated(
  subject: String,
  issuer: String,
  permissions: List[String]
)
