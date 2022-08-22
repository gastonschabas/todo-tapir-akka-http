package com.gaston.todo.tapir.server.module

import com.gaston.todo.tapir.contract.auth.{BearerToken, UserAuthenticated}
import com.gaston.todo.tapir.server.auth.Authentication

import scala.util.Success

trait ServerDependenciesFake extends ServerDependencies {

  override val authentication: Authentication = (_: BearerToken) =>
    Success(UserAuthenticated("subject", "issuer", Nil))

}
