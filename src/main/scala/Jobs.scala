package io.github.timsetsfire.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import io.github.timsetsfire.datarobot.Utilities._getDataReady
import io.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import io.github.timsetsfire.datarobot.enums._
import io.github.timsetsfire.datarobot.Utilities._
import io.github.timsetsfire.datarobot.Implicits._
import io.github.timsetsfire.datarobot.enums.JobType

import JobType._

/** Jobs
  * @param id (string) – the job ID of the job
  * @param projectId (string) – the project the job belongs to
  * @param status (string) – the status of the job - will be either ‘queue’, ‘inprogress’, ‘error’, ‘ABORTED’, or ‘COMPLETED’.
  * @param jobType (string) – the type of the job
  * @param isBlocked (boolean) – True if a job is waiting for its dependencies to be resolved first.
  * @param url (string) – a url that can be used to request details about the job (note: if the job is of a type about which we do not have routes to return more details, then this will be a link back to this same generic job retrieval route)
  */
class Job(
    val id: String,
    val projectId: String,
    val status: String,
    val jobType: JobType.Value,
    val isBlocked: Boolean,
    val url: String
) {

  override def toString = s"Job($jobType)"

  var location = ""
  def setLocationOfResult(url: String) = {
    location = url
  }

  def refresh()(implicit client: DataRobotClient) = {
    val url2 = url.replace(client.endpoint, "")
    val r = client.get(url2).asString
    val job = jobType match { 
      case PREDICT => parse(r.body).extract[PredictJob]
      case MODEL => parse(r.body).extract[ModelJob]
      case _ => parse(r.body).extract[Job]
    }
     r.code match {
      case 303 => job.setLocationOfResult(r.headers("location").apply(0))
      case _   => Unit
    }
    job
  }

  def getResultWhenComplete(
      maxWait: Int = 600000
  )(implicit client: DataRobotClient) = {
    val loc = Waiter.waitForAsyncResolution(this.url, maxWait).apply(0)
    val resp = client.get(loc.replace(client.endpoint, "")).asString
    val data = parse(resp.body)
    jobType match {
      case FEATURE_IMPACT  => data.extract[FeatureImpacts]
      case FEATURE_EFFECTS => data.extract[FeatureEffects]
      case FEATURE_FIT     => data.extract[FeatureFits]
      case MODEL           => data.extract[Model]
      case PRIME_RULESETS  => data.extract[List[PrimeRuleset]]
      case PRIME_MODEL => data.extract[PrimeModel]
      case PRIME_VALIDATION => data.extract[PrimeFileMetaData]
      case PREDICT => {
        val task = data.extract[Map[String, Any]].get("task")
        task match { 
          case Some("Binary") => data.extract[Predictions[BinaryPrediction]]
          case Some("Mulitclass") => data.extract[Predictions[MulticlassPrediction]]
          case Some("Regression") => data.extract[Predictions[RegressionPrediction]]
          case _ => throw new Exception(s"can't current return predict job of task type ${task}")
        }
      }
      case _ => 
        throw new Exception(
          s"can't currently return of job type ${jobType}"
        )
    }
  }
}

/** ModelJob
  * @param status (string) – the status of the job - will be either ‘queue’, ‘inprogress’, ‘error’, ‘ABORTED’, or ‘COMPLETED’.
  * @param isBlocked (boolean) – True if a job is waiting for its dependencies to be resolved first.
  * @param processes (array) – a json array of processes the modeling job includes
  * @param projectId (string) – the project the job is running within
  * @param samplePct (float) – the percentage of the dataset the job is using
  * @param modelType (string) – the type of model used by the job
  * @param featurelistId (string) – the featurelist the model is using
  * @param modelCategory (string)–indicates the type of model used by the job- either ‘prime’ for DataRobot Prime models, ‘blend’ for blender models, or ‘model’ for other cases
  * @param blueprintId (string) – the blueprint used by the model - note that this is not an ObjectId
  * @param id (string) – the job id
  */
