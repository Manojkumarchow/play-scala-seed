package controllers.model

import play.api.libs.json.{Format, Json}

final case class Location(lat: Double, long: Double)
object Location {
  implicit val locationFormat: Format[Location] = Json.format[Location]
}

final case class Place(name: String, location: Location)

object Place {
  implicit val placeFormat: Format[Place] = Json.format[Place]
  var list: List[Place] = {
    List(
      Place(
        "Sandleford",
        Location(51.377797, -1.318965)
      ),
      Place(
        "Watership Down",
        Location(51.235685, -1.309197)
      )
    )
  }

  def save(place: Place): Unit = {
    list = list ::: List(place)
  }
}
