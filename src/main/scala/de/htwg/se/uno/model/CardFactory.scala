package de.htwg.se.uno.model

import scala.util.Random

object CardFactory {

  val colors = List("red", "blue", "green", "yellow")
  val numberRange = 0 to 9
  val actionTypes = List("draw two", "reverse", "skip")
  val wildActions = List("wild", "wild draw four")

  def createFullDeck(): List[Card] = {
    val numberCards = for {
      color <- colors
      number <- numberRange
      count = if (number == 0) 1 else 2  // 0 nur 1×, 1–9 doppelt
      _ <- 1 to count
    } yield NumberCard(color, number)

    val actionCards = for {
      color <- colors
      action <- actionTypes
      _ <- 1 to 2  // jede Aktionskarte 2× pro Farbe
    } yield ActionCard(color, action)

    val wildCards = for {
      action <- wildActions
      _ <- 1 to 4  // 4x pro Wildcard-Typ
    } yield WildCard(action)

    Random.shuffle((numberCards ++ actionCards ++ wildCards).toList)
  }
}

