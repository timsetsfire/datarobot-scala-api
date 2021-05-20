ThisBuild / organization := "io.github.timsetsfire"
ThisBuild / organizationName := "io.github.timsetsfire"
ThisBuild / organizationHomepage := Some(url("https://github.com/timsetsfire/"))
ThisBuild / name := "datarobot"
ThisBuild / version := "0.1.1"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/timsetsfire/datarobot-scala-api"),
    "scm:git@github.com:timsetsfire/datarobot-scala-api.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "timsetsfire",
    name  = "Tim Whittaker",
    email = "timsetsfire@gmail.com",
    url   = url("https://github.com/timsetsfire/")
  )
)


ThisBuild / description := "Rough Scala Client for DataRobot"
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/timsetsfire/datarobot-scala-api"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
sonatypeCredentialHost := "s01.oss.sonatype.org"

// sonatypeBundleDirectory := (ThisBuild / baseDirectory).value / target.value.getName / "sonatype-staging" / (ThisBuild / version).value

publishTo := sonatypePublishToBundle.value
// ThisBuild / publishTo := {
//   val nexus = "https://s01.oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }

// ThisBuild / publishTo := Some(
//   Resolver.file("file", new File("/Users/timothy.whittaker/tmp"))
// )

ThisBuild / publishMavenStyle := true

crossScalaVersions := Seq("2.11.12", "2.12.13")

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.json4s" %% "json4s-native" % "3.5.3", // compatible with spark
  "org.json4s" %% "json4s-jackson" % "3.5.3", // comparitlbe with spark
  "org.json4s" %% "json4s-ext" % "3.5.3", // compatible with spark
  "org.apache.spark" %% "spark-core" % "2.4.6" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.4.6" % "provided",
  "org.apache.spark" %% "spark-mllib" % "2.4.6" % "provided",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "org.scala-graph" %% "graph-core" % "1.11.5",
  "org.scala-graph" %% "graph-dot" % "1.11.5",
  "org.yaml" % "snakeyaml" % "1.28"
)

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfuture"
)

// export GPG_TTY=$(tty)
// make sure to set env var PGP_PASSPHRASE