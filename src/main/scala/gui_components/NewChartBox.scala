package gui_components

import data_processing.LeagueData.*
import file_handling.FileManagerException
import scalafx.application.Platform
import scalafx.collections.ObservableMap
import scalafx.scene.control.ProgressIndicator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

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

    // Set given width and height.
    prefHeight = boxHeight
    prefWidth  = boxWidth

    // Set selected chart if chart type is defined.
    if chartType != null && chartType != "null" then
      if !leftVBox.chartMap.contains(chartType) then
        throw new FileManagerException("Invalid chart type: " + chartType)
      leftVBox.selectChart.setValue(chartType)

    // Set selected league if league is defined.
    if league != null && league != "null" then
      if !leagueMap.contains(league) then
        throw new FileManagerException("Invalid league: " + league)
      leftVBox.selectLeague.setValue(league)
      leftVBox.selectedLeagueID = leagueMap(league)

    // Set selected season if season is defined.
    if season != null && season != "null" then
      if !seasonMap.contains(season) then
        throw new FileManagerException("Invalid season: " + season)
      leftVBox.selectSeason.setValue(season)
      leftVBox.selectedSeasonID = seasonMap(season)

    // Set selected club an upate the club map if club is defined.
    if club != null && club != "null" then
      this.children += new ProgressIndicator()
      // Make an API call.
      val futureData = Future { getLeagueData(leftVBox.selectedLeagueID, leftVBox.selectedSeasonID).teams }
      futureData.onComplete {
        // If API call is succesfull, update club selection and chart data.
        case Success(clubs) =>
          Platform.runLater {
            leftVBox.clubMap.setValue(ObservableMap(clubs.toSeq: _*))
            if !leftVBox.clubMap.value.contains(club) then
              throw new FileManagerException("Invalid club: " + club)
            leftVBox.selectClub.setValue(club)
            leftVBox.selectedClubID.setValue(leftVBox.clubMap.value(leftVBox.selectClub.value.value))
            leftVBox.selectClubData.visible = true
            this.children.dropRightInPlace(1)
          }
        // If API call fails, set new prompt text for combo box indicatin an error.
        case Failure(exception) =>
          Platform.runLater {
            this.children.dropRightInPlace(1)
            leftVBox.selectClub.setPromptText("Error loading data")
          }
          throw exception
      }

    // Set selected data set.
    if !leftVBox.dataSets.contains(dataSet) then
      throw new FileManagerException("Invalid data set: " + dataSet)
    leftVBox.selectClubData.setValue(dataSet)










