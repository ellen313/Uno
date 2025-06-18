package de.htwg.se.uno.model.cardComponent

import play.api.libs.json.{JsError, JsObject, JsResult, JsString, JsValue, Json, OFormat}

object CardJsonFormat {
  implicit val numberCardFormat: OFormat[NumberCard] = Json.format[NumberCard]
  implicit val actionCardFormat: OFormat[ActionCard] = Json.format[ActionCard]
  implicit val wildCardFormat: OFormat[WildCard] = Json.format[WildCard]

  implicit val cardFormat: OFormat[Card] = new OFormat[Card] {
    override def reads(json: JsValue): JsResult[Card] = (json \ "type").validate[String].flatMap {
      case "NumberCard" => numberCardFormat.reads(json)
      case "ActionCard" => actionCardFormat.reads(json)
      case "WildCard" => wildCardFormat.reads(json)
      case other => JsError(s"Unknown card type: $other")
    }

    override def writes(card: Card): JsObject = {
      val base: JsObject = card match {
        case n: NumberCard => numberCardFormat.writes(n).as[JsObject]
        case a: ActionCard => actionCardFormat.writes(a).as[JsObject]
        case w: WildCard => wildCardFormat.writes(w).as[JsObject]
      }
      base + ("type" -> JsString(card.getClass.getSimpleName.stripSuffix("$")))
    }
  }
}
