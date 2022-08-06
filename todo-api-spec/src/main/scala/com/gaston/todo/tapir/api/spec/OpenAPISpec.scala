package com.gaston.todo.tapir.api.spec

import com.gaston.todo.tapir.endpoint.Endpoints
import sttp.apispec.openapi.OpenAPI
import sttp.apispec.openapi.circe.yaml._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter

object OpenAPISpec extends App {

  lazy val openAPI: OpenAPI =
    OpenAPIDocsInterpreter().toOpenAPI(
      Endpoints.exposedEndpoints,
      "The ToDo API",
      Endpoints.version
    )

  lazy val yaml = openAPI.toYaml

  println(yaml)

}
