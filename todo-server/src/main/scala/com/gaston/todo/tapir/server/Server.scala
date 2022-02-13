package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.typesafe.config.ConfigFactory

object Server extends App {

  val config = ConfigFactory.load()
  val port = config.getInt("server.port")
  val routesService = new RoutesService(new ToDosRepository)
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(routesService.routes, "0.0.0.0", port)

}
