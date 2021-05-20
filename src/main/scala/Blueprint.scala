package io.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}

import scalax.collection.GraphEdge.DiEdge
import scalax.collection.edge.LDiEdge
import scalax.collection.Graph
import scalax.collection.io.dot._
import implicits._

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

  def getBlueprintChart()(implicit client: DataRobotClient) =
    Blueprint.getBlueprintChart(this.projectId.get, this.id.get)

  def getBlueprintDocumentation()(implicit client: DataRobotClient) =
    Blueprint.getBlueprintDocumentation(this.projectId.get, this.id.get)
}

object Blueprint {

  import io.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  val path = "blueprints/"

  def getBlueprints(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/blueprints//").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Blueprint])
  }

  def get(projectId: String, blueprintId: String)(
      implicit client: DataRobotClient
  ) = {
    val r =
      client.get(s"projects/${projectId}/blueprints/${blueprintId}/").asString
    val result = parse(r.body) // coming from jackson.JsonMethods
    result.extract[Blueprint]
  }

  def blueprintToGraph(r: scalaj.http.HttpResponse[String]) = {
    val JObject(data) = parse(r.body)
    val JArray(nodes) = data(0)._2
    val JArray(edges) = data(1)._2
    val bpNodes =
      nodes.map { _.extract[BlueprintNode] }.map { bp => (bp.id.get, bp) }.toMap
    val bpEdges = edges.map { edge => edge.extract[Array[String]] }
    val bpGraph = Graph[BlueprintNode, LDiEdge](bpEdges.zipWithIndex.map {
      case (edge, idx) => LDiEdge(bpNodes(edge(0)), bpNodes(edge(1)))(idx)
    }: _*)
    bpGraph
  }

  def getBlueprintChart(projectId: String, blueprintId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .get(s"projects/${projectId}/blueprints/${blueprintId}/blueprintChart/")
      .asString
    blueprintToGraph(r)
  }

  def getReducedBlueprintChart(projectId: String, modelId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .get(s"projects/${projectId}/models/${modelId}/blueprintChart/")
      .asString
    blueprintToGraph(r)
  }

  def getBlueprintDocumentation(projectId: String, blueprintId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client
      .get(s"projects/${projectId}/blueprints/${blueprintId}/blueprintDocs/")
      .asString
    val result = parse(r.body)
    val JArray(json) = result
    json.map { _.extract[Map[String, Any]] }
  }

  def toDot(
      graph: scalax.collection.Graph[
        BlueprintNode,
        scalax.collection.edge.LDiEdge
      ]
  ) = {

    val root = DotRootGraph(directed = true, id = Some("Blueprint"))
    def edgeTransformer(
        innerEdge: Graph[BlueprintNode, LDiEdge]#EdgeT
    ): Option[(DotGraph, DotEdgeStmt)] = innerEdge.edge match {
      case LDiEdge(source, target, label) =>
        Some((root, DotEdgeStmt(source.value.id.get, target.value.id.get)))
    }
    def nodeTransformer(
        innerNode: Graph[BlueprintNode, LDiEdge]#NodeT
    ): Option[(DotGraph, DotNodeStmt)] =
      Some(
        (
          root,
          DotNodeStmt(
            innerNode.value.id.get,
            List(
              DotAttr("id", innerNode.value.id.get),
              DotAttr("label", s"<${innerNode.value.toString}>")
            )
          )
        )
      )

    graph.toDot(root, edgeTransformer, cNodeTransformer = Some(nodeTransformer))

  }

}

case class BlueprintNode(id: Option[String], label: Option[String]) {
  override def toString = label match {
    case Some(s) => s
    case _       => s"Blueprint Node: $id"
  }
}
case class BlueprintEdge(from: Option[String], to: Option[String]) {
  override def toString = s"$from -> $to"
}
// val JObject(data) = parse(bpChart.body)
// val JArray(nodes) = data(0)._2
// val JArray(edges) = data(1)._2
// val bpNodes = nodes.map{ _.extract[BlueprintNode]}.map { bp => (bp.id.get, bp)}.toMap
// val bpEdges = edges.map{ edge => edge.extract[Array[String]]}
// val bpGraph = Graph[BlueprintNode, LDiEdge](bpEdges.zipWithIndex.map{ case(edge, idx) => LDiEdge(bpNodes(edge(0)), bpNodes(edge(1)))(idx)}:_*)
