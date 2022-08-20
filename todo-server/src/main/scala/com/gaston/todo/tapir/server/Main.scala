package com.gaston.todo.tapir.server

import com.gaston.todo.tapir.server.runner.ServerImpl

object Main extends App {

  val server = new ServerImpl
  server.start()

}
