package gui_components

import data_processing.ClubData.Response
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.chart.PieChart
import scalafx.scene.chart.PieChart.Data
import scalafx.scene.control.Tooltip

// Class for pie charts added to the dashboard.
class MyPieChart extends PieChart with MyChart:
  
  // Add initial data to the pie chart.
  private var dataSeq = Seq(Data("data", 10))
  data = dataSeq

  
  // Add the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
  
    // Set fixtures data.
    if dataSet == "Fixtures" then
      dataSeq = Seq(Data("Wins", clubData.wins), Data("Draws", clubData.draws), Data("Losses", clubData.loses))
      
    // Set cards data.
    else if dataSet == "Cards" then
      dataSeq = Seq(Data("Yellow cards", clubData.totalYellows), Data("Red cards", clubData.totalReds))
      
    // Set goals data.
    else
      dataSeq = Seq(Data("Goals scored", clubData.scored), Data("Goals conceded", clubData.conceded))
    data = dataSeq

    // Add tooltip to chart.
    for d <- dataSeq do
      val tooltip = new Tooltip(d.getName + ": " + d.getPieValue.toInt)
      Tooltip.install(d.getNode, tooltip)
      d.getNode.setOnMouseEntered( event =>
        tooltip.show(d.getNode, event.getSceneX, event.getSceneY + 30)
      )
      d.getNode.setOnMouseExited( event =>
        tooltip.hide()
      )

  // Update the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    title = dataSet + " of " + club + " in season " + season










