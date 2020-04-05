package com.datarobot

import org.json4s._
import org.json4s.jackson.Serialization.{write, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.datarobot.Utilities._
import com.datarobot.enums.EnumFormats.enumFormats
import com.datarobot.Utilities._
import java.io.Serializable
import scala.collection.mutable.{Map => MMap}

case class AdvancedTuningSession(model: Model, description: Option[String] = None)(implicit client: DataRobotClient) {

    type TaskName = String

    implicit val jsonDefaultFormats = DefaultFormats ++ enumFormats

    private val currentHyperparameters = model.getHyperParameters

    val taskNames = currentHyperparameters("tuningParameters").map{ r => Seq( r("taskName").asInstanceOf[String] -> Seq(r("parameterName").asInstanceOf[String] -> r("parameterId" ).asInstanceOf[String])) }
    
    private val privateMap = taskNames.tail.foldLeft( taskNames.head) { (x, y) => if(x(0)._1 == y(0)._1) Seq(x(0)._1 -> (y(0)._2 ++ x(0)._2)) else x ++ y }.map{ r => (r._1, r._2.toMap)}.toMap

    val advTuningMap: MMap[String, Seq[Seq[(String, Any)]]] = MMap("tuningParameters" -> Seq())

    def getTaskNames = privateMap.keys
    def getParameterNames(taskName: TaskName) = {
        privateMap(taskName)
    }
    
    def setParameter(taskName: TaskName, parameterName: String, value: Any) = {
        advTuningMap.update("tuningParameters", advTuningMap("tuningParameters") ++ Seq( Seq("parameterId" -> privateMap(taskName)(parameterName), "value" -> value)))
    }

    def startTuning() = {
        val finalMap = Map( "tuningDescription" -> description, "tuningParameters" -> advTuningMap("tuningParameters").map{ _.toMap})
        val data = write(finalMap)
        val r = client.postData(s"projects/${model.projectId}/models/${model.id}/advancedTuning/", data).asString
        val loc = r.code match { 
            case 202 => r.headers("location")(0).replace(client.endpoint, "")
            case _ => throw new Exception(s"${r.code}: ${r.body}")
        }
        val job = client.get(loc).asString 
        job.code match { 
            case 200 => parse(job.body).extract[ModelJob]
            case _ => throw new Exception(s"${r.code}: ${r.body}")
        }
    }
    
}


// # Get available task names,
// # and available parameter names for a task name that exists on this model
// tune.get_task_names()
// tune.get_parameter_names('Eureqa Generalized Additive Model Classifier (3000 Generations)')

// tune.set_parameter(
//     task_name='Eureqa Generalized Additive Model Classifier (3000 Generations)',
//     parameter_name='EUREQA_building_block__sine',
//     value=1)

// job = tune.run()