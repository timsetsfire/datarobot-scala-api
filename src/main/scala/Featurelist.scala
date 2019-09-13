package com.datarobot

/** featurelist
  * @constructor
  * @param id (string) – the ID of the featurelist
  * @param projectId (string) – the ID of the project the featurelist belongs to
  * @param name (string) – the name of the featurelist
  * @param features (array) – a json array of names of features included in the featurelist
  * @param numModels (int) – (New in version v2.13) the number of models that currently use this featurelist. A model is considered to use a featurelist if it is used to train the model or as a monotonic constraint featurelist, or if the model is a blender with at least one component model using the featurelist.
  * @param created (string) – (New in version v2.13) a timestamp string specifying when the featurelist was created
  * @param isUserCreated (boolean) – (New in version v2.13) whether the featurelist was cre- ated manually by a user or by DataRobot automation
  * @param description (string) – (New in version v2.13) a user-friendly description of the fea- turelist, which can be updated by users
  */

case class Featurelist(
  id: Option[String],
  projectId: Option[String],
  name: Option[String],
  features: Option[Seq[String]],
  numModels: Option[Int],
  created: Option[String],
  isUserCreated: Option[String],
  description: Option[String]
) {
  override def toString = s"Featurelist(${name.get})"
}
