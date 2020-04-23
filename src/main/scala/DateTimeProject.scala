package com.github.timsetsfire.datarobot

import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import scalaj.http.HttpOptions
import org.json4s._
import org.json4s.jackson.Serialization.{write, formats}
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

  /**
    * @todo implement this
    */
  def createModelingFeaturelist() =
    throw new NotImplementedError("This is not a Timeseries project")

  /**
    * @todo implement this
    */
  def getModelingFeaturelists(projectId: String)(
      implicit client: DataRobotClient
  ) = throw new NotImplementedError("This is not a Timeseries project")

  /**
    * @todo implement this
    */
  def getModelingFeatures(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("This is not a Timeseries project")

  /**
    * @todo implement this
    */
  def getDateTimeModel(modelId: String)(implicit client: DataRobotClient) =
    DateTimeModel.getDateTimeModel(id, modelId)

  /**
    * @todo implement this
    */
  def getDateTimeModels()(implicit client: DataRobotClient) =
    DateTimeModel.getDateTimeModels(id)

  def getDateTimePartitioning()(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${id}/datetimePartitioning/").asString
    val json = parse(r.body)
    json.extract[DateTimePartition]
  }

  // post datetime adn frozendatetime models page 83
}

// object DateTimeProject {

//   def multiseriesProperties(projectId: String, datetimePartitionColumn: String, multiseriesIdColumns: Seq[String])(
//     implicit client: DataRobotClient
//   ) = {
//     val data = _getDataReady( Seq("datetimePartitionColumn" -> datetimePartitionColumn, "multiseriesIdColumns" -> multiseriesIdColumns))
//     val r = client.postData("projects/${projectId}/multiseriesProperties/", data).asString
//     r
//   }

//   def getMultiseriesProperties(projectId: String, datetimePartitionColumn: String)(
//     implicit client: DataRobotClient
//   ) = {
//     val r = client.get("projects/${projectId}/features/${datetimePartitionColumn}/multiseriesProperties/").asString
//     r
//   }

// }
