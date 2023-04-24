package gui_components

import data_processing.ClubData.Response
import scalafx.geometry.Insets
import scalafx.scene.chart.Chart
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.scene.paint.Color.PaleGreen

// Trait for updating different chart types.
trait MyChart extends Chart:

  // Set title, style and backround for chart.
  title = "Selected chart is displayed here"
  style = "-fx-font-size: 8pt;"
  background = Background(Array(new BackgroundFill(PaleGreen, CornerRadii.Empty, Insets.Empty)))

  // Update the data of a chart.
  def updateData(clubData: Response, dataSet: String): Unit

  // Update the title of a chart.
  def updateTitle(club: String, season: String, dataSet: String): Unit

