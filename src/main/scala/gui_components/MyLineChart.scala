package gui_components

import data_processing.ClubData.Response
import javafx.scene.chart as JChart
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}

class MyLineChart extends LineChart[Number, Number](new NumberAxis(), new NumberAxis()) with MyChart:

  // Create some sample data.
  private var dataBuf = ObservableBuffer[JChart.XYChart.Data[Number, Number]](
    XYChart.Data(1.0, 2.0),
    XYChart.Data(2.0, 4.0)
  )

  // Creates a series from the sample data and adds it to the chart.
  private val series = new XYChart.Series[Number, Number] { data = dataBuf }
  this.data = series

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    dataBuf = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()

    // Form during the season.
    if dataSet == "Fixtures" then
      // Sets x- and y-axis names.
      this.getXAxis.setLabel("Games played")
      this.getYAxis.setLabel("Points per game")
      // Sets data.
      val form = clubData.formToPoints
      val formData =
        for (c, i) <- form.zipWithIndex do
          dataBuf += XYChart.Data(i + 1, c)

    // Goals scored by minute.
    else
      // Sets x- and y-axis names.
      this.getXAxis.setLabel("Time (end of 15 minute interval)")
      this.getYAxis.setLabel("Goals scored")
      // Sets data.
      for (x, i) <- clubData.goalsByMinute.zipWithIndex do
        dataBuf += XYChart.Data((i + 1) * 15, x)

    // Adds new data.
    series.data = dataBuf
    this.data = series

  // Updates the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    if dataSet == "Fixtures" then
      title = "Form of " + club + " in season " + season
    else
      title = "Goals scored by minute for " + club + " in season " + season

