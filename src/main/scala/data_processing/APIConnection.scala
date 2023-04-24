package data_processing

import data_processing.ClubData.{InitialResponse as ClubInitialResponse, Response as ClubResponse}
import data_processing.LeagueData.{InitialResponse as LeagueInitialResponse, Response as LeagueResponse}
import io.circe.*
import io.circe.generic.auto.*
import io.circe.parser.*
import io.circe.syntax.*
import requests.*
import scala.io.Source
import scala.util.{Failure, Success, Try}
import file_handling.FileManagerException

object APIConnection:

  // Fetches and provides the API key.
  private lazy val apiKey: String = {
    val apiKeyFile = "apiKey.txt"
    val source = Source.fromFile(apiKeyFile)
    val key = source.getLines().mkString
    source.close()
    key
  }

  // Calculates the number of calls to web API.
  private var callTimes = 0

  // Ensures the call limit on web API doesn't exceed.
  private def apiCallCounter() =
    callTimes += 1
    if callTimes > 290 then
      throw new FileManagerException("Too many calls to API")

  // Fetches data from the API.
  def fetch(url: String): String =
    apiCallCounter()
    val res = Try {
      requests.get(url, headers = List(("X-RapidAPI-Key", apiKey), ("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")))
      .text()
    }
    res match
      case Success(data) => data
      case Failure(e)    =>
        Console.err.println("Connection failed.")
        throw new FileManagerException("Connection failed")

  // Decodes the data received into a Response object.
  def decodeTeams(data: String) =
    decode[LeagueInitialResponse](data).toTry match
      case Success(result) => LeagueResponse(result)
      case Failure(error) =>
        Console.err.println("Invalid data received.")
        error.printStackTrace()
          throw new FileManagerException("Invalid data received")

  // Decode team statistics data.
  def decodeTeamStats(data: String) =
    decode[ClubInitialResponse](data).toTry match
      case Success(result) => ClubResponse(result)
      case Failure(error) =>
        Console.err.println("Invalid data received.")
        error.printStackTrace()
          throw new FileManagerException("Invalid data received")
