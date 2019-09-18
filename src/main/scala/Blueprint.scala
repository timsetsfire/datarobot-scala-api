package com.datarobot


/** Blueprint
  * @param projectId (string) – the project the blueprint belongs to
  * @param processes (array) – a list of strings representing processes the blueprint uses
  * @param id (string) – the blueprint ID of this blueprint - note that this is not an ObjectId
  * @param modelType (string) – the model this blueprint will produce
  * @param blueprintCategory (string) – (New in version v2.6) describes the category of the blueprint and indicates the kind of model this blueprint produces. Will be either “DataRobot” or “Scaleout DataRobot”.
  * @param monotonicIncreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically increasing relationship to the target. If null, no such constraints are enforced.
  * @param monotonicDecreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist that defines the set of features with a monotonically decreasing relationship to the target. If null, no such constraints are enforced.
  * @param supportsMonotonicConstraints – (new in v2.11) boolean, whether this model supports enforcing montonic constraints
  */

case class Blueprint(
  projectId: Option[String],
  processes: Option[Array[String]],
  id: Option[String],
  modelType: Option[String],
  blueprintCategory: Option[String],
  monotonicIncreasingFeaturelistId: Option[String],
  monotonicDecreasingFeaturelistId: Option[String],
  supportsMonotonicConstraints: Option[Boolean]
) {
  override def toString = s"Blueprint(${modelType.get})"
}
