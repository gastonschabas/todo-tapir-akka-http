package com.gaston.todo.tapir.server.repository

import com.gaston.todo.tapir.contract.response.ToDoResponse
import org.scalatest.OptionValues
import org.scalatest.funsuite.AsyncFunSuite
import org.scalatest.matchers.should.Matchers

import java.util.UUID

class ToDosRepositoryInMemoryTest
    extends AsyncFunSuite
    with Matchers
    with OptionValues {

  class BaseFixture {
    val toDosRepositoryInMemory = new ToDosRepositoryInMemory()
  }

  def baseFixture = new BaseFixture

  test("trying to find a ToDo tasks should return None when it wasn't found") {
    val fixture = baseFixture
    fixture.toDosRepositoryInMemory
      .getToDo("user", UUID.randomUUID())
      .map(todo => todo should be(None))
  }

  test("a toDo should be returned when it was saved") {
    val fixture = baseFixture
    val user = "user"
    val toDoVO = ToDoVO("title", "description")
    for {
      id <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO)
      toDo <- fixture.toDosRepositoryInMemory.getToDo(user, id)
    } yield toDo should be(
      Some(ToDoResponse(id, toDoVO.title, toDoVO.description))
    )
  }

  test("a ToDo saved and then deleted should not be found") {
    val fixture = baseFixture
    val user = "user"
    val toDoVO = ToDoVO("title", "description")
    for {
      id <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO)
      _ <- fixture.toDosRepositoryInMemory.getToDo(user, id)
      _ <- fixture.toDosRepositoryInMemory.deleteToDo(user, id)
      toDo <- fixture.toDosRepositoryInMemory.getToDo(user, id)
    } yield toDo should be(None)
  }

  test(
    "take more ToDos than all the ones saved should return a list with all the ToDos saved"
  ) {
    val fixture = baseFixture
    val user = "user"
    val toDoVO = ToDoVO("title", "description")
    val toDoVO2 = ToDoVO("title2", "description2")
    val toDoVO3 = ToDoVO("title3", "description3")
    for {
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO)
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO2)
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO3)
      todos <- fixture.toDosRepositoryInMemory.takeToDos(user, 5)
    } yield todos.length should be(3)
  }

  test(
    "get less ToDos than the ones saved should return a list with a length equals to the take parameter"
  ) {
    val fixture = baseFixture
    val user = "user"
    val toDoVO = ToDoVO("title", "description")
    val toDoVO2 = ToDoVO("title2", "description2")
    val toDoVO3 = ToDoVO("title3", "description3")
    for {
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO)
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO2)
      _ <- fixture.toDosRepositoryInMemory.addToDo(user, toDoVO3)
      todos <- fixture.toDosRepositoryInMemory.takeToDos(user, 2)
    } yield todos.length should be(2)
  }

}
