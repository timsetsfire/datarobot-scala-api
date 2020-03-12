package com.datarobot.enums

object CVMethod extends Enumeration {
        type CVMethod = Value
        val DATETIME, RANDOM, STRATIFIED, USER, GROUP= Value
        val map = Map(DATETIME -> "datetime", RANDOM -> "random", STRATIFIED -> "stratified", USER -> "user", GROUP -> "group")
    }

object ValidationType extends Enumeration { 
    type ValidationType = Value 
    val CV, TVH = Value
    val map = Map(CV -> "CV", TVH -> "TVH")
}

object VariableTypeTransform extends Enumeration { 
    type VariableTypeTransform = Value 
    val CATEGORICAL, CATEGORICAL_INT, NUMERIC, TEXT = Value
    val map = Map( CATEGORICAL -> "categorical", CATEGORICAL_INT -> "categoricalInt", NUMERIC -> "numeric", TEXT -> "text")
}

object DateExtractionUnits extends Enumeration { 
    type DateExtractionUnits = Value 
    val YEAR, YEARDAY, MONTH, MONTHDAY, WEEK, WEEKDAY = Value
    val map = Map(YEAR -> "year", YEARDAY -> "yearDay", MONTH -> "month", MONTHDAY -> "monthDay", WEEK -> "week", WEEKDAY -> "weekDay")
}

object TargetType extends Enumeration { 
    type TargetType = Value
    val BINARY, MULTICLASS, REGRESSION = Value 
    val map = Map(BINARY -> "binary", MULTICLASS -> "multiclass", REGRESSION -> "regression")
}

object ModelingMode extends Enumeration { 
    type ModelModel = Value 
    val AUTOPILOT, QUICKRUN, MANUAL, COMPREHENSIVE = Value
    val map = Map(AUTOPILOT -> "auto", QUICKRUN -> "quick", MANUAL -> "manual", COMPREHENSIVE -> "comprehensive")
}