package data_processing

import scala.io.Source
import scala.util.{Failure, Success, Try}
import requests.*
import io.circe.parser.decode
import io.circe.*, io.circe.parser.*, io.circe.syntax.*, io.circe.generic.auto.*
import data_processing.LeagueData.{InitialResponse => LeagueInitialResponse}
import data_processing.LeagueData.{Response => LeagueResponse}

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
      throw new Exception("Too many calls to API")

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
        throw e

  // Decodes the data received into a Response object.
  def decodeTeams(data: String) =
    decode[LeagueInitialResponse](data).toTry match
      case Success(result) => LeagueResponse(result)
      case Failure(error) =>
        Console.err.println("Invalid data received.")
        error.printStackTrace()
          throw error