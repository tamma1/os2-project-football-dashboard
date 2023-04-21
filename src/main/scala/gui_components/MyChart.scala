package gui_components

import scalafx.scene.chart.Chart
import data_processing.ClubData.Response
import scalafx.geometry.Insets
import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.scene.paint.Color.*

// Trait for updating different chart types.
trait MyChart extends Chart:

  // Sets title and style for chart.
  title = "Selected chart is displayed here"
  style = "-fx-font-size: 8pt;"
  background = Background(Array(new BackgroundFill(PaleGreen, CornerRadii.Empty, Insets.Empty)))

  // Updates the data of a chart.
  def updateData(clubData: Response, dataSet: String): Unit

  // Updates the title of a chart.
  def updateTitle(club: String, season: String, dataSet: String): Unit

