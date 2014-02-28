
import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import io.gatling.http.Headers.Values._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class MarketplaceScenario extends Simulation {

	val httpProtocol = http
		.baseURL("http://10.40.1.115:8080")
		.acceptHeader("application/json")
		.acceptEncodingHeader("gzip,deflate,sdch")
		.acceptLanguageHeader("en-US,en;q=0.8")
		.authorizationHeader("Basic dGVzdEFkbWluMTpwYXNzd29yZA==")

	val create_item_headers = Map(
		"""Content-Type""" -> """application/json""")

	val scn = scenario("Marketplace Scenario")
		.exec(http("create service item")
			.post("""/marketplace/api/serviceItem""")
			.headers(create_item_headers)
			.body(RawFileBody("create_service_item.json"))
			.basicAuth("""testAdmin1""","""password"""))

//	setUp(scn.inject(
//    rampRate(1 usersPerSec) to (300 usersPerSec) during(5 minutes)))
//      .protocols(httpProtocol)
//      .assertions(global.responseTime.max.lessThan(4000))
  setUp(scn.inject(1 usersPerSec) during(3 minutes)))
      .protocols(httpProtocol)
      .assertions(global.responseTime.max.lessThan(4000))
}
