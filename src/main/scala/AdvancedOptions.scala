package com.datarobot


/**
  * @constructor The advancedOptions object specifies more settings for a project
  * @param blueprintThreshold an upper bound on running time (in hours), such that models exceeding the bound will be excluded in subsequent autopilot runs
  * @param responseCap defaults to False, if specified used to cap the maximum response of a model
  * @param seed defaults to null, the random seed to be used if specified
  * @param weights the name of the weight column, if specified, otherwise null.
  * @param rateTopPctThreshold - Optional, the percentage threshold between 0.1 and 50 for specifying the Rate@Top% metric.
  * @param offset the list of names of the offset columns, if specified, other- wise null.
  * @param exposure the name of the exposure column, if specified, other- wise null.
  * @param eventsCount the name of the event count column, if specified, otherwise null.
  * @param smartDownsampled whether the project uses smart downsampling to throw away excess rows of the majority class. Smart downsampled projects express all sample percents in terms of percent of minority rows (as opposed to percent of all rows).
  * @param majorityDownsamplingRate the percentage be- tween 0 and 100 of the majority rows that are kept, or null for projects without smart down- sampling
  * @param downsampledMinorityRows the total number of the minority rows available for modeling, or null for projects without smart downsampling
  * @param downsampledMajorityRows the total number of the majority rows available for modeling, or null for projects without smart downsampling
  * @param accuracyOptimizedMb Include additional, longer-running models that will be run by the autopilot and available to run manually
  * @param scaleoutModelingMode Specifies the behavior of Scaleout models for the project. This is one of disabled, repositoryOnly, autopilot
  * @param defaultMonotonicIncreasingFeaturelistId null or str, the ID of the featurelist specifying a set of features with a monotonically increasing relationship to the target. All blueprints generated in the project use this as their default monotonic constraint, but it can be overriden at model submission time.
  * @param defaultMonotonicDecreasingFeaturelistId null or str, the ID of the featurelist specifying a set of features with a monotonically decreasing relationship to the target. All blueprints generated in the project use this as their default monotonic constraint, but it can be overriden at model submission time.
  * @param onlyIncludeMonotonicBlueprints boolean (default to False), whether the project only includes blueprints support enforcing monotonic constraints
  * @param blendBestModels optional, defaults to True. Blend best models during Autopilot run.
  * @param minSecondaryValidationModelCount optional, defaults to 0. Compute “All backtest” scores (datetime models) or cross validation scores for the specified number of highest ranking models on the Leaderboard, if over the Autopilot default.
  * @param scoringCodeOnly optional, defaults to False. Keep only models that can be converted to scorable java code during Autopilot run.
  * @param prepareModelForDeployment optional, defaults to True. Prepare model for deployment during Autopilot run. The preparation includes creating reduced feature list models, retraining best model on higher sample size, computing insights and assigning “RECOMMENDED FOR DEPLOYMENT” label.
  * @param allowedPairwiseInteractionGroups (array) –) op- tional. For GAM models - specify groups of columns for which pairwise interactions will be allowed. E.g. if set to [[“A”, “B”, “C”], [“C”, “D”]] then GAM models will allow interactions between columns AxB, BxC, AxC, CxD. All others (AxD, BxD) will not be considered. If not specified - all possible interactions will be considered by model.
  */
case class AdvancedOptions(
  blueprintThreshold: Option[Int] = None,
  responseCap: Boolean = false,
  seed: Option[Int]= None,//
  weights: Option[String]= None, //
  rateTopPctThreshold: Option[Float]=None, //
  offset: Option[String]= None,//
  exposure: Option[String]= None,//
  eventsCount: Option[String]= None,//
  smartDownsampled: Boolean= false,//
  majorityDownsamplingRate: Option[Double]= None,//
  downsampledMinorityRows: Option[Int]= None,
  downsampledMajorityRows: Option[Int]= None,
  accuracyOptimizedMb: Option[Boolean] = None,//
  scaleoutModelingMode: String = "disabled",//
  defaultMonotonicIncreasingFeaturelistId: Option[String]= None, // 
  defaultMonotonicDecreasingFeaturelistId: Option[String]= None, //
  onlyIncludeMonotonicBlueprints: Boolean = false,  //
  blendBestModels: Boolean = true, //
  minSecondaryValidationModelCount: Int = 0, //
  scoringCodeOnly: Boolean = false, //
  prepareModelForDeployment: Boolean = true, //
  allowedPairwiseInteractionGroups: Option[Seq[Seq[String]]] = None //
)
  // featureSettings: Option[Seq[FeatureSetting]] = None