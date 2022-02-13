lazy val tapirVersion = "0.11.11"

lazy val root =
  (project in file("."))
    .aggregate(`endpoints`, apiSpec, `server`)
    .settings(
      name         := "todo-tapir-akka-http",
      version      := "0.1",
      scalaVersion := "2.13.8",
      ThisBuild / scalacOptions := Seq(
        "-encoding",
        "utf8",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-Xfatal-warnings",
        "-language:higherKinds"
      )
    )

lazy val endpoints = (project in file("todo-endpoints")).settings(
  name := "todo-endpoints",
  libraryDependencies := Seq(
    "com.softwaremill.tapir" %% "tapir-core"       % tapirVersion,
    "com.softwaremill.tapir" %% "tapir-json-circe" % tapirVersion
  )
)

lazy val apiSpec = (project in file("todo-api-spec"))
  .settings(
    name := "todo-api-spec",
    libraryDependencies := Seq(
      "com.softwaremill.tapir" %% "tapir-openapi-docs"       % tapirVersion,
      "com.softwaremill.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion
    )
  )
  .dependsOn(endpoints)

lazy val server = (project in file("todo-server"))
  .settings(
    libraryDependencies := Seq(
      "com.softwaremill.tapir"     %% "tapir-akka-http-server" % tapirVersion,
      "com.typesafe"                % "config"                 % "1.4.1",
      "com.typesafe.scala-logging" %% "scala-logging"          % "3.9.4",
      "ch.qos.logback"              % "logback-classic"        % "1.2.10"
    ),
    Compile / run / mainClass := Some("com.gaston.todo.tapir.server.Server")
  )
  .dependsOn(endpoints, apiSpec)
  .enablePlugins(JavaAppPackaging)
