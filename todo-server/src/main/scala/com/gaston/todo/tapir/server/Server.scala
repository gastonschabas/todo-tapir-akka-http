package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.gaston.todo.tapir.server.route.Routes
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

object Server extends App {

  val config: Config = ConfigFactory.load()

  val toDosRepository = new ToDosRepository
  val logger = Logger(this.getClass.getName)
  val port = config.getInt("http.port")
  val routes = new Routes(toDosRepository)
  implicit val actorSystem: ActorSystem = ActorSystem()

  logger.info(s"starting server at http://localhost:$port/")
  Http()
    .newServerAt("0.0.0.0", port)
    .bindFlow(routes.routes)
    .foreach(_ => logger.info(s"started server at http://localhost:$port/"))

  StdIn.readLine()

}
