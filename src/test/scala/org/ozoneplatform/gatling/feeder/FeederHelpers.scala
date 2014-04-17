package org.ozoneplatform.gatling.feeder

import org.ozoneplatform.gatling.feeder.FeederUtils._
import scala.util.Random
import play.api.libs.json.JsObject

object FeederHelpers {
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
   * Given an array of strings, choose one at random
   */
  def randWord(words: Array[String]): String = words(randInt(0, words.size - 1))

  /**
   * Given an array of strings, return an array of size count of strings chosen at random from the original list
   */
  def randWordListAsString(words: Array[String], count: Int, acc: String = ""): String =
    if (count < 0) acc
    else if (acc.size > 0) randWordListAsString(words, count - 1, randWord(words) + " " + acc)
    else randWordListAsString(words, count - 1, randWord(words))

  /**
   * Given an array of strings, return an array of size count of unique strings chosen at random from the original list
   */
  def randWordSet(words: Array[String] = wordDistro, count: Int, acc: Array[String] = Array[String]()): Array[String] =
    if (acc.size == count) acc
    else {
      val word = randWord(words)
      if (acc.contains(word)) randWordSet(words, count, acc)
      else randWordSet(words, count, acc ++ Array[String](word))
    }

  /**
   * Given a string, return a random substring with the given size
   */
  def randomString(text: String, maxLength: Int): String = {
    val start = randInt(0, text.size - (maxLength + 1))
    text.slice(start, start + maxLength)
  }

  def randomEmail: String = randWord(wordDistro) + "@" + randWord(wordDistro) + "." + randWord(wordDistro)

  def randomItemAsJson(jsonData: Array[JsObject]): JsObject = jsonData(randInt(0, jsonData.size - 1))

  def filterAdminUsers(words: Array[JsObject], acc: Array[JsObject] = Array[JsObject]()): Array[JsObject] =
    if (words.size == 0) acc
    else if ((words.head \ "username").toString().contains("Admin")) filterAdminUsers(words.tail, acc ++ Array[JsObject](words.head))
    else filterAdminUsers(words.tail, acc)
}
