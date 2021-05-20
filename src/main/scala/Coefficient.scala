
package io.github.timsetsfire.datarobot

import breeze.linalg.Counter

case class Coefficient(
    coefficient: Double,
    originalFeature: String,
    stageCoefficients: List[String],
    transformations: List[Map[String, String]],
    derivedFeature: String,
    `type`: String)

case class ModelCoefficients(modelId: String, coefficients: List[Coefficient], intercept: Option[Double] = None, link: Option[String] = None) { 
    override def toString = s"ModelCoefficients(${modelId})"

    val coefCounter: Counter[String, Double] = Counter()
    
    coefficients.foreach{ coef => 
        coefCounter.update( coef.derivedFeature, coef.coefficient)
    }
}


