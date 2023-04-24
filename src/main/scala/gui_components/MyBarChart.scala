package gui_components

import data_processing.ClubData.Response
import javafx.scene.chart as JChart
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}
import scalafx.scene.control.Tooltip

// Class for bar charts added to the dashboard.
class MyBarChart extends BarChart[String, Number](new CategoryAxis(), new NumberAxis()) with MyChart:

  // Set animated to false because the animated chart doesn't work properly.
  animated = false

  // Add some sample data.
  private var dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](XYChart.Data("data", 1.0))

  // Create a series from the sample data and add it to the chart.
  private val series = new XYChart.Series[String, Number] { data = dataBuf }
  this.data = series

  // Add the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =

    // Fixtures data
    if dataSet == "Fixtures" then
      this.getYAxis.setLabel("Games")
      dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
        XYChart.Data("Wins", clubData.wins),
        XYChart.Data("Draws", clubData.draws),
        XYChart.Data("Losses", clubData.loses),
      )
    // Cards data
    else if dataSet == "Cards" then
      this.getYAxis.setLabel("Cards received")
      dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
        XYChart.Data("Yellow cards", clubData.totalYellows),
        XYChart.Data("Red cards", clubData.totalReds)
      )
    // Goals data
    else
      this.getYAxis.setLabel("Goals")
      dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
        XYChart.Data("Scored", clubData.scored),
        XYChart.Data("Conceded", clubData.conceded)
      )
    // Add new data to chart.
    series.data = dataBuf
    this.data = series

    // Add tooltip to chart.
    for d <- dataBuf do
      val tooltip = new Tooltip(d.XValue.value + ": " + d.YValue.value.intValue())
      Tooltip.install(d.getNode: scalafx.scene.Node, tooltip)
      d.getNode.setOnMouseEntered(event =>
        tooltip.show(d.getNode, event.getSceneX, event.getSceneY + 30)
      )
      d.getNode.setOnMouseExited(event =>
        tooltip.hide()
      )

  // Update the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    title = dataSet + " of " + club + " in season " + season





