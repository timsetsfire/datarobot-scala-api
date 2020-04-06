package com.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.Utilities._getDataReady
import com.datarobot.enums.EnumFormats.enumFormats
import com.datarobot.enums._
import com.datarobot.Utilities._
import com.datarobot.Implicits._

import java.util.jar.{JarOutputStream, JarInputStream}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
  


/** Model
  * @param featurelistId  – the ID of the featurelist used by the model
  * @param processes  – a json list of processes used by the model
  * @param featurelistName  – the name of the featurelist used by the model
  * @param projectId  – the ID of the project to which the model belongs
  * @param samplePct  – the percentage of the dataset used in training the model
  * @param trainingDuration  – the duration spanned by the dates in the partition column for the data used to train the model
  * @param trainingRowCount  – the number of rows used to train the model
  * @param trainingStartDate  – the start date of the dates in the partition column for the data
used to train the model
  * @param trainingEndDate  – the end date of the dates in the partition column for the data used to train the model
  * @param modelCategory – indicateswhatkindofmodelitis -willbeprimeforDataRobotPrime models, blend for blender models, scaleout for scaleout models, and model for all other models
  * @param isFrozen  – boolean, indicating whether the model is frozen, i.e. uses tuning parameters  from a parent model
  * @param metrics  – the performance of the model according to various metrics, see below
  * @param modelType  – identifies the model, e.g. Nystroem Kernel SVM Regressor
  * @param blueprintId  – the blueprint used to construct the model - note this is not an ObjectId
  * @param monotonicIncreasingFeaturelistId  – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically increasing relationship to the target. If null, no such constraints are enforced.
  * @param monotonicDecreasingFeaturelistId  – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically decreasing relationship to the target. If null, no such constraints are enforced.
  * @param supportsMonotonicConstraints  – (new in v2.11) boolean, whether this model supports enforcing montonic constraints
  * @param id – Model ID
  * @param isStarred  (bool) – (New in version v2.13) whether the model has been starred
  * @param predictionThreshold  (float) – (New in version v2.13) threshold used for binary classification in predictions.
  * @param predictionThresholdReadOnly  (boolean) – (New in version v2.13) indicates whether modification of a predictions threshold is forbidden. Threshold modification is forbidden once a model has had a deployment created or predictions made via the dedicated prediction API.
  */
