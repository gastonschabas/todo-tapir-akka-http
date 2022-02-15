package com.gaston.todo.tapir.server.security

import com.nimbusds.jose.jwk.{JWK, JWKSet}
import com.stackstate.pac4j.AkkaHttpSecurity
import com.stackstate.pac4j.store.InMemorySessionStorage
import com.typesafe.config.{Config, ConfigFactory}
import org.pac4j.core.client.Clients
import org.pac4j.core.config.{Config => Pac4jConfig}
import org.pac4j.http.client.direct.DirectBearerAuthClient
import org.pac4j.jwt.config.encryption.RSAEncryptionConfiguration
import org.pac4j.jwt.config.signature.RSASignatureConfiguration
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator
import org.pac4j.jwt.util.JWKHelper

import java.net.URL
import java.security.KeyPair
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

object ServerSecurity {

  val config: Config = ConfigFactory.load()

  val publicKeys: JWKSet =
    JWKSet.load(new URL(config.getString("auth.jwks.url")))
  val jwkSet: Seq[JWK] = publicKeys.getKeys.asScala.toList
  val rsaKeyPairFromJwk: Seq[KeyPair] =
    jwkSet.map(jwk => JWKHelper.buildRSAKeyPairFromJwk(jwk.toJSONString))
  val jwtAuthenticator = new JwtAuthenticator()
  rsaKeyPairFromJwk.foreach { rsaKeyPair =>
    jwtAuthenticator.addEncryptionConfiguration(
      new RSAEncryptionConfiguration(rsaKeyPair)
    )
    jwtAuthenticator.addSignatureConfiguration(
      new RSASignatureConfiguration(rsaKeyPair)
    )
  }

  val directBearerAuthClient: DirectBearerAuthClient =
    new DirectBearerAuthClient(jwtAuthenticator)
  directBearerAuthClient.setAuthorizationGenerator(
    CommonProfileAuthorizationGenerator
  )
  val clients: Clients = new Clients(directBearerAuthClient)
  val pac4jConfig: Pac4jConfig = new Pac4jConfig(clients)
  import scala.concurrent.duration._
  val sessionStorage = new InMemorySessionStorage(sessionLifetime = 10.minutes)
  val akkaHttpSecurity: AkkaHttpSecurity =
    new AkkaHttpSecurity(pac4jConfig, sessionStorage)

}
