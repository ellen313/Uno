package de.htwg.se.uno.model.playerComponent

import play.api.libs.json.{Json, OFormat}
import de.htwg.se.uno.model.cardComponent.CardJsonFormat.*

object PlayerJsonFormat {
  implicit val playerHandFormat: OFormat[PlayerHand] = Json.format[PlayerHand]
}
