package io.github.timsetsfire.datarobot

import scala.util.Try
import java.io.{File, FileInputStream}
import java.net.URL
import scalaj.http.MultiPart
import scalaj.http.HttpOptions
import org.json4s._
import org.json4s.jackson.Serialization.{write, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import io.github.timsetsfire.datarobot.Utilities._
import io.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats

import io.github.timsetsfire.datarobot.enums._
import io.github.timsetsfire.datarobot.Implicits._

import breeze.linalg.Counter2

/**
  * @constructor create a new project.
  * @param id project id
  * @param projectName project name
  * @param fileName the name of the dataset used to create the project
  * @param stage
  * @param autopilotMode 0 if autopiloe, 2 is manual
  * @param created project creation time
  * @param target project target
  * @param metric the metric used to select the best-performing models
  * @param partition partition of given project.  See [[io.github.timsetsfire.datarobot.Partition]].
  * @param recommender does nothing
  * @param advancedOptions advanced options of the project.  See [[io.github.timsetsfire.datarobot.AdvancedOptions]].
  * @param positiveClass for binary classification projects, the class designated to be the positive class. Otherwise, null.
  * @param maxTrainPct the maximum percentage of the dataset that can be used to successfully train a model without going into the validation data
  * @param maxTrainRows the maximum number of rows of the dataset that can be used to suc- cessfully train a model without going into the validation data
  * @param scaleoutMaxTrainPct the maximum percentage of the dataset that can be used to successfully train a scaleout model without going into the validation data. May exceed maxTrainPct, in which case only scaleout models can be trained up to this point.
  * @param scaleoutMaxTrainRows the maximum number of rows of the dataset that can used be used to successfully train a scaleout model without going into the validation data. May exceed maxTrainRows, in which case only scaleout models can be trained up to this point.
  * @param holdoutUnlocked true if holdout has been unlocked
  * @param targetType either Regression, Binary, or Multiclass
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
  import io.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  override def toString = s"Project(${projectName.get})"

  def toDateTimeProject = {
    new DateTimeProject(
      id,
      projectName,
      fileName,
      stage,
      autopilotMode,
      created,
      target,
      metric,
      partition,
      recommender,
      advancedOptions,
      positiveClass,
      maxTrainPct,
      maxTrainRows,
      scaleoutMaxTrainPct,
      scaleoutMaxTrainRows,
      holdoutUnlocked,
      targetType
    )
  }

  /** project */
  // implicit val jsonDefaultFormats = DefaultFormats ++ enumFormats

  /** @return Returns a copy (post-EDA1) copy of the project.
    *  @param newProjectName Name of returns project
    *  @param maxWait Max wait time
    *  @todo add maxWait appropriate to request happening behind the scenes.
    */
  def clone(newProjectName: String, maxWait: Int = 60000)(
      implicit client: DataRobotClient
  ) = {
    val data = _getDataReady(
      Seq("projectId" -> this.id, "projectName" -> newProjectName)
    )
    val r = client.postData("projectClones/", data).asString
    val loc = Waiter.waitForAsyncResolution(r.headers("location")(0), maxWait)
    val pid = new File( new URL(loc(0)).getFile ).getName
    Project.get(pid)
  }

  /** @return Returns [[io.github.timsetsfire.datarobot.ModelJob]] for the requested blender
    *  @param models list of [[io.github.timsetsfire.datarobot.Model]] to use for blending
    *  @param blenderMethod one of [[io.github.timsetsfire.datarobot.enums.BlenderMethod]]
    */
  def createBlender(models: List[Model], blenderMethod: BlenderMethod.Value)(
      implicit client: DataRobotClient
  ) = {
    val params =
      Seq("modelIds" -> models.map { _.id }, "blenderMethod" -> blenderMethod)
    val data = _getDataReady(params)
    val r = client.postData(s"projects/${id}/blenderModels/", data).asString
    val loc = r.code match {
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[ModelJob]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  /**
    * @return Returns Unit.  Deletes projects
    */
  def delete()(implicit client: DataRobotClient) = Project.delete(this.id)

  /**
    * @param models List of models to blend together
    * @param blenderMethod
    */
  def getBlenderEligibility(
      models: List[Model],
      blenderMethod: BlenderMethod.Value
  )(implicit client: DataRobotClient) = {
    val params =
      Seq("modelIds" -> models.map { _.id }, "blenderMethod" -> blenderMethod)
    val data = _getDataReady(params)
    val r = client
      .postData(s"projects/${id}/blenderModels/blendCheck/", data)
      .asString
    parse(r.body).extract[Map[String, Any]]
  }

  /**
    * @return returns a list of eligible blueprints
    * @see [[io.github.timsetsfire.datarobot.Blueprint.getBlueprints]]
    */
  def getBlueprints()(implicit client: DataRobotClient) =
    Blueprint.getBlueprints(this.id)

  /**
    * @param id blueprint id
    * @return blueprint
    * @see [[io.github.timsetsfire.datarobot.Blueprint.get]]
    */
  def getBlueprint(id: String)(implicit client: DataRobotClient) =
    Blueprint.get(this.id, id)

  /**
    * @param blueprintId blueprint d
    * @todo implement this
    */
  def getBlueprintChart(blueprintId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    ) //Blueprint.getBlueprintChart(this.id, blueprintId)

  /**
    * @param blueprintId blueprint d
    * @todo implement this
    */
  def getReducedBlueprintChart(
      blueprintId: String
  )(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    ) //Blueprint.getReducedBlueprintChart(this.id, blueprintId)

  /**
    * @param blueprintId blueprint d
    * @todo implement this
    */
  def getBlueprintDocumentation(
      blueprintId: String
  )(implicit client: DataRobotClient) =
    throw new NotImplementedError(
      "Nope"
    )

  /**
    * @return list of models for the project
    */
  def getModels()(implicit client: DataRobotClient) =
    Model.getModels(this.id)

  /**
    * @param modelId model id to get
    * @return model for specified `modelId`
    */
  def getModel(modelId: String)(implicit client: DataRobotClient) =
    Model.getModel(this.id, modelId)

  /**
    * @return list of frozen models for project
    */
  def getFrozenModels()(implicit client: DataRobotClient) =
    Model.getFrozenModels(this.id)

  /**
    * @param modelId model id to get
    * @return frozen model for specified id
    * @todo implement this
    */
  def getFrozenModel(modelId: String)(implicit client: DataRobotClient) = ???

  /**
    * @return return [[io.github.timsetsfire.datarobot.Feature]] given specified featurename
    */
  def getFeature(featureName: String)(implicit client: DataRobotClient) =
    Feature.get(this.id, featureName)

  /**
    * @return set of all features available in project
    */
  def getFeatures()(implicit client: DataRobotClient) =
    Feature.getFeatures(this.id)

  /**
    * @return set of metrics available if given `featureName` is set as target
    */
  def getMetrics(featureName: String)(implicit client: DataRobotClient) =
    Feature.getMetrics(this.id, featureName)

  /** Create feature list within a project.  see also [[io.github.timsetsfire.datarobot.Featurelist.createFeaturelist]]
    *  @return new feature list object [[io.github.timsetsfire.datarobot.Featurelist]]
    *  @param name Name for new featurelist
    *  @param features List of feature names to include in list
    */
  def createFeaturelist(name: String, features: List[String])(
      implicit client: DataRobotClient
  ) = Featurelist.createFeaturelist(this.id, name, features)
  def deleteFeaturelist(featurelistId: String)(
      implicit cleint: DataRobotClient
  ) = Featurelist.delete(this.id, featurelistId)

  /**
    * @return all featurelists available in project
    */
  def getFeaturelists()(implicit client: DataRobotClient) =
    Featurelist.getFeaturelists(this.id)

  /**
    * @return list of users and permissions for given project
    */
  def getAccessList()(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${this.id}/accessControl/").asString
    val userList = parse(r.body).extract[Map[String, Any]].get("data")
    userList match {
      case Some(v) => v.asInstanceOf[List[Map[String, String]]]
      case None    => List()
    }
  }

  /**
    * @todo
    */
  def setAccessList()(implicit client: DataRobotClient) = ???

  def getJobs(status: Option[String] = None)(implicit client: DataRobotClient) =
    Job.getJobs(id, "jobs", status)
  def getJob(jobId: String)(implicit client: DataRobotClient) =
    Job.get(id, jobId)

  // association matrix helpers

  /**
    * @return Returns associtation matric details for `feature1` and `feature2`.
    */
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

  /**
    * @param metric association matrix metric
    * @param atype association matric value type
    * @param featurelistId
    * @return returns feature association matric for a given featurelist
    */
  def getFeatureAssocationMatrix(
      metric: FeatureAssociationMetric.Value =
        FeatureAssociationMetric.MUTUALINFO,
      atype: FeatureAssociationType.Value = FeatureAssociationType.ASSOCIATION,
      featurelistId: Option[String] = None
  )(implicit client: DataRobotClient) =
    FeatureAssociations.getFeatureAssocationMatrix(
      id,
      metric,
      atype,
      featurelistId
    )

  // {
  //   val url = s"${path}${this.id}/featureAssociationMatrix/"
  //   val params: Seq[(String, String)] = Seq(
  //     "metric" -> FeatureAssociationMetric.MUTUALINFO.toString,
  //     "type" -> FeatureAssociationType.ASSOCIATION.toString
  //   )
  //   val fam: Counter2[String, String, Double] = Counter2()
  //   // val fam2: Counter2[String, String, Double] = Counter2()
  //   val r = featurelistId match {
  //     case Some(fls) =>
  //       client.get(url).params(params ++ Seq("featurelistId" -> fls)).asString
  //     case _ => client.get(url).params(params).asString
  //   }
  //   val map = r.code match {
  //     case 200 => parse(r.body).extract[Map[String, List[Map[String, Any]]]]
  //     case _   => throw new Exception(s"${r.code}: ${r.body}")
  //   }
  //   map("strengths").foreach { map =>
  //     fam.update(
  //       map("feature1").toString,
  //       map("feature2").toString,
  //       map("statistic").asInstanceOf[Number].doubleValue
  //     )
  //   }
  //   parse(r.body).extract[Map[String, List[Map[String, Any]]]]
  //   (fam, map("features"))
  // }

  def getAssociationFeatureLists()(implicit client: DataRobotClient) = {
    val url = s"${path}${this.id}/featureAssociationMatrix/list/"
    val r = client.get(url).asString
    r.code match {
      case 200 => parse(r.body).extract[Seq[Map[String, String]]]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  /** Caluclated assocation matrix for specific featurelist
    * @param featurelistId
    */
  def requestFeatureAssocationMatrix(
      featurelistId: String
  )(implicit client: DataRobotClient) = {
    val url = s"${path}${this.id}/featureAssociationMatrix/"
    val data = _getDataReady(Seq("featurelistId" -> featurelistId))
    val r = client.postData(url, data).asString
    r.code match {
      case 202 =>
        "The feature association matrix calculation request succeeded."
      case _ => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  /**
    * @todo implement this
    */
  def getBlenders()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  /**
    * @todo implement this
    */
  def getDatasets()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  /**
    * @todo implement this
    */
  def getLeaderboardLink()(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  def getModelJob(jobId: String)(implicit client: DataRobotClient) =
    Job.get(id, jobId, "modelJobs")
  def getModelJobs(
      status: Option[String] = None
  )(implicit client: DataRobotClient) =
    Job.getJobs(
      id,
      "modelJobs",
      status
    ) //ModelJob.getModelJobs(this.id, status)

  /**
    * @todo implement this
    */
  def getPredictJob(jobId: String)(implicit client: DataRobotClient) =
    Job.get(id, jobId, "predictJobs")
  def getPredictJobs(status: Option[String] = None)(
      implicit client: DataRobotClient
  ) = Job.getJobs(id, "predictJobs", status)

  /**
    * @todo implement this
    */
  def getPrimeFiles(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  /**
    * @todo implement this
    */
  def getPrimeModels(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  /**
    * @return REturns a list of models recommended by DataRobot
    */
  def getRecommendedModels()(implicit client: DataRobotClient) =
    Model.getRecommendedModels(this.id)

  /**
    * @return Returns model recommended for deployment
    */
  def getRecommendedModel()(implicit client: DataRobotClient) =
    Model.getRecommendedModel(this.id)

  /**
    * @todo implement this
    */
  def getRatingTableModels(projectId: String)(
      implicit client: DataRobotClient
  ) = throw new NotImplementedError("Nope")

  /**
    * @todo implement this
    */
  def getRatingTables(projectId: String)(implicit client: DataRobotClient) =
    throw new NotImplementedError("Nope")

  /**
    * @todo implement this
    */
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

  /** Start modeling
    */
  def setTarget(
      target: String,
      mode: ModelingMode.Value = ModelingMode.AUTOPILOT,
      metric: Option[String] = None,
      quickrun: Boolean = false,
      positiveClass: Option[String] = None,
      partitioningMethod: Option[PartitioningMethod] = None,
      featurelistId: Option[String] = None,
      advancedOptions: Option[AdvancedOptions] = None,
      maxWait: Int = 600000, // need enum for this
      targetType: Option[TargetType.Value] = None,
      workerCount: Option[Int] = None
  )(implicit client: DataRobotClient) = {

    val advOpt = advancedOptions match {
      case Some(adv) => caseClassToMap(adv)
      case _         => Map()
    }
    val partitioning = partitioningMethod match {
      case Some(part) => {
        part match {
          case dt: DateTimePartitioningMethod => {
            val data = _getDataReady(
              Seq(
                "datetimePartitionColumn" -> dt.datetimePartitionColumn,
                "multiseriesIdColumns" -> dt.multiseriesIdColumns
              )
            )
            val r = client
              .postData(s"projects/${this.id}/multiseriesProperties/", data)
              .asString
            val loc = r.headers("location")
          }
          case _ => Unit
        }
        caseClassToMap(part)
      }
      case _ => Map()
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

    println(client.endpoint + s"projects/${this.id}/aim/")
    val status = client.patch(s"projects/${this.id}/aim/", data).asString
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)
    val pid = new File( new URL(loc(0)).getFile ).getName
    val project = Project.get(pid)
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

  /**
    * @param workerCount Number of workers to use for modeling
    */
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
      case 202 => r.headers("location")(0).replace(client.endpoint, "")
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
    val job = client.get(loc).asString
    job.code match {
      case 200 => parse(job.body).extract[ModelJob]
      case _   => throw new Exception(s"${r.code}: ${r.body}")
    }
  }

  def unlockHoldout()(implicit client: DataRobotClient) = {
    this.holdoutUnlocked match {
      case true => Unit
      case false => {
        val data = _getDataReady(Seq(("holdoutUnlocked", true)))
        val r = client.patch(s"${path}${id}/", data).asString
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

  def startAutopilot(
      projectId: String,
      featurelistId: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(("featurelistId", featurelistId)))
    client.patch(s"projects/$projectId/aim/", data)
  }

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

/** Factory for [[io.github.timsetsfire.datarobot.Project]] instances.
  */
object Project {

  import io.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  val path = "projects/"

  var maxWait = 600

  def setMaxWait(x: Int): Unit = { 
    maxWait = x
  }

  /** Create a project from local file
    * @param file
    * @param projectName
    * @param maxWait
    */

    /** create from id
    */
  def apply(projectId: String)(implicit client: DataRobotClient) = {
    get(projectId)(client)
  }

  /** create from file
    */
  def apply(file: java.io.File, projectName: String)(implicit
      client: DataRobotClient
  ) = {
    createFromFile(file.toString, projectName)(client)
    }

  /** create from spark dataframe
    */
  def apply(df: org.apache.spark.sql.DataFrame, projectName: String)(implicit
      client: DataRobotClient
  ) = {
    createFromSparkDf(df, projectName)(client)
  }

  /** create from url
    */
  def apply(url: java.net.URL, projectName: String)(implicit
      client: DataRobotClient
  ) = {
    createFromURL(url.toString, projectName)(client)
  }

  // /** TODO create from datasource ??
  //  */
  // def apply()

  /** create from AiCatalog Dataset
    */
  // def apply(dataset: Dataset, projectName: String)(implicit
  //     client: DataRobotClient
  // ) = {
  //   throw new NotImplementedError("Nope")
  // }

  def createFromFile(
      file: String,
      projectName: Option[String] = None,
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val pName: String = projectName match {
      case Some(s) => s
      case _       => file
    }
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
    val pid = new File( new URL(loc(0)).getFile ).getName
    Project.get(pid)
  }

  // not great.  when toLocalIterator is run, data must fit into mani memory on head node to be feed local iterator
  // not necessarily and issue with beefy databricks, but less than idea when sharing resourcs.
  /** Create a project from Spark DataFrame
    *  @param df
    *  @param projectName
    *  @param maxWait
    */
  def createFromSparkDf(
      df: org.apache.spark.sql.DataFrame,
      projectName: Option[String] = None,
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val fs = DataFrameAsInputStream(df)
    val pName: String = projectName match {
      case Some(s) => s
      case _       => "Spark DataFrame"
    }
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
    val pid = new File( new URL(loc(0)).getFile ).getName
    val project = Project.get(pid)
  }

  /** Create project from HDFS
    *  @todo implement this
    */
  def createFromHDFS(
      url: String,
      port: String = null.asInstanceOf[String],
      projectName: Option[String] = None,
      maxWait: Int = 600000
  ) = throw new NotImplementedError("Nope")

  /** Create project from HDFS
    *  @todo implement this
    */
  def createFromDataSource(
      dataSource: String,
      userName: String,
      password: String,
      projectName: Option[String] = None,
      maxWait: Int = 600000
  ) = throw new NotImplementedError("Nope")

  /** Create project from URL (e.g. csv in S3 bucket)
    *  @param url
    *  @param projectName
    */
  def createFromURL(
      url: String,
      projectName: Option[String] = None
  )(implicit client: DataRobotClient) = {
    val pName = if (projectName == null) url else projectName
    val json = write(Map("url" -> url, "projectName" -> pName))
    client.postData(s"${path}", data = json).asString
  }

  /** Delete a given project
    */
  def delete(projectId: String)(implicit client: DataRobotClient) = {
    client.delete(s"$path$projectId/").asString
  }

  /** Get a project
    *  @param projectId
    */
  def get(projectId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"${path}${projectId}/").asString
    if (r.code == 410) {
      val res = parse(r.body).extract[Map[String, Any]]
      throw new Exception(s"GONE => ${res.getOrElse("message", "??")}")
    }
    val result = parse(r.body) // comding from jackson.JsonMethods
    result.extract[Project]
  }

  /** Get all projects
    */
  def getProjects()(implicit client: DataRobotClient) = {
    val r = client.get(s"$path").asString
    val result = parse(r.body)
    val JArray(ps) = result
    ps.map(p => p.extract[Project])
  }

  /** Start modeling with autopilot
    */
  def start(
      sourceData: String,
      target: String,
      projectName: Option[String] = None,
      workerCount: Option[Int] = None,
      metric: Option[AccuracyMetric.Value] = None,
      autoPilotOn: Boolean = true,
      blueprintThreshold: Option[Int] = Some(1),
      partitioningMethod: Option[PartitioningMethod] = None,
      positiveClass: Option[String] = None,
      targetType: Option[TargetType.Value] = None,
      mode: String = "autopilot",
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    // create project from data
    val project = this.createFromFile(
      sourceData,
      projectName match {
        case Some(s) => projectName
        case _       => sourceData
      },
      maxWait
    )
    // set project target

    val partitioning = partitioningMethod match {
      case Some(part) => {
        part match {
          case dt: DateTimePartitioningMethod => {
            val data = _getDataReady(
              Seq(
                "datetimePartitionColumn" -> dt.datetimePartitionColumn,
                "multiseriesIdColumns" -> dt.multiseriesIdColumns
              )
            )
            val r = client
              .postData(s"projects/${project.id}/multiseriesProperties/", data)
              .asString
            val loc = r.headers("location")
          }
          case _ => Unit
        }
        caseClassToMap(part)
      }
      case _ => Map()
    }

    val initData: Seq[(String, Any)] = Seq(
      "target" -> target,
      "workerCount" -> workerCount,
      "metric" -> metric,
      "autoPilotOn" -> autoPilotOn,
      "blueprintThreshold" -> blueprintThreshold,
      "positiveClass" -> positiveClass,
      "targetType" -> targetType,
      "mode" -> mode,
      "maxWait" -> maxWait
    ).filter(_._1 != None)
    val data = _getDataReady(initData ++ partitioning)

    client.patch(s"projects/${project.id}/aim", data).asString

    Project.get(project.id)

  }



}
