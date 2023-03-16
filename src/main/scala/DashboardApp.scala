import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.control.*
import scalafx.scene.paint.Color.*
import scalafx.geometry.Insets


object DashboardApp extends JFXApp3:

  def start(): Unit =

    // Create window
    stage = new JFXApp3.PrimaryStage:
      title = "Football dashboard"
      width = 1300
      height = 800

    // Add root component to scene
    val root = new BorderPane()
    val scene = Scene(parent = root)
    stage.scene = scene

    // Add buttons to the top menu
    val saveButton = new Button("Save dashboard")
    val loadButton = new Button("Load dashboard")
    val refreshButton = new Button("Refresh data")
    val topMenu = new HBox(10, saveButton, loadButton, refreshButton)

    // Set background color for top menu
    topMenu.background = Background(Array(new BackgroundFill(Green, CornerRadii.Empty, Insets.Empty)))

    // Containers for charts
    val container1 = new Pane()
    val container2 = new Pane()
    val container3 = new Pane()
    val container4 = new Pane()
    val containers = List(container1, container2, container3, container4)

    // Add "Add chart" -buttons to containers and set size and border for containers
    containers.foreach(c =>
      c.setPrefSize(650, 350)
      c.children += new Button("Add chart")
      c.border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))
    )

    // Add containers to the chart area
    val chartArea = new GridPane()
    chartArea.add(container1, 0, 0)
    chartArea.add(container2, 1, 0)
    chartArea.add(container3, 0, 1)
    chartArea.add(container4, 1, 1)

    // Add chart area and top menu to root
    root.top = topMenu
    root.center = chartArea









