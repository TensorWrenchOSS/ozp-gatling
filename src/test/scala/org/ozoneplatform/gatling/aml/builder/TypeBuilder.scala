package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class TypeBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj())
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def title(title: String): TypeBuilder = new TypeBuilder(json ++ Json.obj("title" -> title))

  override def toString: String = Json.stringify(json)
}
