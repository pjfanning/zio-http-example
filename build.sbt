ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.2"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-example",
    libraryDependencies ++= Seq(
      "io.d11" %% "zhttp" % "2.0.0-RC6"
    )
  )
