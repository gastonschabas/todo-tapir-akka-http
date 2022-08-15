import laika.helium.Helium

lazy val tapirVersion = "1.0.3"
lazy val openAPICirceYamlVersion = "0.2.1"
lazy val akkaVersion = "2.6.19"
lazy val akkaHttpVersion = "10.2.9"
lazy val pac4jVersion = "4.5.4"
lazy val pac4jAkkaHttpVersion = "0.7.2"
lazy val scalaLoggingVersion = "3.9.5"
lazy val logbackVersion = "1.2.11"
lazy val nimbusJoseJWTVersion = "9.23"
lazy val pureConfigVersion = "0.17.1"

lazy val root =
  (project in file("."))
    .aggregate(`endpoints`, apiSpec, `server`)
    .settings(
      name                     := "todo-tapir-akka-http",
      version                  := "0.1",
      ThisBuild / scalaVersion := "2.13.8",
      ThisBuild / scalacOptions := Seq(
        "-encoding",
        "utf8",
        // Option and arguments on same line,
        "-Xfatal-warnings",
        // New lines for each options,
        "-deprecation",
        "-explaintypes",
        "-unchecked",
        "-feature",
        "-Xcheckinit",
        "-Ywarn-value-discard",
        "-Xlint:constant",
        "-Xlint:infer-any",
        "-Xlint:nullary-unit",
        "-Xlint:private-shadow",
        "-Xlint:type-parameter-shadow",
        "-Xlint:package-object-classes",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-language:existentials",
        "-language:postfixOps"
      )
    )

lazy val endpoints = (project in file("todo-endpoints")).settings(
  name := "todo-endpoints",
  libraryDependencies := Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core"      % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-play" % tapirVersion
  )
)

lazy val apiSpec = (project in file("todo-api-spec"))
  .settings(
    name := "todo-api-spec",
    libraryDependencies := Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % openAPICirceYamlVersion
    )
  )
  .dependsOn(endpoints)

lazy val server = (project in file("todo-server"))
  .settings(
    libraryDependencies := Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"      % akkaVersion,
      "com.typesafe.akka" %% "akka-http"        % akkaHttpVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion,
      "com.typesafe.scala-logging" %% "scala-logging"   % scalaLoggingVersion,
      "ch.qos.logback"              % "logback-classic" % logbackVersion,
      "com.nimbusds"                % "nimbus-jose-jwt" % nimbusJoseJWTVersion,
      "com.github.pureconfig"      %% "pureconfig"      % pureConfigVersion
    ),
    Compile / run / mainClass := Some("com.gaston.todo.tapir.server.Server"),
    laikaSite / target := baseDirectory.value.getAbsoluteFile / "src" / "main" / "resources" / "html",
    laikaTheme := Helium.defaults.all
      .metadata(
        title = Some("ToDo API"),
        description = Some("A Simple Rest API to manage ToDo tasks"),
        authors = List("Gastón Schabas"),
        language = Some("en-US"),
        version = Some(version.value)
      )
      .build
  )
  .dependsOn(endpoints, apiSpec)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(LaikaPlugin)
