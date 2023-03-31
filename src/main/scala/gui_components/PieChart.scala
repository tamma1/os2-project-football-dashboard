package gui_components

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.chart.PieChart

// Class for pie charts added to the dashboard.
class MyPieChart extends PieChart:
  
  // Adds data to the pie chart.
  val dataSeq = Seq(PieChart.Data("test1", 10), PieChart.Data("test2", 20))
  data = dataSeq
