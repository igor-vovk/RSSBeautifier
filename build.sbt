name := """rss-beautifier"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.rometools" % "rome" % "1.5.1",
  "de.jetwick" % "snacktory" % "1.2-SNAPSHOT",
  "com.esotericsoftware" % "kryo" % "3.0.2",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "karussell_snapshots" at "https://github.com/karussell/mvnrepo/raw/master/snapshots/"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
