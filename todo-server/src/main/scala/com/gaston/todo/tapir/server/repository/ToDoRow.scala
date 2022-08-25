package com.gaston.todo.tapir.server.repository

import java.util.UUID

final case class ToDoRow(
  id: UUID,
  title: String,
  description: String,
  user: String
)
