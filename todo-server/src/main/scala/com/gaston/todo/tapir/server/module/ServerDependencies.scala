package com.gaston.todo.tapir.server.module

import akka.actor.ActorSystem
import com.gaston.todo.tapir.server.api.ToDosApi
import com.gaston.todo.tapir.server.auth.{Authentication, AuthenticationImpl}
import com.gaston.todo.tapir.server.config.AppConfig
import com.gaston.todo.tapir.server.repository.{
  ToDosRepository,
  ToDosRepositoryInMemory
}
import com.softwaremill.macwire._
import com.typesafe.scalalogging.Logger
import pureconfig._
import pureconfig.generic.auto._

trait ServerDependencies {

  val appConfig = ConfigSource.default.load[AppConfig] match {
    case Left(configError) =>
      throw new RuntimeException(configError.prettyPrint(2))
    case Right(config) => config
  }

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec = actorSystem.dispatcher

  val toDosRepository: ToDosRepository = wire[ToDosRepositoryInMemory]
  val authentication: Authentication = wire[AuthenticationImpl]
  val logger = Logger(this.getClass.getName)
  val serverConfig = appConfig.serverConfig
  val toDosApi = wire[ToDosApi]

}
