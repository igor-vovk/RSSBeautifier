name := """rss-beautifier"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  cache,
  ws,
  "com.rometools" % "rome" % "1.5.1",
  "de.jetwick" % "snacktory" % "1.2-SNAPSHOT",
  "com.esotericsoftware" % "kryo" % "3.0.2",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.6.play24",
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "karussell_snapshots" at "https://github.com/karussell/mvnrepo/raw/master/snapshots/"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
