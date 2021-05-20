package io.github.timsetsfire.datarobot

case class ResidualHistogramInterval(intervalEnd: Double, intervalStart: Double, occurrences: Int)
case class Residual(actual: Double,predicted: Double,residual: Double,rownumber: Double)
case class ResidualData(residualMean: Double, 
                        coefficientOfDetermination: Double,
                        data: List[List[Double]],
                        histogram: List[ResidualHistogramInterval]) {
                            override def toString = s"Residual@${this.hashCode.toHexString}"
                            val residuals = data.map{case List(a,b,c,d) => Residual(a,b,c,d)}
                        }

case class Residuals(
    source: String,
    data: ResidualData
)


// residuals