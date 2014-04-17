package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class ServiceItemBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj(
      "types" -> Json.obj("id" -> 1),
      "launchUrl" -> "https://"
    ))
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def title(title: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("title" -> title))

  def description(description: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("description" -> description))

  def approve(): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("approvalStatus" -> "Approved", "isOutside" -> true))

  def submit(): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("approvalStatus" -> "Pending"))

  override def toString: String = Json.stringify(json)
}
