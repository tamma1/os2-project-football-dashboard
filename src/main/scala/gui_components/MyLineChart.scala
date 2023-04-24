package gui_components

import data_processing.ClubData.Response
import javafx.scene.chart as JChart
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.control.Tooltip

class MyLineChart extends LineChart[Number, Number](new NumberAxis(), new NumberAxis()) with MyChart:

  // Create some sample data.
  private val sampleData = ObservableBuffer[JChart.XYChart.Data[Number, Number]](
    XYChart.Data(1.0, 2.0),
    XYChart.Data(2.0, 4.0)
  )

  // Create a series from the sample data and add it to the chart.
  private val sampleSeries = JChart.XYChart.Series[Number, Number]
  sampleSeries.setData(sampleData)
  this.data = sampleSeries

  // Add the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    // Form during the season.
    if dataSet == "Fixtures" then
      // Sets x- and y-axis names.
      this.getXAxis.setLabel("Game number")
      this.getYAxis.setLabel("Points earned in game")
      // Set data.
      val fixtures = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (c, i) <- clubData.formToPoints.zipWithIndex do
        fixtures += XYChart.Data(i + 1, c)
      // Adds new data to chart.
      val fixtureSeries = JChart.XYChart.Series[Number, Number]
      fixtureSeries.setData(fixtures)
      fixtureSeries.setName("Points")
      this.data = fixtureSeries

    // Cards received by minute.
    else if dataSet == "Cards" then
      // Set axis names.
      this.getXAxis.setLabel("Time (end of 15 minute interval)")
      this.getYAxis.setLabel("Cards received")
      // Set data for yellow and red cards.
      val yellowCards = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (x, i) <- clubData.yellowByMinute.zipWithIndex do
        yellowCards += XYChart.Data((i + 1) * 15, x)
      val redCards = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (r, i) <- clubData.redByMinute.zipWithIndex do
        redCards += XYChart.Data((i + 1) * 15, r)
      // Create yellow and red card series, and add them to chart.
      val yellowSeries = new JChart.XYChart.Series[Number, Number]
      val redSeries = new JChart.XYChart.Series[Number, Number]
      this.data = Seq(redSeries, yellowSeries)
      yellowSeries.setData(yellowCards)
      yellowSeries.setName("Yellow cards")
      redSeries.setData(redCards)
      redSeries.setName("Red cards")

    // Goals scored by minute.
    else
      // Set axis names.
      this.getXAxis.setLabel("Time (end of 15 minute interval)")
      this.getYAxis.setLabel("Goals")
      // Set scored and conceded goals data.
      val goalsScored = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (x, i) <- clubData.scoredByMinute.zipWithIndex do
        goalsScored += XYChart.Data((i + 1) * 15, x)
      val goalsConceded = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (g, i) <- clubData.concededByMinute.zipWithIndex do
        goalsConceded += XYChart.Data((i + 1) * 15, g)
      // Create scored and conceded goals series, and add them to chart.
      val scoredSeries = JChart.XYChart.Series[Number, Number]
      scoredSeries.setData(goalsScored)
      scoredSeries.setName("Goals scored")
      val concededSeries = JChart.XYChart.Series[Number, Number]
      concededSeries.setData(goalsConceded)
      concededSeries.setName("Goals conceded")
      this.data = Seq(scoredSeries, concededSeries)

    // Add tooltip to chart.
    for s <- this.data.value do
      for d <- s.getData do
        val tooltip = new Tooltip(
          this.getXAxis.getLabel + ": " + d.getXValue.intValue() + "\n" + s.getName + ": " + d.getYValue.intValue()
        )
        Tooltip.install(d.getNode: scalafx.scene.Node, tooltip)
        d.getNode.setOnMouseEntered(event =>
          tooltip.show(d.getNode, event.getSceneX, event.getSceneY + 30)
        )
        d.getNode.setOnMouseExited(event =>
          tooltip.hide()
        )


  // Update the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    if dataSet == "Fixtures" then
      title = "Form of " + club + " in season " + season
    else if dataSet == "Cards" then
      title = "Cards by minute for " + club + " in season " + season
    else
      title = "Goals by minute for " + club + " in season " + season

