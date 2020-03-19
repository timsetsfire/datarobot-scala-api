# datarobot scala api

Scala API (very rough)

```
import com.datarobot._
import com.datarobot.Implicits._
val DATAROBOT_API_TOKEN = "your-key"
val DATAROBOT_ENDPOINT = "https://app.datarobot.com/api/v2/"
implicit val client = DataRobotClient(DATAROBOT_API_TOKEN, DATAROBOT_ENDPOINT)
val file = "code/10K_Lending_Club_Loans.csv"
val project = Project.createFromFile(file, "LendingClub Scala API v6")
project.setTarget("is_bad")
project.setWorkerCount(20)
```

### Spark Example 
```
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.datarobot._
import com.datarobot.Implicits._

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