package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.gaston.todo.tapir.server.auth.Authentication
import com.gaston.todo.tapir.server.config.AppConfig
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.gaston.todo.tapir.server.route.Routes
import com.typesafe.scalalogging.Logger
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object Server extends App {

  val appConfig = ConfigSource.default.load[AppConfig] match {
    case Left(configError) =>
      throw new RuntimeException(configError.prettyPrint(2))
    case Right(config) => config
  }

  val toDosRepository = new ToDosRepository
  val authentication = new Authentication(appConfig = appConfig)
  val logger = Logger(this.getClass.getName)
  val serverConfig = appConfig.serverConfig
  val routes = new Routes(toDosRepository, authentication)
  implicit val actorSystem: ActorSystem = ActorSystem()

  logger.info(s"starting server at http://localhost:${serverConfig.port}/")
  Http()
    .newServerAt(serverConfig.interface, serverConfig.port)
    .bindFlow(routes.routes)
    .foreach(_ =>
      logger.info(s"started server at http://localhost:${serverConfig.port}/")
    )

  StdIn.readLine()

}
