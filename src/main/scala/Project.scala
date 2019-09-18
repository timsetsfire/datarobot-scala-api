package com.datarobot
import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.Utilities._

/** Project
  * @constructor
    @param id – theIDofaproject
    @param projectName – the name of a project
    @param fileName – the name of the dataset used to create the project
    @param stage – the stage of the project - if modeling, then the target is successfully set, and modeling or predictions can proceed.
    @param autopilotMode (int) – the current autopilot mode, either 0 for full autopilot or 2 for manual mode
    @param created – the time of project creation
    @param target – the target of the project
    @param metric – the metric used to select the best-performing models
    @param partition – a json object described below
    @param recommender – a json object described below
    @param advancedOptions – a json object described below
    @param positiveClass – if the project uses binary classification, the class designated to be the positive class. Otherwise, null.
    @param maxTrainPct – the maximum percentage of the dataset that can be used to successfully train a model without going into the validation data
    @param maxTrainRows – the maximum number of rows of the dataset that can be used to suc- cessfully train a model without going into the validation data
    @param scaleoutMaxTrainPct – the maximum percentage of the dataset that can be used to successfully train a scaleout model without going into the validation data. May exceed maxTrainPct, in which case only scaleout models can be trained up to this point.
    @param scaleoutMaxTrainRows – the maximum number of rows of the dataset that can used be used to successfully train a scaleout model without going into the validation data. May exceed maxTrainRows, in which case only scaleout models can be trained up to this point.
    @param holdoutUnlocked – whether the holdout has been unlocked targetType–eitherRegression,Binary(forbinaryclassification)orMulticlass,depend-
    ing on the selected target
    */
case class Project(
  id: Option[String],
  var projectName: Option[String],
  fileName: Option[String],
  stage: Option[String],
  autopilotMode: Option[Double],
  created: Option[String],
  target: Option[String],
  metric: Option[String],
  partition: Option[Partition],
  recommender: Option[Recommender],
  advancedOptions: Option[AdvancedOptions],
  positiveClass: Option[Double],
  maxTrainPct: Option[Double],
  maxTrainRows: Option[Double],
  scaleoutMaxTrainPct: Option[Double],
  scaleoutMaxTrainRows: Option[Double],
  var holdoutUnlocked: Option[Boolean],
  targetType: Option[String]) {
  //
    import Project._

    implicit val jsonDefaultFormats = DefaultFormats

    override def toString = s"Project(${projectName.get})"

    def getModel(id: String)(implicit client: DataRobotClient) = {
      val r = client.get(s"${path}${this.id.get}/models/${id}/").asString
      parse(r.body).extract[Model]
    }
    def getModels()(implicit client: DataRobotClient) = {
      val r = client.get(s"${path}${this.id.get}/models/").asString
      val JArray(json) = parse(r.body)
      json.map{ j => j.extract[Model] }
    }
    def delete()(implicit client: DataRobotClient) = client.delete(s"${Project.path}${id.get}")

    def getBlueprint(id: String)(implicit client: DataRobotClient) = {
      val r = client.get(s"${path}${this.id.get}/blueprints/${id}/").asString
      parse(r.body).extract[Blueprint]
    }

    def getBlueprintChart(id: String)(implicit client: DataRobotClient) = ???

    def getBlueprints()(implicit client: DataRobotClient) = {
      val r = client.get(s"${path}${this.id.get}/blueprints/").asString
      val JArray(json) = parse(r.body)
      json.map{ j => j.extract[Blueprint]}
    }

    def getFeaturelists()(implicit client: DataRobotClient) = {
      val r = client.get(s"${path}${id.get}/featurelists/").asString
      val JArray(json) = parse(r.body)
      json.map{ j => j.extract[Featurelist] }
    }



    def setWorkerCount(wc: Int)(implicit client: DataRobotClient) = {
      val data = _getDataReady(Seq(("workerCount", wc)))
      Project.update(id.get, data)
    }
    def unlockHoldout()(implicit client: DataRobotClient)= {
      val data = _getDataReady(Seq(("holdoutUnlocked", true)))
      val r = Project.update(id.get, data)
      holdoutUnlocked = Some(true)
    }
    def setProjectName(name: String)(implicit client: DataRobotClient) = {
      val data = _getDataReady(Seq(( "projectName", name)))
      val r = Project.update(id.get, data)
      projectName = Some(name)
    }
  }

object Project {

  implicit val jsonDefaultFormats = DefaultFormats

  val path = "projects/"
  //
  def createFromFile(file: String, projectName: String = null.asInstanceOf[String], maxWait: Int = 600000)(implicit client: DataRobotClient) = {
    val pName = if(projectName == null) file else projectName
    val fs: java.io.InputStream = new java.io.FileInputStream(file)
    val bytesInStream = fs.available
    val dataMP = MultiPart(name= "file", filename = file, mime = "text/csv", data = fs, numBytes = bytesInStream, lenWritten => Unit)
    val projectNameMP = MultiPart(name = "projectName", filename = "", mime = "text/plain", data = pName)
    val status = client.postMulti("projects/", projectNameMP, dataMP)
    val loc = Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)
    Project.get(loc(0).replace(s"${client.endpoint}${this.path}", ""))
  }

  def createFromHDFS(
    url: String,
    port: String = null.asInstanceOf[String],
    projectName: String = null.asInstanceOf[String],
    maxWait: Int = 600000) = ???

  def createFromDataSource(
    dataSource: String,
    userName: String,
    password: String,
    projectName: String = null.asInstanceOf[String],
    maxWait: Int = 600000) = ???

  def createFromURL(url: String, projectName: String = null.asInstanceOf[String])(implicit client: DataRobotClient) = {
    val pName = if(projectName == null) url else projectName
    val json = write(Map("url" -> url, "projectName" -> pName))
    client.postData(s"${path}", data=json).asString
  }
  def delete(projectId: String)(implicit client: DataRobotClient) = {
    client.delete(s"$path$projectId")
  }

  def fromAsync(url: String) = ???

  def get(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/").asString
    val result = parse(r.body) // comding from jackson.JsonMethods
    result.extract[Project]
  }
  def listProjects()(implicit client: DataRobotClient) = {
    val r = client.get(s"$path").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Project])
  }

  def setTarget()(implicit client: DataRobotClient) = ???

  def start(
    sourceData: String,
    target: String,
    projectName: String = null.asInstanceOf[String],
    mode: String = "autopilot",
    maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    this.createFromFile(sourceData, projectName, maxWait)
    // add in to set target and being
  }

  def update(projectId: String, data: String)(implicit client: DataRobotClient) = client.patch(s"${path}${projectId}", data)




}
