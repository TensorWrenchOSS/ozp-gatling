package org.ozoneplatform.gatling.builder

import play.api.libs.json._

class ServiceItemBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj(
      "types" -> Json.obj("id" -> 1),
      "launchUrl" -> "https://"
    ))
  }

  def title(title: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("title" -> title))

  def description(description: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("description" -> description))

  override def toString: String = Json.stringify(json)
}
