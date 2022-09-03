package com.gaston.todo.tapir.server

import com.gaston.todo.tapir.server.runner.ServerImpl
import kamon.Kamon

object Main extends App {

  Kamon.init()
  val server = new ServerImpl
  server.start()

}
