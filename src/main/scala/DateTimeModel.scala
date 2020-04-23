package com.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.Utilities._getDataReady
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import com.github.timsetsfire.datarobot.enums._
import com.github.timsetsfire.datarobot.Utilities._
import com.github.timsetsfire.datarobot.Implicits._

/**
  * @constructor
  * @param featurelistId – the ID of the featurelist used by the model
  * @param processes – a json list of processes used by the model
  * @param featurelistName – the name of the featurelist used by the model
  * @param projectId – the ID of the project to which the model belongs
  * @param modelCategory –indicateswhatkindofmodelitis-willbeprimeforDataRobotPrime models, blend for blender models, and model for all other models
  * @param samplePct – always null for datetime models
  * @param timeWindowSamplePct – an integer between 1 and 99, indicating the percentage of sampling within the time window. The points kept are determined by samplingMethod (random uniform by default). Will be null if no sampling was specified.
  * @param samplingMethod (string) – string (New in version 2.20). Either ‘random’ or ‘latest’, indicates sampling method used to select training data. For row-based project this is the way how requested number of rows are selected. For other projects (duration-based, start/end, project settings) - how specified percent of rows (timeWindowSamplePct) is selected from specified time window.
  * @param isFrozen – boolean, indicating whether the model is frozen, i.e. uses tuning parameters from a parent model
  * @param metrics
  * @param modelType
  * @param metrics – the performance of the model according to ous metrics, see below modelType – identifies the model, e.g. Nystroem Kernel SVM Regressor blueprintId – the blueprint used to construct the model - note this is not an ObjectId
  * @param monotonicIncreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically increasing relationship to the target. If null, no such constraints are enforced.
  * @param monotonicDecreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically decreasing relationship to the target. If null, no such constraints are enforced.
  * @param supportsMonotonicConstraints –boolean,whetherthismodelsupportsenforcing montonic constraints
  * @param id –theIDofthemodel
  * @param dataSelectionMethod – either “duration”, “rowCount”, or “selectedDateRange”. Identifies which of trainingDuration, trainingRowCount, or trainingStartDate and train- ingEndDate define the training size of the model when making predictions and scoring.
  * @param trainingDuration – the duration spanned by the dates in the partition column for the data used to train the model
  * @param trainingRowCount – the number of rows used to train the model
  * @param trainingStartDate – the start date of the dates in the partition column for the data used to train the model
  * @param trainingEndDate – the end date of the dates in the partition column for the data used to train the model
  * @param trainingInfo – json object describing the holdout and prediction training data as de- scribed below
  * @param holdoutScore – the holdout score of the model according to the project metric, if the score is available and the holdout is unlocked
  * @param holdoutStatus – the status of the holdout score. Either “COMPLETED”, “INSUFFI- CIENT_DATA” or “HOLDOUT_BOUNDARIES_EXCEEDED”
  * @param backtests (array) – information on each backtesting fold of the model as detailed below
  * @param predictionThreshold (float) – (New in version v2.13) threshold used for binary classification in predictions.
  * @param predictionThresholdReadOnly (boolean) – (New in version v2.13) indicates whether modification of a predictions threshold is forbidden. Threshold modification is forbidden once a model has had a deployment created or predictions made via the dedicated prediction API.
  * @param effectiveFeatureDerivationWindowStart (int) – (New in version v2.16) Only available for time series projects. How many timeUnits into the past relative to the forecast point the user needs to provide history for at prediction time. This can differ from the featureDerivationWindowStart set on the project due to the differencing method and period selected, or if the model is a time series native model such as ARIMA. Will be a negative integer.
  * @param effectiveFeatureDerivationWindowEnd (int) – (New in version v2.16) Only available for time series projects. How many timeUnits into the past relative to the forecast point the feature derivation window should end. Will be a non-positive integer.
  * @param forecastWindowStart (int) – (New in version v2.16) Only available for time series projects. How many timeUnits into the future relative to the forecast point the forecast window should start. Will be a non-negative integer.
  * @param forecastWindowEnd (int) – (New in version v2.16) Only available for time series projects. How many timeUnits into the future relative to the forecast point the forecast window should end. Will be a non-negative integer.
  * @param windowsBasisUnit (string) – (New in version v2.16) Only available for time series projects. Indicates which unit is the basis for the feature derivation window and the forecast window. Will be either detected time unit or “ROW”.
  * @param parentModelId (string) – (New in version v2.20) if this model is frozen, this is the ID of the parent model. Otherwise Null.
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

case class DateTimeMetric(backtesting: Option[Double], 
    holdout: Option[Double], 
    backtestingScores: Seq[Option[Double]],
    crossValdiation: Option[Double],
    validation: Option[Double]
  )