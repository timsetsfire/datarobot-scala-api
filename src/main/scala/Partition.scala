package io.github.timsetsfire.datarobot

import io.github.timsetsfire.datarobot.enums.{CVMethod, ValidationType}
import io.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats
import org.json4s.jackson.Serialization.{writePretty, write}
import io.github.timsetsfire.datarobot.Implicits._



/** Partitioning for a given project that will be returned as part of a response from API
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
class Partition(
  val cvMethod: Option[CVMethod.Value] = None,
  val validationType: Option[ValidationType.Value] = None,
  val datetimeCol: Option[String] = None,
  val datetimePartitionColumn: Option[String]= None,
  val validationPct: Option[Double]= None,
  val reps: Option[Int]= None,
  val cvHoldoutLevel: Option[String]= None,
  val holdoutLevel: Option[String]= None,
  val userPartitionCol: Option[String]= None,
  val trainingLevel: Option[String]= None,
  val partitionKeyCols: Option[String]= None,
  val holdoutPct: Option[Double]= None,
  val validationLevel: Option[String]= None,
  val useTimeSeries: Option[Boolean]= None
) {
  override def toString = writePretty(this)
}

/** Date Time Partitioning use for a Time Series Project.  This should not be constructed directly.  See [[io.github.timsetsfire.datarobot.DateTimePartitioningMethod]] when 
 * setting up DateTime Parititioning for a project via UI.  
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
  partitioningWarnings: Option[Seq[PartitionWarning]],
  featureSettings: Option[Seq[FeatureSetting]],
  numberOfKnownInAdvanceFeatures: Option[Int],
  numberOfDoNotDeriveFeatures: Option[Int],
  useCrossSeriesFeatures: Option[Boolean],
  aggregationType: Option[String],
  crossSeriesGroupByColumns: Option[String],
  calendarId: Option[String]
)

case class PartitionWarning(partition: String, warnings: Seq[String], backtestIndex: Int)
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


