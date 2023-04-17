package gui_components

import data_processing.ClubData.Response
import javafx.scene.chart as JChart
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}

// Class for bar charts added to the dashboard.
class MyBarChart extends BarChart[String, Number](new CategoryAxis(), new NumberAxis()) with MyChart:

  // Set animated to false because the animated chart doesn't work properly.
  animated = false

  // Adds some data.
  private var dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
    XYChart.Data("data", 1.0),
  )

  // Creates a series from the data and adds it to the chart.
  private val series = new XYChart.Series[String, Number] { data = dataBuf }
  this.data = series

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    // Fixtures data
    if dataSet == "Fixtures" then
      dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
        XYChart.Data("wins", clubData.wins),
        XYChart.Data("draws", clubData.draws),
        XYChart.Data("loses", clubData.loses),
        XYChart.Data("played", clubData.played)
      )
    // Goals data
    else
      dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
        XYChart.Data("scored", clubData.scored),
        XYChart.Data("conceded", clubData.conceded)
      )
    // Adds new data.
    series.data = dataBuf
    this.data = series
    
  // Updates the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    title = dataSet + " of " + club + " in season " + season





