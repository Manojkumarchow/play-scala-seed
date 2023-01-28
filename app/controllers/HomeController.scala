package controllers

import controllers.model.Place
import play.api.Logger
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)
    extends BaseController {
  val log: Logger = Logger(this.getClass)
  val logger = log.logger

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.index())
  }

  def listPlaces: Action[AnyContent] = Action {
    val json = Json.toJson(Place.list)
    Ok(json)
  }

//  def savePlace: Action[JsValue] = Action(parse.json) { request =>
//    val placeResult = request.body.validate[Place]
//    placeResult.fold(
//      errors => {
//        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
//      },
//      place => {
//        Place.save(place)
//        Ok(Json.obj("message" -> ("Place '" + place.name + "' saved.")))
//      }
//    )
//  }

  def validateJson[A: Reads]: BodyParser[A] = parse.json.validate(
    _.validate[A].asEither.left.map { e =>
      logger.error(s"Validation Failed - ${JsError.toJson(e)}")
      BadRequest(JsError.toJson(e))
    }
  )

  // if we don't care about validation we could replace `validateJson[Place]`
  // with `BodyParsers.parse.json[Place]` to get an unvalidated case class
  // in `request.body` instead.
  def savePlaceConcise: Action[Place] = Action(validateJson[Place]) { request =>
    // `request.body` contains a fully validated `Place` instance.
    val place = request.body
    logger.info(s"request body - ${request.body}")
    Place.save(place)
    Ok(Json.obj("message" -> ("Place '" + place.name + "' saved.")))
  }

}
