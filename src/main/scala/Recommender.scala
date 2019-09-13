package com.datarobot


/** The recommender object will describe additional options specified if the project is used for a recommender problem. It will be of the following form:
  * @constructor Returns an instance of recommnder
  * @param recommenderItemId if a recommender problem, the name of the column containing item ids, otherwise null
  * @param isRecommender indicates whether the project is a recommender problem
  * @param recommenderUserId if a recommender problem, the name of the column containing
user ids, otherwise null
  */
case class Recommender(
  recommenderItemId: Option[String],
  isRecommender: Option[Boolean],
  recommenderUserId: Option[String]
)
