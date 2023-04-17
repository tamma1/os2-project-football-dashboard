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

  // Create a series from the data and adds it to the chart.
  private val series = new XYChart.Series[Number, Number] { data = dataBuf }
  this.data = series

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    dataBuf = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
    // Form during the season.
    if dataSet == "Fixtures" then
      // Convertt form to points.
      val formToPoints = clubData.form.map( c =>
        if c == 'W' then 3
        else if c == 'D' then 1
        else 0)
      // Stores the data points.
      val formData =
        var count = 0
        for (c, i) <- formToPoints.zipWithIndex do
          count = count + c
          dataBuf += XYChart.Data(i + 1, count)
    // Goals scored by minute.
    else
      for (x, i) <- clubData.goalsByMinute.zipWithIndex do
        dataBuf += XYChart.Data((i + 1) * 15, x)

    // Add new data.
    series.data = dataBuf
    this.data = series

  // Updates the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    if dataSet == "Fixtures" then
      title = "Form of " + club + " in season " + season
    else
      title = "Goals scored by minute for " + club + " in season " + season

