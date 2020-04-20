package com.github.timsetsfire.datarobot

import com.github.timsetsfire.datarobot.enums.{CVMethod, ValidationType}
import com.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats
import org.json4s.jackson.Serialization.{writePretty, write}


/** Partitioning for a given project
  * @constructor create a new Partition object
  * @param datetimeCol if a date partition column was used, the name of the column Note that datetimeCol applies to an old partitioning method no longer supported for new projects, as of API version v2.0.
  * @param cvMethod the partitioning method used, will be either “random”, “stratified”, “date- time”, “user”, “group”, or “date”. Note that “date” partitioning is an old partitioning method no longer supported for new projects, as of API version v2.0.
  * @param datetimePartitionCol if a datetime partition column was used, the name of the column
  * @param validationPct if train-validation-holdoutsplitwasused,thepercentageofthedataset used for the validation set
  * @param reps if cross validation was used, the number of folds to use
  * @param cvHoldoutLevel if a user partition column was used with cross validation, the value
  * @param holdoutLevel if a user partition column was used with train-validation-holdout split, the value assigned to the holdout set
  * @param userPartitionCol – if a user partition column was used, the name of the column
  * @param validationType – either CV for cross-validation or TVH for train-validation-holdout
split
  * @param trainingLevel – if a user partition column was used with train-validation-holdoutsplit, the value assigned to the training set
  * @param partitionKeyCols – if group partitioning was used, the name of the column.
  * @param holdoutPct – the percentage of the dataset reserved for the holdout set
  * @param validationLevel – if a user partition column was used with train-validation-holdout split, the value assigned to the validation set
  * @param useTimeSeries – (New in version v2.9) A boolean value indicating whether a time series project was created as opposed to a regular project using datetime partitioning.
  */
