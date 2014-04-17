package org.ozoneplatform.gatling.aml.feeder

import io.gatling.core.Predef._
import play.api.libs.json.Json
import play.api.libs.json.JsObject
import org.ozoneplatform.gatling.aml.feeder.FeederHelpers._

object Feeders {

  /**
   * returns a randomly generated email
   * 
   * @param propertyName the key used to store the value in the session
   * @return
   */
  def emailFeeder(propertyName: String = "email"): Feeder[String] =
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> randomEmail)
    }

  /**
   * returns a string made up of a random number of randomly selected words
   * @param words the words to choose from (uses wordDistro by default)
   * @param maxSize the max size of the list
   * @param propertyName the key used to store the value in the session
   * @return
   */
  def wordListFeeder(words: Array[String] = wordDistro, maxSize: Int = 5, propertyName: String = "wordList"): Feeder[String] =
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> randWordListAsString(wordDistro, randInt(1, maxSize)))
    }

  /**
   * Chooses a random word from a list
   *
   * @param words the list of words from which to select the feed
   * @param propertyName the key used for this feeder
   * @return
   */
  def wordFeeder(words: Array[String] = wordDistro, propertyName: String = "randomWord"): Feeder[String] =
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> randWord(words))
    }

  /**
   * Selects a random string of text from a corpus
   *
   * @param maxLength the maximum length of the string
   * @param propertyName the key to use when putting the value in the session
   * @return
   */
  def blurbFeeder(maxLength: Int = 255, propertyName: String = "blurb"): Feeder[String] =
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> randomString(corpus, maxLength))
    }

  /**
   * Provides a random Service Item rating from 1.0 - 5.0
   * @param propertyName the key to use in the session when storing the value
   * @return
   */
  def itemRatingFeeder(propertyName: String = "itemRating"): Feeder[Float] =
    new Feeder[Float] {
      override def hasNext = true

      override def next(): Map[String, Float] = Map(propertyName -> randInt(1, 5).asInstanceOf[Float])
    }

  /**
   * Given a string of profiles as json, filter out the admins and return a randomly chosen admin user name
   *
   * @param storeUsersAsJson the response from a GET to api/profile
   * @param propertyName the key to use when inserting the userName into the session (defaults to userName)
   * @return
   */
  def selectAdminUserFeeder(storeUsersAsJson: String, propertyName: String = "userName"): Feeder[String] =
    new Feeder[String] {
      val storeUsers = filterAdminUsers((Json.parse(storeUsersAsJson) \ "data").as[Array[JsObject]])

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (randomItemAsJson(storeUsers) \ "username").as[String])
    }

  /**
   * Given a string of profiles as json, return a randomly chosen username from that list
   *
   * @param storeUsersAsJson the response from a GET to api/profile
   * @param propertyName the key to use when inserting the userName into the session (defaults to userName)
   * @return
   */
  def selectUserNameFeeder(storeUsersAsJson: String, propertyName: String = "userName"): Feeder[String] =
    new Feeder[String] {
      val storeUsers = (Json.parse(storeUsersAsJson) \ "data").as[Array[JsObject]]

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (randomItemAsJson(storeUsers) \ "username").as[String])
    }

  /**
   * Given a string of service items as json, return a randomly chosen service item id from that list
   *
   * @param storeItemsAsJson the response from a GET to api/serviceItem
   * @param propertyName the key to use when inserting the id into the session (defaults to serviceItemId)
   * @return
   */
  def selectServiceItemIdFeeder(storeItemsAsJson: String, propertyName: String = "serviceItemId"): Feeder[Int] =
    new Feeder[Int] {
      val storeItems = (Json.parse(storeItemsAsJson) \ "data").as[Array[JsObject]]

      override def hasNext = true

      override def next(): Map[String, Int] = Map(propertyName -> (randomItemAsJson(storeItems) \ "id").as[Int])
    }

  /**
   * Generates a random username
   * 
   * @param isAdmin whether or not this user is an Admin (defaults to false)
   * @param propertyName the key to use when inserting the username into the session (defaults to userName)
   * @return
   */
  def generateUserNameFeeder(isAdmin: Boolean = false, propertyName: String = "userName"): Feeder[String] =
    new Feeder[String] {

      def baseString(): String = randWordListAsString(dictionary, 4).replaceAll(" ", "")

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (if (isAdmin) "Admin" + baseString()  else baseString()))
    }
}
