import cats.effect._
import fs2.{Stream, StreamApp}
import fs2.StreamApp.ExitCode
import cats.implicits._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import java.sql.DriverManager
import java.sql.Connection

import clients.MySqlClient
import io.circe.syntax._
import io.circe.generic.auto._
import models.{Game, GameOver, GoalSummary}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.server.middleware.{CORS, CORSConfig}

import scala.concurrent.ExecutionContext.Implicits.global

object APIServer extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    //implicit def gameEncoder: EntityEncoder[IO, models.Game] = ???
    val driver = "com.mysql.jdbc.Driver"
    val url = "jdbc:mysql://localhost:3306"
    val username = "root"
    val password = "herewegoagain"
    val client = new MySqlClient(url, username, password)

    val gameService = HttpService[IO] {
      case GET -> Root / "standings" / division =>
        client.getSeason(division)
      case GET -> Root / "games" =>
        client.getGames()
      case GET -> Root / "games" / IntVar(gameId) =>
        client.getGame(gameId)
      case GET -> Root / "games" / "active" =>
        client.getActiveGames()
      case req @ POST -> Root / "games" =>
        Ok(client.createGame(req.as[Game]))
      // Next endpoint is used to tell the server that the game is over.
      case req @ PUT -> Root / "games" / IntVar(gameId) =>
        client.updateGame(gameId, req.as[GameOver])
        Ok()
      case DELETE -> Root / "games" / IntVar(gameId) =>
        client.deleteGame(gameId)
        Ok()
      case req @ POST -> Root / "goals" =>
        Ok(client.createGoalSummary(req.as[GoalSummary]))
      //case GET -> Root / "standings" / IntVar(gameId) =>
        //client.endGame(gameId)
    }

    val corsGameService = CORS(gameService)
    //val services = gameService.combineK(helloWorldService)
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(corsGameService,"/")
      .serve
  }
}
