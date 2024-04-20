import laika.helium.Helium

lazy val tapirVersion = "1.9.11"
lazy val openAPICirceYamlVersion = "0.9.0"
lazy val akkaVersion = "2.6.20"
lazy val akkaHttpVersion = "10.2.9"
lazy val akkaHttpJsonVersion = "1.39.2"
lazy val pac4jVersion = "4.5.4"
lazy val pac4jAkkaHttpVersion = "0.7.2"
lazy val scalaLoggingVersion = "3.9.5"
lazy val nimbusJoseJWTVersion = "9.37.3"
lazy val macwireVersion = "2.5.9"
lazy val logbackVersion = "1.5.5"
lazy val pureConfigVersion = "0.17.6"
lazy val postgreSqlDriverVersion = "42.7.3"
lazy val flywayVersion = "10.11.0"
lazy val scalaTestVersion = "3.2.18"
lazy val slickVersion = "3.5.0"
lazy val kamonVersion = "2.7.1"
lazy val testcontainersScalaVersion = "0.41.3"

lazy val root =
  (project in file("."))
    .aggregate(`endpoints`, apiSpec, `server`)
    .settings(
      name                     := "todo-tapir-akka-http",
      version                  := "0.1",
      ThisBuild / scalaVersion := "2.13.13",
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
    "com.softwaremill.sttp.tapir" %% "tapir-core"           % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-jsoniter-scala" % tapirVersion,
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.28.4",
    "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.28.4"
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
      "com.github.pureconfig"      %% "pureconfig"      % pureConfigVersion,
      "com.softwaremill.macwire"   %% "macros"          % macwireVersion,
      "com.typesafe.slick"         %% "slick"           % slickVersion,
      "com.typesafe.slick"         %% "slick-hikaricp"  % slickVersion,
      "org.flywaydb"                % "flyway-core"     % flywayVersion,
      "org.flywaydb"   % "flyway-database-postgresql" % flywayVersion,
      "org.postgresql" % "postgresql"                 % postgreSqlDriverVersion,
      "io.kamon"      %% "kamon-bundle"               % kamonVersion,
      "io.kamon"      %% "kamon-apm-reporter"         % kamonVersion,
      "org.scalatest" %% "scalatest"                  % scalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion     % Test,
      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % Test,
      "de.heikoseeberger" %% "akka-http-jsoniter-scala" % akkaHttpJsonVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersScalaVersion % Test,
      "com.dimafeng" %% "testcontainers-scala-postgresql" % testcontainersScalaVersion % Test
    ),
    Compile / run / mainClass := Some("com.gaston.todo.tapir.server.Main"),
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
