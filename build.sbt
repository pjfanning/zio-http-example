ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-example",
    resolvers +=
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "io.d11" %% "zhttp" % "2.0.0-RC10",
      "com.github.pjfanning" %% "zio-metrics-micrometer" % "0.20.6",
      "io.micrometer" %  "micrometer-registry-prometheus" % "1.9.1"
    )
  )
