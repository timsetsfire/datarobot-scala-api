package com.datarobot


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
  * @param id – theIDofthemodel
  * @param isStarred  (bool) – (New in version v2.13) whether the model has been starred
  * @param predictionThreshold  (float) – (New in version v2.13) threshold used for binary classification in predictions.
  * @param predictionThresholdReadOnly  (boolean) – (New in version v2.13) indicates whether modification of a predictions threshold is forbidden. Threshold modification is forbidden once a model has had a deployment created or predictions made via the dedicated prediction API.
  */

case class Model(
  featurelistId: Option[String],
  processes: Option[Array[String]],
  featurelistName: Option[String],
  projectId: Option[String],
  samplePct: Option[Double],
  trainingDuration: Option[String],
  trainingRowCount: Option[String],
  trainingStartDate: Option[String],
  trainingEndDate: Option[String],
  modelCategory: Option[String],
  isFrozen: Option[String],
  metrics: Option[Map[String, Any]],
  modelType: Option[String],
  blueprintId: Option[String],
  monotonicIncreasingFeaturelistId: Option[String],
  monotonicDecreasingFeaturelistId: Option[String],
  supportsMonotonicConstraints: Option[String],
  id: Option[String],
  isStarred: Option[String],
  predictionThreshold: Option[String],
  predictionThresholdReadOnly: Option[String]
) {
  override def toString = s"Model(${modelType.get})"
}
