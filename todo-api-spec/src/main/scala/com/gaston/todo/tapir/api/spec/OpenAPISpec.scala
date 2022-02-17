package com.gaston.todo.tapir.api.spec

import com.gaston.todo.tapir.endpoint.Endpoints

object OpenAPISpec extends App {

  import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
  import sttp.tapir.openapi.OpenAPI
  import sttp.tapir.openapi.circe.yaml._

  val openAPI: OpenAPI =
    OpenAPIDocsInterpreter().toOpenAPI(
      Endpoints.exposedEndpoints,
      "The ToDo API",
      Endpoints.version
    )

  val yaml = openAPI.toYaml

  println(yaml)

}
