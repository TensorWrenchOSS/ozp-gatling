package org.ozoneplatform.gatling.action

import io.gatling.http.Predef._
import org.ozoneplatform.gatling.feeder.FeederUtils
import io.gatling.core.config.Protocol

object ActionHelpers {

  val baseUrl = FeederUtils.getBaseUrl

  val restHttpProtocol: Protocol = http
    .baseURL(baseUrl)
    .acceptHeader("application/json")

  val restApiHeaders = Map("Content-Type" -> "application/json")

  val configHeaders = Map("Content-Type" -> "text/html")

  val adminTypeHeaders = Map("Content-Type" -> "application/x-www-form-urlencoded")
}
