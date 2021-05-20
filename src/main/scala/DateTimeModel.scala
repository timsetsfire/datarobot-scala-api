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

/** @return DateTimeModel object
  * @constructor class used to represent DateTime models.  These should not be created directly. 
  * @param featurelistId – the ID of the featurelist used by the model
  * @param processes – a json list of processes used by the model
  * @param featurelistName – the name of the featurelist used by the model
  * @param projectId – the ID of the project to which the model belongs
  * @param modelCategory –indicateswhatkindofmodelitis-willbeprimeforDataRobotPrime models, blend for blender models, and model for all other models
  * @param samplePct – always null for datetime models
  * @param timeWindowSamplePct – an integer between 1 and 99, indicating the percentage of sampling within the time window. The points kept are determined by samplingMethod (random uniform by default). Will be null if no sampling was specified.
  * @param samplingMethod (string) – string (New in version 2.20). Either ‘random’ or ‘latest’, indicates sampling method used to select training data. For row-based project this is the way how requested number of rows are selected. For other projects (duration-based, start/end, project settings) - how specified percent of rows (timeWindowSamplePct) is selected from specified time window.
  * @param isFrozen – boolean, indicating whether the model is frozen, i.e. uses tuning parameters from a parent model
  * @param metrics - an object containing metrics for backtests that have compled. 
  * @param modelType - identifies the model, e.g. Nystroem Kernel SVM Regressor blueprintId – the blueprint used to construct the model - note this is not an ObjectId
  * @param metrics – the performance of the model according to ous metrics, see below modelType – 
  * @param monotonicIncreasingFeaturelistId the ID of the featurelist that defines the set of features with a monotonically increasing relationship to the target. If null, no such constraints are enforced.
  * @param monotonicDecreasingFeaturelistId the ID of the featurelist that defines the set of features with a monotonically decreasing relationship to the target. If null, no such constraints are enforced.
  * @param supportsMonotonicConstraints –boolean,whether this model supports enforcing montonic constraints
  * @param id –the ID of the model
  * @param dataSelectionMethod – either “duration”, “rowCount”, or “selectedDateRange”. Identifies which of trainingDuration, trainingRowCount, or trainingStartDate and train- ingEndDate define the training size of the model when making predictions and scoring.
  * @param trainingDuration – the duration spanned by the dates in the partition column for the data used to train the model
  * @param trainingRowCount – the number of rows used to train the model
  * @param trainingStartDate – the start date of the dates in the partition column for the data used to train the model
  * @param trainingEndDate – the end date of the dates in the partition column for the data used to train the model
  * @param trainingInfo – json object describing the holdout and prediction training data as de- scribed below
  * @param holdoutScore – the holdout score of the model according to the project metric, if the score is available and the holdout is unlocked
  * @param holdoutStatus – the status of the holdout score. Either “COMPLETED”, “INSUFFI- CIENT_DATA” or “HOLDOUT_BOUNDARIES_EXCEEDED”
  * @param backtests an array of information on each backtesting fold of the model
  * @param predictionThreshold threshold used for binary classification in predictions.
  * @param predictionThresholdReadOnly indicates whether modification of a predictions threshold is forbidden. Threshold modification is forbidden once a model has had a deployment created or predictions made via the dedicated prediction API.
  * @param effectiveFeatureDerivationWindowStart How many timeUnits into the past relative to the forecast point the user needs to provide history for at prediction time. This can differ from the featureDerivationWindowStart set on the project due to the differencing method and period selected, or if the model is a time series native model such as ARIMA. Will be a negative integer.
  * @param effectiveFeatureDerivationWindowEnd How many timeUnits into the past relative to the forecast point the feature derivation window should end. Will be a non-positive integer.
  * @param forecastWindowStart How many timeUnits into the future relative to the forecast point the forecast window should start. Will be a non-negative integer.
  * @param forecastWindowEnd How many timeUnits into the future relative to the forecast point the forecast window should end. Will be a non-negative integer.
  * @param windowsBasisUnit Indicates which unit is the basis for the feature derivation window and the forecast window. Will be either detected time unit or “ROW”.
  * @param parentModelId This is the ID of the parent model. Otherwise Null.
  */
