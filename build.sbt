val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .enablePlugins(CoverallsPlugin)
  .settings(
      name := "Uno",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := scala3Version,

      libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
      libraryDependencies += "org.scalafx" %% "scalafx" % "20.0.0-R31",

      coverageEnabled := true
  )
