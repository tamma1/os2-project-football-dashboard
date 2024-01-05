package gui_components

import data_processing.LeagueData.*
import javafx.scene.layout.StackPane
import scalafx.application.Platform
import scalafx.beans.property.*
import scalafx.beans.value.ObservableValue
import scalafx.collections.{ObservableBuffer, ObservableMap}
import scalafx.event.{ActionEvent, EventHandler}
import scalafx.geometry.Insets
import scalafx.scene.control
import scalafx.scene.control.{ComboBox, ProgressIndicator}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


// Container for ComboBoxes to select data.
class DataSelection extends VBox:
  
  // Set some properties.
  spacing = 5
  padding = Insets(2, 2, 2, 2)
  maxWidth = 120
  style = "-fx-font-size: 9pt;"
  border = new Border(new BorderStroke(Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(1)))

  // Map for chart types.
  val chartMap: Map[String, MyChart] = Map(
    "Pie Chart" -> new MyPieChart(),
    "Bar Chart" -> new MyBarChart(),
    "Line Chart" -> new MyLineChart()
  )

  
  // Container for selected data.
  private var selectedLeague = ""
  var selectedLeagueID = -1
  private var selectedSeason = ""
  var selectedSeasonID = -1
  var selectedClub = ""
  val selectedClubID = new IntegerProperty(this, "clubID", -1)
  val clubMap = new MapProperty[String, Int](this, "clubMap", ObservableMap[String, Int]())
  private val selectedData = new StringProperty(this, "data", "Fixtures")

  // ComboBox for selecting chart type.
  val selectChart = new ComboBox[String]()
  selectChart.promptText = "Select chart"
  selectChart.items = ObservableBuffer().concat(chartMap.keys)

  // ComboBox for selecting league.
  val selectLeague = new ComboBox[String]()
  selectLeague.promptText = "Select league"
  selectLeague.items = ObservableBuffer().concat(leagueMap.keys.toList.sorted)
  selectLeague.visible = false

  // ComboBox for selecting season.
  val selectSeason = new ComboBox[String]()
  selectSeason.promptText = "Select season"
  selectSeason.items = ObservableBuffer().concat(seasonMap.keys.toList.sorted)
  selectSeason.visible = false

  // Container for loading indicator and club selection list.
  private val clubSelectionContainer = new HBox()
  clubSelectionContainer.spacing = 3
  clubSelectionContainer.maxWidth = 130
  var selectClub = new ComboBox[String]()
  private val loading = new ProgressIndicator()
  loading.visible = false
  clubSelectionContainer.children.addAll(selectClub, loading)
  clubSelectionContainer.visible = false

  // ComboBox for selecting data set.
  var selectClubData = new ComboBox[String]()
  selectClubData.promptText = "Select data"
  val dataSets = List("Fixtures", "Goals", "Cards")
  selectClubData.items = ObservableBuffer().concat(dataSets)
  selectClubData.visible = false
  selectClubData.setValue(selectedData.value)

  // Add ComboBoxes to this selection area.
  this.children.addAll(selectChart, selectLeague, selectSeason, clubSelectionContainer, selectClubData)

  
  // Method for updating the clubSelection.
  private def updateClubs() =
  
    // Create a ComboBox for selecting club.
    selectClub = new ComboBox[String]()
    selectClub.promptText = "Select club"
    selectClub.disable = true
    
    // Create a loading indicator.
    val loading = new ProgressIndicator()
    loading.prefHeight = 10
    loading.prefWidth = 20
    
    // Add ComboBox and loading indicator to container.
    clubSelectionContainer.children(0) = selectClub
    clubSelectionContainer.children(1) = loading
    clubSelectionContainer.visible = true

    
    // Map of clubs and their IDs wrapped in a Future.
    val futureData = Future { getLeagueData(leagueMap(selectedLeague), seasonMap(selectedSeason)).teams }
    
    // Add the clubs to the combo box list when data is fetched.
    futureData.onComplete {
      case Success(clubs) =>
        Platform.runLater {
          selectClub.items = ObservableBuffer().concat(clubs.keys.toList.sorted)
          
          // Save new club map.
          clubMap.setValue(ObservableMap(clubs.toSeq: _*))
          loading.visible = false
          selectClub.disable = false

          // Update the selected club when a selection is made.
          selectClub.value.onChange { (_, _, newValue) =>
            selectedClub = newValue
            selectedClubID.set(clubMap.value(newValue))
            selectClubData.visible = true
          }
        }
        
      // If API call fails, set a prompt text for combo box indicating an error.
      case Failure(exception) =>
        Platform.runLater {
          selectClub.setPromptText("Error loading data")
          loading.visible = false
        }
        throw exception
    }

  
  // Add season selection when league is selected, and save the league ID.
  selectLeague.value.onChange { (_, _, newValue) =>
    selectedLeague = newValue
    selectedLeagueID = leagueMap(newValue)
    selectSeason.visible = true
    
    // Update club selection if season is selected.
    if selectSeason.getValue != null then
      updateClubs()
  }

  // Add a ComboBox containing the clubs from the selected league and season.
  selectSeason.value.onChange { (_, _, newValue) =>
    selectedSeason = newValue
    selectedSeasonID = seasonMap(newValue)
    updateClubs()
  }

  // Update the selected data set.
  selectClubData.value.onChange { (_, _, newValue) =>
    selectedData.set(newValue)
  }

