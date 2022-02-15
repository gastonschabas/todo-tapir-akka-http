package com.gaston.todo.tapir.server.security

import org.pac4j.core.authorization.generator.AuthorizationGenerator
import org.pac4j.core.context.WebContext
import org.pac4j.core.profile.UserProfile

import java.util.Optional

object CommonProfileAuthorizationGenerator extends AuthorizationGenerator {

  @SuppressWarnings(
    Array(
      "org.wartremover.warts.NonUnitStatements",
      "org.wartremover.warts.AsInstanceOf"
    )
  )
  override def generate(
    context: WebContext,
    profile: UserProfile
  ): Optional[UserProfile] = {
    Option(profile)
      .filter(p =>
        p.containsAttribute(CommonProfileAttributes.Permissions.toString)
      )
      .foreach(p =>
        p.addPermissions(
          profile
            .getAttribute(CommonProfileAttributes.Permissions.toString)
            .asInstanceOf[java.util.Collection[String]]
        )
      )
    Option(profile)
      .filter(p => p.containsAttribute(CommonProfileAttributes.Roles.toString))
      .foreach(p =>
        p.addPermissions(
          profile
            .getAttribute(CommonProfileAttributes.Roles.toString)
            .asInstanceOf[java.util.Collection[String]]
        )
      )
    Optional.of(profile)
  }
}

object CommonProfileAttributes extends Enumeration {
  type CommonProfileAttribute = Value
  val Permissions: CommonProfileAttribute = Value("permissions")
  val Roles: CommonProfileAttribute = Value("roles")
}
