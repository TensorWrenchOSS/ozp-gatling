package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class ServiceItemBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj(
      "launchUrl" -> "https://",
      "imageLargeUrl" -> "https://",
      "imageSmallUrl" -> "https://",
      "contacts" -> Json.arr(),
      "categories" -> Json.arr()
    ))
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def types(typesId: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("types" -> Json.obj("id" -> typesId)))

  def title(title: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("title" -> title))

  def description(description: String): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("description" -> description))

  def approve(): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("approvalStatus" -> "Approved", "isOutside" -> true))

  def submit(): ServiceItemBuilder = new ServiceItemBuilder(json ++ Json.obj("approvalStatus" -> "Pending"))

  def addContact(contact: ContactBuilder): ServiceItemBuilder =
    new ServiceItemBuilder(json ++ Json.obj("contacts" -> ((json \ "contacts").as[JsArray] :+ Json.parse(contact.toString))))

  def addCategory(categoryId: String): ServiceItemBuilder =
    new ServiceItemBuilder(json ++ Json.obj("categories" -> ((json \ "categories").as[JsArray] :+ Json.obj("id" -> categoryId))))

  override def toString: String = Json.stringify(json)
}