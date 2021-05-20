package io.github.timsetsfire.datarobot

case class FeatureEffect(
    featureType: Option[String],
    weightLabel: Option[String],
    featureName: Option[String],
    predictedVsActual: PredictedVsActualData,
    featureImpactScore: Double,
    partialDependence: PartialDependenceData
) {
  override def toString = s"FeatureEffect(${featureName.get})"
}

case class FeatureEffects(
    projectId: String,
    modelId: String,
    featureEffects: List[FeatureEffect],
    source: String,
    backtestIndex: Option[String]
) {
  override def toString = {
    backtestIndex match {
      case None => s"FeatureEffects(${source})"
      case Some(bi) =>
        s"FeatureEffects(Source = ${source}, Backtest Index = ${bi})"
    }
  }
}
