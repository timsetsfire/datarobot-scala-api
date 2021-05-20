package io.github.timsetsfire.datarobot

import io.github.timsetsfire.datarobot.enums.{CVMethod, ValidationType}
import io.github.timsetsfire.datarobot.Implicits._

trait PartitioningMethod

case class UserTVH(
    userPartitionCol: String,
    trainingLevel: String,
    valdiationLevel: String,
    holdoutLevel: String,
    seed: Int = 0
) extends PartitioningMethod {
  val cvMethod = CVMethod.USER
  val validationType = ValidationType.TVH
}

case class UserCV(userPartitionCol: String, cvHoldoutPct: Double, seed: Int = 0)
    extends PartitioningMethod {
  val cvMethod = CVMethod.USER
  val validationType = ValidationType.CV
}

case class StratifiedTVH(validationPct: Double, holdoutPct: Double, seed: Int = 0)
    extends PartitioningMethod {
  val cvMethod = CVMethod.STRATIFIED
  val validationType = ValidationType.TVH
}

case class StratifedCV(reps: Int, holdoutPct: Double, seed: Int = 0)
    extends PartitioningMethod {
  val cvMethod = CVMethod.STRATIFIED
  val validationType = ValidationType.CV
}

case class RandomTVH(validationPct: Double, holdoutPct: Double, seed: Int = 0)
    extends PartitioningMethod {
  val cvMethod = CVMethod.RANDOM
  val validationType = ValidationType.TVH
}

case class RandomCV(reps: Int, holdoutPct: Double, seed: Int = 0)
    extends PartitioningMethod {
  val cvMethod = CVMethod.RANDOM
  val validationType = ValidationType.CV
}

case class GroupTVH(
    partitionKeyCols: String,
    validationPct: Double,
    holdoutPct: Double,
    seed: Int = 0
) extends PartitioningMethod {
  val cvMethod = CVMethod.GROUP
  val validationType = ValidationType.TVH
}

case class GroupCV(
    partitionKeyCols: String,
    reps: Int,
    holdoutPct: Double,
    seed: Int = 0
) extends PartitioningMethod {
  val cvMethod = CVMethod.GROUP
  val validationType = ValidationType.CV
}

