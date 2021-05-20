package io.github.timsetsfire.datarobot

/** FeatureSetting
  * @constructor 
  * @param featureName (string) – The name of the feature being specified.
  * @param aPriori (boolean) – (Deprecated in v2.11) Optional, renamed to knownInAdvance, see below for more detail.
  * @param knownInAdvance (boolean)–(Newinversionv2.11)Optional,fortimeseriesprojects only. Sets whether the feature is known in advance, i.e., values for future dates are known at prediction time. If not specified, the feature uses the value from the defaultToKnownInAd- vance flag.
  * @param doNotDerive (boolean) – (New in version v2.17) Optional, for time series projects only. Sets whether the feature is do-not-derive, i.e., is excluded from feature derivation. If not specified, the feature uses the value from the defaultToDoNotDerive flag.
  **/

case class FeatureSetting(featureName: String, 
                          aprior: Option[Boolean] = None, 
                          knownInAdvance: Option[Boolean] = None, 
                          doNotDerive: Option[Boolean] = None)
