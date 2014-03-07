package org.ozoneplatform.gatling.feeder

import io.gatling.core.Predef._
import scala.util.Random
import play.api.libs.json.Json
import scala.collection.immutable.List
import org.ozoneplatform.gatling.feeder.FeederUtils._
import play.api.libs.json.JsObject

object Feeders {
  val corpus = getTextCorpus
  val wordDistro = getWordsDistro
  val dictionary = getDictionaryWords

  private val RNG = new Random

  def randInt(a: Int, b: Int) = RNG.nextInt((b + 1) - a) + a

  def randWord(words: Array[String]): String = words(randInt(0, words.size - 1))

  def randWords(words: Array[String], count: Int, acc: String = ""): String =
    if (count < 0) acc
    else if (acc.size > 0) randWords(words, count - 1, randWord(words) + " " + acc)
    else randWords(words, count - 1, randWord(words))

  def randWordList(words: Array[String], count: Int, acc: List[String] = List[String]()): List[String] = {
    if (acc.size == count) acc
    else {
      val word = randWord(words)
      if (acc.contains(word)) randWordList(words, count, acc)
      else randWordList(words, count, acc ++ List[String](word))
    }
  }

  def randomString(text: String, size: Int): String = {
    val start = randInt(0, text.size - (size + 1))
    text.slice(start, start + size)
  }

  def itemTitle: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemTitle" -> randWords(wordDistro, randInt(1, 6)))
    }
  }
  
  /**
   * Chooses a random word ("tag") from a subset of randomly selected words
   *
   * @param tagCount the number of tags to put in the candidate list
   * @return
   */
  def itemTag(tagCount: Int): Feeder[String] = wordFeed(randWordList(wordDistro, tagCount) toArray, "itemTag")

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

  /**
   *
   * @return
   */
  def itemDescription: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemDescription" -> randomString(corpus, 3000))
    }
  }

  def itemComment: Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemComment" -> randomString(corpus, 3000))
    }
  }

  def itemRating: Feeder[Float] = {
    new Feeder[Float] {
      override def hasNext = true

      override def next(): Map[String, Float] = Map("itemRating" -> randInt(1, 5).asInstanceOf[Float])
    }
  }

  def randomServiceItemId: Feeder[Int] = {
    new Feeder[Int] {
      lazy val storeItemsAsJsonString = getStoreItemsAsJsonString
      lazy val storeItems = (Json.parse(storeItemsAsJsonString) \ "data").as[Array[JsObject]]

      override def hasNext = true

      override def next(): Map[String, Int] = Map("serviceItemId" -> (randomItemAsJson(storeItems) \ "id").as[Int])
    }
  }

  def randomItemAsJson(jsonData: Array[JsObject]): JsObject = jsonData(randInt(0, jsonData.size - 1))

  def adminUser(userCount: Int) : Feeder[String] = randomUserName(userCount, "testAdmin")

  def user(userCount: Int) : Feeder[String] = randomUserName(userCount, "testUser")

  def userNumber(userCount: Int): String = if (userCount == 1) "1" else randInt(1, userCount).toString

  def randomUserName(userCount: Int, namePrefix: String, property: String = "userName"): Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map(property -> (namePrefix + userNumber(userCount)))
    }
  }

  def adminLoop(count: Int) = userNameLoop(count, "testAdmin")

  def userLoop(count: Int) = userNameLoop(count, "testUser")
  
  def userNameLoop(userCount: Int, namePrefix: String, property: String = "userName"): Feeder[String] = {

    new Feeder[String] {
      @volatile var counter = 0

      override def hasNext = true

      override def next(): Map[String, String] = {
        val userNumber = (counter % userCount + 1).toString
        counter += 1

        Map(property -> (namePrefix + userNumber))
      }
    }
  }
}