/**
@param datetimePartitionColumn –Thedatecolumnthatwillbeusedasadate- time partition column
@param useTimeSeries  (New in version v2.8) Optional, defaults to false. A boolean value indicating whether a time series project should be created instead of a regular project which uses datetime partitioning.
@param unsupervisedMode  (New in version v2.20) Optional, defaults to false. A boolean value indicating whether an unsupervised project should be created.
@param multiseriesIdColumns  (New in version v2.11) Optional, may only be specified for projects using time series. An array of column names identifying the multi- series id column(s) to use to identify series within the data. Currently only one multiseries id column may be specified. See the multiseries section of the docs for more context.
@param defaultToAPriori  (Deprecated in version v2.11) Optional, renamed to defaultToKnownInAdvance, see below for more detail.
@param defaultToKnownInAdvance  (New in version v2.11) Optional, for time series projects only. Sets whether all features default to being treated as known in advance features, which are features that are known into the future. Features marked as known in advance must be specified into the future when making predictions. The default is false, all features are not known in advance. Individual features can be set to a value different than the default using the featureSettings parameter. See the Time Series Overview for more context.
@param defaultToDoNotDerive  (New in version v2.17) Optional, for time se- ries projects only. Sets whether all features default to being treated as do-not-derive features, excluding them from feature derivation. Individual features can be set to a value different than the default by using the featureSettings parameter.
@param featureDerivationWindowStart  (New in version v2.8) Optional, may only be specified for projects using time series. How many timeUnits of the datetimeParti- tionColumn into the past relative to the forecast point the feature derivation window should begin. Must be a negative integer, if specified.
@param featureDerivationWindowEnd (int)–(Newinversion2.8)Optional,mayonlybe specified for projects using time series. How many timeUnits of the datetimePartitionCol- umn into the past relative to the forecast point the feature derivation window should end. Must be a non-positive integer, if specified.
@param forecastWindowStart (Newinversionv2.8)Optional,mayonlybespecified for projects using time series. How many timeUnits of the datetimePartitionColumn into the future relative to the forecast point the forecast window should start. Must be a non-negative integer, if specified.
@param forecastWindowEnd  (New in version v2.8) Optional, may only be specified for projects using time series. How many timeUnits of the datetimePartitionColumn into the future relative to the forecast point the forecast window should end. Must be a non- negative integer, if specified.
@param windowsBasisUnit  (New in version v2.14) Optional, may only be speci- fied for projects using time series. Indicates which unit is basis for feature derivation window and forecast window. Valid options are detected time unit or “ROW”. If omitted, the default value is detected time unit.
@param validationDuration  Optional. A duration string representing the de- fault validation duration for all backtests. If the primary date/time feature in a time series project is irregular, you cannot set a default validation length. Instead, set each duration individually.
@param disableHoldout  (New in version v2.8) Optional. A boolean value indi- cating whether date partitioning should skip allocating a holdout fold. If omitted, the default value is false. When specifying disableHoldout: true, holdoutStartDate and holdoutDura- tion must not be set.
@param holdoutStartDate  Optional. A datetime string representing the start date of the holdout fold. When specifying holdoutStartDate, one of holdoutEndDate or holdout- Duration must also be specified. This attribute cannot be specified when disableHoldout is true.
@param holdoutEndDate  Optional. A datetime string representing the end date of the holdout fold. When specifying holdoutEndDate, holdoutStartDate must also be speci- fied. This attribute cannot be specified when disableHoldout is true.
@param holdoutDuration  Optional. A duration string representing the duration of the holdout fold. When specifying holdoutDuration, holdoutStartDate must also be spec- ified. This attribute cannot be specified when disableHoldout is true.
@param gapDuration  Optional, a duration string representing the duration of the gap between the training and the holdout data for the holdout model. For time series projects, defaults to the duration of the gap between the end of the feature derivation win- dow and the beginning of the forecast window. For OTV projects, defaults to a zero duration (P0Y0M0D).
@param numberOfBacktests  Optional, the number of backtests to use. If omitted, defaults to a positive value selected by the server based on the validation and gap durations.
@param autopilotDataSelectionMethod –Optional,either“duration”or“row- Count”. Defaults to “duration”. Whether models created via the autopilot will use “row- Count” or “duration” as their dataSelectionMethod.
@param treatAsExponential  (New in version v2.9) Optional, defaults to “auto”. Used to specify whether to treat data as exponential trend and apply transformations like log-transform. Valid options are “always”, “never”, “auto”.
@param differencingMethod  (New in version v2.9) Optional, defaults to “auto” for timeseries projects. Used to specify which differencing method to apply if the data is stationary. Valid options are “auto”, “simple”, “none”, “seasonal”. Parameter “periodicities” must be specified if “seasonal” is chosen.
@param backtests  Optional. An array specifying individual backtests. The index of the backtests specified should range from 0 to numberOfBacktests - 1.
@param featureSettings  (New in version v2.9) Optional, an array specifying per feature settings. Features can be left unspecified.
@param periodicities  (Newinversionv2.9)Optional,alistofperiodicities.Ifthis is provided, parameter “differencing_method” will default to “seasonal” if not provided or “auto”.
@param useCrossSeriesFeatures  (New in version v2.14) Indicating whether to use cross-series features.
@param aggregationType  (New in version v2.14) The aggregation type to apply when creating cross-series features. Optional, must be one of “total” or “average”.
@param crossSeriesGroupByColumns  (New in version v2.15) List of columns (currently of length 1). Optional setting that indicates how to further split series into related groups. For example, if every series is sales of an individual product, the series group-by could be the product category with values like “men’s clothing”, “sports equipment”, etc.. Must be used with multiseries and useCrossSeriesFeatures enabled.
@param calendarId  – (New in version v2.15) Optional, the ID of the calendar to use with this project.
**/
case class DateTimePartitioningMethod(
    val validationType: Option[ValidationType.Value] = None,
    val datetimePartitionColumn: Option[String] = None,
    val useTimeSeries: Option[Boolean] = None,
    val unsupervisedMode: Option[Boolean] = None,
    val multiseriesIdColumns: Option[Array[String]] = None,
    val defaultToAPriori: Option[Boolean] = None,
    val defaultToKnownInAdvance: Option[Boolean] = None,
    val defaultToDoNotDerive: Option[Boolean] = None,
    val featureDerivationWindowStart: Option[Int] = None,
    val featureDerivationWindowEnd: Option[Int] = None,
    val forecastWindowStart: Option[Int] = None,
    val forecastWindowEnd: Option[Int] = None,
    val windowsBasisUnit: Option[String] = None,
    val validationDuration: Option[String] = None,
    val disableHoldout: Option[Boolean] = None,
    val holdoutStartDate: Option[String] = None, // java.time.Instant
    val holdoutEndDate: Option[String] = None, // java.time.Instant
    val holdoutDuration: Option[String] = None,
    val gapDuration: Option[String] = None,
    val numberOfBacktests: Option[Int] = None,
    val autopilotDataSelectionMethod: Option[String] = None,
    val treatAsExponential: Option[String] = None,
    val differencingMethod: Option[String] = None,
    val backtests: Option[Seq[DateTimeBackTestsSetup]] = None,
    val featureSettings: Option[Seq[FeatureSetting]] = None,
    val periodicities: Option[Seq[Periodicity]] = None,
    val useCrossSeriesFeatures: Option[Boolean] = None,
    val aggregationType: Option[String] = None,
    val crossSeriesGroupByColumns: Option[Array[String]] = None,
    val calendarId: Option[String] = None
) extends PartitioningMethod {
  val cvMethod = CVMethod.DATETIME
}

class DateTimeBackTestsSetup(
    val index: Option[Int] = None,
    val primaryTrainingStartDate: Option[String] = None,
    val primaryTrainingEndDate: Option[String] = None,
    val validationStartDate: Option[String] = None,
    val validationEndDate: Option[String] = None,
    val validationDuration: Option[String] = None,
    val gapDuration: Option[String] = None
)

case class Periodicity(
    timeSteps: Option[Int] = None,
    timeUnit: Option[String] = None
)