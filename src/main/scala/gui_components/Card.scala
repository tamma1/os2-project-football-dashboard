package gui_components

import scalafx.scene.layout.*
import scalafx.scene.control.Label
import data_processing.ClubData.Response
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.paint.Color.*


// Class for displaying additional data in the chart boxes.
class Card extends VBox:

  padding = Insets(2, 2, 2, 2)
  spacing = 3
  children += MyLabel("Additional data is displayed here")
  border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))

  def updateText(clubData: Response, dataSet: String, chart: String) =
    this.children = ObservableBuffer()
    // Additional text for fixtures.
    if dataSet == "Fixtures" then
      val total = MyLabel("Total games played: " + clubData.played)
      val wins = MyLabel("Wins: " + clubData.wins)
      val draws = MyLabel("Draws: " + clubData.draws)
      val losses = MyLabel("Losses: " + clubData.loses)
      val losingStreak = MyLabel("Longest losing streak: " + clubData.longestLosingStreak)
      val winStreak = MyLabel("Longest winning streak: " + clubData.longestWinStreak)
      val totalPoints = MyLabel("Total points: " + clubData.totalPoints)
      val averagePoints = MyLabel("Average points per game: " + clubData.averagePointsRounded)
      this.children.addAll(total, wins, draws, losses, losingStreak, winStreak, totalPoints, averagePoints)
      // Additional text for line chart.
      if chart == "Line Chart" then
        val standardDeviation = MyLabel("Standard deviation: " + clubData.pointsStandardDeviationRounded)
        this.children += standardDeviation

    // Additional text for cards.
    else if dataSet == "Cards" then
      val totalCards = MyLabel("Total cards received: " + clubData.totalCards)
      val averageYellow = MyLabel("Average yellow cards per game: " + clubData.yellowsPerGameRounded)
      val averageRed = MyLabel("Average red cards per game: " + clubData.redsPerGameRounded)
      val averageTotal = MyLabel("Average cards received per game: " + clubData.averageCardsPerGame)
      this.children.addAll(totalCards, averageYellow, averageRed, averageTotal)
      // Additional text for line chart.
      if chart == "Line Chart" then
        val intervalWithMostCards = MyLabel("Interval with most cards received: " + clubData.intervalWithMostCards + "min")
        val cardsInThatInterval = MyLabel("Cards received in that interval: " + clubData.mostCardsInInterval)
        val percentage = MyLabel("Percentage of cards received in that interval: " + clubData.mostCardsPercentage)
        this.children.addAll(intervalWithMostCards, cardsInThatInterval, percentage)

    // Additional text for goals.
    else
      val scored = MyLabel("Goals scored: " + clubData.scored)
      val conceded = MyLabel("Goals conceded: " + clubData.conceded)
      val goalsPerGame = MyLabel("Average goals scored per game: " + clubData.averageGoalsPerGame)
      val concededPerGame = MyLabel("Average goals conceded per game: " + clubData.averageConcededPerGame)
      val mostGoalsScored = MyLabel("Most goals scored in a game: " + clubData.mostGoalsInAGame)
      val mostGoalsConceded = MyLabel("Most goals conceded in a game: " + clubData.mostGoalsConcededInAGame)
      this.children.addAll(scored, conceded, goalsPerGame, concededPerGame, mostGoalsScored, mostGoalsConceded)
      // Additional text for line chart.
      if chart == "Line Chart" then
        val intervalWithMostGoals = MyLabel("Interval with most goals scored: " + clubData.intervalWithMostGoals + " min")
        val goalsInThatInterval = MyLabel("Goals scored in that interval: " + clubData.mostGoalsInInterval)
        val percentage = MyLabel("Percentage of goals scored in that interval: " + clubData.mostGoalsPercentage)
        this.children.addAll(intervalWithMostGoals, goalsInThatInterval, percentage)


  // Class for setting some properties of the labels added to this card.
  class MyLabel(text: String) extends Label(text):
    maxWidth = 215
    wrapText = true
    style = "-fx-font-size: 9pt;"
    background = new Background(Array(new BackgroundFill(LightGray, CornerRadii.Empty, Insets.Empty)))





