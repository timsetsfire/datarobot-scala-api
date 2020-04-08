package com.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.enums.{VariableTypeTransform, DateExtractionUnits}

import com.github.timsetsfire.datarobot.Utilities._



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

object Featurelist {

  import com.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  /** feature list related methods **/ 
  def createFeaturelist(projectId: String, name: String, features:List[String])(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(
      ("name", name),
      ( "features", features)
    ))
    val r = client.postData(s"projects/${projectId}/featurelists/", data).asString
    if(r.code == 201) {
      parse(r.body).extract[Featurelist]
    }
    else {
      throw new Exception(s"featurelist not successfully created")
    }
  }

  def getFeaturelists(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/featurelists/").asString
    val JArray(json) = parse(r.body)
    json.map{ j => j.extract[Featurelist] }
  }

  def delete(projectId: String, featurelistId: String)(implicit client: DataRobotClient) = {
    client.delete(s"projects/${projectId}/featurelists/${featurelistId}/").asString
  }
  
}
