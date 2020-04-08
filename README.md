# datarobot scala api

Scala API (very rough)

```
import com.github.timsetsfire.datarobot._
import com.github.timsetsfire.datarobot.Implicits._
val DATAROBOT_API_TOKEN = "your-key"
val DATAROBOT_ENDPOINT = "https://app.datarobot.com/api/v2/"
implicit val client = DataRobotClient(DATAROBOT_API_TOKEN, DATAROBOT_ENDPOINT)
val file = "code/10K_Lending_Club_Loans.csv"
val project = Project.createFromFile(file, "LendingClub Scala API v6")
project.setTarget("is_bad" mode = "manual")
val blueprints = project.getBlueprints
project.train(blueprints(0))
project.setWorkerCount(20)
```

### Spark Example 

The intention of putting in support for spark DataFrame was to be able to create projects
on datasets less than 5GB in size.  Anything larger than that, I would recommend use HDFS or Hive connection to get data into datarobot.  Creating a Project from a Spark Dataframe does so by avoiding a write to disk and then load.  It also avoids `collect`.  

While `collect` executes given job in all partitions (executors side) and collects all results (driver side) with `Array.concat(results: _*)` method. The `toLocalIterator` does the contrary. Instead of launching the job simultaneously on all partitions it executes the job on 1 partition at once. So, the driver must have enough memory to store the biggest partition.  

I've tested this out with a 1.5GB dataset on Databrick Community.  
Cluster details: 0 Workers, 1 Driver: 15.3 GB Memory, 2 Cores, 1 DBU


```code :scala
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.github.timsetsfire.datarobot._
import com.github.timsetsfire.datarobot.Implicits._

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
val project = Project.createFromSparkDf(df, "LendingClub Scala API")
```