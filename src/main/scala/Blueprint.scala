package com.github.timsetsfire.datarobot
import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}


/** Blueprint
  * @param projectId the project the blueprint belongs to
  * @param processes a list of strings representing processes the blueprint uses
  * @param id the blueprint ID of this blueprint - note that this is not an ObjectId
  * @param modelType the model this blueprint will produce
  * @param blueprintCategory (New in version v2.6) describes the category of the blueprint and indicates the kind of model this blueprint produces. Will be either “DataRobot” or “Scaleout DataRobot”.
  * @param monotonicIncreasingFeaturelistId the ID of the featurelist that defines the set of features with a monotonically increasing relationship to the target. If null, no such constraints are enforced.
  * @param monotonicDecreasingFeaturelistId the ID of the featurelist that defines the set of features with a monotonically decreasing relationship to the target. If null, no such constraints are enforced.
  * @param supportsMonotonicConstraints whether this model supports enforcing montonic constraints
  */

case class Blueprint(
  projectId: Option[String],
  processes: Option[Array[String]],
  id: Option[String],
  modelType: Option[String],
  blueprintCategory: Option[String],
  monotonicIncreasingFeaturelistId: Option[String],
  monotonicDecreasingFeaturelistId: Option[String],
  supportsMonotonicConstraints: Option[Boolean]
) {
  import Blueprint._

  override def toString = s"Blueprint(${modelType.get})"

  def getBlueprintChart()(implicit client:DataRobotClient) = Blueprint.getBlueprintChart(this.projectId.get, this.id.get) 

  def getBlueprintDocumentation()(implicit client:DataRobotClient) = Blueprint.getBlueprintDocumentation(this.projectId.get, this.id.get)

  def getReducedBlueprintChart()(implicit client:DataRobotClient) = Blueprint.getReducedBlueprintChart(this.projectId.get, this.id.get)

//case class BlueprintChart 

//case class ModelBlueprintChart extends BlueprintChart 
}

object Blueprint { 

  import com.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  val path = "blueprints/"

  def getBlueprints(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/blueprints//").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Blueprint])
  }
  
  def get(projectId: String, blueprintId: String)(implicit client: DataRobotClient) = { 
    val r = client.get(s"projects/${projectId}/blueprints/${blueprintId}/").asString
    val result = parse(r.body) // comding from jackson.JsonMethods
    result.extract[Blueprint]
  }

  def getBlueprintChart(projectId: String, blueprintId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/blueprints/${blueprintId}/blueprintChart/").asString
    val result = parse(r.body)
    result.extract[Map[String, Any]]
  }

  def getReducedBlueprintChart(projectId: String, modelId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/models/${modelId}/blueprintChart/").asString
    val result = parse(r.body)
    result.extract[Map[String, Any]]
  }

  def getBlueprintDocumentation(projectId: String, blueprintId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/blueprints/${blueprintId}/blueprintDocs/").asString  
    val result = parse(r.body)
    result.extract[Map[String, Any]]
  }
  
}
