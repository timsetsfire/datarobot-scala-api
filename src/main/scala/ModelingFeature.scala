
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

case class ModelingFeature(
    uniqueCount: Int, lowInformation: Boolean, 
    name: String, 
    featureType: String, 
    importance: Double, 
    max: Option[String], 
    dateFormat: Option[String],
    median: Option[String],
    targetLeakage: String,
    min: Option[String],
    stdDev: Option[Double],
    projectId: String,
    naCount: Option[Int],
    parentFeatureNames: Option[Seq[String]],
    featureLineageId: Option[String],
    mean: Option[String]
) {
    override def toString = s"ModelingFeature(${name})"
}


object ModelingFeature {

    def getModelingFeatures(projectId: String)(implicit client: DataRobotClient) = { 
        val r = client.get(s"projects/${projectId}/modelingFeatures/").asString
        val JObject(ls) = parse(r.body)
        val JArray(json) = ls(2)._2
        json.map { j => j.extract[ModelingFeature] }
    }
}
