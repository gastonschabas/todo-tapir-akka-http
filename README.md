# ToDo Tapir Akka Http

Small project to see how [tapir](https://tapir.softwaremill.com/en/latest/) can be integrated with
[akka http](https://doc.akka.io/docs/akka-http/current/)

* [Requirements](#requirements)
* [Directory structure](#directory-structure)
* [Running locally](#running-locally)
  * [Dev Mode](#dev-mode)
  * [Prod Mode](#prod-mode)
* [Libraries](#libraries)
* [Demo](#demo)
* [API Spec](#api-spec)

# Requirements

- jdk 11
- scala 2.13.8
- sbt 1.7.1

# Directory structure

The project has three modules.

- **todo-endpoints**: the meta description of the endpoints that the REST API have
- **todo-api-spec**: generates de Open API spec
- **todo-server**: the implementation of the server logic

# Running locally

The server can be executed in Dev or Prod mode. The `default port` is `8080`, but it can be overridden if the
variable `server-config.port` is set.

## Dev Mode

Execute the following command

```shell
sbt server/run
```

## Prod Mode

First a build must be created running the following command

```shell
sbt server/stage
```

A binary file will be created in the directory `todo-server/target/universal/stage/bin/`. It can be executed as a
regular bash script.

```shell
./todo-server/target/universal/stage/bin/server
```

# Libraries

- [Tapir](https://tapir.softwaremill.com/en/latest/): 1.0.3
- [akka](https://akka.io/): 2.6.19
- [akka-http](https://doc.akka.io/docs/akka-http/current/index.html): 10.2.9
- [Nimbus Jose JWT](https://connect2id.com/products/nimbus-jose-jwt): 9.23

# Demo

- The Rest API is deployed in [heroku](https://www.heroku.com/) platform.
- The demo con be found in [ToDo Tapir Akka Http](https://todo-tapir-akka-http.herokuapp.com/)

# API Spec

- [Open API Spec](https://todo-tapir-akka-http.herokuapp.com/api/v0.0/spec)