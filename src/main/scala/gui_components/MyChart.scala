package gui_components

import scalafx.scene.chart.Chart
import data_processing.ClubData.Response

// Trait for updating different chart types.
trait MyChart extends Chart:

  // Sets title and style for chart.
  title = "Selected data is displayed here"
  style = "-fx-font-size: 8pt;"

  // Updates the data of a chart.
  def updateData(clubData: Response, dataSet: String): Unit

  // Updates the title of a chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    title = dataSet + " of " + club + " in season " + season

