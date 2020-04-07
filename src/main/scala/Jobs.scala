package com.datarobot

import scala.util.Try
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.Utilities._getDataReady
import com.datarobot.enums.EnumFormats.enumFormats
import com.datarobot.enums._
import com.datarobot.Utilities._
import com.datarobot.Implicits._

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
  val jobType: String, 
  val isBlocked: Boolean,
  val url: String
) 

/** ModelJob
  * @param status (string) – the status of the job - will be either ‘queue’, ‘inprogress’, ‘error’, ‘ABORTED’, or ‘COMPLETED’.
  * @param isBlocked (boolean) – True if a job is waiting for its dependencies to be resolved first.
 280 Chapter31. APIReference
  * @param processes (array) – a json array of processes the modeling job includes
  * @param projectId (string) – the project the job is running within
  * @param samplePct (float) – the percentage of the dataset the job is using
  * @param modelType (string) – the type of model used by the job
  * @param featurelistId (string) – the featurelist the model is using
  * @param modelCategory (string)–indicatesthetypeofmodelusedbythejob-either‘prime’
for DataRobot Prime models, ‘blend’ for blender models, or ‘model’ for other cases
  * @param blueprintId (string) – the blueprint used by the model - note that this is not an ObjectId
  * @param id (string) – the job id
  */
class ModelJob(
  val status: String,
  val isBlocked: Boolean,
  val processes: Array[String],
  val projectId: String,
  val samplePct: Float,
  val modelType: String,
  val featurelistId: String,
  val modelCategory: String,
  val blueprintId: String,
  val id: String
)  {


  def delete()(implicit client: DataRobotClient) = {
    val r = client.delete(s"projects/${projectId}/modelJobs/${id}/").asString
    r
  }

  /** get model when job completes
   * @return Model
   * @note This shouldn't be called until Model Job is done
   * @todo Improve functionality by making syncronous request 
   */ 
  def getResultWhenComplete()(implicit client: DataRobotClient) = {
    val url = this.location.replace(client.endpoint, "")
    val r = client.get(url).asString
    parse(r.body).extract[Model]
  }

  /**
   * @return Returns a new model jobs.  Does not actually update current ModelJob class
   */ 
  def refresh()(implicit client: DataRobotClient) = ModelJob.get(projectId, id)

  var location = ""

  def setLocationOfResult(url: String) = { 
    location = url
  }

}

object ModelJob { 
  def get(projectId: String, jobId: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"projects/${projectId}/modelJobs/${jobId}/").asString
    val json = parse(r.body)
    val mj = json.extract[ModelJob]
    r.code match { 
      case 303 => mj.setLocationOfResult( r.headers("location").apply(0))
      case _ => Unit
    }
    mj
  }

  def getModelJobs(projectId: String, status: Option[String] = None
  )(implicit client: DataRobotClient) = {
    if (List("queue", "inprogress", "error").contains(status.get)) {
      val r = status match {
        case None => client.get(s"projects/${projectId}/modelJobs/").asString
        case Some(status) =>
          client
            .get(s"projects/${projectId}/modelJobs/")
            .params("status" -> status)
            .asString
      }
      val JArray(json) = parse(r.body)
      json.map { j => j.extract[ModelJob] }
    } else {
      throw new Exception(s"don't understand status ${status} request")
    }
  }
}

/** PredictJob

  */
case class PredictJob()
