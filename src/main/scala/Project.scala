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
    @param id – the ID of a project
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

    /** project */ 
    def delete()(implicit client: DataRobotClient)  = Project.delete(this.id.get)

    def setTarget(target: String)(implicit client: DataRobotClient) = Project.setTarget(this.id.get, target)

    def setWorkerCount(wc: Int)(implicit client: DataRobotClient) = Project.setWorkerCount(this.id.get, wc)

    def unlockHoldout()(implicit client: DataRobotClient) = {
      val data = _getDataReady(Seq(("holdoutUnlocked", true)))
      val r = Project.update(id.get, data)
      holdoutUnlocked = Some(true)
    }

    def setProjectName(name: String)(implicit client: DataRobotClient)  = {
      val data = _getDataReady(Seq(( "projectName", name)))
      val r = Project.update(id.get, data)
      projectName = Some(name)
    }
    /** */
    
    /** blueprint */ 
    def getBlueprints()(implicit client: DataRobotClient)  = Blueprint.getBlueprints(this.id.get)
    def getBlueprint(id: String)(implicit client: DataRobotClient)  = Blueprint.get(this.id.get, id)
    def getBlueprintChart(blueprintId: String)(implicit client: DataRobotClient)  = throw new NotImplementedError("Nope") //Blueprint.getBlueprintChart(this.id.get, blueprintId)
    def getReducedBlueprintChart(blueprintId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")//Blueprint.getReducedBlueprintChart(this.id.get, blueprintId)
    def getBlueprintDocumentation(blueprintId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")// Blueprint.getBlueprintDocumentation(this.id.get, blueprintId)
    /** */ 

    /** models */ 

    def getModels()(implicit client: DataRobotClient)  = Model.getModels(this.id.get)

    /** */ 

    /** features */ 
    def getFeature(featureName: String)(implicit client: DataRobotClient) = Feature.get(this.id.get, featureName)
    def getFeatures()(implicit client: DataRobotClient) = Feature.getFeatures(this.id.get)

    def typeTransformFeature(
      name: String,
      parentName: String,
      variableType: String,
      replacement: Boolean,
      dateExtraction: String
    )(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

    def dateExtractionTransformFeature(
      name: String,
      parentName: String,
      variableType: String,
      replacement: Boolean,
      dateExtraction: String
    )(implicit cient: DataRobotClient) = throw new NotImplementedError("Nope")

    def batchTransformFeatures(
      parentNames: Seq[String], 
      variableType: String, 
      prefix: String,
      suffix: String
    )(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
    /** */ 

    /** featurelists */ 
    def createFeaturelist(name: String, features: List[String])(implicit client: DataRobotClient) = Featurelist.createFeaturelist(this.id.get, name, features)
    def deleteFeaturelist(featurelistId: String)(implicit cleint: DataRobotClient) = Featurelist.delete(this.id.get, featurelistId)
    def getFeaturelists()(implicit client: DataRobotClient)  = Featurelist.getFeaturelists(this.id.get)
    /** */ 

    def getModelJobs(status: String = null.asInstanceOf[String])(implicit client: DataRobotClient)  = Project.getModelJobs(this.id.get, status)

  }


object Project {

  implicit val jsonDefaultFormats = DefaultFormats

  val path = "projects/"
  //

  def blend()(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

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
    maxWait: Int = 600000) = throw new NotImplementedError("Nope")

  def createFromDataSource(
    dataSource: String,
    userName: String,
    password: String,
    projectName: String = null.asInstanceOf[String],
    maxWait: Int = 600000) = throw new NotImplementedError("Nope")

  def createFromURL(url: String, projectName: String = null.asInstanceOf[String])(implicit client: DataRobotClient) = {
    val pName = if(projectName == null) url else projectName
    val json = write(Map("url" -> url, "projectName" -> pName))
    client.postData(s"${path}", data=json).asString
  }

  //

  def createModelingFeaturelist() = throw new NotImplementedError("Nope")
  def createTypeTransformFeature() = throw new NotImplementedError("Nope")


  def delete(projectId: String)(implicit client: DataRobotClient) = {
    client.delete(s"$path$projectId")
  }

  def fromAsync(url: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  // getters
  def get(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/").asString
    val result = parse(r.body) // comding from jackson.JsonMethods
    result.extract[Project]
  }

  def getAccessList(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getAllJobs(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getAssociationMatrixDetails(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getAssociations(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getBlenders(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  def getDatasets(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getDatetimeModels(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")


  def getFeatures(projectId: String)(implicit client: DataRobotClient) = Feature.getFeatures(projectId)
  def getFrozenModels(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getLeaderboardLink(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getMetrics(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  def getModelJob(projectId: String, jobId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/modelJobs/${jobId}/").asString
    val json = parse(r.body)
    json.extract[ModelJob]
  }

  def getModelJobs(projectId: String, status: String)(implicit client: DataRobotClient) = {
    if ( List("queue", "inprogress", "error").contains(status)) {
      val r = client.get(s"${path}${projectId}/modelJobs/").params("status" -> status).asString
      val JArray(json) = parse(r.body)
      json.map{ j => j.extract[ModelJob]}
    } else {
      throw new Exception(s"don't understand status ${status} request")
    }
  }

  def getModelingFeaturelists(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getModelingFeatures(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  def getPredictJobs(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getPrimeFiles(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getPrimeModels(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getRatingTableModels(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getRatingTables(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")
  def getStatus(projectId: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  def getProjects()(implicit client: DataRobotClient) = {
    val r = client.get(s"$path").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Project])
  }

  // setters
  def setTarget(projectId: String, target: String)(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(( "target", target)))
    Project.update(projectId, data)
  }

  def setWorkerCount(projectId: String, workerCount: Int)(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(("workerCount", workerCount)))
    Project.update(projectId, data)
  }

  def start(
    sourceData: String,
    target: String,
    projectName: String = null.asInstanceOf[String],
    workerCount: Option[Int] = None,
    metric: Option[String] = None,
    autoPilotOn: Boolean = true,
    blueprintThreshold: Option[Int] = Some(1),
    partitioningMethod: Option[Partition],
    postiveClass: Option[String] = None,
    targetType: Option[String] = None,
    mode: String = "autopilot",
    maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val project = this.createFromFile(sourceData, projectName, maxWait)

    // val initData = Seq(
    //   "workerCount" -> workerCount,
    //   "metric" -> metric,
    //   "autoPilotOn" -> autoPilotOn,
    //   "blueprintThreshold" -> blueprintThreshold,
    //   "partitioningMethod" -> partitionMethod,
    //   "positiveClass" -> positiveClass,
    //   "targetType" -> targetType,
    //   "mode" -> mode,
    //   "maxWait" -> maxWait
    // ).filter( _._1 != None)
    // val data = throw new NotImplementedError("Nope") _getDataReady(Seq(  ))

    this.setTarget(project.id.get, target)
  }

    // add in to set target and being

  def start_autopilot(projectId: String, featurelistId: Option[String] = None) = throw new NotImplementedError("Nope")
  
  // {
  //   val r = if(featurelistId == None) {
  //     client.patch(s"${path}${projectId}/aim")
  //   } else {
  //     client.patch(s"${path}${projectId}/aim").params("featurelistId" -> featurelistId)
  //   }
  //   r}

  
  def update(projectId: String, data: String)(implicit client: DataRobotClient) = client.patch(s"${path}${projectId}", data)
}