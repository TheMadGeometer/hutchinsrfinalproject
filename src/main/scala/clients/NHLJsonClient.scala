package clients

import java.sql.ResultSet

import cats.effect.IO
import io.circe._
import clients.NHLJsonClient.{Player, PlayerInformation, PlayerStatLine}
import io.circe.parser._
import models.TeamSeason
import org.http4s.{Header, Headers, Method, ParseFailure, Request, Uri}
import org.http4s.client.blaze._
import org.http4s.client._


object NHLJsonClient extends App {

  case class teamRoster(players: Vector[Player])

  case class Player(
                     id: Long,
                     fullName: String
                   )

  case class PlayerInformation(
                                id: Long,
                                firstName: String,
                                lastName: String,
                                jerseyNumber: String,
                                dateOfBirth: String,
                                birthCity: String,
                                birthStateProvince: String,
                                birthCountry: String,
                                nationality: String,
                                height: String,
                                weight: Int,
                                currentTeam: String,
                                position: String,
                                shootsCatches: String
                              )

  case class PlayerStatLine(
                             teamName: String,
                             season: String,
                             player_id: Long,
                             gamesPlayed: Int,
                             goals: Int,
                             assists: Int,
                             points: Int,
                             plusMinus: Int,
                             penaltyMinutes: Int
                           )

  override def main(args: Array[String]): Unit = {
    val nhlClient = new NHLJsonClient
    val currentPlayers = nhlClient.getCurrentPlayers

    nhlClient.getTeamStandings.map(io => io
      .map(x => nhlClient.parseTeamStandings(x, "2018-2019")
        .foreach(season => nhlClient.client.addTeamSeason(season))).unsafeRunSync())

    for ((k, v) <- currentPlayers) {
      v.foreach(x => nhlClient.getPlayerInformation(x.id)
      .map(io => io.map(nhlClient.parsePlayerInformation).unsafeRunSync()).foreach(nhlClient.client.addPlayerToDatabase))

      v.foreach(x => nhlClient.getPlayerstatLine(x.id, "20182019")
        .map(io => io.map(y => {
          val stats = nhlClient.parsePlayerStatLine(y, x.id, "20182019")
          nhlClient.client.addPlayerStatline(stats, x.id, "20182019")
        }).unsafeRunSync()))
    }
  }

}

class NHLJsonClient {
  val driver = "com.mysql.jdbc.Driver"
  val url = "jdbc:mysql://localhost:3306"
  val username = "root"
  val password = "herewegoagain"
  val client = new MySqlClient(url, username, password)

  val httpClient: Client[IO] = Http1Client[IO]().unsafeRunSync
  val defaultHeaders = Headers(
    Header("Accept", "application/json"),
    Header("Content-Type", "application/json")
  )

  def getCurrentPlayers: collection.mutable.Map[String, Vector[Player]] = {
    val apiUri = Uri.uri("https://statsapi.web.nhl.com/api/v1/teams?expand=team.roster")
    val request: Request[IO] = Request[IO](method = Method.GET, uri = apiUri, headers = defaultHeaders)
    val teamRosters = new collection.mutable.HashMap[String, Vector[Player]]
    val rosterString: String = httpClient.expect[String](request).unsafeRunSync()
    val rostersJson: Json = parse(rosterString).getOrElse(Json.Null)
    val cursor: HCursor = rostersJson.hcursor
    var teamEntry = cursor.downField("teams").downArray

    while (teamEntry != teamEntry.last) {
      val teamName = teamEntry.get[String]("name").getOrElse("No team name")
      var roster = teamEntry.downField("roster").downField("roster").downArray
      var teamRoster = Vector[Player]()
      while (roster != roster.last) {
        val playerId: Long = roster.downField("person").get[Long]("id").getOrElse(-1L)
        val playerName: String = roster.downField("person").get[String]("fullName").getOrElse("No player name")
        teamRoster = teamRoster :+ Player(playerId, playerName)
        roster = roster.rightN(1)
      }
      teamRosters(teamName) = teamRoster
      teamEntry = teamEntry.rightN(1)
    }
    teamRosters
  }

  def parsePlayerInformation(playerInformation: String): PlayerInformation = {
    val playerInformationJson: Json = parse(playerInformation).getOrElse(Json.Null)
    val cursor: HCursor = playerInformationJson.hcursor
    val playerEntry = cursor.downField("people").downArray
    val id: Long = playerEntry.get[Long]("id").getOrElse(-1L)
    val firstName: String = playerEntry.get[String]("firstName").getOrElse("")
    val lastName: String = playerEntry.get[String]("lastName").getOrElse("")
    val jerseyNumber: String = playerEntry.get[String]("primaryNumber").getOrElse("")
    val dateOfBirth: String = playerEntry.get[String]("birthDate").getOrElse("")
    val birthCity: String = playerEntry.get[String]("birthCity").getOrElse("")
    val birthStateProvince: String = playerEntry.get[String]("birthStateProvince").getOrElse("")
    val birthCountry: String = playerEntry.get[String]("birthCountry").getOrElse("")
    val nationality: String = playerEntry.get[String]("nationality").getOrElse("")
    val height: String = playerEntry.get[String]("height").getOrElse("")
    val weight: Int = playerEntry.get[Int]("weight").getOrElse(-1)
    val currentTeam: String = playerEntry.downField("currentTeam").get[String]("name").getOrElse("")
    val position: String = playerEntry.downField("primaryPosition").get[String]("abbreviation").getOrElse("")
    val shootsCatches: String = playerEntry.get[String]("shootsCatches").getOrElse("")

    println(PlayerInformation(
      id,
      firstName,
      lastName,
      jerseyNumber,
      dateOfBirth,
      birthCity,
      birthStateProvince,
      birthCountry,
      nationality,
      height,
      weight,
      currentTeam,
      position,
      shootsCatches
    ))

    PlayerInformation(
      id,
      firstName,
      lastName,
      jerseyNumber,
      dateOfBirth,
      birthCity,
      birthStateProvince,
      birthCountry,
      nationality,
      height,
      weight,
      currentTeam,
      position,
      shootsCatches
    )
  }

