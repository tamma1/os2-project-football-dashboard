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
import scalafx.scene.control.{Button, ComboBox, ProgressIndicator}
import scala.util.{Try, Failure, Success}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import data_processing.LeagueData.*

// Class for chart boxes that are added to the chart area.
class ChartBox extends StackPane:

  // Sets height, width and color.
  minWidth = 310
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

  // Container for contents in this chart box.
  private val contents = new BorderPane()
  contents.padding = Insets(3, 3, 3, 3)

  // Container for ComboBoxes.
  private val leftVBox = new VBox(10)

  // Map for chart types.
  private val chartMap = Map(
    "Pie Chart" -> new MyPieChart(),
    "Bar Chart" -> new MyBarChart(),
    "Line Chart" -> new MyLineChart()
  )

  // ComboBox for selecting the chart type.
  private val selectChart = new ComboBox[String]()
  selectChart.promptText = "Select chart"
  selectChart.items = ObservableBuffer().concat(chartMap.keys)
  leftVBox.children += selectChart

  // ComboBox for selecting the league.
  private val selectLeague = new ComboBox[String]()
  selectLeague.promptText = "Select league"
  selectLeague.items = ObservableBuffer().concat(leagueMap.keys)
  leftVBox.children += selectLeague

  // Adds ComboBoxes to contents.
  contents.left = leftVBox

  // Adds the selected chart to contents.
  selectChart.value.onChange( (_, _, newValue) =>
    contents.center = chartMap(newValue)
  )

  // Adds a ComboBox containing the clubs from the selected league.
  selectLeague.value.onChange { (_, oldValue, newValue) =>

    // Container for the loading icon and club selection list.
    val clubSelectionContainer = new HBox()
    clubSelectionContainer.spacing = 3
    clubSelectionContainer.maxWidth = 130

    // Creates a ComboBox and a loading indicator and adds them to the container.
    val clubSelection = new ComboBox[String]()
    clubSelection.promptText = "Select club"
    clubSelection.maxWidth = 120
    val loading = new ProgressIndicator()
    loading.prefHeight = 10
    clubSelectionContainer.children.addAll(clubSelection, loading)

    // Clubs wrapped in a Future.
    val futureData = Future {
      getLeagueData(leagueMap(newValue), 2022).teams
    }

    // Adds the clubs to the list when loaded from the Internet.
    futureData.onComplete {
      case Success(clubs) =>
        clubSelection.items = ObservableBuffer().concat(clubs)
        loading.visible = false
      case Failure(exception) =>
        clubSelection.setPromptText("Error loading data")
        loading.visible = false
    }

    // Adds club selection to the left side of the chart box.
    if leftVBox.children.length >= 3 then
      leftVBox.children(2) = clubSelectionContainer
    else
      leftVBox.children += clubSelectionContainer
  }

  // Adds the contents to this chart area.
  this.children += contents








