package org.ozoneplatform.gatling.feeder

import io.gatling.core.Predef._
import scala.util.Random
import play.api.libs.json.{Json, JsObject}

object Feeders {

  private val RNG = new Random

  private def randInt(a:Int, b:Int) = RNG.nextInt(b-a) + a

  private def randWord(words: Array[String]): String = words(randInt(0, words.size - 1))

  private def randWords(words: Array[String], count: Int, acc: String = ""): String =
    if (count < 0) acc
    else if (acc.size > 0) randWords(words, count - 1, randWord(words) + " " + acc)
    else randWords(words, count - 1, randWord(words))

  private def randomString(text: String, size: Int): String = {
    val start = randInt(0, text.size - (size + 1))
    text.slice(start, start + size)
  }

  def itemTitle(words: Array[String]): Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemTitle" -> randWords(words, randInt(1, 6)))
    }
  }

  def itemDescription(text: String): Feeder[String] = {
    new Feeder[String] {
      override def hasNext = true

      override def next(): Map[String, String] = Map("itemDescription" -> randomString(text, 3000))
    }
  }

  def itemJson(itemsAsString: String) : Feeder[JsObject] = {
    new Feeder[JsObject] {
      override def hasNext = true

      override def next(): Map[String, JsObject] = {
        val storeJson = Json.parse(itemsAsString)
        val items = (storeJson \ "data").as[Array[JsObject]]
        val item = items(randInt(0, items.size - 1))

        Map("itemJson" -> item)
      }
    }
  }
}
