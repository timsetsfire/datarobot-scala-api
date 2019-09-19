package com.datarobot
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
  status: Option[String],
  isBlocked: Option[Boolean],
  processes: Option[Array[String]],
  projectId: Option[String],
  samplePct: Option[Float],
  modelType: Option[String],
  featurelistId: Option[String],
  modelCategory: Option[String],
  blueprintId: Option[String],
  id: Option[String]
) {

  def delete()(implicit client: DataRobotClient) = ???

  def refresh()(implicit client: DataRobotClient) = ???
}

/** PredictJob

  */
case class PredictJob()
