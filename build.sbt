val scala3Version = "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "Football dashboard",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "19.0.0-R30"

libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "19" classifier osName)
}