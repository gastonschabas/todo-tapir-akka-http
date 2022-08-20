package com.gaston.todo.tapir.server.module

import akka.actor.ActorSystem
import com.gaston.todo.tapir.server.auth.Authentication
import com.gaston.todo.tapir.server.config.AppConfig
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.gaston.todo.tapir.server.api.ToDosApi
import com.gaston.todo.tapir.server.runner.Server
import com.softwaremill.macwire._
import com.typesafe.scalalogging.Logger
import pureconfig._
import pureconfig.generic.auto._

trait ServerDependencies { self: Server =>

  val appConfig = ConfigSource.default.load[AppConfig] match {
    case Left(configError) =>
      throw new RuntimeException(configError.prettyPrint(2))
    case Right(config) => config
  }

  val toDosRepository = wire[ToDosRepository]
  val authentication = wire[Authentication]
  val logger = Logger(this.getClass.getName)
  val serverConfig = appConfig.serverConfig
  val toDosApi = wire[ToDosApi]
  implicit val actorSystem: ActorSystem = ActorSystem()

}
