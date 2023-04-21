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

  // Creates a series from the sample data and adds it to the chart.
  private val sampleSeries = JChart.XYChart.Series[Number, Number]
  sampleSeries.setData(sampleData)
  this.data = sampleSeries

  // Adds the selected data to the chart.
  def updateData(clubData: Response, dataSet: String) =
    // Form during the season.
    if dataSet == "Fixtures" then
      // Sets x- and y-axis names.
      this.getXAxis.setLabel("Game number")
      this.getYAxis.setLabel("Points earned in game")
      // Sets data.
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
      this.getXAxis.setLabel("Time (end of 15 minute interval)")
      this.getYAxis.setLabel("Cards received")

      val yellowCards = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (x, i) <- clubData.yellowByMinute.zipWithIndex do
        yellowCards += XYChart.Data((i + 1) * 15, x)
      val redCards = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (r, i) <- clubData.redByMinute.zipWithIndex do
        redCards += XYChart.Data((i + 1) * 15, r)

      val yellowSeries = JChart.XYChart.Series[Number, Number]
      yellowSeries.setData(yellowCards)
      yellowSeries.setName("Yellow cards")
      val redSeries = JChart.XYChart.Series[Number, Number]
      redSeries.setData(redCards)
      redSeries.setName("Red Cards")
      this.data = Seq(yellowSeries, redSeries)

    // Goals scored by minute.
    else
      this.getXAxis.setLabel("Time (end of 15 minute interval)")
      this.getYAxis.setLabel("Goals")

      val goalsScored = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (x, i) <- clubData.scoredByMinute.zipWithIndex do
        goalsScored += XYChart.Data((i + 1) * 15, x)
      val goalsConceded = ObservableBuffer[JChart.XYChart.Data[Number, Number]]()
      for (g, i) <- clubData.concededByMinute.zipWithIndex do
        goalsConceded += XYChart.Data((i + 1) * 15, g)

      val scoredSeries = JChart.XYChart.Series[Number, Number]
      scoredSeries.setData(goalsScored)
      scoredSeries.setName("Goals scored")
      val concededSeries = JChart.XYChart.Series[Number, Number]
      concededSeries.setData(goalsConceded)
      concededSeries.setName("Goals conceded")
      this.data = Seq(scoredSeries, concededSeries)

    // Adds tooltip to chart.
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


  // Updates the title of the chart.
  def updateTitle(club: String, season: String, dataSet: String) =
    if dataSet == "Fixtures" then
      title = "Form of " + club + " in season " + season
    else if dataSet == "Cards" then
      title = "Cards by minute for " + club + " in season " + season
    else
      title = "Goals by minute for " + club + " in season " + season

