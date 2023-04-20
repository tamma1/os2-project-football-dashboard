package data_processing

import APIConnection.*
import scala.math.*

// Object for processing club data.
object ClubData:
  // Retrieves statistics of a given club.
  def getClubData(leagueID: Int, season: Int, teamID: Int) =
    val url = s"https://api-football-v1.p.rapidapi.com/v3/teams/statistics?league=${leagueID}&season=${season}&team=${teamID}"
    val data = fetch(url)
    // Transforms some fields in the JSON to fit the case class structure.
    val transformFor = data.replace("\"for\"", "\"forClub\"")
    val transformNumbers = transformFor.replaceAll("(\\d+)-(\\d+)", "m$1")
    // Transforms the retrieved data into a Response case class.
    decodeTeamStats(transformNumbers)

  // Case class for handling club statistics
  case class Response(private val initial: InitialResponse):
    // Number of results in the response.
    val results = initial.results

    // Wins, draws, loses and total matches played.
    val wins = initial.response.fixtures.wins.total
    val draws = initial.response.fixtures.draws.total
    val loses = initial.response.fixtures.loses.total
    val played = initial.response.fixtures.played.total

    // Calculations for fixtures.
    val form = initial.response.form
    val formToPoints = form.map( c =>
        if c == 'W' then 3
        else if c == 'D' then 1
        else 0)
    val totalPoints = formToPoints.sum
    private val averagePointsPerGame = totalPoints.toDouble / played
    val averagePointsRounded = BigDecimal(averagePointsPerGame).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
    val longestWinStreak = initial.response.biggest.streak.wins
    val longestLosingStreak = initial.response.biggest.streak.loses
    private val pointsStandardDeviation =
      val dividend = formToPoints.map( x => pow(x - averagePointsPerGame, 2) ).sum
      sqrt(dividend / played)
    val pointsStandardDeviationRounded = BigDecimal(pointsStandardDeviation).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    // Calculations for goals.
    val scored = initial.response.goals.forClub.total.total
    val conceded = initial.response.goals.against.total.total
    private val minuteList = initial.response.goals.forClub.minute
      .productIterator.map(_.asInstanceOf[MinuteStats]).toList
    val goalsByMinute = minuteList.map( _.total.getOrElse(0) )
    val averageGoalsPerGame = initial.response.goals.forClub.average.total
    val averageConcededPerGame = initial.response.goals.against.average.total
    private val mostGoalsHome = initial.response.biggest.goals.forClub.home
    private val mostGoalsAway = initial.response.biggest.goals.forClub.away
    val mostGoalsInAGame = if mostGoalsAway > mostGoalsHome then mostGoalsAway else mostGoalsHome
    private val mostGoalsConcededHome = initial.response.biggest.goals.against.home
    private val mostGoalsConcededAway = initial.response.biggest.goals.against.away
    val mostGoalsConcededInAGame = if mostGoalsConcededAway > mostGoalsConcededHome then mostGoalsConcededAway else mostGoalsConcededHome
    val mostGoalsInInterval = goalsByMinute.max
    private val mostGoalsIndex = goalsByMinute.indexOf(mostGoalsInInterval)
    val intervalWithMostGoals =
      val firstNumber = if mostGoalsIndex == 0 then "0" else (mostGoalsIndex * 15 + 1).toString
      val secondNumber = ((mostGoalsIndex + 1) * 15).toString
      firstNumber + "-" + secondNumber
    val mostGoalsPercentage =
      val percentageList = minuteList.map( _.percentage.getOrElse("0.0%") )
      percentageList(mostGoalsIndex)



  // Some case classes used to transform the JSON into a case class.
  case class Parameters(
                       league: String,
                       season: String,
                       team: String
                       )

  case class InitialStats(
                         league: League,
                         team: Team,
                         form: String,
                         fixtures: Fixtures,
                         goals: Goals,
                         biggest: Biggest,
                         clean_sheet: Matches,
                         failed_to_score: Matches,
                         penalty: Penalty,
                         lineups: Array[Lineup],
                         cards: Cards
                         )

  case class League(
                   id: Int,
                   name: String,
                   country: String,
                   logo: String,
                   flag: String,
                   season: Int
                   )

  case class Team(
                 id: Int,
                 name: String,
                 logo: String
                 )

  case class Fixtures(
                     played: Matches,
                     wins: Matches,
                     draws: Matches,
                     loses: Matches
                     )

  case class Matches(
                    home: Int,
                    away: Int,
                    total: Int
                    )

  case class Goals(
                  forClub: Scored,
                  against: Scored
                  )

  case class Scored(
                   total: Matches,
                   average: Average,
                   minute: Minute
                   )

  case class Average(
                    home: String,
                    away: String,
                    total: String
                    )

  case class Minute(
                   m0: MinuteStats,
                   m16: MinuteStats,
                   m31: MinuteStats,
                   m46: MinuteStats,
                   m61: MinuteStats,
                   m76: MinuteStats,
                   m91: MinuteStats,
                   m106: MinuteStats
                   )

  case class MinuteStats(
                        total: Option[Int],
                        percentage: Option[String]
                        )

  case class Biggest(
                    streak: Streak,
                    wins: BiggestMatches,
                    loses: BiggestMatches,
                    goals: BiggestGoals
                    )

  case class Streak(
                   wins: Int,
                   draws: Int,
                   loses: Int
                   )

  case class BiggestMatches(
                           home: Option[String],
                           away: Option[String]
                           )

  case class BiggestGoals(
                         forClub: HomeAway,
                         against: HomeAway
                         )

  case class HomeAway(
                     home: Int,
                     away: Int
                     )

  case class Penalty(
                    scored: MinuteStats,
                    missed: MinuteStats,
                    total: Int
                    )

  case class Lineup(
                   formation: String,
                   played: Int
                   )

  case class Cards(
                  yellow: Minute,
                  red: Minute
                  )

  // Case class used by the APIConnection.decodeTeamStats function.
  case class InitialResponse(
                            get: String,
                            parameters: Parameters,
                            errors: Array[String],
                            results: Int,
                            paging: LeagueData.Paging,
                            response: InitialStats
                            )


