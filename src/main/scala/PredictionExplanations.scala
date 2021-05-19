package com.github.timsetsfire.datarobot

import scala.util.Try
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

object PredictionExplanations {

  /** Initialize prediction explanations.  This is a prereq for request prediction explanations
    * @param projectId
    * @param modelId
    */
  def initializePredictionExplanations(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .post(
        s"projects/${projectId}/models/${modelId}/predictionExplanationsInitialization/"
      )
      .asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    parse(job.body).extract[Job]
  }

  /** get a sample of prediction explanations.  This is a prereq for request prediction explanations
    * @param projectId
    * @param modelId
    */
  def getPredExpInit(
      projectId: String,
      modelId: String,
      excludeAdjustedPredictions: Option[Boolean] = None
  )(
      implicit client: DataRobotClient
  ) = {

    val r = excludeAdjustedPredictions match {
      case Some(s) =>
        client
          .get(
            s"projects/${projectId}/models/${modelId}/predictionExplanationsInitialization/"
          )
          .param("excludeAdjustedPredictions", s.toString)
          .asString
      case _ =>
        client
          .get(
            s"projects/${projectId}/models/${modelId}/predictionExplanationsInitialization/"
          )
          .asString
    }

    // need to convert to PredicitonExplanations

  }

  /** Delete an existing prediction initialization
    * @param projectId
    * @param modelId
    */
  def deletePredExpInit(
      projectId: String,
      modelId: String,
      excludeAdjustedPredictions: Option[Boolean] = None
  )(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .delete(
        s"projects/${projectId}/models/${modelId}/predictionExplanationsInitialization/"
      )
      .asString
    r.code
  }

    /** Initialize prediction explanations.  This is a prereq for request prediction explanations
    * @param projectId
    * @param modelId
    */
  def getPredExp(projectId: String, predExpId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .get(s"projects/${projectId}/predictionExplanationsRecords/${predExpId}/")
      .asString

  }

  def deletePredExp(projectId: String, predExpId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .delete(
        s"projects/${projectId}/predictionExplanationsRecords/${predExpId}/"
      )
      .asString
  }

  def getPredExps(
      projectId: String,
      modelId: Option[String] = None,
      offset: Option[Int] = None,
      limit: Option[Int] = None
  )(
      implicit client: DataRobotClient
  ) = {

    val params =
      Seq(("modelId", modelId), ("offset", offset), ("limit", limit))
        .filter {
          _._2.isDefined
        }
        .map { case (k, v) => (k, v.get.toString) }
    val r = client
      .delete(s"projects/${projectId}/predictionExplanationsRecords/")
      .params(params)
      .asString
  }

}
