package com.datarobot

/** Jobs
 * @param id (string) – the job ID of the job
 * @param projectId (string) – the project the job belongs to
 * @param status (string) – the status of the job - will be either ‘queue’, ‘inprogress’, ‘error’, ‘ABORTED’, or ‘COMPLETED’.
 * @param jobType (string) – the type of the job
 * @param isBlocked (boolean) – True if a job is waiting for its dependencies to be resolved first.
 * @param url (string) – a url that can be used to request details about the job (note: if the job is of a type about which we do not have routes to return more details, then this will be a link back to this same generic job retrieval route)
 */ 
case class Job(
  id: String,
  projectId: String, 
  status: String, 
  jobType: String, 
  isBlocked: Boolean,
  url: String
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
case class ModelJob(
  status: String,
  isBlocked: Boolean,
  processes: Array[String],
  projectId: String,
  samplePct: Float,
  modelType: String,
  featurelistId: String,
  modelCategory: String,
  blueprintId: String,
  id: String
) {

    
  def delete()(implicit client: DataRobotClient) = ???

  def refresh()(implicit client: DataRobotClient) = ???

}

/** PredictJob

  */
case class PredictJob()
