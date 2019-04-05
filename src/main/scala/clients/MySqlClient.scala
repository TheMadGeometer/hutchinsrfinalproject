package clients

import java.sql.Connection

import cats.effect.IO
import clients.NHLJsonClient.{PlayerInformation, PlayerStatLine}
import io.circe.Json
import models._
import org.http4s.dsl.io._
import org.apache.commons.dbcp2.BasicDataSource
import org.http4s.Response
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder._

import scala.util.{Failure, Success, Try}

class MySqlClient(
                   url: String,
                   username: String,
                   password: String
                 ) {
  val datasource: BasicDataSource = new BasicDataSource
  datasource.setUrl("jdbc:mysql://localhost:3306/nhl?characterEncoding=latin1&useConfigs=maxPerformance")
  datasource.setUsername(username)
  datasource.setPassword(password)
  datasource.setDriverClassName("com.mysql.jdbc.Driver")

  def getConnection: Connection = {
    val connection: Connection = Try(datasource.getConnection()) match {
      case Success(conn: Connection) => {
        conn
      }
      case Failure(exception) => {
        exception.printStackTrace()
        throw exception
      }
    }
    connection
  }

  def createGoalSummary(goalSummary: IO[GoalSummary]): Json = {
    val connection = getConnection
    val stmt =
      s"""INSERT INTO goal_summaries (game_id, scoring_team_name, period_of_goal, time_of_goal, goal_scorer, assist_one, assist_two)
         | VALUES (?, ?, ?, ?, ?, ? ,?)""".stripMargin
    val sql = connection.prepareStatement(stmt)
    val summary = goalSummary.unsafeRunSync()
    sql.setInt(1, summary.gameId)
    sql.setString(2, summary.scoringTeamName)
    sql.setInt(3, summary.periodOfGoal)
    sql.setString(4, summary.timeOfGoal)
    sql.setInt(5, summary.goalScorer)
    sql.setInt(6, summary.assistOne)
    sql.setInt(7, summary.assistTwo)
    sql.execute()
    connection.close()
    summary.asJson
  }

  def endGame(gameId: Int): IO[Response[IO]] = {
    val connection = getConnection
    val stmt = s"""UPDATE games SET is_done=true WHERE game_id=?"""
    val sql = connection.prepareStatement(stmt)
    sql.setInt(1, gameId)
    sql.execute()
    connection.close()
    Ok(null)
  }

  def getSeason(division: String): IO[Response[IO]] = {
    val connection = getConnection
    val sql = connection.prepareStatement(
      s"""SELECT * FROM team_seasons AS s INNER JOIN (SELECT team_name, division FROM teams AS t WHERE division = ?) AS c ON s.team_name=c.team_name"""
    )
    sql.setString(1, division)
    val rs = sql.executeQuery()
    var teamSeasons = Vector[TeamSeason]()

    while (rs.next()) {
      val season: TeamSeason = TeamSeason(
        rs.getString(1),
        rs.getString(2),
        rs.getInt(3),
        rs.getInt(4),
        rs.getInt(5),
        rs.getInt(6),
        rs.getInt(7),
        rs.getInt(8),
        rs.getInt(9),
        rs.getInt(10),
        rs.getString(12)
      )
      teamSeasons = teamSeasons :+ season
    }
    connection.close()
    Ok(Seasons(teamSeasons).asJson)
  }

  def getActiveGames(): IO[Response[IO]] = {
    val connection = getConnection
    val sql = connection.prepareStatement(s"""SELECT * FROM games WHERE is_done=false""")
    val rs = sql.executeQuery()
    var games = List[Game]()

    while (rs.next()) {
      val game: Game = Game(
        Some(rs.getInt(1)),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getString(5),
        rs.getInt(6),
        rs.getString(7),
        rs.getInt(8),
        rs.getBoolean(9),
        rs.getBoolean(10)
      )
      games = game :: games
    }
    connection.close()
    Ok(Games(games).asJson)
  }

  def getGames(): IO[Response[IO]] = {
    val connection = getConnection
    val sql = connection.prepareStatement(s"""SELECT * FROM games""")
    val rs = sql.executeQuery()
    var games = List[Game]()

    while (rs.next()) {
      val game: Game = Game(
        Some(rs.getInt(1)),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getString(5),
        rs.getInt(6),
        rs.getString(7),
        rs.getInt(8),
        rs.getBoolean(9),
        rs.getBoolean(10)
      )
      games = game :: games
    }
    connection.close()
    Ok(Games(games).asJson)
  }

  def getGame(gameId: Int): IO[Response[IO]] = {
    val connection = getConnection
    val stmt = s"""SELECT * FROM games WHERE game_id=?"""
    val sql = connection.prepareStatement(stmt)
    sql.setInt(1, gameId)
    val rs = sql.executeQuery()
    rs.next()

    val returnGame = Game(
      Some(rs.getInt(1)),
      rs.getString(2),
      rs.getString(3),
      rs.getString(4),
      rs.getString(5),
      rs.getInt(6),
      rs.getString(7),
      rs.getInt(8),
      rs.getBoolean(9),
      rs.getBoolean(10)
    )
    connection.close()
    Ok(returnGame.asJson)
  }

  def createGame(createGameRequest: IO[Game]): Json = {
    val connection = getConnection
    val stmt =
      s"""INSERT INTO games (game_year, game_date, game_time, home_team_name, home_team_score, away_team_name, away_team_score, is_done, was_overtime_game) VALUES (?,?,?,?,?,?,?,?,?)""".stripMargin
    val sql = connection.prepareStatement(stmt)
    val game = createGameRequest.unsafeRunSync()
    sql.setString(1, game.year)
    sql.setString(2, game.date)
    sql.setString(3, game.time)
    sql.setString(4, game.homeTeamName)
    sql.setInt(5, game.homeTeamScore)
    sql.setString(6, game.awayTeamName)
    sql.setInt(7, game.awayTeamScore)
    sql.setBoolean(8, game.isDone)
    sql.setBoolean(9, game.wasOvertimeGame)
    sql.execute()
    connection.close()
    game.asJson
  }

  def updateGame(gameId: Int, gameInfo: IO[GameOver]): Unit = {
    val connection = getConnection
    val stmt = s"""UPDATE games SET is_done=TRUE, was_overtime_game=? WHERE game_id=?"""
    val game = gameInfo.unsafeRunSync()
    val sql = connection.prepareStatement(stmt)
    sql.setBoolean(1, game.wasOvertimeGame)
    sql.setInt(2, gameId)
    sql.execute()
    connection.close()
  }

  def deleteGame(gameId: Int): Unit = {
    val connection = getConnection
    val stmt = s"DELETE FROM games WHERE game_id = ?"
    val sql = connection.prepareStatement(stmt)
    sql.setInt(1, gameId)
    sql.execute()
    connection.close()
  }

  def addPlayerToDatabase(player: PlayerInformation): Unit = {
    val connection = getConnection
    val stmt =
      s"""INSERT INTO players (player_id, first_name, last_name, jersey_number, date_of_birth, birth_city, birth_state_province,
         |birth_country, nationality, height, weight, current_team, primary_position, shoots_catches)
         |VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)""".stripMargin
    val sql = connection.prepareStatement(stmt)
    sql.setInt(1, player.id.toInt)
    sql.setString(2, player.firstName)
    sql.setString(3, player.lastName)
    sql.setString(4, player.jerseyNumber)
    sql.setString(5, player.dateOfBirth)
    sql.setString(6, player.birthCity)
    sql.setString(7, player.birthStateProvince)
    sql.setString(8, player.birthCountry)
    sql.setString(9, player.nationality)
    sql.setString(10, player.height)
    sql.setInt(11, player.weight)
    sql.setString(12, player.currentTeam)
    sql.setString(13, player.position)
    sql.setString(14, player.shootsCatches)

    sql.execute()
    connection.close()

  }

  def addPlayerStatline(playerStats: PlayerStatLine, playerId: Long, season: String): Unit = {
    val connection = getConnection
    val stmt =
      s"""INSERT INTO player_stat_lines (season, team_name, player_id, games_played, goals, assists, points, plus_minus,
         |penalty_minutes) VALUES (?,?,?,?,?,?,?,?,?)""".stripMargin
    val sql = connection.prepareStatement(stmt)
    sql.setString(1, season)
    sql.setString(2, playerStats.teamName)
    sql.setInt(3, playerId.toInt)
    sql.setInt(4, playerStats.gamesPlayed)
    sql.setInt(5, playerStats.goals)
    sql.setInt(6, playerStats.assists)
    sql.setInt(7, playerStats. points)
    sql.setInt(8, playerStats.plusMinus)
    sql.setInt(9, playerStats.penaltyMinutes)
    sql.execute()
    connection.close()
  }

  def addTeamSeason(teamSeason: TeamSeason): Unit = {
    val connection = getConnection
    val stmt =
      s"""UPDATE team_seasons SET season_year=?, team_name=?, games_played=?, wins=?, losses=?, overtime_losses=?, points=?, goals_for=?,
         |goals_against=?, goal_differential=? WHERE season_year=? AND team_name=?""".stripMargin
    val sql = connection.prepareStatement(stmt)
    sql.setString(1, teamSeason.seasonYear)
    sql.setString(2, teamSeason.teamId)
    sql.setInt(3, teamSeason.gamesPlayed)
    sql.setInt(4, teamSeason.wins)
    sql.setInt(5, teamSeason.losses)
    sql.setInt(6, teamSeason.overtimeLosses)
    sql.setInt(7, teamSeason.points)
    sql.setInt(8, teamSeason.goalsFor)
    sql.setInt(9, teamSeason.goalsAgainst)
    sql.setInt(10, teamSeason.goalDifferential)

    sql.setString(11, teamSeason.seasonYear)
    sql.setString(12, teamSeason.teamId)
    sql.execute()
    connection.close()
  }



}