case class Partition(
  cvMethod: Option[CVMethod.Value] = None,
  validationType: Option[ValidationType.Value] = None,
  datetimeCol: Option[String] = None,
  datetimePartitionCol: Option[String]= None,
  validationPct: Option[Double]= None,
  reps: Option[Int]= None,
  cvHoldoutLevel: Option[String]= None,
  holdoutLevel: Option[String]= None,
  userPartitionCol: Option[String]= None,
  trainingLevel: Option[String]= None,
  partitionKeyCols: Option[String]= None,
  holdoutPct: Option[Double]= None,
  validationLevel: Option[String]= None,
  useTimeSeries: Option[Boolean]= None
)   {
  override def toString = writePretty(this)
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
case class DateTimePartitionSetUp(
  datetimePartitionColumn: Option[String],
  useTimeSeries: Option[Boolean],
  unsupervisedMode: Option[Boolean],
  multiseriesIdColumns: Option[Array[String]],
  defaultToAPriori: Option[Boolean],
  defaultToKnownInAdvance: Option[Boolean],
  defaultToDoNotDerive: Option[Boolean],
  featureDerivationWindowStart : Option[Int],
  featureDerivationWindowEnd: Option[Int],
  forecastWindowStart: Option[Int],
  forecastWindowEnd: Option[Int],
  windowsBasisUnit: Option[String], 
  validationDuration: Option[String], 
  disableHoldout: Option[Boolean],
  holdoutStartDate: Option[String] ,  // java.time.Instant
  holdoutEndDate: Option[String] , // java.time.Instant
  holdoutDuration: Option[String] ,
  gapDuration: Option[String] ,
  numberOfBacktests : Option[Int],
  autopilotDataSelectionMethod: Option[String],
  treatAsExponential: Option[String] ,
  differencingMethod: Option[String] ,
  backtests: Option[Seq[DateTimeBacktestsSetup]],
  featureSettings: Option[Seq[FeatureSetting]],
  periodicities: Option[Seq[Periodicity]],
  useCrossSeriesFeatures: Option[Boolean],
  aggregationType: Option[String] ,
  crossSeriesGroupByColumns: Option[Array[String]],
  calendarId: Option[String]
) 

case class DateTimeBackTestsSetup(
  index: Option[Int] = None,
  primaryTrainingStartDate: Option[String] = None,
  primaryTrainingEndDate: Option[String] = None,
  validationStartDate: Option[String] = None,
  validationEndDate: Option[String] = None,
  validationDuration: Option[String] = None,
  gapDuration: Option[String] = None
)

case class Periodicity(timeSteps: Option[Int] = None, timeUnit: Option[String] = None)

// scala> val p = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
// p: java.text.SimpleDateFormat = java.text.SimpleDateFormat@6b2ed43a

// scala> p.parse("2019-07-10T07:10:00.000000Z")
// res22: java.util.Date = Wed Jul 10 07:10:00 EDT 2019

/** 
@param projectId – The ID of the project 
@param datetimePartitionColumn (string)–Thedatecolumnthatwillbeusedasadate-time partition column
@param dateFormat (string) – The date format of the partition column
@param useTimeSeries (boolean)–(Newinversionv2.8)Abooleanvalueindicatingwhether a time series project should be created instead of a regular project which uses datetime partitioning.
@param unsupervisedMode (boolean) – (New in version v2.20) A boolean value indicating whether an unsupervised project should be created
@param defaultToAPriori (boolean)–(Deprecatedinversionv2.11)RenamedtodefaultTo- KnownInAdvance. This parameter always has the same value as defaultToKnownInAdvance and will be removed in a future release.
@param defaultToKnownInAdvance (boolean) – (New in version v2.11) Indicates whether all features in a time series project default to being treated as known in advance, unless overridden by featureSettings. Features marked as known in advance must be specified into the future when making predictions. See the Time Series Overview for more context.
@param defaultToDoNotDerive (boolean) – (New in version v2.17) Indicates whether all features in a time series project default to being treated as do-not-derive features, which excludes them from feature derivation.
@param featureDerivationWindowStart – (New in version v2.8) Will only be specified for projects using time series. How many timeUnits of the datetimePartitionColumn into the past relative to the forecast point the feature derivation window should begin. Will be a negative integer, if present.
@param featureDerivationWindowEnd – (New in version 2.8) Will only be specified for projects using time series. How many timeUnits of the datetimePartitionColumn into the past relative to the forecast point the feature derivation window should end. Will be a non- positive integer, if present.
@param forecastWindowStart – (New in version v2.8) Will only be specified for projects using time series. How many timeUnits of the datetimePartitionColumn into the future relative to the forecast point the forecast window should start. Will be a non-negative integer, if present.
@param forecastWindowEnd – (New in version v2.8) Will only be specified for projects using time series. How many timeUnits of the datetimePartitionColumn into the future relative to the forecast point the forecast window should end. Will be a non-negative integer, if present.
@param windowsBasisUnit – (New in version v2.14) Will only be specified for projects using time series. Indicates which unit is basis for feature derivation window and forecast window. Will be either detected time unit or “ROW”.
@param validationDuration – The default validation duration for all backtests. Will not be specified if the primary date/time feature in a time series project is irregular.
@param availableTrainingStartDate –Thestartdateofavailabletrainingdataforscoring the holdout
@param availableTrainingDuration – The duration of available training duration for scor- ing the holdout
@param availableTrainingEndDate –Theenddateofavailabletrainingdataforscoringthe holdout
@param primaryTrainingStartDate – The start date of the primary training data for scoring the holdout
@param primaryTrainingDuration – The duration of the primary training data for scoring the holdout
@param primaryTrainingEndDate – The end date of the primary training data for scoring the holdout
@param gapStartDate – The start date of the gap between the training and holdout scoring data
@param gapDuration – The duration of the gap between the training and holdout scoring data
@param gapEndDate – The end date of gap between the training and holdout scoring data
@param holdoutStartDate – The start date of the holdout scoring data
@param holdoutDuration – The duration of the holdout scoring data
@param holdoutEndDate – The end date of the holdout scoring data
@param numberOfBacktests – The number of backtests used
@param autopilotDataSelectionMethod – Whether models created via the autopilot will use “rowCount” or “duration” as their dataSelectionMethod.
@param backtests – An array of the configured backtests
@param partitioningWarnings – An array of available warnings about potential problems with the chosen partitioning that could cause issues during modeling, although the partition- ing may be successfully submitted
@param featureSettings – An array of per feature settings
@param numberOfKnownInAdvanceFeatures – (New in version v2.14) Number of features that are marked as known in advance.
@param numberOfDoNotDeriveFeatures – (New in version v2.17) Number of features that are marked as “do not derive”.
@param useCrossSeriesFeatures (boolean) – (New in version v2.14) Indicating whether to use cross-series features.
@param aggregationType (string) – (New in version v2.14) The aggregation type to apply when creating cross-series features. Optional, must be one of “total” or “average”.
@param crossSeriesGroupByColumns (array) – (New in version v2.15) List of columns (currently of length 1). Optional setting that indicates how to further split series into related groups. For example, if every series is sales of an individual product, the series group-by could be the product category with values like “men’s clothing”, “sports equipment”, etc..
@param calendarId (string) – (new in version v2.15) Optional, the id of a calendar to use with this project.
*/ 
case class DateTimePartition(
  projectId: Option[String],
  datetimePartitionColumn: Option[String],
  dateFormat: Option[String],
  useTimeSeries: Option[Boolean],
  unsupervisedMode: Option[Boolean],
  defaultToAPriori: Option[Boolean],
  defaultToKnownInAdvance: Option[Boolean],
  defaultToDoNotDerive: Option[Boolean],
  featureDerivationWindowStart: Option[Int],
  featureDerivationWindowEnd: Option[Int],
  forecastWindowStart: Option[Int],
  forecastWindowEnd: Option[Int],
  windowsBasisUnit: Option[String],
  validationDuration: Option[String],
  availableTrainingStartDate: Option[String],
  availableTrainingDuration: Option[String],
  availableTrainingEndDate: Option[String],
  primaryTrainingStartDate: Option[String],
  primaryTrainingDuration: Option[String],
  primaryTrainingEndDate: Option[String],
  gapStartDate: Option[String],
  gapDuration: Option[String],
  gapEndDate: Option[String],
  holdoutStartDate: Option[String],
  holdoutDuration: Option[String],
  holdoutEndDate: Option[String],
  numberOfBacktests: Option[Int],
  autopilotDataSelectionMethod: Option[String],
  backtests: Option[Seq[DateTimeBacktests]],
  partitioningWarnings: Option[String],
  featureSettings: Option[Seq[FeatureSetting]],
  numberOfKnownInAdvanceFeatures: Option[Int],
  numberOfDoNotDeriveFeatures: Option[Int],
  useCrossSeriesFeatures: Option[Boolean],
  aggregationType: Option[String],
  crossSeriesGroupByColumns: Option[String],
  calendarId: Option[String]
)


case class DateTimeBacktests(
  index: Option[Int],
  validationRowCount: Option[Int],
  primaryTrainingDuration: Option[String],
  primaryTrainingEndDate: Option[String], // java.time.Instant
  availableTrainingStartDate: Option[String],  // java.time.Instant
  primaryTrainingStartDate: Option[String],
  validationEndDate: Option[String],
  availableTrainingDuration: Option[String],
  availableTrainingRowCount: Option[Int],
  gapEndDate: Option[String],  // java.time.Instant
  validationDuration: Option[String],
  gapStartDate: Option[String], // java.time.Instant
  availableTrainingEndDate: Option[String], // java.time.Instant
  primaryTrainingRowCount: Option[Int],
  validationStartDate: Option[String], // java.time.Instant
  totalRowCount: Option[Int],
  gapRowCount: Option[Int],
  gapDuration: Option[String]
) 