package gui_components

import data_processing.LeagueData.*
import scalafx.collections.ObservableMap
import scalafx.scene.control.ProgressIndicator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalafx.application.Platform


// Class for creating new chart boxes with given parameters.
class NewChartBox(
                   boxHeight: Double,
                   boxWidth: Double,
                   chartType: String,
                   league: String,
                   season: String,
                   club: String,
                   dataSet: String
                 )
  extends ChartBox:

    // Sets given width and height.
    prefHeight = boxHeight
    prefWidth  = boxWidth

    // Sets selected chart.
    if chartType != null && chartType != "null" then
      leftVBox.selectChart.setValue(chartType)

    // Sets selected league
    if league != null && league != "null" then
      leftVBox.selectLeague.setValue(league)
      leftVBox.selectedLeagueID = leagueMap(league)

    // Sets selected season.
    if season != null && season != "null" then
      leftVBox.selectSeason.setValue(season)
      leftVBox.selectedSeasonID = seasonMap(season)

    // Sets selected club an upates the club map.
    if club != null && club != "null" then
      this.children += new ProgressIndicator()
      val futureData = Future { getLeagueData(leftVBox.selectedLeagueID, leftVBox.selectedSeasonID).teams }
      futureData.onComplete {
        case Success(clubs) =>
          Platform.runLater {
            leftVBox.clubMap.setValue(ObservableMap(clubs.toSeq: _*))
            leftVBox.selectClub.setValue(club)
            leftVBox.selectedClubID.setValue(leftVBox.clubMap.value(leftVBox.selectClub.value.value))
            leftVBox.selectClubData.visible = true
            this.children.dropRightInPlace(1)
          }
        case Failure(exception) =>
          Platform.runLater {
            this.children.dropRightInPlace(1)
            leftVBox.selectClub.setPromptText("Error loading data")
          }
          throw exception
      }

    // Sets selected data set.
    leftVBox.selectClubData.setValue(dataSet)










