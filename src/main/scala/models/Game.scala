package models

case class Game(
                 gameId: Option[Int],
                 year: String,
                 date: String,
                 time: String,
                 homeTeamName: String,
                 homeTeamScore: Int,
                 awayTeamName: String,
                 awayTeamScore: Int,
                 isDone: Boolean,
                 wasOvertimeGame: Boolean
               )
