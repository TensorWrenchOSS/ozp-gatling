package org.ozoneplatform.gatling.aml.action

import io.gatling.http.Predef._
import org.ozoneplatform.gatling.aml.feeder.FeederUtils
import io.gatling.http.HeaderNames._
import io.gatling.http.HeaderValues._

object ActionHelpers {

  val baseUrl = FeederUtils.getBaseUrl

  val restHttpProtocol = http.baseURL(baseUrl)

  val searchHeaders = Map(ACCEPT -> APPLICATION_JSON)

  val restApiHeaders = Map(
    CONTENT_TYPE -> APPLICATION_JSON,
    ACCEPT -> APPLICATION_JSON
  )

  val configHeaders = Map(CONTENT_TYPE -> TEXT_HTML)

  val adminTypeHeaders = Map(CONTENT_TYPE -> APPLICATION_FORM_URLENCODED)
}
