package org.ozoneplatform.gatling.aml.action

import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils
import io.gatling.core.config.Protocol

object ActionHelpers {

  val baseUrl = FeederUtils.getBaseUrl

  val restHttpProtocol: Protocol = http
    .baseURL(baseUrl)
    .acceptHeader("application/json")

  val restApiHeaders = Map("Content-Type" -> "application/json")

  val searchHeaders = Map("Content-Type" -> "application/json")

  val configHeaders = Map("Content-Type" -> "text/html")

  val adminTypeHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")

  val setUIRelatedHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")

  val owfRelatedHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")
}
