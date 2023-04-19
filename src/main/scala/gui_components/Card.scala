package gui_components

import scalafx.scene.layout.VBox
import scalafx.scene.control.Label
import data_processing.ClubData.Response
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets

// Class for displaying additional data in the chart boxes.
class Card extends VBox:

  padding = Insets(2, 2, 2, 2)
  spacing = 3
  children += Label("Additional data is displayed here.")

  def updateText(clubData: Response, dataSet: String, chart: String) =
    this.children = ObservableBuffer()
    // Additional text for fixtures.
    if dataSet == "Fixtures" then
      val total = Label("Total games played: " + clubData.played)
      val wins = Label("Wins: " + clubData.wins)
      val draws = Label("Draws: " + clubData.draws)
      val loses = Label("Losses: " + clubData.loses)
      val losingStreak = Label("Longest losing streak: " + clubData.longestLosingStreak)
      val winStreak = Label("Longest winning streak: " + clubData.longestWinStreak)
      val totalPoints = Label("Total points: " + clubData.totalPoints)
      val averagePoints = Label("Average points per game: " + clubData.averagePointsRounded)
      this.children.addAll(total, wins, draws, loses, losingStreak, winStreak, totalPoints, averagePoints)
      // Additional text for line chart.
      if chart == "Line Chart" then
        val standardDeviation = Label("Standard deviation: " + clubData.pointsStandardDeviationRounded)
        this.children += standardDeviation
    else
      val goalsPerGame = Label("Average goals per game: " + clubData.averageGoalsPerGame)
      val mostGoalsScored = Label("Most goals scored in a game: " + clubData.mostGoalsInAGame)
      val mostGoalsConceded = Label("Most goals concede in a game: " + clubData.mostGoalsConcededInAGame)
      this.children.addAll(goalsPerGame, mostGoalsScored, mostGoalsConceded)





