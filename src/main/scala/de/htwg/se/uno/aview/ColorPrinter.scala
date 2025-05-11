import model.*
// $COVERAGE-OFF$
object ColorPrinter {
  //ansi escape codes
  val Reset = "\u001B[0m"
  val Red = "\u001B[31m"
  val Green = "\u001B[32m"
  val Blue = "\u001B[34m"
  val Yellow = "\u001B[93m"

  def printCard(card: Card): Unit = {
    val colorCode = card.color.toLowerCase match {
      case "red" => Red
      case "green" => Green
      case "blue" => Blue
      case "yellow" => Yellow
      case _ => Reset
    }

    card match {
      case NumberCard(color, value) =>
        println(s"${colorCode}NumberCard($color, $value)$Reset")
      case ActionCard(color, action) =>
        println(s"${colorCode}ActionCard($color, $action)$Reset")

      case  wild: WildCard =>
        val actionString = wild.action match {
          case "wild" => "Wild Card"
          case "wild draw four" => "Wild Draw Four"
        }
        println(s"${Reset}$actionString$Reset")
    }
  }
}
// $COVERAGE-ON$
