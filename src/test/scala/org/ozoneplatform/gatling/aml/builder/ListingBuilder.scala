package org.ozoneplatform.gatling.aml.builder

import play.api.libs.json._

class ListingBuilder(jsonIn: JsObject) {

  val json = jsonIn

  def this() {
    this(Json.obj(
      "launchUrl" -> "https://",
      "contacts" -> Json.arr(),
      "categories" -> Json.arr()
    ))
  }

  def this(jsonString: String) {
    this(
      Json.parse(jsonString).as[JsObject]
    )
  }

  def types(types: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("type" -> types))

  def title(title: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("title" -> title))

  def description(description: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("description" -> description))

  def descriptionShort(descriptionShort: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("descriptionShort" -> descriptionShort))

  def versionName(versionName: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("versionName" -> versionName))

  def smallIconId(smallIconId: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("smallIconId" -> smallIconId))

  def largeIconId(largeIconId: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("largeIconId" -> largeIconId))

  def bannerIconId(bannerIconId: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("bannerIconId" -> bannerIconId))

  def featuredBannerIconId(featuredBannerIconId: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("featuredBannerIconId" -> featuredBannerIconId))

  def requirements(requirements: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("requirements" -> requirements))

  def whatIsNew(whatIsNew: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("whatIsNew" -> whatIsNew))

  def agency(agency: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("agency" -> agency))

  def approve(): ListingBuilder = new ListingBuilder(json ++ Json.obj("approvalStatus" -> "Approved", "isOutside" -> true))

  def orgApprove(): ListingBuilder = new ListingBuilder(json ++ Json.obj("approvalStatus" -> "APPROVED_ORG"))

  def submit(): ListingBuilder = new ListingBuilder(json ++ Json.obj("approvalStatus" -> "Pending"))

  def addContact(contact: ContactBuilder): ListingBuilder =
    new ListingBuilder(json ++ Json.obj("contacts" -> ((json \ "contacts").as[JsArray] :+ Json.parse(contact.toString))))

  def addCategory(categories: String): ListingBuilder =
    new ListingBuilder(json ++ Json.obj("categories" -> Json.arr(categories)))

  def addScreenshot(screenshots: ScreenshotBuilder): ListingBuilder =
    new ListingBuilder(json ++ Json.obj("screenshots" -> Json.arr(Json.parse(screenshots.toString))))

  //def addScreenshot(screenshots: ScreenshotBuilder): ListingBuilder =
   // new ListingBuilder(json ++ Json.obj("screenshots" -> ((json \ "screenshots").as[JsArray] :+ Json.parse(screenshot.toString))))

  def tags(tags: String): ListingBuilder = new ListingBuilder(json ++ Json.obj("tags" -> Json.arr(tags)))


  override def toString: String = Json.stringify(json)
}