class Model(
    val featurelistId: String,
    val processes: Array[String],
    val featurelistName: String,
    val projectId: String,
    val samplePct: Option[Double],
    val trainingDuration: Option[String],
    val trainingRowCount: Option[String],
    val trainingStartDate: Option[String],
    val trainingEndDate: Option[String],
    val modelCategory: String,
    val isFrozen: Boolean = false,
    val metrics: Map[String, Any],
    val modelType: String,
    val blueprintId: String,
    val monotonicIncreasingFeaturelistId: Option[String] = None,
    val monotonicDecreasingFeaturelistId: Option[String] = None,
    val supportsMonotonicConstraints: Option[String] = None,
    val id: String,
    var isStarred: Boolean = false,
    var predictionThreshold: Option[String],
    var predictionThresholdReadOnly: Option[String]
) {

  import com.datarobot.Implicits.jsonDefaultFormats
  override def toString = s"Model(${modelType})"



  def getCapabilities()(implicit client: DataRobotClient) = Model.getCapabilities(projectId, id)

  def getHyperParameters()(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/${id}/advancedTuning/parameters/").asString
    val json = parse(r.body)
    val temp = json.extract[Map[String, List[Map[String, Any]]]]
    temp
  }

  def getModelCoefficients()(implicit client: DataRobotClient) = {
     val r = client.get(s"projects/${projectId}/models/${id}/parameters/").asString
     val map = r.code match { 
       case 200 => parse(r.body).extract[Map[String,List[ Map[String,Any]]]]
       case _ => throw new Exception(s"${r.code}: ${r.body}")
     }
     val coef = map("derivedFeatures").map{coefficientHelper}
     val parameters = map("parameters")
     val intercept = parameters.filter( m => m("name") == "Intercept") match { 
       case List(s) => s.get("value").asInstanceOf[Option[Double]]
       case _ => None
     }
     val link  = parameters.filter( m => m("name") == "Link function") match { 
       case List(s) => s.get("value").asInstanceOf[Option[String]]
       case _ => None
     }
     ModelCoefficients(id, coef, intercept, link)
    //  (coef, parameters)
  }

  def getLiftCharts()(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/liftChart/").asString
    val json = parse(r.body)
    json.extract[Map[String, List[LiftChart]]]
  }

  def getLiftChart(source: Source.Value)(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/missingReport/").asString
    r.code match { 
      case 200 => parse(r.body).extract[LiftChart]
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  } 

  def getMissingValueReport()(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/missingReport/").asString
    parse(r.body).extract[Map[String, List[Map[String, Any]]]].getOrElse("missingValuesReport", List(Map()))
  }

  def getScoringCode(destination: Option[String] = None, sourceCode: Boolean = false)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/${id}/scoringCode/").param("sourceCode", s"${sourceCode}").asBytes
    val byteArrayOutputStream = new ByteArrayOutputStream()  //.getBytes("UTF-8")
    r.code match { 
      case 200 => byteArrayOutputStream.write(r.body)
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
    (destination, sourceCode) match { 
      case (Some(s), _) => byteArrayOutputStream.writeTo(new java.io.FileOutputStream(s))
      case (None, true) => {
        byteArrayOutputStream.writeTo(new java.io.FileOutputStream(s"${this.id}-source.jar"))
      }
      case (None, false) => {
        byteArrayOutputStream.writeTo(new java.io.FileOutputStream(s"${this.id}.jar"))
      }
    }
  }

  def getRocCurves()(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/rocCurve/").asString
    val json = r.code match { 
      case 200 => parse(r.body)
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
    json.extract[Map[String, List[RocCurve]]]
  }

  def getRocCurve(source: Source.Value)(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/rocCurve/${source}/").asString
    r.code match { 
      case 200 => parse(r.body).extract[RocCurve]
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  } 

  def getWordCloud()(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/models/${id}/wordCloud/").asString
    val map = r.code match { 
      case 200 => parse(r.body).extract[Map[String, List[ NGram]]]
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
    //WordCloud( map("ngrams"))
    WordCloud(map("ngrams"))
  }


  def getResiduals()(implicit client: DataRobotClient) = { 
    // needs work
    val r = client.get(s"projects/${projectId}/models/${id}/residuals/").asString
    r.code match { 
      case 404 => throw new Exception(s"${r.code}: ${r.body}")
      case _ => parse(r.body).extract[List[Map[String, Any]]].zipWithIndex
    }
  }

  def toggleStar(starred: Boolean)(implicit client: DataRobotClient) = {
    val params = Seq("isStarred" -> starred)
    val data = _getDataReady(params)
    val r = client.patch(s"projects/${projectId}/models/${id}/", data).asString
    r.code match {
      case 204 => {
        this.isStarred = true
        this
      }
      case _ =>
        throw new Exception(s"Something went wrong. Response return: ${r.code}")
    }
  }
  def starModel()(implicit client: DataRobotClient) = toggleStar(true)
  def unstarModel()(implicit client: DataRobotClient) = toggleStar(false)

  def requestFrozenModel(
      samplePct: Option[Float] = None,
      trainingRowCount: Option[Int] = None
  )(implicit client: DataRobotClient) = {
    (samplePct, trainingRowCount) match {
      case (Some(s), Some(r)) =>
        throw new Exception(
          "only one of samplePct trainingRowCount should be set"
        )
      case _ => Unit
    }
    val params = Seq(
      "modelId" -> this.id,
      "samplePct" -> samplePct,
      "trainingRowCount" -> trainingRowCount
    )
    val data = _getDataReady(params)
    val r =
      client.postData(s"projects/${projectId}/frozenModels/", data).asString
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

}

object Model {

  import com.datarobot.Implicits.jsonDefaultFormats
  
  def getCapabilities(projectId: String, modelId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/${modelId}/supportedCapabilities/").asString
    val json = r.code match { 
       case 200 => parse(r.body)
       case _ => throw new Exception(s"${r.code}: ${r.body}")
     }
    val map = json.extract[Map[String, Any]]
     val reasons = map("reasons").asInstanceOf[Map[String, String]]
     val capabilities = map.filter{ case(k,v) => k != "reasons"}.mapValues{ _.asInstanceOf[Boolean]}
     (capabilities, reasons)
  }

  def getFrozenModels(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/frozenModels/").asString
    val JObject(ls) = parse(r.body)
    val JArray(json) = ls(2)._2
    json.map { j => j.extract[FrozenModel] }
  }

  def getModel(projectId: String, modelId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/${modelId}/").asString
    val json = parse(r.body)
    json.extract[Model] 
  }

  def getModels(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/").asString
    val JArray(json) = parse(r.body)
    json.map { j => j.extract[Model] }
  }

  def getRecommendedModel(
      projectId: String
  )(implicit client: DataRobotClient) = {
    val r = client
      .get(s"projects/${projectId}/recommendedModels/recommendedModel/")
      .asString
    parse(r.body).extract[Model]
  }

  def getRecommendedModels(
      projectId: String
  )(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/recommendedModels/").asString
    val JArray(json) = parse(r.body)
    json.map { j => j.extract[Model] }
  }

  def get(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client.get(s"projects/${projectId}/models/${modelId}/").asString
    parse(r.body).extract[Model]
  }

  def deleteModel(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def createModel()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def createFrozenModel(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def checkModelCapabilities(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def createBlender(modelIds: Seq[String])(implicit client: DataRobotClient) =
    ???
  def getModelParameters(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getLiftChartData(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getResidualsChartData(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getRocCurveData(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getWordCloudData(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getMissingValuesReport(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def getScoringCode(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???
  def advancedTuning(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = throw new NotImplementedError("nope")
  def getReducedBlueprintChart(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = ???

  def requestFeatureFit() = ???
  def requestAndGetFeatureFit() = ???
  def getFeatureFit() = ???
  def requestFeatureEffect() = ???
  def requestAndGetFeatureEffect() = ???
  def getFeatureEffect() = ???
  def requestFeatureImpact() = ???
  def requestAndGetFeatureImpact() = ???
  def getFeatureImpact() = ???

}

class FrozenModel(
    featurelistId: String,
    processes: Array[String],
    featurelistName: String,
    projectId: String,
    samplePct: Option[Double],
    trainingDuration: Option[String],
    trainingRowCount: Option[String],
    trainingStartDate: Option[String],
    trainingEndDate: Option[String],
    modelCategory: String,
    isFrozen: Boolean = true,
    metrics: Map[String, Any],
    modelType: String,
    blueprintId: String,
    monotonicIncreasingFeaturelistId: Option[String] = None,
    monotonicDecreasingFeaturelistId: Option[String] = None,
    supportsMonotonicConstraints: Option[String] = None,
    id: String,
    isStarred: Boolean = false,
    predictionThreshold: Option[String],
    predictionThresholdReadOnly: Option[String]
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
    ) {
  override def toString = s"FrozenModel(${modelType})"
}
