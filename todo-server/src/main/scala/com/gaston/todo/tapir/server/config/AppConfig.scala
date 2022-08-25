package com.gaston.todo.tapir.server.config

// auth config
case class Jwks(url: String, connectTimeout: Int, readTimeout: Int)
case class AuthConfig(issuer: String, audience: String, jwks: Jwks)

// server config
case class ServerConfig(interface: String, port: Int)

case class DbProperties(
  serverName: String,
  portNumber: String,
  databaseName: String,
  user: String,
  password: String,
  jdbcUrl: String
)
case class DbConfig(
  connectionPool: String,
  dataSourceClass: String,
  properties: DbProperties,
  numThreads: Int
)

case class AppConfig(
  authConfig: AuthConfig,
  serverConfig: ServerConfig,
  dbConfig: DbConfig
)
