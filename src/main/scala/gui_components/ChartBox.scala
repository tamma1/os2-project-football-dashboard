package gui_components

import data_processing.ClubData.*
import javafx.scene.control.ComboBox as JComboBox
import javafx.scene.layout.{FlowPane as JFlowPane, StackPane as JStackPane}
import scalafx.Includes.*
import scalafx.application.Platform
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.geometry.Pos.*
import scalafx.scene.Cursor
import scalafx.scene.control.{Button, ComboBox, ProgressIndicator}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


// Class for chart boxes that are added to the chart area.
class ChartBox extends StackPane:

  // Sets some properties for the chart box.
  minWidth = 550
  minHeight = 200
  prefWidth = 670
  prefHeight = 280
  background = new Background(Array(new BackgroundFill(White, CornerRadii.Empty, Insets.Empty)))
  border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))
  alignment = Center

  // Some variables for saving values.
  private var dragging = false
  private var startX = 0.0
  private var startY = 0.0
  private var originalWidth = prefWidth.value
  private var originalHeight = prefHeight.value
  private val resizeMargin = 10

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
    x > right || y > bottom

  // Container for contents in this chart box.
  private val contents = new BorderPane()
  contents.padding = Insets(1, 1, 1, 1)

  // Container for some buttons in the top area of the chart box.
  private val topArea = new HBox(10)
  topArea.padding = Insets(1, 1, 1, 1)
  topArea.background = Background(Array(new BackgroundFill(ForestGreen, CornerRadii.Empty, Insets.Empty)))
  topArea.alignment = CenterRight
  topArea.border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))

  // Button for removing the chart box.
  private val removeButton = new Button("Remove chart")
  topArea.children.addAll(removeButton)

  // Remove this chart when remove button is clicked.
  removeButton.setOnAction( _ =>
    val parentArea = this.getParent.asInstanceOf[JFlowPane]
    parentArea.children.removeAll(this)
  )

  // Card for displaying additional information
  private val card = new Card()

  // ComboBoxes for selecting chart and data.
  private val leftVBox = new DataSelection()

  // Display the selected chart.
  private var chart: MyChart = new MyPieChart()
  leftVBox.selectChart.value.onChange( (_, _, newValue) =>
    chart = leftVBox.chartMap(newValue)
    contents.center = chart
    leftVBox.selectedChart = newValue
    leftVBox.selectLeague.visible = true
    if clubDataResponse.isDefined then
      chart.updateData(clubDataResponse.get, leftVBox.selectedData.value)
      chart.updateTitle(leftVBox.selectedClub, leftVBox.selectedSeason, leftVBox.selectedData.value)
      card.updateText(clubDataResponse.get, leftVBox.selectedData.value, newValue)
  )

  // Container for the last club data response.
  private var clubDataResponse: Option[Response] = None

  // Updates the chart when a new club is selected.
  leftVBox.selectedClubID.onChange( (_, _, newValue) =>
    // Disable this chart box and add a progress indicator when data is being fetched from internet.
    this.disable = true
    val loading = new ProgressIndicator()
    this.children += loading
    // Update the chart when data is loaded.
    val newData = Future { getClubData(leftVBox.selectedLeagueID, leftVBox.selectedSeasonID, newValue.intValue()) }
    newData.onComplete {
      case Success(clubData) =>
        Platform.runLater {
          chart.updateData(clubData, leftVBox.selectedData.value)
          chart.updateTitle(leftVBox.selectedClub, leftVBox.selectedSeason, leftVBox.selectedData.value)
          card.updateText(clubData, leftVBox.selectedData.value, leftVBox.selectedChart)
          clubDataResponse = Some(clubData)
          this.children.dropRightInPlace(1)
          this.disable = false
          this.prefWidth = this.getWidth + 1
          this.prefWidth = this.getWidth - 1
        }
      case Failure(exception) =>
        Platform.runLater {
          chart.title = "Error loading data"
          this.disable = false
          this.children.dropRightInPlace(0)
        }
        throw exception
      }
    )

  // Updates the chart when a new data set is selected.
  leftVBox.selectedData.onChange( (_, _, newValue) =>
    if clubDataResponse.isDefined then
      chart.updateData(clubDataResponse.get, newValue)
      chart.updateTitle(leftVBox.selectedClub, leftVBox.selectedSeason, newValue)
      card.updateText(clubDataResponse.get, newValue, leftVBox.selectedChart)
      this.prefWidth = this.getWidth + 1
      this.prefWidth = this.getWidth - 1
  )

  // Adds top, left and right area to the chart box.
  contents.top = topArea
  contents.left = leftVBox
  contents.right = card

  // Adds the contents to this chart area.
  this.children += contents








