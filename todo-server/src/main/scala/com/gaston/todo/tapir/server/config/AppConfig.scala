package com.gaston.todo.tapir.server.config

// auth config
case class Jwks(url: String, connectTimeout: Int, readTimeout: Int)
case class AuthConfig(issuer: String, audience: String, jwks: Jwks)

// server config
case class ServerConfig(interface: String, port: Int)

case class AppConfig(authConfig: AuthConfig, serverConfig: ServerConfig)
