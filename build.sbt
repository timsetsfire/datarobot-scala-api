name := "datarobot-scala"

version := "0.1.0"

organization := "io.github.timsetsfire"

scalaVersion := "2.12.8"

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

enablePlugins(BuildInfoPlugin)
buildInfoKeys := Seq[BuildInfoKey](version)
buildInfoPackage := "datarobot"

crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.8", "2.13.0")

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Xfuture"
)

// scalacOptions in (Compile, doc) ++= Seq("-doc-root-content", baseDirectory.value+"/root-doc.txt")

// publishTo := sonatypePublishToBundle.value

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

publishTo := Some(
  Resolver.file("file", new File("/Users/timothy.whittaker/tmp"))
)

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>http://github.com/timsetsfire/scala-datarobot-api</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:timsetsfire/datarobot-scala-api.git</url>
    <connection>scm:git:git@github.com:timsetsfire/datarobot-scala-api.git</connection>
  </scm>
  <developers>
    <developer>
      <id>timsetsfire</id>
      <name>Tim Whittaker</name>
      <url>http://github.com/timsetsfire</url>
    </developer>
  </developers>
)
