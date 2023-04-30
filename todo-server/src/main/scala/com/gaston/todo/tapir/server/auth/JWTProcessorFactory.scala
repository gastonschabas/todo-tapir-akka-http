package com.gaston.todo.tapir.server.auth

import com.gaston.todo.tapir.server.config.AuthConfig
import com.nimbusds.jose.jwk.source.{JWKSource, JWKSourceBuilder}
import com.nimbusds.jose.proc.{
  DefaultJOSEObjectTypeVerifier,
  JWSVerificationKeySelector,
  SecurityContext
}
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jose.{JOSEObjectType, JWSAlgorithm}
import com.nimbusds.jwt.proc.{DefaultJWTClaimsVerifier, DefaultJWTProcessor}
import com.nimbusds.jwt.{JWTClaimNames, JWTClaimsSet}

import java.net.URL
import scala.jdk.CollectionConverters._

object JWTProcessorFactory {

  def apply(authConfig: AuthConfig): DefaultJWTProcessor[SecurityContext] = {
    // Create a JWT processor for the access tokens
    val jwtProcessor: DefaultJWTProcessor[SecurityContext] =
      new DefaultJWTProcessor[SecurityContext]

    // Set the required "typ" header "jwt" for access tokens issued by the
    // Auth0 server, may not be set by other servers
    jwtProcessor.setJWSTypeVerifier(
      new DefaultJOSEObjectTypeVerifier(JOSEObjectType.JWT)
    )

    // The public RSA keys to validate the signatures will be sourced from the
    // OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
    // object caches the retrieved keys to speed up subsequent look-ups and can
    // also handle key-rollover
    val keySource: JWKSource[SecurityContext] =
      JWKSourceBuilder
        .create(
          new URL(authConfig.jwks.url),
          new DefaultResourceRetriever(
            authConfig.jwks.connectTimeout,
            authConfig.jwks.readTimeout
          )
        )
        .build()

    // The expected JWS algorithm of the access tokens (agreed out-of-band)
    val expectedJWSAlg: JWSAlgorithm = JWSAlgorithm.RS256

    // Configure the JWT processor with a key selector to feed matching public
    // RSA keys sourced from the JWK set URL
    val keySelector =
      new JWSVerificationKeySelector[SecurityContext](expectedJWSAlg, keySource)

    jwtProcessor.setJWSKeySelector(keySelector)

    // Set the required JWT claims for access tokens issued by the Auth0
    // server, may differ with other servers
    val jwtClaimsSet: JWTClaimsSet = new JWTClaimsSet.Builder()
      .audience(authConfig.audience)
      .issuer(authConfig.issuer)
      .claim("gty", "client-credentials")
      .build

    val requiredClaims = Set(
      JWTClaimNames.SUBJECT,
      JWTClaimNames.AUDIENCE,
      JWTClaimNames.ISSUER,
      JWTClaimNames.EXPIRATION_TIME,
      JWTClaimNames.ISSUED_AT,
      "gty",
      "azp",
      "scope",
      "permissions"
    ).asJava

    jwtProcessor.setJWTClaimsSetVerifier(
      new DefaultJWTClaimsVerifier[SecurityContext](
        jwtClaimsSet,
        requiredClaims
      )
    )

    jwtProcessor
  }

}
