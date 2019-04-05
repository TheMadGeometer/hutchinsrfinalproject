package models

case class TeamSeason(
                       seasonYear: String,
                       teamId: String,
                       gamesPlayed: Int,
                       wins: Int,
                       losses: Int,
                       overtimeLosses: Int,
                       points: Int,
                       goalsFor: Int,
                       goalsAgainst: Int,
                       goalDifferential: Int,
                       division: String
                     )
