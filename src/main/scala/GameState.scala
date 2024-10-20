case class GameState(
                      players: List[PlayerHand],
                      gameBoard: GameBoard,      
                      currentPlayerIndex: Int,   
                      allCards: List[Card]      
                    )
