package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class ScreenshotBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj())
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def smallImageId(smallImageId: String): ScreenshotBuilder = new ScreenshotBuilder(json ++ Json.obj("smallImageId" -> smallImageId))

  def largeImageId(largeImageId: String): ScreenshotBuilder = new ScreenshotBuilder(json ++ Json.obj("largeImageId" -> largeImageId))

  override def toString: String = Json.stringify(json)
}