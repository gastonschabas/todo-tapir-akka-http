package com.gaston.todo.tapir.server.auth

case class AuthToken(subject: String, issuer: String, permissions: List[String])
