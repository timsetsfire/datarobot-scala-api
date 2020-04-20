
package com.github.timsetsfire.datarobot

case class FeatureImpact(featureName: String, impactNormalized: Double, impactUnnormalized: Double, redundantWith: Option[String] = None) {
    override def toString = s"FeatureImpact(${featureName})"
}

case class FeatureImpacts(featureImpacts: List[FeatureImpact],
                     shapBased: Boolean,
                     count: Int, 
                     ranRedundancyDetection: Boolean,
                     next: Option[String],
                     previous: Option[String]
) {
    override def toString = s"FeatureImpact${this.hashCode.toHexString}"
}

