package file_handling

import gui_components.NewChartBox
import scalafx.scene.layout.FlowPane

import java.io.{BufferedWriter, FileNotFoundException, FileWriter, IOException}
import scala.collection.mutable.Buffer
import scala.io.Source

// Object for saving/loading dashboard
object FileManager:

  // Method for saving the dashboard data to file.
  def saveData(filePath: String, chartBoxes: List[List[String]]) =

    // Initialize file content with CHARTBOX chunk.
    val fileContent = Buffer[String]("CHARTBOXES:")

    // Loop through chart boxes and add data to file content.
    for (box, i) <- chartBoxes.zipWithIndex do
      fileContent += "BOX" + i + ":"
      fileContent += "HEIGHT: " + box.head
      fileContent += "WIDTH: " + box(1)
      fileContent += "CHART: " + box(2)
      fileContent += "LEAGUE: " + box(3)
      fileContent += "SEASON: " + box(4)
      fileContent += "CLUB: " + box(5)
      fileContent += "DATASET: " + box(6)
      fileContent += "*"

    // End of file
    fileContent.dropRightInPlace(1)
    fileContent += "#"
    fileContent += "ENDOFFILE"

    // Write file content to file.
    try {
      val fileWriter = new FileWriter(filePath)
      val buffWriter = new BufferedWriter(fileWriter)

      try {
        buffWriter.write(fileContent.mkString("\n"))
      } finally {
        buffWriter.close()
      }

    // Handle exceptions.
    } catch {
      case _: FileNotFoundException =>
        throw new FileManagerException("Error with saving dashboard data: Save file not found")
      case _: IOException =>
        throw new FileManagerException("Error with saving dashboard data: IOException")
      case _: Throwable =>
        throw new FileManagerException("Error with saving dashboard data: Unexpected exception.")
    }


  // Method for loading the dashboard data to file.
  def loadData(filePath: String, chartArea: FlowPane) =
    var source: Option[Source] = None
    try {
      source = Some(Source.fromFile(filePath))
      val lines = source.get.getLines()

      // Save loaded data here.
      val boxData = Buffer[List[String]]()

      var currentLine = ""
      var isStartOfBox = true
      var currentBoxParams = Buffer[String]()

      // Loop over the lines in file.
      while currentLine != "ENDOFFILE" && lines.hasNext do
        currentLine = lines.next().trim
        currentLine match
          // Checks if current line is the start of a new box.
          case s"BOX${n}:" =>
            if !isStartOfBox then
              boxData += currentBoxParams.toList
            currentBoxParams = Buffer[String]()
            isStartOfBox = false
          // Checks if current line is the end of a box.
          case "*" | "#" =>
            boxData += currentBoxParams.toList
            currentBoxParams = Buffer[String]()
            isStartOfBox = true
          // Checks if the line contains a parameter
          case line if line.contains(":") =>
            val params = line.split(":")
            if params.length >= 2 then
              val param = params(1).trim
              currentBoxParams += param
          case _ =>

      // Creates new chart boxes with loaded data and sets the boxes to chart area.
      chartArea.children.clear()
      for box <- boxData do
        if box.length != 7 then throw new FileManagerException("Invalid load file structure")
        val height = box(0).toDoubleOption.getOrElse(throw new FileManagerException("Invalid height: " + box(0)))
        val width = box(1).toDoubleOption.getOrElse(throw new FileManagerException("Invalid width: " + box(1)))
        val newBox = new NewChartBox(height, width, box(2), box(3), box(4), box(5), box(6))
        chartArea.children += newBox

    } catch {
      // Handle exceptions.
      case e: FileManagerException =>
        throw e
      case _: FileNotFoundException =>
        throw new FileManagerException("Error with loading dashboard data: Load file not found")
      case _: IOException =>
        throw new FileManagerException("Error with loading dashboard data: IOException")
      case _: Throwable =>
        throw new FileManagerException("Error with loading dashboard data: Unexpected exception.")

    } finally {
      // Close source.
      source.foreach( _.close() )
    }

// Custom exception class
class FileManagerException(msg: String) extends Exception(msg)













