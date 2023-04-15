package gui_components

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.chart.PieChart
import scalafx.scene.chart.PieChart.Data
import data_processing.ClubData.*

// Class for pie charts added to the dashboard.
class MyPieChart extends PieChart with MyChart:
  
  // Adds data to the pie chart.
  private var dataSeq = Seq(Data("test1", 10), Data("test2", 20))
  data = dataSeq

  def updateData(leagueID: Int, seasonID: Int, clubID: Int, dataSet: String) =
    val newData = getClubData(leagueID, seasonID, clubID)
    if dataSet == "Fixtures" then
      dataSeq = Seq(Data("Wins", newData.wins), Data("Draws", newData.draws), Data("Loses", newData.loses))
    else
      dataSeq = Seq(Data("Goals scored", newData.scored), Data("Goals conceded", newData.conceded))
    data = dataSeq

