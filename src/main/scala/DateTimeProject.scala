package com.github.timsetsfire.datarobot

import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import scalaj.http.HttpOptions
import org.json4s._
import org.json4s.jackson.Serialization.{write, writePretty, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.Utilities._
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats

import com.github.timsetsfire.datarobot.enums._
import com.github.timsetsfire.datarobot.Implicits._

class DateTimeProject(
    id: String,
    projectName: String,
    fileName: String,
    stage: String,
    autopilotMode: Option[Double],
    created: String,
    target: Option[String],
    metric: Option[String],
    partition: Option[Partition],
    recommender: Option[Recommender],
    advancedOptions: Option[AdvancedOptions],
    positiveClass: Option[Double],
    maxTrainPct: Option[Double],
    maxTrainRows: Option[Double],
    scaleoutMaxTrainPct: Option[Double],
    scaleoutMaxTrainRows: Option[Double],
    holdoutUnlocked: Boolean = false,
    targetType: Option[String]
) extends Project(
      id,
      projectName,
      fileName,
      stage,
      autopilotMode,
      created,
      target,
      metric,
      partition,
      recommender,
      advancedOptions,
      positiveClass,
      maxTrainPct,
      maxTrainRows,
      scaleoutMaxTrainPct,
      scaleoutMaxTrainRows,
      holdoutUnlocked,
      targetType
    ) {
  override def toString = s"DateTimeProject(${projectName})"

  def createModelingFeaturelist() =
    throw new NotImplementedError("Not implemented")

  def getModelingFeaturelists()(
      implicit client: DataRobotClient
  ) = ModelingFeaturelist.getModelingFeaturelists(id)

  def getModelingFeaturelist(featurelistId: String)(
      implicit client: DataRobotClient
  ) = ModelingFeaturelist.getModelingFeaturelist(id, featurelistId)

  def getModelingFeatures()(implicit client: DataRobotClient) =
    ModelingFeature.getModelingFeatures(id)

  def getDateTimeModel(modelId: String)(implicit client: DataRobotClient) =
    DateTimeModel.getDateTimeModel(id, modelId)

  def getDateTimeModels()(implicit client: DataRobotClient) =
    DateTimeModel.getDateTimeModels(id)

  def getDateTimePartitioning()(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${id}/datetimePartitioning/").asString
    val json = parse(r.body)
    json.extract[DateTimePartition]
  }

  def trainDateTime(
      blueprint: Blueprint,
      featurelistId: Option[String] = None,
      trainingDuration: Option[String] = None,
      trainingRowCount: Option[Int],
      useProjectSettings: Option[Boolean] = None,
      sourceProjectId: Option[String] = None,
      timeWindowSamplePct: Option[Int] = None,
      samplingMethod: Option[String] = None,
      monotonicIncreasingFeaturelistId: Option[String] = None,
      monotonicDecreasingFeaturelistId: Option[String] = None
  )(implicit client: DataRobotClient) = {

    val params = Seq(
      "blueprintId" -> blueprint.id,
      "featurelistId" -> featurelistId,
      "trainingDuration" -> trainingDuration,
      "trainingRowCount" -> trainingRowCount,
      "useProjectSettings" -> useProjectSettings,
      "sourceProjectId" -> sourceProjectId,
      "timeWindowSamplePct" -> timeWindowSamplePct,
      "samplingMethod" -> samplingMethod,
      "monotonicIncreasingFeaturelistId" -> monotonicIncreasingFeaturelistId,
      "monotonicDecreasingFeaturelistId" -> monotonicDecreasingFeaturelistId
    )
    val data = _getDataReady(params)
    val r =
      client.postData(s"projects/${this.id}/datetimeModels/", data).asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[ModelJob]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }
  // post datetime adn frozendatetime models page 83
}

object DateTimeProject {

  def multiseriesProperties(
      projectId: String,
      datetimePartitionColumn: String,
      multiseriesIdColumns: Seq[String]
  )(
      implicit client: DataRobotClient
  ) = {
    val data = _getDataReady(
      Seq(
        "datetimePartitionColumn" -> datetimePartitionColumn,
        "multiseriesIdColumns" -> multiseriesIdColumns
      )
    )
    val r = client
      .postData(s"projects/${projectId}/multiseriesProperties/", data)
      .asString
    val loc = r.headers("location")
    r
  }
            
  def getMultiseriesProperties(
      projectId: String,
      datetimePartitionColumn: String
  )(
      implicit client: DataRobotClient
  ) = {
    val r = client.get(
        s"projects/${projectId}/features/${datetimePartitionColumn}/multiseriesProperties/"
      ).asString
    writePretty(parse(r.body))
  }
}

// import java.time.format.DateTimeFormatter;
// val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(res13.dateFormat.get.replace("%", "").toUpperCase);

//         //Date string with offset information
//         val  dateString = "03/08/2019T16:20:17:717+05:30";
//         val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu'T'HH:mm:ss:SSSXXXXX");
//         java.time.Instant.parse(dateString, DATE_TIME_FORMATTER);
//         //Date string with offset information
//         String dateString = "03/08/2019T16:20:17:717+05:30";
