package data_processing

import APIConnection.*

// Object for processing league data.
object LeagueData:

  // Retrieve data from a league in a specific season and return a Response case class containing the data.
  def getLeagueData(leagueID: Int, season: Int) =
    val url = s"https://api-football-v1.p.rapidapi.com/v3/teams?league=${leagueID}&season=${season}"
    val data = fetch(url)
    decodeTeams(data)

  // Store the data received from the API.
  case class Response(private val initial: InitialResponse):
    val teams = initial.response.map( t => (t.team.name, t.team.id) ).toMap
    val results = initial.results

  // Some case classes used to transform the JSON into a case class.
  case class InitialResponse(
                            get: String,
                            parameters: Parameters,
                            errors: Array[String],
                            results: Int,
                            paging: Paging,
                            response: Array[InitialTeams]
                            )
   
  case class Team(
                 id: Int,
                 name: String,
                 code: String,
                 country: String,
                 founded: Int,
                 national: Boolean,
                 logo: String
                 )

  case class Venue(
                  id: Int,
                  name: String,
                  address: String,
                  city: String,
                  capacity: Int,
                  surface: String,
                  image: String
                  )

  case class InitialTeams(team: Team, venue: Venue)

  case class Parameters(league: String, season: String)

  case class Paging(current: Int, total: Int)


  // Map of leageue names and their IDs.
  val leagueMap: Map[String, Int] = Map(
    "Premier League" -> 39,
    "La Liga" -> 140,
    "Bundesliga" -> 78,
    "Serie A" -> 135,
    "Ligue 1" -> 61
  )

  // Map of seasons.
  val seasonMap: Map[String, Int] = Map(
    "2015-2016" -> 2015,
    "2016-2017" -> 2016,
    "2017-2018" -> 2017,
    "2018-2019" -> 2018,
    "2019-2020" -> 2019,
    "2020-2021" -> 2020,
    "2021-2022" -> 2021,
    "2022-2023" -> 2022,
    "2023-2024" -> 2023
  )

