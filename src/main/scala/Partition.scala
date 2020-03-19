package com.datarobot

import com.datarobot.enums.{CVMethod, ValidationType}
import org.json4s._

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
  cvMethod: CVMethod.Value,
  validationType: ValidationType.Value,
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
) 


// class PartitionSerializer extends CustomSerializer[Partition](format => (
//     {
//         case JObject(
//             JField("cvMethod", JString(cvMethod))::
//             JField("validationType", JString(validationType))::
//             JField("datetimeCol", JString(datetimeCol))::
//             JField("datetimePartitionCol", JString(datetimePartitionCol))::
//             JField("validationPct", JDouble(validationPct))::
//             JField("reps", JInt(reps))::
//             JField("cvHoldoutLevel", JString(cvHoldoutLevel))::
//             JField("holdoutLevel", JString(holdoutLevel))::
//             JField("userPartitionCol", JString(userPartitionCol))::
//             JField("trainingLevel", JString(trainingLevel))::
//             JField("partitionKeyCols", JString(partitionKeyCols))::
//             JField("holdoutPct", JDouble(holdoutPct))::
//             JField("validationLevel", JString(validationLevel))::
//             JField("useTimeSeries", JBool(useTimeSeries)) :: Nil
//         ) => Partition(
//                 CVMethod.revMap(cvMethod),
//                 ValidationType.revMap(validationType),
//                 Some(datetimeCol),
//                 Some(datetimePartitionCol),
//                 Some(validationPct),
//                 Some(reps.toInt),
//                 Some(cvHoldoutLevel),
//                 Some(holdoutLevel),
//                 Some(userPartitionCol),
//                 Some(trainingLevel),
//                 Some(partitionKeyCols),
//                 Some(holdoutPct),
//                 Some(validationLevel),
//                 Some(useTimeSeries)
//                 )
//     }, 
//     {
//         case p: Partition => 
//             JObject(
//             JField("cvMethod", JString(CVMethod.map(p.cvMethod)))::
//             JField("validationType", JString(ValidationType.map(p.validationType)))::
//             JField("datetimeCol", JString(p.datetimeCol.get))::
//             JField("datetimePartitionCol", JString(p.datetimePartitionCol.get))::
//             JField("validationPct", JDouble(p.validationPct.get))::
//             JField("reps", JInt(p.reps.get))::
//             JField("cvHoldoutLevel", JString(p.cvHoldoutLevel.get))::
//             JField("holdoutLevel", JString(p.holdoutLevel.get))::
//             JField("userPartitionCol", JString(p.userPartitionCol.get))::
//             JField("trainingLevel", JString(p.trainingLevel.get))::
//             JField("partitionKeyCols", JString(p.partitionKeyCols.get))::
//             JField("holdoutPct", JDouble(p.holdoutPct.get))::
//             JField("validationLevel", JString(p.validationLevel.get))::
//             JField("useTimeSeries", JBool(p.useTimeSeries.get)) :: Nil)
//     }
// ))