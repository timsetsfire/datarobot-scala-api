package com.datarobot.enums

object CVMethod extends Enumeration {
        type CVMethod = Value
        val DATETIME, RANDOM, STRATIsFIED, USER = Value
    }

object VariableTypeTransform extends Enumeration { 
    type VariableTypeTransform = Value 
    val CATEGORICAL, CATEGORICAL_INT, NUMERIC, TEXT = Value
    val map = Map( CATEGORICAL -> "categorical", CATEGORICAL_INT -> "categoricalInt", NUMERIC -> "numeric", TEXT -> "text")
}

object DateExtractionUnits extends Enumeration { 
    type VariableTypeTransform = Value 
    val YEAR, YEARDAY, MONTH, MONTHDAY, WEEK, WEEKDAY = Value
    val map = Map(YEAR -> "year", YEARDAY -> "yearDay", MONTH -> "month", MONTHDAY -> "monthDay", WEEK -> "week", WEEKDAY -> "weekDay")
}