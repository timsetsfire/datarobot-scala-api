package com.github.timsetsfire.datarobot


case class NGram(
    count: Int,
    coefficient: Double,
    ngram: String,
    variable: String,
    `class`: String,
    frequency: Double,
    isStopword: Boolean
)

case class WordCloud( ngrams: List[NGram])