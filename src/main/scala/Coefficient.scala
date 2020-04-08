// import org.apache.spark.SparkConf
// import org.apache.spark.sql.SparkSession
// import org.apache.spark.SparkContext
// import org.apache.log4j.Logger
// import org.apache.log4j.Level
// import com.github.timsetsfire.datarobot._

// Logger.getLogger("org").setLevel(Level.WARN)
// Logger.getLogger("akka").setLevel(Level.WARN)

// val conf = new SparkConf().setMaster("local[*]").setAppName("scoring-app").set("spark.driver.maxResultSize", "2G")
// // conf.setJars(Array("DRSpark-assembly-0.5.0.jar", "5cdab64d2153872de37697c1aa.jar")) 
// val sc = new SparkContext(conf)  // initialize spark context
// val sqlContext = new org.apache.spark.sql.SQLContext(sc)  // initialize sql context
// implicit val spark = SparkSession.builder.config(conf).getOrCreate() // start spark session 
// import spark.implicits._

// val c: scala.collection.immutable.Map[String,Any] = Map("coefficient" -> 0.07845659765035268, "originalFeature" -> "addr_state", "stageCoefficients" -> List(), "transformations" -> List(Map("name" -> "One-hot", "value" -> "AZ")), "derivedFeature" -> "addr_state-AZ", "type" -> "CAT")

package com.github.timsetsfire.datarobot

import breeze.linalg.Counter

case class Coefficient(
    coefficient: Double,
    originalFeature: String,
    stageCoefficients: List[String],
    transformations: List[Map[String, String]],
    derivedFeature: String,
    `type`: String)

case class ModelCoefficients(modelId: String, coefficients: List[Coefficient], intercept: Option[Double] = None, link: Option[String] = None) { 
    override def toString = s"ModelCoefficients(${modelId})"

    val coefCounter: Counter[String, Double] = Counter()
    
    coefficients.foreach{ coef => 
        coefCounter.update( coef.derivedFeature, coef.coefficient)
    }
}


