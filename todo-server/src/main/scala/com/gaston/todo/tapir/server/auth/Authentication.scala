package com.gaston.todo.tapir.server.auth

import com.gaston.todo.tapir.contract.auth.{BearerToken, UserAuthenticated}

import scala.util.Try

trait Authentication {
  def validateToken(bearerToken: BearerToken): Try[UserAuthenticated]
}
