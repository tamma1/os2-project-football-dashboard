val scala3Version = "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "Football dashboard",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "19.0.0-R30"

libraryDependencies += "com.lihaoyi" %% "requests" % "0.8.0"

libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.4",
      "io.circe" %% "circe-generic" % "0.14.4",
      "io.circe" %% "circe-parser" % "0.14.4"
    )

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