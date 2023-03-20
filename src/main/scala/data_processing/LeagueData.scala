package data_processing

import APIConnection.*

object LeagueData:

  // Receives data from a league in a specific season and returns a Response case class containing the data.
  def getLeagueData(leagueID: Int, season: Int) =
    val url = s"https://api-football-v1.p.rapidapi.com/v3/teams?league=${leagueID}&season=${season}"
    val data = fetch(url)
    decodeTeams(data)

  // Stores the data received from the API.
  case class Response(private val initial: InitialResponse):
    val teams = initial.response.map(t => t.team.name)
    val results = initial.results

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

  case class Errors(time: String, bug: String, report: String)

  case class InitialTeams(team: Team, venue: Venue)

  case class Parameters(league: String, season: String)

  case class Paging(current: Int, total: Int)

  // Case class used by the APIConnection.decodeTeams function.
  case class InitialResponse(
                            get: String,
                            parameters: Parameters,
                            errors: Array[String],
                            results: Int,
                            paging: Paging,
                            response: Array[InitialTeams]
                            )


