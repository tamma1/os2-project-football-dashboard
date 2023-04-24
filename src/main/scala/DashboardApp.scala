import file_handling.FileManager
import gui_components.*
import file_handling.*
import javafx.scene.layout.{BorderPane as JBorderPane, HBox as JHBox, StackPane as JStackPane, VBox as JVBox}
import javafx.scene.control.ComboBox as JComboBox
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.collections.ObservableBuffer
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.*
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle

import scala.collection.mutable.Buffer


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

    // Save dashboard to files.
    saveButton.setOnAction( _ =>
      val chartBoxes = chartArea.children.map( _.asInstanceOf[JStackPane])
      // Retrieve necessery data from chart boxes.
      val hAndW = chartBoxes.map( a => (a.getPrefHeight.toString, a.getPrefWidth.toString) )
      val dataSelections = chartBoxes
        .map( _.getChildren.head.asInstanceOf[JBorderPane] )
        .map( _.getLeft.asInstanceOf[JVBox].getChildren)
      // Save retrieved data to buffer.
      val chartBuf = Buffer[List[String]]()
      for (d, i) <- dataSelections.zipWithIndex do
        val dataBuf = Buffer[String](hAndW(i)._1, hAndW(i)._2)
        d.map( a => if a.isInstanceOf[JComboBox[String]] then
          a.asInstanceOf[JComboBox[String]]
        else
          a.asInstanceOf[JHBox].getChildren.head.asInstanceOf[JComboBox[String]])
        .map( _.getValue )
        .foreach( v => dataBuf += v)
        chartBuf += dataBuf.toList
      try {
        FileManager.saveData("resources/save1.dbsave", chartBuf.toList)
      } catch {
        case e: Throwable => showErrorDialog("Saving dashboard failed.", e)
      }
    )

    // Load dashboard from files
    loadButton.setOnAction( _ =>
      try {
        FileManager.loadData("resources/save1.dbsave", chartArea)
      } catch {
         case e: Throwable => showErrorDialog("Loading dashboard failed.", e)
      }
    )

    // Method for showing error dialog.
    def showErrorDialog(message: String, exception: Throwable) = new Alert(Alert.AlertType.Error) {
      title = "Error"
      headerText = message
      contentText = "Operation threw exception: " + exception.toString
      exception.printStackTrace()
    }.showAndWait()


    // Adds chart area and top menu to root.
    root.top = topMenu
    root.center = chartArea














