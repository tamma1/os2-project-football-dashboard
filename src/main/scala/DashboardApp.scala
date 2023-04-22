import gui_components.*
import javafx.scene.layout.StackPane as JStackPane
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.*
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle


object DashboardApp extends JFXApp3:

  def start(): Unit =

    // Creates initial window.
    stage = new JFXApp3.PrimaryStage:
      title = "Football dashboard"
      width = 1400
      height = 820

    // Adds root component to scene.
    val root = new BorderPane()
    val scene = Scene(parent = root)
    stage.scene = scene

    // Adds buttons to the top menu.
    val addChartButton = new Button("Add chart")
    val saveButton = new Button("Save dashboard")
    val loadButton = new Button("Load dashboard")
    val topMenu = new HBox(10, addChartButton, saveButton, loadButton)

    // Sets background color and adds padding for top menu.
    topMenu.background = Background(Array(new BackgroundFill(SpringGreen, CornerRadii.Empty, Insets.Empty)))
    topMenu.padding = Insets(6, 6, 6, 6)

    // Creates chart area.
    val chartArea = new FlowPane(8, 8)
    chartArea.padding = Insets(8, 8, 8, 8)

    // Adds a new chart box to chart area when "add chart" -button is clicked.
    addChartButton.setOnAction( _ =>
      // Creates a new ChartBOx
      val newChart = new ChartBox()
      // Sets the height of the new chartBox to the height of the chartBox added earlier-
      val lastChartHeight =
        chartArea.children.lastOption match
          case Some(a)  => a.asInstanceOf[JStackPane].getHeight
          case _        => newChart.prefHeight.value
      newChart.prefHeight = lastChartHeight
      // Adds chartBox to chartArea.
      chartArea.children += newChart
    )

    // Adds chart area and top menu to root.
    root.top = topMenu
    root.center = chartArea














