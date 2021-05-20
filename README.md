# datarobot scala api (very rough)

still working to add documentation for nice javadocs.  

## getting started

clone this repo and build via sbt

Check it out in action at [Zepl notebook](https://bit.ly/33WAWkL)

## Usage 

```
import io.github.timsetsfire.datarobot._
import io.github.timsetsfire.datarobot.Implicits._
val DATAROBOT_API_TOKEN = "your-key"
val DATAROBOT_ENDPOINT = "https://app.datarobot.com/api/v2/"
implicit val client = DataRobotClient(DATAROBOT_API_TOKEN, DATAROBOT_ENDPOINT)
// or can take a yaml file with keys token and endpoint
// implicit val client = DataRobotClient("config.yaml")
val file = "code/10K_Lending_Club_Loans.csv"
val project = Project(file, "LendingClub Scala API v6")
project.setTarget("is_bad" mode = "manual")
val blueprints = project.getBlueprints
project.train(blueprints(0))
project.setWorkerCount(-1)
```

### Spark Example 

The intention of putting in support for spark DataFrame was to be able to create projects with Spark DataFrames less than 5GB in size.  Anything larger than that, I would recommend use HDFS or Hive connection to get data into datarobot.  Also, this is pushing up against the data cap on DataRobot managed cloud.  Creating a Project from a Spark Dataframe does avoid writing data to disk, and it also avoids `collect`.  

While `collect` executes given job in all partitions (executors side) and collects all results (driver side) with `Array.concat(results: _*)` method. The `toLocalIterator` does the contrary. Instead of launching the job simultaneously on all partitions it executes the job on 1 partition at once. So, the driver must have enough memory to store the biggest partition.  This is the main utility leverage to create the form data that is ultimately posted to DataRobot.  I'm sure it's probably not doing exactly what I want, but it does work and could probably be made much better.  

I've tested this out with a 1.5GB dataset on Databrick Community.  
Cluster details: 0 Workers, 1 Driver: 15.3 GB Memory, 2 Cores, 1 DBU

It runs a little slow, and it may make more sense to actually write the data to disk and then load, but have not comparison in terms of timing.  


```code :scala
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level
import io.github.timsetsfire.datarobot._
import io.github.timsetsfire.datarobot.Implicits._

Logger.getLogger("org").setLevel(Level.WARN)
Logger.getLogger("akka").setLevel(Level.WARN)

val conf = new SparkConf().setMaster("local[*]").setAppName("scoring-app")
val sc = new SparkContext(conf)  // initialize spark context
val sqlContext = new org.apache.spark.sql.SQLContext(sc)  // initialize sql context
implicit val spark = SparkSession.builder.config(conf).getOrCreate() // start spark session 
import spark.implicits._

val readerOptions = Map("header" -> "true", "inferSchema" -> "true") 
val df = sqlContext.read.format("csv").options(readerOptions).load("/Users/timothy.whittaker/Desktop/sbt-projects/datarobot/code/10K_Lending_Club_Loans.csv")

val DATAROBOT_API_TOKEN = "your-token"
val DATAROBOT_ENDPOINT = "https://app.datarobot.com/api/v2/"
implicit val client = DataRobotClient(DATAROBOT_API_TOKEN, DATAROBOT_ENDPOINT)
val project = Project(df, "LendingClub Scala API")
```

A cool thing about using with Spark -> alot of the `get` methods return lists of case classes.  For example, with Features in a DataRobot project, we can do something like

```:scala
val features = project.getFeatures
val featuresDf = features.toDF
```

Using DataBricks or Zeppelin provide immediate functionality to query the dataframe and put some vizualizations on top of it.  
