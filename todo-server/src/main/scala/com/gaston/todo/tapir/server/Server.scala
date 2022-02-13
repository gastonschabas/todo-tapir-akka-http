package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends App {

  val logger = Logger("server")
  val config = ConfigFactory.load()
  val port = config.getInt("http.port")
  val routesService = new RoutesService(new ToDosRepository)
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  logger.info(s"starting server at $port")
  Http()
    .bindAndHandle(routesService.routes, "0.0.0.0", port)
    .map(_ => logger.info(s"server at $port"))

}
