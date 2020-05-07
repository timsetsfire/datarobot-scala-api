package com.github.timsetsfire.datarobot

import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import scalaj.http.HttpOptions

import org.json4s.jackson.Serialization.{write, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.Utilities._
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import com.github.timsetsfire.datarobot.enums._
import com.github.timsetsfire.datarobot.Implicits._

import org.json4s._
// import org.json4s.jackson.Serialization.write
// import org.json4s.jackson.JsonMethods._
// import org.json4s.native.JsonMethods
// import org.json4s.{DefaultFormats, Extraction, JValue}
// import com.github.timsetsfire.datarobot.Utilities._getDataReady
// import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
// import com.github.timsetsfire.datarobot.enums._
// import com.github.timsetsfire.datarobot.Utilities._
// import com.github.timsetsfire.datarobot.Implicits._

case class PredictionDataset(
    name: String,
    id: String,
    projectId: String,
    numColumns: Int,
    numRows: Int,
    created: String,
    forecastPoint: Option[String],
    forecastPointCutoff: Option[String],
    predictionsStartDate: Option[String],
    predictionsEndDate: Option[String],
    actualValueColumn: Option[String],
    hasTimeSeriesExpandedDataset: Boolean,
    containsTargetValues: Option[Boolean],
    detectedActualValueColumns: Option[Array[String]],
    dataQualityWarnings: Map[String, Boolean],
    dataStartDate: Option[String],
    dataEndDate: Option[String],
    forecastPointRange: Seq[String], 
    maxForecastDate: Option[String]
) {
  override def toString = s"PredictionDataset($name)"
}

object PredictionDataset {

  def getPredictionDatasets(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/$projectId/predictionDatasets/").asString
    val JObject(data) = parse(r.body)
    val JArray(json) = data(2)._2
    json.map{ _.extract[PredictionDataset]}
  }

  def getPredictionDataset(projectId: String, id: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/$projectId/predictionDatasets/$id/").asString
    parse(r.body).extract[PredictionDataset]
  }

  def uploadDataset(
      projectId: String, 
      file: String,
      relaxKnownInAdvanceFeaturesCheck: Option[Boolean] = None,
      forecastPoint: Option[String] = None,
      predictionsStartDate: Option[String] = None,
      predictionsEndDate: Option[String] = None,
      credentials: Option[List[String]] = None, // Placeholder - need to fix this
      secondaryDatasetsConfigId: Option[String] = None
  )(implicit client: DataRobotClient)= {

    credentials match {
      case Some(s) =>
        throw new Exception(
          "predictions with secondary datasets not yet supported via api"
        )
      case _ => Unit
    }
    secondaryDatasetsConfigId match {
      case Some(s) =>
        throw new Exception(
          "predictions with secondary datasets not yet supported via api"
        )
      case _ => Unit
    }
    val data = Seq(
      ("relaxKnownInAdvanceFeaturesCheck", relaxKnownInAdvanceFeaturesCheck),
      ("forecastPoint", forecastPoint),
      ("predictionsEndDate", predictionsEndDate),
      ("predictionsStartDate", predictionsStartDate)
    ).filter { _._2.getOrElse("") != "" }.map {
      case (k, v) => (k, v.get.toString)
    }

    val fs: java.io.InputStream =
      new java.io.FileInputStream(file)
    val bytesInStream = fs.available
    val dataMP = MultiPart(
      name = "file",
      filename = file,
      mime = "text/csv",
      data = fs,
      numBytes = bytesInStream,
      lenWritten => Unit
    )

    val status = client
      .postMulti(s"projects/$projectId/predictionDatasets/fileUploads/", dataMP)
      .params(data)
      .asString
    status.code match {
      case 202    => Unit
      case x: Int => throw new Exception(s"$x: ${status.body}")
    }
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), 600000)

    val r = client.get(loc(0).replace(client.endpoint, "")).asString
    parse(r.body).extract[PredictionDataset]

  }



  def uploadSparkDf(
      projectId: String, 
      df: org.apache.spark.sql.DataFrame,
      relaxKnownInAdvanceFeaturesCheck: Option[Boolean] = None,
      forecastPoint: Option[String] = None,
      predictionsStartDate: Option[String] = None,
      predictionsEndDate: Option[String] = None,
      credentials: Option[List[String]] = None, // Placeholder - need to fix this
      secondaryDatasetsConfigId: Option[String] = None, 
      maxWait: Int = 60000
  )(implicit client: DataRobotClient)= {

    credentials match {
      case Some(s) =>
        throw new Exception(
          "predictions with secondary datasets not yet supported via api"
        )
      case _ => Unit
    }
    secondaryDatasetsConfigId match {
      case Some(s) =>
        throw new Exception(
          "predictions with secondary datasets not yet supported via api"
        )
      case _ => Unit
    }
    val data = Seq(
      ("relaxKnownInAdvanceFeaturesCheck", relaxKnownInAdvanceFeaturesCheck),
      ("forecastPoint", forecastPoint),
      ("predictionsEndDate", predictionsEndDate),
      ("predictionsStartDate", predictionsStartDate)
    ).filter { _._2.getOrElse("") != "" }.map {
      case (k, v) => (k, v.get.toString)
    }

    val fs = DataFrameAsInputStream(df)
    val bytesInStream = fs.numBytes
    val dataMP = MultiPart(
      name = "file",
      filename = "Spark DataFrame",
      mime = "text/csv",
      data = fs,
      numBytes = bytesInStream,
      lenWritten => Unit
    )

    val status = client
      .postMulti(s"projects/$projectId/predictionDatasets/fileUploads/", dataMP)
      .params(data).options(HttpOptions.connTimeout(650)).option(HttpOptions.readTimeout(maxWait)).asString
    status.code match {
      case 202    => Unit
      case x: Int => throw new Exception(s"$x: ${status.body}")}
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)

    val r = client.get(loc(0).replace(client.endpoint, "")).asString
    parse(r.body).extract[PredictionDataset]

  }
}






// import com.github.timsetsfire.datarobot._
// import com.github.timsetsfire.datarobot.enums._
// import com.github.timsetsfire.datarobot.Implicits._

// val token = "L-KEWy2boF5520IYONK096R-XzHOuVsk"
// val DATAROBOT_ENDPOINT = "https://app.datarobot.com/api/v2/"
// implicit val client = DataRobotClient(token, DATAROBOT_ENDPOINT)

// val file = "./data/sales_multiseries_training.csv"

// val projectId = "5ea0fad92bbcef013104c3a7"
// // val p = Project.get(projectId).toDateTimeProject
// // // val m = DateTimeModel.getDateTimeModel(p.id, "5ea0fe73db476d0e5dcac775")
// val relaxKnownInAdvanceFeaturesCheck: Option[Boolean] = Some(true) //Some(true)
// val forecastPoint: Option[String] = Some("2014-07-04")
// val predictionsEndDate: Option[String] = None
// val predictionsStartDate: Option[String] = None
// val credentials: Option[String] = None
// val secondaryDatasetsConfigId: Option[String] = None
