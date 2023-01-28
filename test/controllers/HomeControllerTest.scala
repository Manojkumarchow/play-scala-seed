package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

/** Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerTest
    extends PlaySpec
    with GuiceOneAppPerTest
    with Injecting {

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "return json object for successful addition" in {
      val json = Json.parse("""
      {
        "name": "Kodumuru",
        "location": {
            "lat": 51.244031,
            "long": -1.263224
        }
      }
      """)
      val request = FakeRequest(POST, "/places").withJsonBody(json)
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe json

      (contentAsJson(result) \ "name").as[String] mustBe "Kodumuru"
      (contentAsJson(result) \ "location" \ "lat")
        .as[Double] mustBe 51.244031
      (contentAsJson(result) \ "location" \ "long")
        .as[Double] mustBe -1.263224
    }

    "return error json object for invalid place json request" in {
      val json = Json.parse("""
      {
        "name": "Kodumuru",
        "location": {
            "lat": 51.244031
        }
      }
      """)
      val errorjson = Json.parse("""
          |{
          |    "obj.location.long": [
          |        {
          |            "msg": [
          |                "error.path.missing"
          |            ],
          |            "args": []
          |        }
          |    ]
          |}
          |""".stripMargin)
      val request = FakeRequest(POST, "/places").withJsonBody(json)
      val result = route(app, request).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")
      contentAsJson(result) mustBe errorjson
    }
  }
}
