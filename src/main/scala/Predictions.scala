package io.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import io.github.timsetsfire.datarobot.Utilities._getDataReady
import io.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import io.github.timsetsfire.datarobot.enums._
import io.github.timsetsfire.datarobot.Utilities._
import io.github.timsetsfire.datarobot.Implicits._

trait Prediction

case class BinaryPrediction(
    rowId: Int,
    prediction: String,
    positiveProbability: Double,
    predictionValues: Seq[ClassificationPredictionValue]
) extends Prediction

case class MulticlassPrediction(
    rowId: Int,
    prediction: String,
    positiveProbability: Option[Double] = None,
    predictionValues: Seq[ClassificationPredictionValue]
) extends Prediction

case class RegressionPrediction(
    rowId: Int,
    prediction: Double,
    positiveProbability: Option[Double] = None
) extends Prediction

case class TimeSeriesPrediction(
    rowId: Int,
    prediction: Double,
    positiveProbability: Option[Double] = None,
    forecastPoint: String,
    forecastDistance: Int,
    timestampe: String,
    seriesId: String,
    predictionIntervalLowerBound: Double,
    predictionIntervalUpperBound: Double,
    target: Option[String] = None
) extends Prediction

case class TimeSeriesClassification(
    rowId: Int,
    prediction: Double,
    positiveProbability: Option[Double] = None,
    forecastPoint: String,
    forecastDistance: Int,
    timestampe: String,
    seriesId: String,
    predictionThreshhold: Double,
    predictionValues: Seq[ClassificationPredictionValue],
    predictionIntervalLowerBound: Double,
    predictionIntervalUpperBound: Double,
    target: Option[String] = None
) extends Prediction

case class Predictions[T <: Prediction](
    positiveClass: Option[String],
    predictions: Seq[T],
    task: String,
    includePredictionIntervals: Option[Boolean],
    predictionIntervalsSize: Option[Int]
)

case class ClassificationPredictionValue(label: String, value: Double)


case class PredictionsMetaData(
    actualValueColumn: Option[String], 
    forecastPoint: Option[String], 
    predictionIntervalsSize: Option[String], 
    url: String, 
    projectId: String, 
    predictionsEndDate: Option[String], 
    includesPredictionIntervals: Option[Boolean], 
    predictionsStartDate: Option[String], 
    maxExplanations: Option[Int], 
    explanationAlgorithm: Option[String], 
    id: String, 
    datasetId: String, 
    modelId: String
)


object Predictions { 
    def getPredictions(projectId: String, modelId: Option[String] = None, datasetId: Option[String] = None)(implicit client: DataRobotClient) = { 
        
        val params = Seq(("modelId", modelId), ("datasetId", datasetId)).filter( _._2.isDefined).map{ case(k,v) => (k, v.get)}

        val r = params match { 
            case Seq() => client.get(s"projects/$projectId/predictions/").asString
            case _ => client.get(s"projects/$projectId/predictions/").params(params).asString
        }
        val JObject(data) = parse(r.body)
        val JArray(json) = data(2)._2
        json.map{ _.extract[PredictionsMetaData]}
    }
}
