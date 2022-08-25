package com.gaston.todo.tapir.server.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.gaston.todo.tapir.contract.auth.{BearerToken, UserAuthenticated}
import com.gaston.todo.tapir.contract.request.CreateToDoRequest
import com.gaston.todo.tapir.contract.response.{
  CreateToDoResponse,
  ErrorInfo,
  ToDoResponse
}
import com.gaston.todo.tapir.server.auth.Authentication
import com.gaston.todo.tapir.server.config.{AppConfig, DbConfig, DbProperties}
import com.gaston.todo.tapir.server.repository.{
  ToDosRepository,
  ToDosRepositoryInMemory,
  ToDosRepositoryPostgreSql
}
import com.softwaremill.macwire.wire
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import slick.jdbc.PostgresProfile.backend.Database

import scala.util.{Success, Try}

class ToDosApiTest
    extends AnyFunSuite
    with Matchers
    with ScalatestRouteTest
    with PlayJsonSupport {

  class BaseFixture {
    implicit def productHintDbConfig =
      ProductHint[DbConfig](ConfigFieldMapping(CamelCase, CamelCase))
    implicit def productHintDbProperties =
      ProductHint[DbProperties](ConfigFieldMapping(CamelCase, CamelCase))

    val appConfig = ConfigSource.default.load[AppConfig] match {
      case Left(configError) =>
        throw new RuntimeException(configError.prettyPrint(2))
      case Right(config) => config
    }
    val toDosRepository: ToDosRepository = wire[ToDosRepositoryInMemory]

    val authentication: Authentication = new Authentication {
      override def validateToken(
        bearerToken: BearerToken
      ): Try[UserAuthenticated] =
        Success(UserAuthenticated("subject", "issuer", List("read")))
    }

    val authHeader = addHeader("Authorization", "Bearer random-jwt-token")

    val toDosApi = new ToDosApi(toDosRepository, authentication)
  }

  def baseFixture = new BaseFixture

  test(
    "get todo list without ToDo tasks saved should returned an empty ToDo list"
  ) {
    val fixture = baseFixture
    val routes = fixture.toDosApi.routes
    Get(s"/api/v0.0/todo") ~> addHeader(
      "Authorization",
      "Bearer random-jwt-token"
    ) ~> routes ~> check {
      responseAs[List[ToDoResponse]] should be(Nil)
      status should be(StatusCodes.OK)
    }
  }

  test("get todo list should return all the todos saved") {
    val fixture = baseFixture
    val routes = fixture.toDosApi.routes
    val todoRequest = CreateToDoRequest("a todo", "a simple todo")
    Post(
      s"/api/v0.0/todo",
      todoRequest
    ) ~> fixture.authHeader ~> routes ~> check {
      status should be(StatusCodes.OK)
      val response = responseAs[CreateToDoResponse]
      Get(s"/api/v0.0/todo") ~> fixture.authHeader ~> routes ~> check {
        val toDoList = responseAs[List[ToDoResponse]]
        toDoList should be(
          List(
            ToDoResponse(
              response.id,
              todoRequest.title,
              todoRequest.description
            )
          )
        )
        status should be(StatusCodes.OK)
      }
    }
  }

  test("add, remove and get a todo task should return an empty list") {
    val fixture = baseFixture
    val routes = fixture.toDosApi.routes
    val todoRequest = CreateToDoRequest("a todo", "a simple todo")
    Post(
      s"/api/v0.0/todo",
      todoRequest
    ) ~> fixture.authHeader ~> routes ~> check {
      status should be(StatusCodes.OK)
      val response = responseAs[CreateToDoResponse]
      Delete(
        s"/api/v0.0/todo/${response.id}"
      ) ~> fixture.authHeader ~> routes ~> check {
        status should be(StatusCodes.OK)
        Get(s"/api/v0.0/todo") ~> fixture.authHeader ~> routes ~> check {
          val toDoList = responseAs[List[ToDoResponse]]
          toDoList should be(Nil)
          status should be(StatusCodes.OK)
        }
      }
    }
  }

  test("when a todo task is saved it should be able to be queried") {
    val fixture = baseFixture
    val routes = fixture.toDosApi.routes
    val todoRequest = CreateToDoRequest("a todo", "a simple todo")
    Post(
      s"/api/v0.0/todo",
      todoRequest
    ) ~> fixture.authHeader ~> routes ~> check {
      status should be(StatusCodes.OK)
      val response = responseAs[CreateToDoResponse]
      Get(
        s"/api/v0.0/todo/${response.id}"
      ) ~> fixture.authHeader ~> routes ~> check {
        val toDoList = responseAs[ToDoResponse]
        toDoList should be(
          ToDoResponse(response.id, todoRequest.title, todoRequest.description)
        )
        status should be(StatusCodes.OK)
      }
    }
  }

  test(
    "when a todo task is saved and then delete it, it should not be able to be queried"
  ) {
    val fixture = baseFixture
    val routes = fixture.toDosApi.routes
    val todoRequest = CreateToDoRequest("a todo", "a simple todo")
    Post(
      s"/api/v0.0/todo",
      todoRequest
    ) ~> fixture.authHeader ~> routes ~> check {
      status should be(StatusCodes.OK)
      val response = responseAs[CreateToDoResponse]
      Delete(
        s"/api/v0.0/todo/${response.id}"
      ) ~> fixture.authHeader ~> routes ~> check {
        Get(
          s"/api/v0.0/todo/${response.id}"
        ) ~> fixture.authHeader ~> routes ~> check {
          val errorInfo = responseAs[ErrorInfo]
          errorInfo.status should be(StatusCodes.NotFound.intValue)
          errorInfo.errors should not be Nil
          status should be(StatusCodes.NotFound)
        }
      }
    }
  }

}
