import java.io.{FileWriter, BufferedWriter, FileNotFoundException, IOException}
import scala.collection.mutable.Buffer
import scala.io.Source
import scalafx.scene.layout.FlowPane
import scala.util.Try
import gui_components.NewChartBox

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
      case _: FileNotFoundException => println("Error with saving dashboard data: Save file not found")
      case _: IOException => println("Error with saving dashboard data: IOException")
      case _: Throwable => println("Error with saving dashboard data: Unexpected exception.")
    }


  // Method for loading the dashboard data to file.
  def loadData(filePath: String, chartArea: FlowPane) =
    val source = Source.fromFile(filePath)
    val lines = source.getLines()

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

    source.close()

    // Creates new chart boxes with loaded data and sets the boxes to chart area.
    chartArea.children.clear()
    for box <- boxData do
      val newBox = new NewChartBox(box(0).toDouble, box(1).toDouble, box(2), box(3), box(4), box(5), box(6))
      chartArea.children += newBox











