package com.github.timsetsfire.datarobot

import breeze.linalg.Counter2


/** 
 * @param source
 * @param rocPoints
 * @param negativeClassPredictions
 * @param positiveClassPrediction
 */

case class RocPoint(
    liftNegative: Double,
    f1Score: Double,
    truePositiveRate: Double,
    accuracy: Double,
    fractionPredictedAsNegative: Double,
    falseNegativeScore: Double,
    liftPositive: Double,
    falsePositiveRate: Double,
    matthewsCorrelationCoefficient: Double,
    trueNegativeRate: Double,
    negativePredictiveValue: Double,
    falsePositiveScore: Double,
    positivePredictiveValue: Double,
    threshold: Double,
    fractionPredictedAsPositive: Double,
    truePositiveScore: Double,
    trueNegativeScore: Double
) 

case class RocCurve(source: String, rocPoints: List[RocPoint], negativeClassPredictions: List[Double], positiveClassPrediction: List[Double])
{
    override def toString = s"RocCurve(${source})"
}

// case class RocCurve(source: String, rocPoints: List[Map[String, Double]], negativeClassPredictions: List[Double], positiveClassPrediction: List[Double]) {
//     override def toString = s"RocCurve(${source})"

//     val rocPointsCounter2: Counter2[Int, String, Double] = Counter2()
//     rocPoints.zipWithIndex.foreach { case (rocPoint, idx) => 
//         rocPointsCounter2.update(idx, "liftNegative", rocPoint("liftNegative"))
//         rocPointsCounter2.update(idx, "f1Score", rocPoint("f1Score"))
//         rocPointsCounter2.update(idx, "truePositiveRate", rocPoint("truePositiveRate"))
//         rocPointsCounter2.update(idx, "accuracy", rocPoint("accuracy"))
//         rocPointsCounter2.update(idx, "fractionPredictedAsNegative", rocPoint("fractionPredictedAsNegative"))
//         rocPointsCounter2.update(idx, "falseNegativeScore", rocPoint("falseNegativeScore"))
//         rocPointsCounter2.update(idx, "liftPositive", rocPoint("liftPositive"))
//         rocPointsCounter2.update(idx, "falsePositiveRate", rocPoint("falsePositiveRate"))
//         rocPointsCounter2.update(idx, "matthewsCorrelationCoefficient", rocPoint("matthewsCorrelationCoefficient"))
//         rocPointsCounter2.update(idx, "trueNegativeRate", rocPoint("trueNegativeRate"))
//         rocPointsCounter2.update(idx, "negativePredictiveValue", rocPoint("negativePredictiveValue"))
//         rocPointsCounter2.update(idx, "falsePositiveScore", rocPoint("falsePositiveScore"))
//         rocPointsCounter2.update(idx, "positivePredictiveValue", rocPoint("positivePredictiveValue"))
//         rocPointsCounter2.update(idx, "threshold", rocPoint("threshold"))
//         rocPointsCounter2.update(idx, "fractionPredictedAsPositive", rocPoint("fractionPredictedAsPositive"))
//         rocPointsCounter2.update(idx, "truePositiveScore", rocPoint("truePositiveScore"))
//         rocPointsCounter2.update(idx, "trueNegativeScore", rocPoint("trueNegativeScore"))
//     }
// }

