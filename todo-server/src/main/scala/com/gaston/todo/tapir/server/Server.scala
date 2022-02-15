package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.gaston.todo.tapir.server.route.Routes
import com.gaston.todo.tapir.server.security.ServerSecurity
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object Server extends App {

  val config: Config = ConfigFactory.load()

  val akkaHttpSecurity = ServerSecurity.akkaHttpSecurity
  val toDosRepository = new ToDosRepository
  val logger = Logger(this.getClass.getName)
  val port = config.getInt("http.port")
  val routes = new Routes(toDosRepository, akkaHttpSecurity)
  implicit val actorSystem: ActorSystem = ActorSystem()

  logger.info(s"starting server at $port")
  Http()
    .newServerAt("0.0.0.0", port)
    .bindFlow(routes.routes)
    .map(_ => logger.info(s"server at $port"))

  StdIn.readLine()

}
