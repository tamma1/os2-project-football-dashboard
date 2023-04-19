package gui_components

import data_processing.LeagueData.*
import javafx.scene.layout.StackPane
import scalafx.beans.property.*
import scalafx.beans.value.ObservableValue
import scalafx.collections.ObservableBuffer
import scalafx.event.{ActionEvent, EventHandler}
import scalafx.geometry.Insets
import scalafx.scene.control
import scalafx.scene.control.{ComboBox, ProgressIndicator}
import scalafx.scene.layout.{HBox, VBox}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


// Container for ComboBoxes to select data.
class DataSelection extends VBox:
  // Adds spacing and padding.
  spacing = 5
  padding = Insets(2, 2, 2, 2)
  maxWidth = 130

  // Map for chart types.
  val chartMap: Map[String, MyChart] = Map(
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
  clubSelectionContainer.maxWidth = 130
  private var selectClub = new ComboBox[String]()
  clubSelectionContainer.children.addAll(selectClub, new ProgressIndicator())
  clubSelectionContainer.visible = false

  // ComboBox for selecting data set.
  private var selectClubData = new ComboBox[String]()
  selectClubData.promptText = "Select data"
  private val dataSets = List("Fixtures", "Goals")
  selectClubData.items = ObservableBuffer().concat(dataSets)
  selectClubData.visible = false

  // Adds ComboBoxes to this selection area.
  this.children.addAll(selectChart, selectLeague, selectSeason, clubSelectionContainer, selectClubData)

  // Container for selected data.
  var selectedChart = "Pie Chart"
  private var selectedLeague = "Premier League"
  var selectedLeagueID = 39
  var selectedSeason = "2022-2023"
  var selectedSeasonID = 2022
  var selectedClub = "Arsenal"
  val selectedClubID = new IntegerProperty(this, "clubID", 42)
  private var clubMap = Map[String, Int]()
  val selectedData = new StringProperty(this, "data", "Fixtures")

  // Method for updating the clubSelection.
  private def updateClubs() =
    // Creates a ComboBox for selecting club.
    selectClub = new ComboBox[String]()
    selectClub.promptText = "Select club"
    selectClub.disable = true
    // Creates a loading indicator.
    val loading = new ProgressIndicator()
    loading.prefHeight = 10
    loading.prefWidth = 20
    // Adds ComboBox and loading indicator to container.
    clubSelectionContainer.children(0) = selectClub
    clubSelectionContainer.children(1) = loading
    clubSelectionContainer.visible = true

    // Updates the selected club when a selection is made.
    selectClub.value.onChange { (_, _, newValue) =>
      selectedClub = newValue
      selectedClubID.set(clubMap(newValue))
      selectClubData.visible = true
    }

    // Clubs wrapped in a Future.
    val futureData = Future {
      getLeagueData(leagueMap(selectedLeague), seasonMap(selectedSeason)).teams
    }
    // Adds the clubs to the list when loaded from the Internet.
    futureData.onComplete {
      case Success(clubs) =>
        selectClub.items = ObservableBuffer().concat(clubs.keys.toList.sorted)
        clubMap = clubs
        loading.visible = false
        selectClub.disable = false
      case Failure(exception) =>
        selectClub.setPromptText("Error loading data")
        loading.visible = false
        throw exception
    }

  // Updates the ComboBox that contains selectable data sets.
  private def updateClubData() =
    selectClubData.value.onChange { (_, _, newValue) =>
      selectedData.set(newValue)
      selectClubData.visible = true
    }

  // Adds a season selection and updates the selected league.
  selectLeague.value.onChange { (_, _, newValue) =>
    selectedLeague = newValue
    selectedLeagueID = leagueMap(newValue)
    selectSeason.visible = true
    if selectSeason.value.value != null then
      updateClubs()
  }

  // Adds a ComboBox containing the clubs from the selected league and season.
  selectSeason.value.onChange { (_, _, newValue) =>
    selectedSeason = newValue
    selectedSeasonID = seasonMap(newValue)
    updateClubs()
  }

  // Updates the selected data set.
  selectClubData.value.onChange { (_, _, newValue) =>
    selectedData.set(newValue)
  }

