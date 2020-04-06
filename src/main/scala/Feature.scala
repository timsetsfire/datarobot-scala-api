package com.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.enums.{VariableTypeTransform, DateExtractionUnits}


/** Feature
  * @constructor
  * @param id (int) – the feature ID. (Note: Throughout the API, features are specified using their names, not this ID.)
  * @param name (string) – feature name
  * @param projectId (string) – the ID of the project the feature belongs to
  * @param featureType (string) – feature type: ‘Numeric’, ‘Categorical’, etc.
  * @param importance (float) – numeric measure of the strength of relationship between the feature and target (independent of any model or other features)
  * @param lowInformation (bool) – whether feature has too few values to be informative
  * @param uniqueCount (int) – number of unique values
  * @param naCount (int) – number of missing values
  * @param dateFormat (string)–(Newinversionv2.5)thedateformatstringforhowthisfeature was interpreted (or null if not a date feature). If not null, it will be compatible with https: //docs.python.org/2/library/time.html#time.strftime .
  * @param timeSeriesEligible (bool) – (New in version v2.8) whether this feature can be used as a datetime partitioning feature for time series projects. Only sufficiently regular date features can be selected as the datetime feature for time series projects. Always false for non-date features. Date features that cannot be used in datetime partitioning for a time series project may be eligible for an OTV project, which has less stringent requirements.
  * @param timeSeriesEligibilityReason (string)–(Newinversionv2.8)whythefeature is ineligible for time series projects, or “suitable” if it is eligible.
  * @param timeUnit (string) – (New in version v2.8) the unit for the interval between values of this feature, e.g. DAY, MONTH, HOUR. When specifying windows for time series projects, the windows are expressed in terms of this unit. Only present for date features eligible for time series projects, and null otherwise.
  * @param timeStep (int) – (New in version v2.8) The minimum time step that can be used to specify time series windows. The units for this value are the timeUnit. When specifying windows for time series projects, all windows must have durations that are integer multiples of this number. Only present for date features that are eligible for time series projects and null otherwise.
  * @param min – minimum value of the EDA sample of the feature.
  * @param max – maximum value of the EDA sample of the feature.
  * @param mean – arithmetic mean of the EDA sample of the feature.
  * @param median – median of the EDA sample of the feature.
  * @param stdDev – standard deviation of EDA sample of the feature.
  * @param targetLeakage (int) – whether or not the feature has target leakage. ‘SKIPPED_DETECTION’ indicates leakage detection was not run on the feature, ‘FALSE’ indicates no leakage, ‘MODERATE_RISK’ indicates a moderate risk of target leakage, and ‘HIGH_RISK’ indicates a high risk of target leakage
  */

  case class Feature(
    id: Option[String],
    name: Option[String],
    projectId: Option[String],
    featureType: Option[String],
    importance: Option[Double],
    lowInformation: Option[Boolean],
    uniqueCount: Option[Int],
    naCount: Option[Int],
    dateFormat: Option[String],
    timeSeriesEligible: Option[Boolean],
    timeSeriesEligibilityReason: Option[String],
    timeUnit: Option[String],
    timeStep: Option[Int],
    min: Option[Double],
    max: Option[Double],
    mean: Option[Double],
    median: Option[Double],
    stdDev: Option[Double],
    targetLeakage: Option[String]
  ) {
    
    override def toString = s"Feature(${name.get})"

    def getFeatureHistogram()(implicit client: DataRobotClient) = Feature.getFeatureHistogram(projectId.get, this.name.get)

    def getMetrics()(implicit client: DataRobotClient) = Feature.getMetrics(projectId.get, name.get)
  }

  object Feature { 

    import com.datarobot.Implicits.jsonDefaultFormats

    def getFeatures(projectId: String)(implicit client: DataRobotClient) = {
      val r = client.get(s"projects/${projectId}/features/").asString
      val result = parse(r.body)
      val JArray(ps) = result
      ps.map(p => p.extract[Feature])
    }

    def get(projectId: String, featureName: String)(implicit client: DataRobotClient) = { 
      val r = client.get(s"projects/${projectId}/feature/${featureName}/").asString
      parse(r.body).extract[Feature]
    }

// get a feature historgram 
// feature transformations

    def getFeatureHistogram(projectId: String, featureName: String)(implicit client: DataRobotClient) = {
      val r = client.get(s"projects/${projectId}/featureHistograms/${featureName}/").asString
      parse(r.body)
    }

    def getMetrics(projectId: String, featureName: String)(implicit client: DataRobotClient) = { 
      val r = client.get(s"projects/${projectId}/features/metrics/").params( Seq("featureName" -> featureName)).asString
      parse(r.body).extract[Map[String, Any]]      
    }

    def typeFeatureTransform(projectId: String, 
                         //featureName: String, 
                         //transformedFeatureName: String,
                         name: String, 
                         parentName: String, 
                         variableType: VariableTypeTransform.Value,
                         replacement: Option[Boolean] = None, 
                         dateExtraction: Option[DateExtractionUnits.Value] = None
    ) = throw new NotImplementedError("Not yet")

    def dateExtractionTransformFeature(projectId: String, 
      name: String,
      parentName: String,
      variableType: String,
      replacement: Boolean,
      dateExtraction: Option[DateExtractionUnits.Value] = None
    )(implicit cient: DataRobotClient) = throw new NotImplementedError("Nope")

    def batchTransformFeatures(projectId: String,
      parentNames: Seq[String], 
      variableType: DateExtractionUnits.Value, 
      prefix: String,
      suffix: String
    )(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
    /** */ 

  }

  case class FeatureHistogram()

