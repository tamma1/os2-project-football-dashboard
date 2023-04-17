package gui_components

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{CategoryAxis, NumberAxis, BarChart, XYChart}
import scalafx.Includes._
import javafx.scene.chart as JChart
import data_processing.ClubData.Response

// Class for bar charts added to the dashboard.
class MyBarChart extends BarChart[String, Number](new CategoryAxis(), new NumberAxis()) with MyChart:

  // Adds some data.
  val dataBuf = ObservableBuffer[JChart.XYChart.Data[String, Number]](
    XYChart.Data("A", 1.0),
    XYChart.Data("B", 2.0),
    XYChart.Data("C", 3.0),
    XYChart.Data("D", 4.0)
  )

  // Creates a series from the data and adds it to the chart.
  private val series = new XYChart.Series[String, Number] { data = dataBuf }
  this.getData.add(series)

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) = ???



