package com.github.timsetsfire.datarobot

case class FeatureFit(
    featureType: Option[String],
    weightLabel: Option[String],
    featureName: Option[String],
    predictedVsActual: PredictedVsActualData,
    featureImportanceScore: Double,
    partialDependence: PartialDependenceData
) {
  override def toString = s"FeatureFit(${featureName.get})"
}

case class FeatureFits(
    projectId: String,
    modelId: String,
    featureFit: List[FeatureFit],
    source: String,
    backtestIndex: Option[String]
) {
  override def toString = {
    backtestIndex match {
      case None     => s"FeatureFit(${source})"
      case Some(bi) => s"FeatureFit(Source = ${source}, Backtest Index = ${bi})"
    }
  }
}