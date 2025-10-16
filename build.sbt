ThisBuild / scalaVersion := "2.12.18"
ThisBuild / organization := "com.elon"
ThisBuild / version      := "0.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "elon-tweets",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.5.0",
      "org.apache.spark" %% "spark-sql"  % "3.5.0"
    )
  )
