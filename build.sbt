

ThisBuild / organization := "com.datarobot"


libraryDependencies  ++= Seq(
  "com.lihaoyi" %% "ujson" % "0.7.5",
  "com.lihaoyi" %% "requests" % "0.2.0",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.json4s" %% "json4s-native" % "3.6.7",
  "org.json4s" %% "json4s-jackson" % "3.6.7"

)
// libraryDependencies ++= Seq(
//     "com.typesafe.akka" %% "akka-actor" % "2.5.13",
//     "com.typesafe.akka" %% "akka-stream" % "2.5.13",
//     "com.typesafe.akka" %% "akka-http" % "10.1.3",
//   )
// libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "2.4.2"
