scalaVersion := "2.12.8"

version := "0.1.0"

// resolvers ++= Seq(
//   "Spring" at "https://repo.spring.io/plugins-release/"
// )

resolvers += Resolver.bintrayRepo("stanch", "maven")

libraryDependencies  ++= Seq(

  "org.scalaj" %% "scalaj-http" % "2.4.2",
  // "org.json4s" %% "json4s-native" % "3.6.7",
  // "org.json4s" %% "json4s-jackson" % "3.6.7"
  // "org.json4s" %% "json4s-native" % "3.7.0-M2",
  // "org.json4s" %% "json4s-jackson" % "3.7.0-M2",
  // "org.json4s" %% "json4s-ext" % "3.7.0-M2",
  "org.json4s" %% "json4s-native" % "3.5.3", // compatible with spark
  // "org.json4s" %% "json4s-jackson" % "3.5.3", // comparitlbe with spark
  "org.json4s" %% "json4s-ext" % "3.5.3",     // compatible with spark
  "org.apache.spark" %% "spark-core" % "2.4.4" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.4.4" % "provided",
  "org.apache.spark" %% "spark-mllib" % "2.4.4" % "provided",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2", 
  // "org.stanch" %% "reftree" % "1.1.3",
  "org.scala-graph" %% "graph-core" % "1.11.5",
// libraryDependencies += "org.scala-graph" %% "graph-dot" % "1.12.5"
 "org.scala-graph" %% "graph-dot" % "1.11.5"

)
// libraryDependencies += "org.stanch" %% "reftree" % "latest-version"


// libraryDependencies ++= Seq(scalaVersion("org.scala-lang" % "scala-reflect" % _))

// libraryDependencies ++= Seq(scalaVersion("org.scala-lang" % "scalap" % _))



// libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2"
// val renderer = Renderer(
//   renderingOptions = RenderingOptions(density = 75),
//   directory = Paths.get("code", "viz")
// )
// import renderer._