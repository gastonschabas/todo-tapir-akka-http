package com.gaston.todo.tapir.server.auth

import com.gaston.todo.tapir.server.config.AppConfig
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor

import scala.jdk.CollectionConverters._
import scala.util.Try

class Authentication(appConfig: AppConfig) {

  private val jwtProcessor: DefaultJWTProcessor[SecurityContext] =
    JWTProcessorFactory(appConfig.authConfig)

  def validateToken(bearerToken: BearerToken): Try[AuthToken] = Try {
    // Process the token
    val claimsSet = jwtProcessor.process(bearerToken.token, null)
    AuthToken(
      subject = claimsSet.getSubject,
      issuer = claimsSet.getIssuer,
      permissions = claimsSet.getStringListClaim("permissions").asScala.toList
    )
  }

}
