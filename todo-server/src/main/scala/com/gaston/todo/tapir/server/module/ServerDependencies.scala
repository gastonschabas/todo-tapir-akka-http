package com.gaston.todo.tapir.server.module

import akka.actor.ActorSystem
import com.gaston.todo.tapir.server.api.ToDosApi
import com.gaston.todo.tapir.server.auth.{Authentication, AuthenticationImpl}
import com.gaston.todo.tapir.server.config.{
  AppConfig,
  DbConfig,
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
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import slick.jdbc.PostgresProfile.backend.Database

import scala.concurrent.ExecutionContextExecutor

trait ServerDependencies {

  implicit def productHintDbConfig =
    ProductHint[DbConfig](ConfigFieldMapping(CamelCase, CamelCase))

  implicit def productHintDbProperties =
    ProductHint[DbProperties](ConfigFieldMapping(CamelCase, CamelCase))

  val appConfig: AppConfig = ConfigSource.default.load[AppConfig] match {
    case Left(configError) =>
      throw new RuntimeException(configError.prettyPrint(2))
    case Right(config) => config
  }

  lazy val dbProperties: DbProperties = appConfig.dbConfig.properties

  case class DatabaseProperties(
    user: String,
    password: String,
    host: String,
    port: String,
    dbName: String
  )

  val JdbcUrlRegex = "postgres://(\\w+):(\\w+)@([\\w\\-\\.]+):(\\d+)/(\\w+)".r
  val databaseProperties = dbProperties.jdbcUrl match {
    case JdbcUrlRegex(user, password, host, port, dbName) =>
      DatabaseProperties(user, password, host, port, dbName)
  }

  Flyway.configure
    .dataSource(
      dbProperties.jdbcUrl,
      databaseProperties.user,
      databaseProperties.password
    )
    .load()
    .migrate()

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

  val dbConfig: Database = Database.forURL("db-config")

  val toDosRepository: ToDosRepository = wire[ToDosRepositoryPostgreSql]
  val authentication: Authentication = wire[AuthenticationImpl]
  val logger: Logger = Logger(this.getClass.getName)
  val serverConfig: ServerConfig = appConfig.serverConfig
  val toDosApi: ToDosApi = wire[ToDosApi]

}