class DateTimeModel(
    effectiveFeatureDerivationWindowStart: Option[Int],
    backtests: Option[Seq[Backtest]],
    trainingDuration: Option[String],
    dataSelectionMethod: Option[String],
    parentModelId: Option[String],
    holdoutStatus: Option[String],
    modelFamily: Option[String],
    windowsBasisUnit: Option[String],
    forecastWindowStart: Option[Int],
    timeWindowSamplePct: Option[Double],
    samplingMethod: Option[String],
    modelNumber: Option[Int],
    effectiveFeatureDerivationWindowEnd: Option[Int],
    trainingInfo: TrainingInfo,
    forecastWindowEnd: Option[Int],
    linkFunction: Option[String],
    modelType: String,
    supportsMonotonicConstraints: Option[Boolean],
    blueprintId: String,
    isStarred: Boolean,
    id: String,
    projectId: String,
    isFrozen: Boolean,
    featurelistId: String,
    trainingRowCount: Option[Int],
    trainingEndDate: Option[String],
    samplePct: Option[Double],
    modelCategory: String,
    trainingStartDate: Option[String],
    metrics: Map[String, Metric],
    monotonicIncreasingFeaturelistId: Option[String],
    holdoutScore: Option[Double],
    predictionThreshold: Option[Double],
    processes: Array[String],
    featurelistName: String,
    predictionThresholdReadOnly: Option[Boolean],
    monotonicDecreasingFeaturelistId: Option[String]
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
  override def toString = s"DateTimeModel(${modelType})"

  def requestFrozenDateTimeModel(
      featurelistId: Option[String] = None,
      trainingDuration: Option[String] = None, 
      trainingRowCount: Option[Int] = None, 
      trainingStartDate: Option[String] = None,
      trainingEndDate: Option[String] = None, 
      timeWindowSamplePct: Option[Int] = None,
      samplingMethod: Option[String] = None
  )(implicit client: DataRobotClient) = {

    val params = Seq(
      "modelId" -> this.id,
      "featurelistId" -> featurelistId,
      "trainingDuration" -> trainingDuration,
      "trainingRowCount" -> trainingRowCount,
      "trainingStartDate" -> trainingStartDate,
      "trainingEndDate" -> trainingEndDate,
      "timeWindowSamplePct" -> timeWindowSamplePct,
      "samplingMethod" -> samplingMethod
    )
    val data = _getDataReady(params)
    val r =
      client.postData(s"projects/${projectId}/frozenDatetimeModels/", data).asString
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

  def requestMultiseriesScores()(implicit client: DataRobotClient) = { 
    val r = client.post(s"projects/${projectId}/datetimeModels/${id}/multiseriesScores/").asString
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

  def getMultiseriesScores(
    orderBy: Option[String] = None,
    offset: Option[Int] = None, 
    limit: Option[Int] = None, 
    metric: Option[String] = None,
    multiseriesValue: Option[String] = None
  )(implicit client: DataRobotClient) = { 
    val params = Seq( 
      "orderBy" -> orderBy,
      "offset" -> offset,
      "limit" -> limit,
      "metric" -> metric,
      "multiseriesValue" -> multiseriesValue
    ).filter{ case(k,v) => 
      v match {
        case None => false 
        case _ => true
      }
    }.map{ case (k,v) => (k, v.get.toString)}
    val data = _getDataReady(params)
    val r = client.get(s"projects/${projectId}/datetimeModels/${id}/multiseriesScores/").params(params).asString
    val JObject(ls) = parse(r.body)
    val JArray(json) = ls(2)._2
    json.map { j => j.extract[MultiseriesMetrics] }

  }

  def getMultiseriesScoresAsCsv() = throw new Exception("not implemented")
  

  def scoreBacktests()(implicit client: DataRobotClient) = { 
    val r = client.post(s"projects/${projectId}/datetimeModels/${id}/backtests/").asString
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

  override def requestFeatureFit(backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    FeatureInsights.requestFeatureInsights("datetimeModels", "featureFit")(projectId, id, backtestIndex)
  }
  override def requestAndGetFeatureFit(source: String = "validation", backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    val ff = FeatureInsights.requestAndGetFeatureInsights("datetimeModels", "featureFit")(projectId, id, source, backtestIndex)
    ff.asInstanceOf[FeatureFits]
  }
  override def getFeatureFit(source: String = "validation", backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    val ff = FeatureInsights.getFeatureInsights("datetimeModels", "featureFit")(projectId, id, source, backtestIndex)
    ff.asInstanceOf[FeatureFits]
  }


  override def requestFeatureEffects(backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    FeatureInsights.requestFeatureInsights("datetimeModels", "featureEffects")(projectId, id, backtestIndex)
  }
  override def requestAndGetFeatureEffects(source: String = "validation", backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    val ff = FeatureInsights.requestAndGetFeatureInsights("datetimeModels", "featureEffects")(projectId, id, source, backtestIndex)
    ff.asInstanceOf[FeatureEffects]
  }
  override def getFeatureEffects(source: String = "validation", backtestIndex: Option[String] = Some("0"))(implicit client: DataRobotClient) = {
    val ff = FeatureInsights.getFeatureInsights("datetimeModels", "featureEffects")(projectId, id, source, backtestIndex)
    ff.asInstanceOf[FeatureEffects]
  }

  override def getFeatureFitMetaData()(implicit client: DataRobotClient) = FeatureInsights.getFeatureInsightsMetaData("datetimeModels", "featureFit")(projectId, id)
  override def getFeatureEffectsMetaData()(implicit client: DataRobotClient) = FeatureInsights.getFeatureInsightsMetaData("datetimeModels", "featureEffects")(projectId, id)


}



object DateTimeModel { 

  def getDateTimeModels(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/datetimeModels/").asString
    val JObject(ls) = parse(r.body)
    val JArray(json) = ls(2)._2
    json.map { j => j.extract[DateTimeModel] }
  }

  def getDateTimeModel(projectId: String, modelId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/datetimeModels/${modelId}/").asString
    parse(r.body).extract[DateTimeModel] 
  }

}

case class Backtest(status: String, index: Int, score: Double, trainingDuration: String, trainingStartDate: String, trainingRowCount: String, trainingEndDate: String)

case class TrainingInfo(predictionTrainingDuration: String,
    holdoutTrainingStartDate: String,
    predictionTrainingStartDate: String,
    holdoutTrainingDuration: String,
    predictionTrainingRowCount: Int,
    holdoutTrainingEndDate: String,
    predictionTrainingEndDate: String,
    holdoutTrainingRowCount: Int)

// case class DateTimeMetric(backtesting: Option[Double], 
//     holdout: Option[Double], 
//     backtestingScores: Seq[Option[Double]],
//     crossValdiation: Option[Double],
//     validation: Option[Double]
//   )
case class MultiseriesMetrics(
  multiseriesId: String,
  validationScore: Double,
  backtestingScore: Option[Double],
  rowCount: Int,
  multiseriesValues: Seq[String],
  holdoutScore: Option[Double],
  duration: String
) { 
  override def toString = { 
    writePretty(this)
  }
}