package com.gaston.todo.tapir.contract.request

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

case class CreateToDoRequest(title: String, description: String)

object CreateToDoRequest {
  val example1 = CreateToDoRequest(
    "Read DDD by Eric Evans",
    "Book that shows how to implement DDD in a real project"
  )

  implicit val codec: JsonValueCodec[CreateToDoRequest] =
    JsonCodecMaker.make[CreateToDoRequest]

}
