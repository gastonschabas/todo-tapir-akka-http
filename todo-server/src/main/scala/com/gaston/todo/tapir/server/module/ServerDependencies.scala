package com.gaston.todo.tapir.server.module

import akka.actor.ActorSystem
import com.gaston.todo.tapir.server.api.ToDosApi
import com.gaston.todo.tapir.server.auth.{Authentication, AuthenticationImpl}
import com.gaston.todo.tapir.server.config.{
  AppConfig,
  DbProperties,
  ServerConfig
}
import com.gaston.todo.tapir.server.repository.{
  ToDosRepository,
  ToDosRepositoryPostgreSql
}
import com.softwaremill.macwire._
import com.typesafe.scalalogging.Logger
import org.flywaydb.core.Flyway
import pureconfig._
import pureconfig.generic.auto._
import slick.jdbc.PostgresProfile.backend.Database

import scala.concurrent.ExecutionContextExecutor

trait ServerDependencies {

  val appConfig: AppConfig = ConfigSource.default.load[AppConfig] match {
    case Left(configError) =>
      throw new RuntimeException(configError.prettyPrint(2))
    case Right(config) => config
  }

  lazy val dbProperties: DbProperties = appConfig.dbConfig.properties

  Flyway.configure
    .dataSource(dbProperties.jdbcUrl, dbProperties.user, dbProperties.password)
    .load()
    .migrate()

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val dbConfig: Database = Database.forConfig("db-config")

  val toDosRepository: ToDosRepository = wire[ToDosRepositoryPostgreSql]
  val authentication: Authentication = wire[AuthenticationImpl]
  val logger: Logger = Logger(this.getClass.getName)
  val serverConfig: ServerConfig = appConfig.serverConfig
  val toDosApi: ToDosApi = wire[ToDosApi]

}
