package com.gaston.todo.tapir.server.runner

import akka.http.scaladsl.Http
import com.gaston.todo.tapir.server.module.ServerDependencies

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn

class ServerImpl extends Server with ServerDependencies {
  override def start(): Unit = {
    logger.info(s"starting server at http://localhost:${serverConfig.port}/")
    Http()
      .newServerAt(serverConfig.interface, serverConfig.port)
      .bindFlow(toDosApi.routes)
      .foreach(_ =>
        logger.info(s"started server at http://localhost:${serverConfig.port}/")
      )

    val _ = StdIn.readLine()
    ()
  }
}
