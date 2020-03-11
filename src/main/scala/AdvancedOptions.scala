package com.datarobot


/** AdvancedOptions
  * @constructor The advancedOptions object specifies more settings for a project
  * @param blueprintThreshold – an upper bound on running time (in hours), such that models exceeding the bound will be excluded in subsequent autopilot runs
  * @param responseCap – defaults to False, if specified used to cap the maximum response of a model
  * @param seed – defaults to null, the random seed to be used if specified
  * @param weights – the name of the weight column, if specified, otherwise null.
  * @param offset – (New in version v2.6) the list of names of the offset columns, if specified, other- wise null.
  * @param exposure – (New in version v2.6) the name of the exposure column, if specified, other- wise null.
  * @param eventsCount – (New in version v2.8) the name of the event count column, if specified, otherwise null.
  * @param smartDownsampled (bool) – (New in version v2.5) whether the project uses smart downsampling to throw away excess rows of the majority class. Smart downsampled projects express all sample percents in terms of percent of minority rows (as opposed to percent of all rows).
  * @param majorityDownsamplingRate (float) – (New in version v2.5) the percentage be- tween 0 and 100 of the majority rows that are kept, or null for projects without smart down- sampling
  * @param downsampledMinorityRows (int) – (New in version v2.5) the total number of the minority rows available for modeling, or null for projects without smart downsampling
  * @param downsampledMajorityRows (int) – (New in version v2.5) the total number of the majority rows available for modeling, or null for projects without smart downsampling
  * @param scaleoutModelingMode (string) – (New in version v2.8) Specifies the behavior of Scaleout models for the project. This is one of disabled, repositoryOnly, autopilot
  * @param defaultMonotonicIncreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist specifying a set of features with a monotonically increasing relationship to the target. All blueprints generated in the project use this as their default monotonic constraint, but it can be overriden at model submission time.
  * @param defaultMonotonicDecreasingFeaturelistId – (new in v2.11) null or str, the ID of the featurelist specifying a set of features with a monotonically decreasing relationship to the target. All blueprints generated in the project use this as their default monotonic constraint, but it can be overriden at model submission time.
  * @param onlyIncludeMonotonicBlueprints – (new in v2.11) boolean (default to False), whether the project only includes blueprints support enforcing monotonic constraints
  */

case class AdvancedOptions(
  blueprintThreshold: Option[Int],
  responseCap: Option[Boolean],
  seed: Option[Int],
  weights: Option[String],
  offset: Option[String],
  exposure: Option[String],
  eventsCount: Option[String],
  smartDownsampled: Option[Boolean],
  majorityDownsamplingRate: Option[Double],
  downsampledMinorityRows: Option[Int],
  downsampledMajorityRows: Option[Int],
  scaleoutModelingMode: Option[String],
  defaultMonotonicIncreasingFeaturelistId: Option[String],
  defaultMonotonicDecreasingFeaturelistId: Option[String],
  onlyIncludeMonotonicBlueprints: Option[Boolean]
)

// use a string builder pattern matching on option
// val c = scala.collection.mutable.Map[String, String]()
// val x: Option[String] = None
// x match {
//     case Some(lines) => c.put("parameter", lines)
//     case _ => Unit
// }
