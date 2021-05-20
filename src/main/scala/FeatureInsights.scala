package io.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.{write, writePretty}
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import io.github.timsetsfire.datarobot.Utilities._getDataReady
import io.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import io.github.timsetsfire.datarobot.enums._
import io.github.timsetsfire.datarobot.Utilities._
import io.github.timsetsfire.datarobot.Implicits._

object FeatureInsights {

  def getFeatureInsightsMetaData(modelType: String, featureInsight: String)(
      projectId: String,
      modelId: String
  )(implicit client: DataRobotClient) = { 
      val r = client.get(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}Metadata/").asString
      writePretty(parse(r.body))
  }

  def requestFeatureInsights(modelType: String, featureInsight: String)(
      projectId: String,
      modelId: String,
      backtestIndex: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val r = backtestIndex match { 
        case Some(bti) => {
            val data = _getDataReady(Seq("backtestIndex" -> backtestIndex))
            client.postData(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}/",data).asString
        }
        case None => client.post(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}/").asString
    }
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    parse(job.body).extract[Job]
  }

  def requestAndGetFeatureInsights(modelType: String, featureInsight: String)(
      projectId: String,
      modelId: String,
      source: String = "validation",
      backtestIndex: Option[String] = None,
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val r = backtestIndex match { 
        case Some(bti) => {
            val data = _getDataReady(Seq("backtestIndex" -> backtestIndex))
            client.postData(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}/",data).asString
        }
        case None => client.post(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}/").asString
    }
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val params = backtestIndex match { 
        case None =>  Seq("source" -> source) 
        case Some(bti) => Seq("backtestIndex" -> bti, "source" -> source)
    }
    val job = client.get(loc).params(params).asString
    parse(job.body).extract[Job].getResultWhenComplete(maxWait)
  }

  def getFeatureInsights(modelType: String, featureInsight: String)(
      projectId: String,
      modelId: String,
      source: String = "validation",
      backtestIndex: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val params = backtestIndex match { 
        case Some(bti) => Seq("backtestIndex" -> bti, "source" -> source)
        case None => Seq("source" -> source)
    }
    val r = client
      .get(s"projects/${projectId}/${modelType}/${modelId}/${featureInsight}/")
      .params(params)
      .asString
    r.code match {
      case 200 => featureInsight match { 
          case "featureEffects" => parse(r.body).extract[FeatureEffects]
          case "featureFit" => parse(r.body).extract[FeatureFits]
          case _ => throw new Exception(s"do not understand ${featureInsight}")
      }
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }
}
