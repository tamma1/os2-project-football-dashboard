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

  // Set some properties for the chart box.
  minWidth = 550
  minHeight = 200
  prefWidth = 670
  prefHeight = 280
  background = new Background(Array(new BackgroundFill(White, CornerRadii.Empty, Insets.Empty)))
  border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))
  alignment = Center

  // Some variables for saving values used in resizing the chart box.
  private var dragging = false
  private var startX = 0.0
  private var startY = 0.0
  private var originalWidth = prefWidth.value
  private var originalHeight = prefHeight.value
  private val resizeMargin = 10

  // Change the look of the cursor when hovering on a draggable area.
  onMouseMoved = (event: MouseEvent) => {
    if isResizable(event) then
      cursor = Cursor.NWResize
    else
      cursor = Cursor.Default
  }

  // Saves the location of the cursor and the size of the chart box when mouse is pressed on a draggable area.
  onMousePressed = (event: MouseEvent) => {
    prefHeight = this.heightProperty().value

    // Check if area is resizable and set some values.
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

  // Change the size of the chart box when mouse is dragged.
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

  // Set dragging to false when mouse is released.
  onMouseReleased = (_: MouseEvent) => { dragging = false }

  // Determines if an area is draggable or not.
  private def isResizable(event: MouseEvent): Boolean =
    val x = event.x
    val y = event.y
    val widthValue = width.toDouble
    val heightValue = height.toDouble
    val right = widthValue - resizeMargin
    val bottom = heightValue - resizeMargin
    // True on the right and bottom border of the chart box.
    x > right || y > bottom

  // Container for the last club data response.
  private var clubDataResponse: Option[Response] = None

  // Container for contents in this chart box.
  private val contents = new BorderPane()
  contents.padding = Insets(1, 1, 1, 1)

  // Container for some buttons in the top area of the chart box.
  private val topArea = new HBox(10)
  topArea.padding = Insets(2, 2, 2, 2)
  topArea.background = Background(Array(new BackgroundFill(ForestGreen, CornerRadii.Empty, Insets.Empty)))
  topArea.alignment = CenterRight
  topArea.border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))

  // Buttons for removing, duplicating and updating the chart box.
  private val removeButton = new Button("Remove chart")
  private val duplicateButton = new Button("Duplicate chart")
  private val refreshButton = new Button("Refresh data")
  refreshButton.visible = false
  topArea.children.addAll(refreshButton, duplicateButton, removeButton)

  // Remove this chart when remove button is clicked.
  removeButton.setOnAction( _ =>
    val parentArea = this.getParent.asInstanceOf[JFlowPane]
    parentArea.children.removeAll(this)
  )

  // Duplicate this chart when duplicate button is clicked.
  duplicateButton.setOnAction( _ =>
    val parentArea = this.getParent.asInstanceOf[JFlowPane]
    val newChart = new NewChartBox(this.prefHeight.value, this.prefWidth.value, leftVBox.selectChart.value.value,
      leftVBox.selectLeague.getValue, leftVBox.selectSeason.getValue, leftVBox.selectClub.getValue, leftVBox.selectClubData.getValue)
    parentArea.children += newChart
  )

  // Refresh the data in this chart when refresh button is clicked.
  refreshButton.setOnAction( _ =>
    // Check if club is selected.
    if leftVBox.selectClub.getValue != null then
      // Add loading indicator.
      this.disable = true
      val loading = new ProgressIndicator()
      this.children += loading
      // Fetch new data from API.
      val newData = Future { getClubData(leftVBox.selectedLeagueID, leftVBox.selectedSeasonID, leftVBox.selectedClubID.value) }
      newData.onComplete {
        // If data is refreshed succesfully, update chart and delete loading indicator.
        case Success(response) =>
          Platform.runLater {
            chart.updateData(response, leftVBox.selectClubData.getValue)
            card.updateText(response, leftVBox.selectClubData.getValue, leftVBox.selectChart.getValue)
            clubDataResponse = Some(response)
            this.children.dropRightInPlace(1)
            this.disable = false
          }
        // If failed, set new title for chart.
        case Failure(exception) =>
          Platform.runLater {
            chart.title = "Data refresh failed"
            this.disable = false
            this.children.dropRightInPlace(1)
          }
          throw exception
      }
    else
      // If club is not selected, set a new title for chart to indicate an error.
      chart.setTitle("Select club before refreshing")
  )

  // Card for displaying additional information.
  private val card = new Card()

  // ComboBoxes for selecting chart and data.
  val leftVBox = new DataSelection()

  // Chart for displaying selected data in the center of this chart box.
  var chart: MyChart = new MyPieChart()
  
  // Update chart when a new chart is selected.
  leftVBox.selectChart.value.onChange( (_, _, newValue) =>
    chart = leftVBox.chartMap(newValue)
    contents.center = chart
    leftVBox.selectLeague.visible = true
    // Update chart and additional text if club data response is defined.
    if clubDataResponse.isDefined then
      chart.updateData(clubDataResponse.get, leftVBox.selectClubData.getValue)
      chart.updateTitle(leftVBox.selectedClub, leftVBox.selectSeason.getValue, leftVBox.selectClubData.getValue)
      card.updateText(clubDataResponse.get, leftVBox.selectClubData.getValue, newValue)
  )

  // Update the chart when a new club is selected.
  leftVBox.selectedClubID.onChange( (_, _, newValue) =>
    // Disable this chart box and add a progress indicator when data is being fetched from internet.
    this.disable = true
    val loading = new ProgressIndicator()
    this.children += loading
    // Make API call.
    val newData = Future { getClubData(leftVBox.selectedLeagueID, leftVBox.selectedSeasonID, newValue.intValue()) }
    newData.onComplete {
      // Update chart if new data is fetched succesfully.
      case Success(clubData) =>
        Platform.runLater {
          chart.updateData(clubData, leftVBox.selectClubData.getValue)
          chart.updateTitle(leftVBox.selectClub.getValue, leftVBox.selectSeason.getValue, leftVBox.selectClubData.getValue)
          card.updateText(clubData, leftVBox.selectClubData.getValue, leftVBox.selectChart.getValue)
          // Save new data so it can be easily accessed when selected chart type and data set is changed.
          clubDataResponse = Some(clubData)
          // Remove loading indicator.
          this.children.dropRightInPlace(1)
          this.disable = false
          refreshButton.visible = true
        }
      // If API call fails, set new title for chart to indicate an error.
      case Failure(exception) =>
        Platform.runLater {
          chart.title = "Error loading data"
          this.disable = false
          this.children.dropRightInPlace(1)
        }
        throw exception
      }
    )

  // Update the chart when a new data set is selected.
  leftVBox.selectClubData.value.onChange( (_, _, newValue) =>
    if clubDataResponse.isDefined then
      chart.updateData(clubDataResponse.get, newValue)
      chart.updateTitle(leftVBox.selectedClub, leftVBox.selectSeason.getValue, newValue)
      card.updateText(clubDataResponse.get, newValue, leftVBox.selectChart.getValue)
  )

  // Add top, left and right area to the chart box.
  contents.top = topArea
  contents.left = leftVBox
  contents.right = card

  // Add the contents to this chart area.
  this.children += contents









