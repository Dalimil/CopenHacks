name := """CopenHacks hackathon"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "org.sorm-framework" % "sorm" % "0.3.19"
)
dependencyOverrides += "org.scala-lang" % "scala-compiler" % scalaVersion.value
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

