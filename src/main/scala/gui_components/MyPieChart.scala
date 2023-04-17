package gui_components

import data_processing.ClubData.Response
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.chart.PieChart
import scalafx.scene.chart.PieChart.Data

// Class for pie charts added to the dashboard.
class MyPieChart extends PieChart with MyChart:
  
  // Adds initial data to the pie chart.
  private var dataSeq = Seq(Data("data", 10))
  data = dataSeq

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    if dataSet == "Fixtures" then
      dataSeq = Seq(Data("Wins", clubData.wins), Data("Draws", clubData.draws), Data("Loses", clubData.loses))
    else
      dataSeq = Seq(Data("Goals scored", clubData.scored), Data("Goals conceded", clubData.conceded))
    data = dataSeq



