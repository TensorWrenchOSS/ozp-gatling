package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class ContactBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj())
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  //def contactType(contactType: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("type" -> Json.obj("type" -> contactType)))

  def contactType(contactType: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("type" -> contactType))

  def name(name: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("name" -> name))

  def organization(organization: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("organization" -> organization))

  def email(email: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("email" -> email))

  def securePhone(securePhone: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("securePhone" -> securePhone))

  def unsecurePhone(unsecurePhone: String): ContactBuilder = new ContactBuilder(json ++ Json.obj("unsecurePhone" -> unsecurePhone))

  override def toString: String = Json.stringify(json)
}
