package gui_components

import scalafx.geometry.Insets
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.Includes.*
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Pos.TopLeft
import scalafx.scene.Cursor
import scalafx.scene.input.MouseEvent
import scalafx.scene.control.{Button, ComboBox}

// Class for chart boxes that are added to the chart area.
class ChartBox extends StackPane:

  // Sets height, width and color.
  minWidth = 160
  minHeight = 160
  prefWidth = 200
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
    if isResizable(event) then
      dragging = true
      startX = event.sceneX
      startY = event.sceneY
      originalWidth = prefWidth.value
      originalHeight = prefHeight.value
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

  // Container for contesnts in this chart box.
  val contents = new VBox()

  // ComboBox for selecting the chart type.
  val selectChart = new ComboBox[String]
  selectChart.promptText = "Select chart type"
  selectChart.items = ObservableBuffer("Pie Chart", "Bar Chart")
  contents.children += selectChart

  // Adds the selected chart to contents.
  selectChart.value.onChange( (_, oldValue, newValue) =>
    if newValue == "Pie Chart" then
      if contents.children.length == 1 then
        contents.children += new MyPieChart()
      else
        contents.children(1) = new MyPieChart()
    else if newValue == "Bar Chart" then
      if contents.children.length == 1 then
        contents.children += new MyBarChart()
      else
        contents.children(1) = new MyBarChart()
  )
  // Adds the contents to this chart area.
  this.children += contents








