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

import breeze.linalg.Counter2

case class FeatureAssociation()

case class FeatureAssocationDetails(feature1: String, feature2: String)

case class FeatureAssociationMatrix(feature1: String, feature2: String)
case class FeatureAssociationDetail(
    feature: Option[String],
    importanceSortIndex: Option[Int],
    clusterSortIndex: Option[Int],
    clusterName: Option[String],
    alphabeticSortIndex: Option[Int],
    clusterId: Option[Int],
    strengthSortIndex: Option[Int]
)

object FeatureAssociations {
  def getFeatureAssocationMatrix(
      projectId: String,
      metric: FeatureAssociationMetric.Value =
        FeatureAssociationMetric.MUTUALINFO,
      atype: FeatureAssociationType.Value = FeatureAssociationType.ASSOCIATION,
      featurelistId: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val url = s"projects/${projectId}/featureAssociationMatrix/"
    val params: Seq[(String, String)] = Seq(
      "metric" -> FeatureAssociationMetric.MUTUALINFO.toString,
      "type" -> FeatureAssociationType.ASSOCIATION.toString
    )
    val fam: Counter2[String, String, Double] = Counter2()
    // val fam2: Counter2[String, String, Double] = Counter2()
    val r = featurelistId match {
      case Some(fls) =>
        client.get(url).params(params ++ Seq("featurelistId" -> fls)).asString
      case _ => client.get(url).params(params).asString
    }
    val map = r.code match {
      case 200 => parse(r.body)
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }

    val JObject(data) = parse(r.body)
    val JArray(strengths) = data(0)._2
    val JArray(features) = data(1)._2

    val fad = features.map { _.extract[FeatureAssociationDetail] }

    strengths.map { _.extract[Map[String, Any]] }.foreach { map =>
      fam.update(
        map("feature1").toString,
        map("feature2").toString,
        map("statistic").asInstanceOf[Number].doubleValue
      )
    }
    parse(r.body).extract[Map[String, List[Map[String, Any]]]]
    (fam, fad)
  }

}
