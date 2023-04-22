ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "zio-http-example",
    resolvers +=
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-http" % "3.0.0-RC1",
      "dev.zio" %% "zio" % "2.0.13",
      "com.github.pjfanning" %% "zio-metrics-micrometer" % "0.20.6",
      "io.micrometer" % "micrometer-registry-prometheus" % "1.10.6"
    )
  )
