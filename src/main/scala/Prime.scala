package com.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.{write, writePretty}
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.Utilities._getDataReady
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import com.github.timsetsfire.datarobot.enums._
import com.github.timsetsfire.datarobot.Utilities._
import com.github.timsetsfire.datarobot.Implicits._

import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

class PrimeModel(
    featurelistId: String,
    processes: Array[String],
    featurelistName: String,
    projectId: String,
    samplePct: Option[Double],
    trainingDuration: Option[String],
    trainingRowCount: Option[Int],
    trainingStartDate: Option[String],
    trainingEndDate: Option[String],
    modelCategory: String,
    isFrozen: Boolean = false,
    metrics: Map[String, Metric],
    modelType: String,
    blueprintId: String,
    monotonicIncreasingFeaturelistId: Option[String] = None,
    monotonicDecreasingFeaturelistId: Option[String] = None,
    supportsMonotonicConstraints: Option[Boolean] = None,
    id: String,
    isStarred: Boolean = false,
    predictionThreshold: Option[Double],
    predictionThresholdReadOnly: Option[Boolean],
    val parentModelId: String,
    val rulesetId: Int,
    val ruleCount: Int,
    val score: Double
) extends Model(
      featurelistId,
      processes,
      featurelistName,
      projectId,
      samplePct,
      trainingDuration,
      trainingRowCount,
      trainingStartDate,
      trainingEndDate,
      modelCategory,
      isFrozen,
      metrics,
      modelType,
      blueprintId,
      monotonicIncreasingFeaturelistId,
      monotonicDecreasingFeaturelistId,
      supportsMonotonicConstraints,
      id,
      isStarred,
      predictionThreshold,
      predictionThresholdReadOnly
    )

object PrimeModel {

  def checkPrimeEligiblility(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r =
      client.get(s"projects/$projectId/models/$modelId/primeInfo/").asString
    writePretty(parse(r.body))
  }

  def getPrimeModel(projectId: String, primeModelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r =
      client.get(s"projects/$projectId/primeModels/$primeModelId/").asString
    parse(r.body).extract[PrimeModel]
  }

  def getPrimeModels(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/$projectId/primeModels/").asString
    val JObject(data) = parse(r.body)
    val JArray(json) = data(2)._2
    json.map { _.extract[PrimeModel] }
  }

  def createPrimeRuleset(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .post(s"projects/$projectId/models/$modelId/primeRulesets/")
      .asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[Job]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def getPrimeRulesets(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r =
      client.get(s"projects/$projectId/models/$modelId/primeRulesets/").asString
    val JArray(json) = parse(r.body)
    json.map { _.extract[PrimeRuleset] }
  }

  def createPrimeModel(projectId: String, modelId: String, rulesetId: String)(
      implicit client: DataRobotClient
  ) = {
    val data = _getDataReady(
      Seq(("parentModelId", modelId), ("rulesetId", rulesetId))
    )
    val r = client.postData(s"projects/$projectId/primeModels/", data).asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[Job]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def createPrimeModelCode(
      projectId: String,
      primeModelId: String,
      language: String = "python"
  )(implicit client: DataRobotClient) = {
    val data = _getDataReady(
      Seq(("modelId", primeModelId), ("language", language))
    )
    val r = client.postData(s"projects/$projectId/primeFiles/", data).asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[Job]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def getPrimeModelCodeMetaData(projectId: String, primeFileId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client.get(s"projects/$projectId/primeFiles/$primeFileId/").asString
    parse(r.body).extract[PrimeFileMetaData]
  }

  def downloadPrimeModelCode(projectId: String, primeFileId: String, path: String = "./")(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .get(s"projects/$projectId/primeFiles/$primeFileId/download/")
      .asBytes
    val fileName = r
      .headers("Content-Disposition")
      .apply(0)
      .replace("attachment;filename=\"", "")
      .init
    val byteArrayOutputStream = new ByteArrayOutputStream() //.getBytes("UTF-8")
    r.code match {
      case 200 => byteArrayOutputStream.write(r.body)
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    byteArrayOutputStream.writeTo(new java.io.FileOutputStream(path + fileName))
  }
}

case class PrimeRuleset(
    projectId: String,
    rulesetId: String,
    score: Double,
    parentModelId: String,
    ruleCount: Int,
    modelId: Option[String]
)
case class PrimeRulesets(rules: List[PrimeRuleset])
case class PrimeFileMetaData(
    language: String,
    isValid: Boolean,
    rulesetId: String,
    parentModelId: String,
    projectId: String,
    id: String,
    modelId: String
)
