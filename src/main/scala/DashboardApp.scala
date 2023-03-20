import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.control.*
import scalafx.scene.paint.Color.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.input.MouseEvent
import scalafx.scene.shape.Rectangle
import scalafx.scene.Cursor
import gui_components.*


object DashboardApp extends JFXApp3:

  def start(): Unit =

    // Creates initial window.
    stage = new JFXApp3.PrimaryStage:
      title = "Football dashboard"
      width = 1300
      height = 800

    // Adds root component to scene.
    val root = new BorderPane()
    val scene = Scene(parent = root)
    stage.scene = scene

    // Adds buttons to the top menu.
    val addChartButton = new Button("Add chart")
    val saveButton = new Button("Save dashboard")
    val loadButton = new Button("Load dashboard")
    val refreshButton = new Button("Refresh data")
    val topMenu = new HBox(10, addChartButton, saveButton, loadButton, refreshButton)

    // Sets background color for top menu.
    topMenu.background = Background(Array(new BackgroundFill(Green, CornerRadii.Empty, Insets.Empty)))

    // Creates chart area.
    val chartArea = new FlowPane(10, 10)

    // Adds a new chart box to chart area when add chart -button is clicked.
    addChartButton.setOnAction( _ =>
      val selectDataButton = new Button("Select data")
      val newChart = new ChartBox()
      newChart.children += selectDataButton
      chartArea.children += newChart
    )

    // Adds chart area and top menu to root.
    root.top = topMenu
    root.center = chartArea














