package gui_components

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.Includes._
import javafx.scene.chart as JChart

class MyLineChart extends LineChart[Number, Number](new NumberAxis(), new NumberAxis()):
  
  // Create some sample data.
  val dataBuf = ObservableBuffer[JChart.XYChart.Data[Number, Number]](
    XYChart.Data(1.0, 2.0),
    XYChart.Data(2.0, 4.0),
    XYChart.Data(3.0, 6.0),
    XYChart.Data(4.0, 8.0),
    XYChart.Data(5.0, 10.0)
  )

  // Create a series from the data and adds it to the chart.
  private val series = new XYChart.Series[Number, Number] {
    name = "Sample Data"
    data = dataBuf
  }
  this.getData.add(series)

