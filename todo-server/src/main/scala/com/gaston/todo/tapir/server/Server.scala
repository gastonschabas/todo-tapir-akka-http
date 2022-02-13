package com.gaston.todo.tapir.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.gaston.todo.tapir.server.repository.ToDosRepository
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object Server extends App {

  val config = ConfigFactory.load()
  val routesService = new RoutesService(new ToDosRepository)
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(
    routesService.routes,
    "localhost",
    config.getInt("server.port")
  )

  println(
    "Server started, visit http://localhost:8080/api/v0.0 for the API docs"
  )
  StdIn.readLine()

}
