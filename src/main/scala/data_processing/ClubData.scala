package data_processing

import APIConnection.*
import scala.math.*

// Object for processing club data.
object ClubData:
  
  // Retrieve statistics of a given club.
  def getClubData(leagueID: Int, season: Int, teamID: Int) =
    val url = s"https://api-football-v1.p.rapidapi.com/v3/teams/statistics?league=${leagueID}&season=${season}&team=${teamID}"
    val data = fetch(url)
    
    // Transform some fields in the JSON to fit the case class structure.
    val transformFor = data.replace("\"for\"", "\"forClub\"")
    val transformNumbers = transformFor.replaceAll("(\\d+)-(\\d+)", "m$1")
    
    // Transform the retrieved data into a Response case class.
    decodeTeamStats(transformNumbers)

  // Case class for handling club statistics
  case class Response(private val initial: InitialResponse):

    // Method for rounding doubles.
    private def rounded(num: Double): Double =
      BigDecimal(num).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

    // Number of results in the response.
    val results = initial.results

    private val response = initial.response
    
    // Wins, draws, loses and total matches played.
    private val fixtures = response.fixtures
    val wins = fixtures.wins.total
    val draws = fixtures.draws.total
    val loses = fixtures.loses.total
    val played = fixtures.played.total

    
    // Calculations for fixtures.
    private val biggest = response.biggest
    
    private val form = response.form
    val formToPoints = form.map( c =>
        if c == 'W' then 3
        else if c == 'D' then 1
        else 0)
    val totalPoints = formToPoints.sum
    
    private val averagePointsPerGame = totalPoints.toDouble / played
    val averagePointsRounded = rounded(averagePointsPerGame)
    
    val longestWinStreak = biggest.streak.wins
    val longestLosingStreak = biggest.streak.loses
    
    private val pointsStandardDeviation =
      val dividend = formToPoints.map( x => pow(x - averagePointsPerGame, 2) ).sum
      sqrt(dividend / played)
    val pointsStandardDeviationRounded = rounded(pointsStandardDeviation)

    
    // Calculations for goals.
    private val goals = response.goals
    
    val scored = goals.forClub.total.total
    val conceded = goals.against.total.total
    
    private val scoredMinuteList = goals.forClub.minute
      .productIterator.map(_.asInstanceOf[MinuteStats]).toList
    val scoredByMinute = scoredMinuteList.map( _.total.getOrElse(0) )
    
    private val concededMinuteList = goals.against.minute
      .productIterator.map(_.asInstanceOf[MinuteStats]).toList
    val concededByMinute = concededMinuteList.map( _.total.getOrElse(0) )
    
    val averageGoalsPerGame = goals.forClub.average.total
    val averageConcededPerGame = goals.against.average.total
    
    private val mostGoalsHome = biggest.goals.forClub.home
    private val mostGoalsAway = biggest.goals.forClub.away
    val mostGoalsInAGame = if mostGoalsAway > mostGoalsHome then mostGoalsAway else mostGoalsHome
    
    private val mostGoalsConcededHome = biggest.goals.against.home
    private val mostGoalsConcededAway = biggest.goals.against.away
    val mostGoalsConcededInAGame = if mostGoalsConcededAway > mostGoalsConcededHome then mostGoalsConcededAway else mostGoalsConcededHome
    
    val mostGoalsInInterval = scoredByMinute.max
    
    private val mostGoalsIndex = scoredByMinute.indexOf(mostGoalsInInterval)
    val intervalWithMostGoals =
      val firstNumber = if mostGoalsIndex == 0 then "0" else (mostGoalsIndex * 15 + 1).toString
      val secondNumber = ((mostGoalsIndex + 1) * 15).toString
      firstNumber + "-" + secondNumber
    val mostGoalsPercentage =
      val percentageList = scoredMinuteList.map( _.percentage.getOrElse("0.0%") )
      percentageList(mostGoalsIndex)

    
    // Calculations for cards.
    private val cards = response.cards
    
    private val yellowList = cards.yellow.productIterator.map( _.asInstanceOf[MinuteStats] ).toList
    val yellowByMinute = yellowList.map( _.total.getOrElse(0) )
    val totalYellows = yellowByMinute.sum
    private val yellowsPerGame = totalYellows.toDouble / played
    val yellowsPerGameRounded = rounded(yellowsPerGame)
    
    private val redList = cards.red.productIterator.map( _.asInstanceOf[MinuteStats] ).toList
    val redByMinute = redList.map( _.total.getOrElse(0) )
    val totalReds = redByMinute.sum
    private val redsPerGame = totalReds.toDouble / played
    val redsPerGameRounded = rounded(redsPerGame)
    
    val totalCards = totalReds + totalYellows
    val averageCardsPerGame = rounded(totalCards.toDouble / played)
    
    private val cardList = yellowByMinute.zip(redByMinute).map( (y, r) => y + r)
    val mostCardsInInterval = cardList.max
    private val mostCardsIndex = cardList.indexOf(mostCardsInInterval)
    val intervalWithMostCards =
      val firstNum = if mostCardsIndex == 0 then "0" else (mostCardsIndex * 15 + 1).toString
      val secondNum = ((mostCardsIndex + 1) * 15).toString
      firstNum + "-" + secondNum
    val mostCardsPercentage = 
      val percentage = (mostCardsInInterval.toDouble / totalCards) * 100
      rounded(percentage).toString + "%"
      

  // Some case classes used to transform the JSON into a case class.
  case class InitialResponse(
                            get: String,
                            parameters: Parameters,
                            errors: Array[String],
                            results: Int,
                            paging: LeagueData.Paging,
                            response: InitialStats
                            )
  
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

  