  def getPlayerInformation(id: Long): Either[ParseFailure, IO[String]] = {
    val apiUri = Uri.fromString(s"https://statsapi.web.nhl.com/api/v1/people/$id")
    apiUri.map(uri => {
      val request: Request[IO] = Request[IO](method = Method.GET, uri, headers = defaultHeaders)
      httpClient.expect[String](request)
    })
  }

  def getPlayerstatLine(id: Long, season: String): Either[ParseFailure, IO[String]] = {
    val apiUri = Uri.fromString(s"https://statsapi.web.nhl.com/api/v1/people/$id/stats?stats=statsSingleSeason&season=$season")
    apiUri.map(uri => {
      val request: Request[IO] = Request[IO](method = Method.GET, uri, headers = defaultHeaders)
      httpClient.expect[String](request)
    })
  }

  def parsePlayerStatLine(playerStats: String, playerId: Long, season: String): PlayerStatLine = {
    val playerStatsJson: Json = parse(playerStats).getOrElse(Json.Null)
    val cursor: HCursor = playerStatsJson.hcursor
    val statsEntry = cursor.downField("stats").downArray.downField("splits").downArray.downField("stat")

    val firstConn = client.getConnection
    val stmt = firstConn.prepareStatement(s"SELECT current_team FROM players WHERE player_id=?")
    stmt.setInt(1, playerId.toInt)
    val myRs = stmt.executeQuery()
    myRs.next()
    val teamName = myRs.getString(1)
    firstConn.close()

    val assists = statsEntry.get[Int]("assists").getOrElse(-1)
    val goals = statsEntry.get[Int]("goals").getOrElse(-1)
    val pim = statsEntry.get[Int]("pim").getOrElse(-1)
    val gamesPlayed = statsEntry.get[Int]("games").getOrElse(-1)
    val plusMinus = statsEntry.get[Int]("plusMinus").getOrElse(-1)
    val points = statsEntry.get[Int]("points").getOrElse(-1)


    println(PlayerStatLine(teamName, season, playerId, gamesPlayed, goals, assists, points, plusMinus, pim))
    PlayerStatLine(teamName, season, playerId, gamesPlayed, goals, assists, points, plusMinus, pim)
  }

  def getTeamStandings: Either[ParseFailure, IO[String]] = {
    val apiUri = Uri.fromString(s"https://statsapi.web.nhl.com/api/v1/standings")
    apiUri.map(uri => {
      val request: Request[IO] = Request[IO](method = Method.GET, uri, headers = defaultHeaders)
      httpClient.expect[String](request)
    })
  }

  def parseTeamStandings(standings: String, season: String): Vector[TeamSeason] = {
    val standingsJson: Json = parse(standings).getOrElse(Json.Null)
    val cursor: HCursor = standingsJson.hcursor
    var recordsCursor = cursor.downField("records").downArray
    var teamSeasons = Vector[TeamSeason]()
    while (recordsCursor != recordsCursor.last) {
      var division = recordsCursor.downField("division").get[String]("name").getOrElse("")
      var teamRecordCursor = recordsCursor.downField("teamRecords").downArray
      while (teamRecordCursor != teamRecordCursor.last) {

        val teamName: String = teamRecordCursor.downField("team").get[String]("name").getOrElse("")
        val wins: Int = teamRecordCursor.downField("leagueRecord").get[Int]("wins").getOrElse(-1)
        val losses: Int = teamRecordCursor.downField("leagueRecord").get[Int]("losses").getOrElse(-1)
        val ot: Int = teamRecordCursor.downField("leagueRecord").get[Int]("ot").getOrElse(-1)
        val goalsFor: Int = teamRecordCursor.get[Int]("goalsScored").getOrElse(-1)
        val goalsAgainst: Int = teamRecordCursor.get[Int]("goalsAgainst").getOrElse(-1)
        val points: Int = teamRecordCursor.get[Int]("points").getOrElse(-1)
        val gamesPlayed: Int = teamRecordCursor.get[Int]("gamesPlayed").getOrElse(-1)
        val goalDifferential: Int = goalsFor - goalsAgainst

        val teamSeason = TeamSeason(
          season,
          teamName,
          gamesPlayed,
          wins,
          losses,
          ot,
          points,
          goalsFor,
          goalsAgainst,
          goalDifferential,
          division
        )

        //println(teamSeason)

        teamSeasons = teamSeasons :+ teamSeason

        teamRecordCursor = teamRecordCursor.rightN(1)
      }
      recordsCursor = recordsCursor.rightN(1)
    }
    teamSeasons.foreach(println)
    teamSeasons
  }
}
