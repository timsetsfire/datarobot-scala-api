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

case class ModelingFeaturelist(
    id: Option[String],
    projectId: Option[String],
    name: Option[String],
    features: Option[Seq[String]],
    numModels: Option[Int],
    created: Option[String],
    isUserCreated: Option[String],
    description: Option[String]
) {
  override def toString = s"ModelingFeaturelist(${name.get})"

}
object ModelingFeaturelist {

  def getModelingFeaturelist(projectId: String, featurelistId: String)(
      implicit client: DataRobotClient
  ) {
    val r = client.get(s"projects/${projectId}/modelingFeaturelists/${featurelistId}/").asString
    parse(r.body).extract[ModelingFeaturelist]
  }

  def getModelingFeaturelists(
      projectId: String
  )(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/modelingFeaturelists/").asString
    val JObject(ls) = parse(r.body)
    val JArray(json) = ls(2)._2
    json.map { j => j.extract[ModelingFeaturelist] }
  }

}
