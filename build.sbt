ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-example",
    resolvers +=
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-http" % "3.0.0-RC3",
      "dev.zio" %% "zio" % "2.0.18",
      "com.github.pjfanning" %% "zio-metrics-micrometer" % "0.21.0",
      "io.micrometer" % "micrometer-registry-prometheus" % "1.11.0"
    )
  )
