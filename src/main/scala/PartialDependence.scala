package io.github.timsetsfire.datarobot

case class PredictedVsActual(bin: List[String], 
                            rowCount: Option[Double],
                            actual: Option[Double],
                            predicted: Option[Double],
                            label: Option[String])
case class PredictedVsActualData(isCapped: Boolean, data: List[PredictedVsActual]) {
    override def toString = s"PredictedVsActualData( isCapped = ${isCapped})"
}

case class PartialDependence(dependence: Double, label: String)
case class PartialDependenceData(isCapped: Boolean, data: List[PartialDependence]) {
        override def toString = s"PartialDependenceData( isCapped = ${isCapped})"
}