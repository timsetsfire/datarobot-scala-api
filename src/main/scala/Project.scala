package com.datarobot

import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import scalaj.http.HttpOptions
import org.json4s._
import org.json4s.jackson.Serialization.{write, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.Utilities._
import com.datarobot.enums.EnumFormats.enumFormats

import com.datarobot.enums._
import com.datarobot.Implicits._

import breeze.linalg.Counter2

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
class Project(
    val id: String,
    var projectName: String,
    val fileName: String,
    var stage: String,
    var autopilotMode: Option[Double],
    val created: String,
    var target: Option[String],
    var metric: Option[String],
    var partition: Option[Partition],
    private val recommender: Option[Recommender],
    var advancedOptions: Option[AdvancedOptions],
    var positiveClass: Option[Double],
    var maxTrainPct: Option[Double],
    var maxTrainRows: Option[Double],
    var scaleoutMaxTrainPct: Option[Double],
    var scaleoutMaxTrainRows: Option[Double],
    var holdoutUnlocked: Boolean = false,
    var targetType: Option[String]
) {
  //
  import Project._

  override def toString = s"Project(${projectName.get})"

  /** project */
  implicit val jsonDefaultFormats = DefaultFormats ++ enumFormats

  def blend()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  def clone(newProjectName: String, maxWait: Int = 60000)(implicit client: DataRobotClient) = {
    val data = _getDataReady( Seq("projectId" -> this.id, "projectName" -> newProjectName))
    val r = client.postData("projectClones/", data ).asString
    val loc = Waiter.waitForAsyncResolution(r.headers("location")(0), maxWait)
    val project = Project.get(loc(0).replace(s"${client.endpoint}${path}", ""))
    project
  }
  
  def createModelingFeaturelist() =
    throw new NotImplementedError("This is not a Timeseries project")
  def createTypeTransformFeature() =
    throw new NotImplementedError("Not yet implemented")

  def delete()(implicit client: DataRobotClient) = Project.delete(this.id)

  // /** */

  /** blueprint */
  def getBlueprints()(implicit client: DataRobotClient) =
    Blueprint.getBlueprints(this.id)
  def getBlueprint(id: String)(implicit client: DataRobotClient) =
    Blueprint.get(this.id, id)
  def getBlueprintChart(blueprintId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    ) //Blueprint.getBlueprintChart(this.id, blueprintId)
  def getReducedBlueprintChart(
      blueprintId: String
  )(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    ) //Blueprint.getReducedBlueprintChart(this.id, blueprintId)
  def getBlueprintDocumentation(
      blueprintId: String
  )(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    ) // Blueprint.getBlueprintDocumentation(this.id, blueprintId)
  /** */
  /** models */
  def getModels()(implicit client: DataRobotClient) =
    Model.getModels(this.id)
  def getFrozenModels()(implicit client: DataRobotClient) =
    Model.getFrozenModels(this.id)

  /** */
  /** features */
  def getFeature(featureName: String)(implicit client: DataRobotClient) =
    Feature.get(this.id, featureName)
  def getFeatures()(implicit client: DataRobotClient) =
    Feature.getFeatures(this.id)
  def getMetrics(featureName: String)(implicit client: DataRobotClient) =
    Feature.getMetrics(this.id, featureName)

  /** featurelists */
  def createFeaturelist(name: String, features: List[String])(
      implicit client: DataRobotClient
  ) = Featurelist.createFeaturelist(this.id, name, features)
  def deleteFeaturelist(featurelistId: String)(
      implicit cleint: DataRobotClient
  ) = Featurelist.delete(this.id, featurelistId)
  def getFeaturelists()(implicit client: DataRobotClient) =
    Featurelist.getFeaturelists(this.id)

  /** */
  // for each project

  def getAccessList(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/accessControl/").asString
    val userList = parse(r.body).extract[Map[String, Any]].get("data")
    userList match {
      case Some(v) => v.asInstanceOf[List[Map[String, String]]]
      case None    => List()
    }
  }

  def getAllJobs()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  // association matrix helpers

  def getFeatureAssociationMatrixDetails(
      feature1: String,
      feature2: String,
      featurelistId: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val url = s"${path}${id}/featureAssociationMatrixDetails/"
    val params = featurelistId match {
      case Some(id) =>
        Seq(
          "feature1" -> feature1,
          "feature2" -> feature2,
          "featurelistId" -> id
        )
      case None => Seq("feature1" -> feature1, "feature2" -> feature2)
    }
    val r = client.get(url).params(params).asString
    r.code match {
      case 200 => parse(r.body).extract[Map[String, Any]]
      case 404 =>
        throw new Exception(
          s"query parameters are misspecified or no such project ${id} exists"
        )
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def getFeatureAssocationMatrix(
      metric: FeatureAssociationMetric.Value =
        FeatureAssociationMetric.MUTUALINFO,
      atype: FeatureAssociationType.Value = FeatureAssociationType.ASSOCIATION,
      featurelistId: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val url = s"${path}${this.id}/featureAssociationMatrix/"
    val params: Seq[(String, String)] = Seq(
      "metric" -> FeatureAssociationMetric.MUTUALINFO.toString,
      "type" -> FeatureAssociationType.ASSOCIATION.toString
    )
    val fam: Counter2[String, String, Double] = Counter2()
    // val fam2: Counter2[String, String, Double] = Counter2()
    val r = featurelistId match {
      case Some(fls) =>
        client.get(url).params(params ++ Seq("featurelistId" -> fls)).asString
      case _ => client.get(url).params(params).asString
    }
    val map = r.code match {
      case 200 => parse(r.body).extract[Map[String, List[Map[String, Any]]]]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    map("strengths").foreach { map =>
      fam.update(
        map("feature1").toString,
        map("feature2").toString,
        map("statistic").asInstanceOf[Number].doubleValue
      )
    }
    parse(r.body).extract[Map[String, List[Map[String, Any]]]]
    (fam, map("features"))
  }

  def getAssociationFeatureLists()(implicit client: DataRobotClient) = {
    val url = s"${path}${this.id}/featureAssociationMatrix/list/"
    val r = client.get(url).asString 
    r.code match { 
      case 200 => parse(r.body).extract[Seq[Map[String,String]]]
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def requestFeatureAssocationMatrix(
      featurelistId: String
  )(implicit client: DataRobotClient) = {
    val url = s"${path}${this.id}/featureAssociationMatrix/"
    // val fam2: Counter2[String, String, Double] = Counter2()
    val data = _getDataReady(Seq("featurelistId" -> featurelistId))
    val r = client.postData(url, data).asString
    r.code match {
      case 202 =>
        "The feature association matrix calculation request succeeded."
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def getBlenders()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def getDatasets()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def getDatetimeModels()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  
  def getLeaderboardLink()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  def getModelJob(jobId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${id}/modelJobs/${jobId}/").asString
    val json = parse(r.body)
    json.extract[ModelJob]
  }

  def getModelJobs(
      status: Option[String] = None
  )(implicit client: DataRobotClient) = {
    if (List("queue", "inprogress", "error").contains(status)) {
      val r = status match {
        case None => client.get(s"${path}${id}/modelJobs/").asString
        case Some(status) =>
          client
            .get(s"${path}${id}/modelJobs/")
            .params("status" -> status)
            .asString
      }
      val JArray(json) = parse(r.body)
      json.map { j => j.extract[ModelJob] }
    } else {
      throw new Exception(s"don't understand status ${status} request")
    }
  }

  // this won't be implemented in non TS project
  def getModelingFeaturelists(projectId: String)(
      implicit client: DataRobotClient
  ) = throw new NotImplementedError("This is not a Timeseries project")
  // this won't be implemented in non TS project
  def getModelingFeatures(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("This is not a Timeseries project")

  def getPredictJobs(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def getPrimeFiles(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def getPrimeModels(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  def getRecommendedModels()(implicit client: DataRobotClient) =
    Model.getRecommendedModels(this.id)
  def getRecommendedModel()(implicit client: DataRobotClient) =
    Model.getRecommendedModel(this.id)

  def getRatingTableModels(projectId: String)(
      implicit client: DataRobotClient
  ) = throw new NotImplementedError("Nope")
  def getRatingTables(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")
  def getStatus(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  def setProjectName(name: String)(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(("projectName", name)))
    val r = client.patch(s"${path}${id}/", data).asString
    r.code match {
      case 200 => this.projectName = name
      case _ =>
        throw new Exception(s"Something went wrong. Response return: ${r.code}")
    }
    this
  }

  def setTarget(
      target: String,
      mode: ModelingMode.Value = ModelingMode.AUTOPILOT,
      metric: Option[String] = None,
      quickrun: Boolean = false,
      positiveClass: Option[String] = None,
      partitioningMethod: Option[Partition] = None,
      featurelistId: Option[String] = None,
      advancedOptions: Option[AdvancedOptions] = None,
      maxWait: Int = 60000, // need enum for this
      targetType: Option[TargetType.Value] = None,
      workerCount: Option[Int] = None
  )(implicit client: DataRobotClient) = {

    val advOpt = advancedOptions match {
      case Some(adv) => caseClassToMap(adv)
      case _         => Map()
    }
    val partitioning = partitioningMethod match {
      case Some(part) => caseClassToMap(part)
      case _          => Map()
    }
    val st = Seq(
      "target" -> target,
      "metric" -> metric,
      "mode" -> mode.id,
      "quickrun" -> quickrun,
      "featurelistId" -> featurelistId,
      "positiveClass" -> positiveClass,
      "targetType" -> targetType
    )
    val data = _getDataReady(st ++ advOpt ++ partitioning)
    println(data)
    //need to fix
    println(client.endpoint + s"projects/${this.id}/aim/")
    val status = client.patch(s"projects/${this.id}/aim/", data).asString
    //val asyncLocation = response.headers("Location")
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)
    val project = Project.get(loc(0).replace(s"${client.endpoint}${path}", ""))
    workerCount match {
      case Some(wc) => {
        project.setWorkerCount(wc)
      }
      case _ => Unit
    }
    this.refresh
    //# Waits for project to be ready for modeling, but ignores the return value
    //self.from_async(async_location, max_wait=max_wait)
    //self.refresh()
  }

  def setWorkerCount(workerCount: Int)(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(("workerCount", workerCount)))
    val response = client.patch(s"projects/${id}/", data).asString
    if (response.code == 405) {
      throw new Exception("405 Method Not Allowed")
    }
  }

  def train(
    blueprint: Blueprint,
    featurelistId: Option[String] = None,
    samplePct: Option[Float] = None, 
    trainingRowCount: Option[Int] = None,
    sourceProjectId: Option[String] = None, 
    scoringType: Option[String] = None, 
    monotonicIncreasingFeaturelistId: Option[String] = None, 
    monotonicDecreasingFeaturelistId: Option[String] = None
    )(implicit client: DataRobotClient) = { 

    val params = Seq(
      "blueprintId" -> blueprint.id,
      "featurelistId" -> featurelistId,
      "samplePct" -> samplePct, 
      "trainingRowCount" -> trainingRowCount, 
      "sourceProjectId" -> sourceProjectId, 
      "scoringType" -> scoringType, 
      "monotonicIncreasingFeaturelistId" -> monotonicIncreasingFeaturelistId, 
      "monotonicDecreasingFeaturelistId" -> monotonicDecreasingFeaturelistId
    )
    val data = _getDataReady(params)
    val r = client.postData(s"${path}${this.id}/models/", data).asString
    val loc = r.code match { 
      case 200 => r.headers("location")(0).replace(client.endpoint, "")
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString 
    job.code match { 
      case 200 => parse(job.body).extract[ModelJob]
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def unlockHoldout()(implicit client: DataRobotClient) = {
    this.holdoutUnlocked match {
      case true => Unit
      case false => {
        val data = _getDataReady(Seq(("holdoutUnlocked", true)))
        val r = client.update(s"${path}${id}/", data).asString
        r.code match {
          case 200 => this.holdoutUnlocked = true
          case _ =>
            throw new Exception(
              s"Something went wrong. Response return: ${r.code}"
            )
        }
      }
    }
    this
  }

  def start_autopilot(
      projectId: String,
      featurelistId: Option[String] = None
  ) = ???

  def refresh()(implicit client: DataRobotClient) = {
    //throw new NotImplementedError("not yet")
    val temp = Project.get(this.id)
    this.stage = temp.stage
    this.autopilotMode = temp.autopilotMode
    this.target = temp.target
    this.metric = temp.metric
    this.partition = temp.partition

    this.advancedOptions = temp.advancedOptions
    this.positiveClass = temp.positiveClass
    this.maxTrainPct = temp.maxTrainPct
    this.maxTrainRows = temp.maxTrainRows
    this.scaleoutMaxTrainPct = temp.scaleoutMaxTrainPct
    this.scaleoutMaxTrainRows = temp.scaleoutMaxTrainRows
    this.holdoutUnlocked = temp.holdoutUnlocked
    this.targetType = temp.targetType
    this
  }

}

object Project {

  implicit val jsonDefaultFormats = DefaultFormats ++ enumFormats

  // implicit val formats = Serialization.formats(NoTypeHints) + new PartitionSerializer

  val path = "projects/"

  def createFromFile(
      file: String,
      projectName: String = null.asInstanceOf[String],
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val pName = if (projectName == null) file else projectName
    val fs: java.io.InputStream = new java.io.FileInputStream(file)
    val bytesInStream = fs.available
    val dataMP = MultiPart(
      name = "file",
      filename = file,
      mime = "text/csv",
      data = fs,
      numBytes = bytesInStream,
      lenWritten => Unit
    )
    val projectNameMP = MultiPart(
      name = "projectName",
      filename = "",
      mime = "text/plain",
      data = pName
    )
    val status = client.postMulti("projects/", projectNameMP, dataMP).asString
    status.code match {
      case 202    => Unit
      case x: Int => throw new Exception(s"$x: ${status.body}")
    }
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)
    Project.get(loc(0).replace(s"${client.endpoint}${this.path}", ""))
  }

  // not great.  when toLocalIterator is run, data must fit into mani memory on head node to be feed local iterator
  // not necessarily and issue with beefy databricks, but less than idea when sharing resourcs.
  def createFromSparkDf(
      df: org.apache.spark.sql.DataFrame,
      projectName: String = null.asInstanceOf[String],
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val fs = DataFrameAsInputStream(df)
    val pName = if (projectName == null) "Spark DataFrame" else projectName
    val bytesInStream = fs.numBytes // 6309760
    val dataMP = MultiPart(
      name = "file",
      filename = "Spark DataFrame",
      mime = "text/csv",
      data = fs,
      numBytes = bytesInStream,
      lenWritten => Unit
    )
    val projectNameMP = MultiPart(
      name = "projectName",
      filename = "",
      mime = "text/plain",
      data = pName
    )
    val status = client
      .postMulti(
        "projects/",
        projectNameMP,
        dataMP
      )
      .option(HttpOptions.connTimeout(650))
      .option(HttpOptions.readTimeout(maxWait))
      .asString
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)
    Project.get(loc(0).replace(s"${client.endpoint}${this.path}", ""))
  }

  def createFromHDFS(
      url: String,
      port: String = null.asInstanceOf[String],
      projectName: String = null.asInstanceOf[String],
      maxWait: Int = 600000
  ) = throw new NotImplementedError("Nope")

  def createFromDataSource(
      dataSource: String,
      userName: String,
      password: String,
      projectName: String = null.asInstanceOf[String],
      maxWait: Int = 600000
  ) = throw new NotImplementedError("Nope")

  def createFromURL(
      url: String,
      projectName: String = null.asInstanceOf[String]
  )(implicit client: DataRobotClient) = {
    val pName = if (projectName == null) url else projectName
    val json = write(Map("url" -> url, "projectName" -> pName))
    client.postData(s"${path}", data = json).asString
  }

  def delete(projectId: String)(implicit client: DataRobotClient) = {
    client.delete(s"$path$projectId").asString
  }

  // def fromAsync(url: String)(implicit client: DataRobotClient) = throw new NotImplementedError("Nope")

  // getters
  def get(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/").asString
    if (r.code == 410) {
      val res = parse(r.body).extract[Map[String, Any]]
      throw new Exception(s"GONE => ${res.getOrElse("message", "??")}")
    }
    val result = parse(r.body) // comding from jackson.JsonMethods
    result.extract[Project]
  }

  def getProjects()(implicit client: DataRobotClient) = {
    val r = client.get(s"$path").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Project])
  }

  def start(
      sourceData: String,
      target: String,
      projectName: Option[String] = None,
      workerCount: Option[Int] = None,
      metric: Option[AccuracyMetric.Value] = None,
      autoPilotOn: Boolean = true,
      blueprintThreshold: Option[Int] = Some(1),
      partitioningMethod: Option[Partition] = None,
      positiveClass: Option[String] = None,
      targetType: Option[TargetType.Value] = None,
      mode: String = "autopilot",
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    // create project from data
    val project = this.createFromFile(
      sourceData,
      projectName.getOrElse(sourceData),
      maxWait
    )
    // set project target

    val initData: Seq[(String, Any)] = Seq(
      "target" -> target,
      "workerCount" -> workerCount,
      "metric" -> metric,
      "autoPilotOn" -> autoPilotOn,
      "blueprintThreshold" -> blueprintThreshold,
      "partitioningMethod" -> partitioningMethod,
      "positiveClass" -> positiveClass,
      "targetType" -> targetType,
      "mode" -> mode,
      "maxWait" -> maxWait
    ).filter(_._1 != None)
    val data = _getDataReady(initData)

    client.patch(s"projects/${project.id}/aim", data).asString

    Project.get(project.id)

  }

}
