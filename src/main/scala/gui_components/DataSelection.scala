package gui_components

import data_processing.LeagueData.*
import scalafx.collections.ObservableBuffer
import scalafx.event.{ActionEvent, EventHandler}
import scalafx.geometry.Insets
import scalafx.scene.control
import scalafx.scene.control.{ComboBox, ProgressIndicator}
import scalafx.scene.layout.{HBox, VBox}
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


// Container for ComboBoxes to select data.
class DataSelection extends VBox:
  // Adds spacing and padding.
  spacing = 5
  padding = Insets(2, 2, 2, 2)

  // Map for chart types.
  val chartMap = Map(
    "Pie Chart" -> new MyPieChart(),
    "Bar Chart" -> new MyBarChart(),
    "Line Chart" -> new MyLineChart()
  )

  // ComboBox for selecting chart type.
  val selectChart = new ComboBox[String]()
  selectChart.promptText = "Select chart"
  selectChart.items = ObservableBuffer().concat(chartMap.keys)

  // ComboBox for selecting the league.
  val selectLeague = new ComboBox[String]()
  selectLeague.promptText = "Select league"
  selectLeague.items = ObservableBuffer().concat(leagueMap.keys.toList.sorted)
  selectLeague.visible = false

  // ComboBox for selecting the season.
  private val selectSeason = new ComboBox[String]()
  selectSeason.promptText = "Select season"
  selectSeason.items = ObservableBuffer().concat(seasonMap.keys.toList.sorted)
  selectSeason.visible = false


  // Container for the loading icon and club selection list.
  private val clubSelectionContainer = new HBox()
  clubSelectionContainer.spacing = 3
  clubSelectionContainer.maxWidth = 140
  private var selectClub = new ComboBox[String]()
  clubSelectionContainer.children.addAll(selectClub, new ProgressIndicator())
  clubSelectionContainer.visible = false

  // Add ComboBoxes to this selection area
  this.children.addAll(selectChart, selectLeague, selectSeason, clubSelectionContainer)

  // Container for selected data.
  var selectedChart = "Pie Chart"
  private var selectedLeague = "Premier League"
  private var selectedSeason = "2022-2023"
  private var selectedClub = "Arsenal"

  // Method for updating the clubSelection.
  private def updateClubs() =
    // Creates a ComboBox for selecting club.
    val clubSelection = new ComboBox[String]()
    clubSelection.promptText = "Select club"
    clubSelection.maxWidth = 120
    // Creates a loading indicator.
    val loading = new ProgressIndicator()
    loading.prefHeight = 10
    loading.prefWidth = 20
    // Adds ComboBox and loading indicator to container.
    clubSelectionContainer.children(0) = clubSelection
    clubSelectionContainer.children(1) = loading
    clubSelectionContainer.visible = true

    // Updates the selected club when a selection is made.
    clubSelection.value.onChange { (_, _, newValue) =>
      selectedClub = newValue
      println(selectedClub)
    }

    // Clubs wrapped in a Future.
    val futureData = Future {
      getLeagueData(leagueMap(selectedLeague), seasonMap(selectedSeason)).teams.keys.toList.sorted
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

  // Adds a season selection and updates the selected league.
  selectLeague.value.onChange { (_, _, newValue) =>
    selectedLeague = newValue
    selectSeason.visible = true
    if selectSeason.value.value != null then
      updateClubs()
  }

  // Adds a ComboBox containing the clubs from the selected league and season.
  selectSeason.value.onChange { (_, _, newValue) =>
    selectedSeason = newValue
    updateClubs()
  }

