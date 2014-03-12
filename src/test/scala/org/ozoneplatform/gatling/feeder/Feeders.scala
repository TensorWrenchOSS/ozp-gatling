package org.ozoneplatform.gatling.feeder

import io.gatling.core.Predef._
import scala.util.Random
import play.api.libs.json.Json
import scala.collection.immutable.List
import org.ozoneplatform.gatling.feeder.FeederUtils._
import play.api.libs.json.JsObject
import scala.collection.mutable.ArrayBuffer

object Feeders {
  val corpus = getTextCorpus
  val wordDistro = getWordsDistro
  val dictionary = getDictionaryWords

  private val RNG = new Random

  /**
   * return a psuedo random integer between a and b inclusive
   *
   */
  def randInt(a: Int, b: Int) = RNG.nextInt((b + 1) - a) + a

  /**
   * Given an array of string, choose one at random
   */
  def randWord(words: Array[String]): String = words(randInt(0, words.size - 1))

  /**
   * Given an array of strings, return an array of size count of strings chosen at random from the original list
   */
  def randWordList(words: Array[String], count: Int, acc: String = ""): String =
    if (count < 0) acc
    else if (acc.size > 0) randWordList(words, count - 1, randWord(words) + " " + acc)
    else randWordList(words, count - 1, randWord(words))

  def randWordSet(words: Array[String], count: Int, acc: List[String] = List[String]()): List[String] = {
    if (acc.size == count) acc
    else {
      val word = randWord(words)
      if (acc.contains(word)) randWordSet(words, count, acc)
      else randWordSet(words, count, acc ++ List[String](word))
    }
  }

  def randomString(text: String, size: Int): String = {
    val start = randInt(0, text.size - (size + 1))
    text.slice(start, start + size)
  }

  def searchQuery: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("queryString" -> randWordList(wordDistro, 5))
    }
  }

  def itemTitle: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemTitle" -> randWordList(wordDistro, randInt(1, 6)))
    }
  }
  
  /**
   * Chooses a random word ("tag") from a subset of randomly selected words
   *
   * @param tagCount the number of tags to put in the candidate list
   * @return
   */
  def itemTag(tagCount: Int): Feeder[String] = wordFeed(randWordSet(wordDistro, tagCount) toArray, "itemTag")

  /**
   * Chooses a random word from a list
   *
   * @param words the list of words from which to select the feed
   * @param property the key used for this feeder
   * @return
   */
  def wordFeed(words: Array[String] = dictionary, property: String = "randomWord"): Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(property -> randWord(words))
    }
  }

  def itemDescription: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemDescription" -> randomString(corpus, 3000))
    }
  }

  def itemComment: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemComment" -> randomString(corpus, 100))
    }
  }

  def itemRating: Feeder[Float] = {
    new Feeder[Float] {
      override def hasNext = true

      override def next(): Map[String, Float] = Map("itemRating" -> randInt(1, 5).asInstanceOf[Float])
    }
  }

  /**
   * Given an array of profiles as JsObjects, return the subset of profiles from that array that are admins
   *
   * @param words
   * @param acc
   * @return
   */
  def filterAdminUsers(words: Array[JsObject], acc: Array[JsObject] = Array[JsObject]()): Array[JsObject] = {
    if (words.size == 0) acc
    else if ((words.head \ "username").toString().contains("Admin")) filterAdminUsers(words.tail, acc ++ Array[JsObject](words.head))
    else filterAdminUsers(words.tail, acc)
  }

  /**
   * Given a string of profiles as json, filter out the admins and return a randomly chosen admin user name
   *
   * @param storeUsersAsJson
   * @param propertyName
   * @return
   */
  def randomAdminUserName(storeUsersAsJson: String, propertyName: String = "adminUserName"): Feeder[String] = {
    new Feeder[String] {
      val storeUsers = filterAdminUsers((Json.parse(storeUsersAsJson) \ "data").as[Array[JsObject]])

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (randomItemAsJson(storeUsers) \ "username").as[String])
    }
  }

  /**
   * Given a string of profiles as json, return a randomly chosen username from that list
   *
   * @param storeUsersAsJson
   * @param propertyName
   * @return
   */
  def randomUserName(storeUsersAsJson: String, propertyName: String = "userName"): Feeder[String] = {
    new Feeder[String] {
      val storeUsers = (Json.parse(storeUsersAsJson) \ "data").as[Array[JsObject]]

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (randomItemAsJson(storeUsers) \ "username").as[String])
    }
  }

  /**
   * Given a string of service items as json, return a randomly chosen service item id from that list
   *
   * @param storeItemsAsJson
   * @return
   */
  def randomServiceItemId(storeItemsAsJson: String): Feeder[Int] = {
    new Feeder[Int] {
      val storeItems = (Json.parse(storeItemsAsJson) \ "data").as[Array[JsObject]]

      override def hasNext = true

      override def next(): Map[String, Int] = Map("serviceItemId" -> (randomItemAsJson(storeItems) \ "id").as[Int])
    }
  }

  def randomItemAsJson(jsonData: Array[JsObject]): JsObject = jsonData(randInt(0, jsonData.size - 1))

  def generateUserName(isAdmin: Boolean = false, propertyName: String = "userName"): Feeder[String] = {
    new Feeder[String] {

      def baseString(): String = randWordList(dictionary, 4).replaceAll(" ", "")

      override def hasNext = true

      override def next(): Map[String, String] = Map(propertyName -> (if (isAdmin) "Admin" + baseString()  else baseString()))
    }
  }
}