class ModelJob(
    status: String,
    isBlocked: Boolean,
    val processes: Array[String],
    projectId: String,
    val samplePct: Option[Float] = None,
    val modelType: String,
    val featurelistId: String,
    val modelCategory: String,
    val blueprintId: String,
    id: String
) extends Job(
      id,
      projectId,
      status,
      JobType.MODEL,
      isBlocked,
      s"projects/${projectId}/modelJobs/${id}/"
    ) {

  override def toString = s"ModelJob($modelType)"
  // val url = s"projects/${projectId}/modelJobs/${id}/"
  def delete()(implicit client: DataRobotClient) = {
    val r = client.delete(url).asString
    r
  }

  /** get model when job completes
    * @return Model
    * @note This shouldn't be called until Model Job is done
    * @todo Improve functionality by making syncronous request
    */
  /**
    * @return Returns a new model jobs.  Does not actually update current ModelJob class
    */
  override def refresh()(implicit client: DataRobotClient) =
    ModelJob.get(projectId, id)

}

object Job {

  def get(projectId: String, jobId: String, jobUrl: String = "jobs")(
      implicit client: DataRobotClient
  ) = {
    val r = client.get(s"projects/${projectId}/$jobUrl/${jobId}/").asString
    val json = parse(r.body)
    val mj = jobUrl match {
      case "modelJobs"   => json.extract[ModelJob]
      case "predictJobs" => json.extract[PredictJob]
      case _             => json.extract[Job]
    }
    r.code match {
      case 303 => mj.setLocationOfResult(r.headers("location").apply(0))
      case _   => Unit
    }
    mj
  }

  def getJobs(
      projectId: String,
      jobUrl: String = "jobs",
      status: Option[String] = None
  )(
      implicit client: DataRobotClient
  ) = {
    val r = status match {
      case None => client.get(s"projects/${projectId}/$jobUrl/").asString
      case Some(status) =>
        if (List("queue", "inprogress", "error").contains(status.get)) {
          client
            .get(s"projects/${projectId}/$jobUrl/")
            .param("status", status)
            .asString
        } else {
          throw new Exception(s"don't understand status ${status} request")
        }
    }

    val JArray(json) = parse(r.body) match {
      case JObject(details) => details.toMap.apply("jobs")
      case s                => s
    }
    json.map { j =>
      jobUrl match {
        case "predictJobs" => j.extract[PredictJob]
        case "modelJobs"   => j.extract[ModelJob]
        case _             => j.extract[Job]
      }
    }
  }
}

object ModelJob {
  def get(projectId: String, jobId: String)(
      implicit client: DataRobotClient
  ) = {
    val r = client.get(s"projects/${projectId}/modelJobs/${jobId}/").asString
    val json = parse(r.body)
    val mj = json.extract[ModelJob]
    r.code match {
      case 303 => mj.setLocationOfResult(r.headers("location").apply(0))
      case _   => Unit
    }
    mj
  }
  def getModelJobs(projectId: String, status: Option[String] = None)(
      implicit client: DataRobotClient
  ) = {
    val r = status match {
      case None => client.get(s"projects/${projectId}/modelJobs/").asString
      case Some(status) =>
        if (List("queue", "inprogress", "error").contains(status.get)) {
          client
            .get(s"projects/${projectId}/modelJobs/")
            .param("status", status)
            .asString
        } else {
          throw new Exception(s"don't understand status ${status} request")
        }
    }
    val JArray(json) = parse(r.body)
    json.map { j => j.extract[ModelJob] }
  }
}

/** PredictJob
  * @param id (string) – the job ID of the job
  * @param projectId (string) – the project the job belongs to
  * @param modelId (string) – the model the job belongs to
  * @param status (string) – the status of the job - will be either ‘queue’, ‘inprogress’, ‘error’, ‘ABORTED’, or ‘COMPLETED’.
  * @param isBlocked (boolean) – True if a job is waiting for its dependencies to be resolved first.
  * @param message (string) –  If the server has a message to accompany the latest status update, it will be included here
  */
class PredictJob(
    id: String,
    projectId: String,
    val modelId: String,
    status: String,
    isBlocked: Boolean,
    val message: Option[String]
) extends Job(
      id,
      projectId,
      status,
      JobType.PREDICT,
      isBlocked,
      s"projects/${projectId}/predictJobs/${id}/"
    )
