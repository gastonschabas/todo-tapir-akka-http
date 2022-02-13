package com.gaston.todo.tapir.api.spec

import com.gaston.todo.tapir.endpoint.Endpoints

object OpenAPISpec {

  import tapir.docs.openapi._
  import tapir.openapi.OpenAPI
  import tapir.openapi.circe.yaml._

  val openAPI: OpenAPI =
    Endpoints.exposedEndpoints.toOpenAPI("The ToDo API", Endpoints.version)

  val yaml = openAPI.toYaml

}
