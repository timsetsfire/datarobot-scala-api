package com.github.timsetsfire.datarobot

import breeze.linalg.Counter2

/** 
 * @param source 
 * @param bins
 */
case class LiftChart(source: String, bins: List[Map[String, Double]]) { 
    override def toString = s"LiftChart(${source})"
    
    val binsCounter2: Counter2[Int, String, Double] = Counter2()
    bins.zipWithIndex.foreach { case (bin, idx) => 
        binsCounter2.update(idx, "actual", bin("actual"))
        binsCounter2.update(idx, "predicted", bin("predicted"))
        binsCounter2.update(idx, "binWeight", bin("binWeight"))
    }
}