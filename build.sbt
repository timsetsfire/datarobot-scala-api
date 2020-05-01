scalaVersion := "2.11.8"

version := "0.1.0"

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
  "org.apache.spark" %% "spark-core" % "2.4.4" ,
  "org.apache.spark" %% "spark-sql" % "2.4.4" ,
  "org.apache.spark" %% "spark-mllib" % "2.4.4" ,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
)

// libraryDependencies ++= Seq(scalaVersion("org.scala-lang" % "scala-reflect" % _))

// libraryDependencies ++= Seq(scalaVersion("org.scala-lang" % "scalap" % _))



// libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2"
