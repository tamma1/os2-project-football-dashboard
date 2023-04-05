package gui_components

import data_processing.LeagueData.*
import javafx.scene.layout.{FlowPane as JFlowPane, StackPane as JStackPane}
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Cursor
import scalafx.scene.control.{Button, ComboBox, ProgressIndicator}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*


// Class for chart boxes that are added to the chart area.
class ChartBox extends StackPane:

  // Sets height, width and color.
  minWidth = 330
  minHeight = 160
  prefWidth = 400
  prefHeight = 200
  background = new Background(Array(new BackgroundFill(White, CornerRadii.Empty, Insets.Empty)))
  border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))
  alignment = TopLeft

  // Some variables for saving values.
  private var dragging = false
  private var startX = 0.0
  private var startY = 0.0
  private var originalWidth = prefWidth.value
  private var originalHeight = prefHeight.value
  private val resizeMargin = 15

  // Changes the look of the cursor when hovering on a draggable area.
  onMouseMoved = (event: MouseEvent) => {
    if isResizable(event) then
      cursor = Cursor.NWResize
    else
      cursor = Cursor.Default
  }

  // Saves the location of the cursor and the size of the chart box when mouse is pressed on a draggable area.
  onMousePressed = (event: MouseEvent) => {
    prefHeight = this.heightProperty().value

    if isResizable(event) then
      dragging = true
      startX = event.sceneX
      startY = event.sceneY
      originalWidth = prefWidth.value
      originalHeight = prefHeight.value
      // Change the prefHeight of all chart areas on the same row in the FlowPane.
      val yInScene = this.localToScene(this.getBoundsInLocal).getMinY
      val onSameRow = this.getParent.asInstanceOf[JFlowPane].children
        .map(_.asInstanceOf[JStackPane])
        .filter( a => a.localToScene(a.getBoundsInLocal).getMinY == yInScene)
      onSameRow.foreach( a => (a.setPrefHeight(this.prefHeight.value)))
  }

  // Changes the size of the chart box when mouse is dragged.
  onMouseDragged = (event: MouseEvent) => {
    if dragging then
      val deltaX = event.sceneX - startX
      val deltaY = event.sceneY - startY

      val minWidthValue = minWidth.toDouble + resizeMargin * 2
      val minHeightValue = minHeight.toDouble + resizeMargin * 2

      val newWidthValue = math.max(minWidthValue, originalWidth + deltaX)
      val newHeightValue = math.max(minHeightValue, originalHeight + deltaY)

      prefWidth = newWidthValue
      prefHeight = newHeightValue
      // Change the prefHeight of all chart areas on the same row in the FlowPane.
      val yInScene = this.localToScene(this.getBoundsInLocal).getMinY
      val onSameRow = this.getParent.asInstanceOf[JFlowPane].children
        .map(_.asInstanceOf[JStackPane])
        .filter( a => a.localToScene(a.getBoundsInLocal).getMinY == yInScene)
      onSameRow.foreach( a => (a.setPrefHeight(this.prefHeight.value)))
  }

  // Sets dragging to false when mouse is released.
  onMouseReleased = (_: MouseEvent) => { dragging = false }

  // Determines if an area is draggable or not.
  private def isResizable(event: MouseEvent): Boolean =
    val x = event.x
    val y = event.y
    val widthValue = width.toDouble
    val heightValue = height.toDouble
    val right = widthValue - resizeMargin
    val bottom = heightValue - resizeMargin
    x > right && x < widthValue && y > bottom && x > minWidth.toDouble && y > minHeight.toDouble

  // Container for contents in this chart box.
  private val contents = new BorderPane()
  contents.padding = Insets(1, 0, 3, 3)

  // Container for some buttons in the top area of the chart box.
  private val topArea = new HBox(10)
  topArea.padding = Insets(1, 1, 1, 1)
  topArea.background = Background(Array(new BackgroundFill(Lime, CornerRadii.Empty, Insets.Empty)))
  topArea.alignment = CenterRight
  topArea.border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))

  // Button for removing the chart.
  private val removeButton = new Button("Remove chart")
  topArea.children += removeButton

  // Remove this chart when remove button is clicked.
  removeButton.setOnAction( _ =>
    val parentArea = this.getParent.asInstanceOf[JFlowPane]
    parentArea.children.removeAll(this)
  )

  // ComboBoxes for selecting chart and data.
  private val leftVBox = new DataSelection()

  // Map for chart types.
  val chartMap = Map(
    "Pie Chart" -> new MyPieChart(),
    "Bar Chart" -> new MyBarChart(),
    "Line Chart" -> new MyLineChart()
  )

  // Adds the selected chart to contents.
  leftVBox.selectChart.value.onChange( (_, _, newValue) =>
    contents.center = leftVBox.chartMap(newValue)
    leftVBox.selectLeague.visible = true
  )

  // Adds top area to the chart box.
  contents.top = topArea

  // Adds ComboBoxes to contents.
  contents.left = leftVBox

  // Adds the contents to this chart area.
  this.children += contents








