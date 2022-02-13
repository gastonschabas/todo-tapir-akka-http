# ToDo Tapir Akka Http

Small project to see how [tapir](https://tapir.softwaremill.com/en/latest/) can be integrated with 
[akka http](https://doc.akka.io/docs/akka-http/current/)

# Requirements

- jdk 11
- scala 2.13.8
- sbt 1.6.2

# Directory structure

The project has three modules.

- **todo-endpoints**: the meta description of the endpoints that the REST API have
- **todo-api-spec**: generates de Open API spec 
- **todo-server**: the implementation of the server logic

# Running locally

The server can be executed in Dev or Prod mode. The `default port` is `8080`, but it can be overridden if the 
environment variable `TODO_SERVER_PORT` is set 

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

- [tapir](https://tapir.softwaremill.com/en/latest/)
- [akka http](https://doc.akka.io/docs/akka-http/current/)

# API Spec

|URI|query params|result|
|---|------------|------|
|**/api/v0.0**|No|a brief of the API|
|**/api/v0.0/todo**|**limit**: a number limiting the total of the ToDos returned|a list of the pending ToDos|
