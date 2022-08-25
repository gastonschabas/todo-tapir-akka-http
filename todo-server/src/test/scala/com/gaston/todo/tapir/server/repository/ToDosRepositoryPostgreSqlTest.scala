package com.gaston.todo.tapir.server.repository

import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import org.flywaydb.core.Flyway
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers
import org.testcontainers.utility.DockerImageName
import slick.jdbc.PostgresProfile.backend.Database

import java.util.UUID

class ToDosRepositoryPostgreSqlTest
    extends AsyncFunSuite
    with Matchers
    with TestContainerForAll {
  override val containerDef: PostgreSQLContainer.Def =
    PostgreSQLContainer.Def(dockerImageName =
      DockerImageName.parse("postgres:14.5-alpine")
    )

  override def afterContainersStart(containers: PostgreSQLContainer): Unit = {
    super.afterContainersStart(containers)
    val flyway = Flyway.configure
      .dataSource(containers.jdbcUrl, containers.username, containers.password)
      .load()
    flyway.migrate()
    ()
  }

  test("querying an empty table should not found a ToDo") {
    withContainers { containers =>
      val db = Database.forURL(
        containers.jdbcUrl,
        containers.username,
        containers.password
      )
      val repo = new ToDosRepositoryPostgreSql(db)
      repo.getToDo("user", UUID.randomUUID()).map(x => x should be(empty))
    }
  }

  test("after a Todo was inserted it must be found") {
    withContainers { containers =>
      val db = Database.forURL(
        containers.jdbcUrl,
        containers.username,
        containers.password
      )
      val repo = new ToDosRepositoryPostgreSql(db)
      val user = "user"
      for {
        toDoResponse <- repo.addToDo(user, ToDoVO("title", "description", user))
        toDoFound <- repo.getToDo(user, toDoResponse.id)
      } yield toDoFound should not be empty
    }
  }
}
