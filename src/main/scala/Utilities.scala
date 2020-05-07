package com.github.timsetsfire.datarobot
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import org.apache.spark.sql.DataFrame
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats
import scalaj.http.HttpOptions

object Utilities {

  import com.github.timsetsfire.datarobot.Implicits.jsonDefaultFormats

  def _getDataReady(data: Seq[(String, Any)]) = {
    write(data.toMap)
  }


  object Waiter {
    def waitForAsyncResolution(url: String, maxWait: Int=600000)(implicit client: DataRobotClient) = {
      val baseTime = System.currentTimeMillis
      
      def asyncHelper(r: String, maxWait: Int )(implicit client: DataRobotClient): IndexedSeq[String] = {
        val resp = client.get(r).option(HttpOptions.connTimeout(650)).option(HttpOptions.readTimeout(maxWait)).asString
        if(System.currentTimeMillis - baseTime > maxWait) throw new Exception("timeout")
        else {
          if(resp.code == 303) {
          resp.headers("location")
          } else if (resp.code != 200) {
            throw new Exception(s"The server gave an unexpected response. Status Code ${resp.code}: ${resp.body}")
          } else {
            val check = parse(resp.body).extract[Map[String, Any]]
            check("status") match { 
              case "ERROR" => throw new Exception(check("message").toString)
              case _ => asyncHelper(r, maxWait)
            }
          
          }
        }
      }
      val url2 = url.replace(client.endpoint, "")
      asyncHelper(url2, maxWait)
    }
  }

  def caseClassToMap(cc: AnyRef) = {
    cc.getClass.getDeclaredFields.foldLeft(Map.empty[String, Any]) { (a, f) =>
    f.setAccessible(true)
    a + (f.getName -> f.get(cc))  }  
  }

  def coefficientHelper(map: Map[String, Any]) = {
    Coefficient(
        map("coefficient").asInstanceOf[Double],
        map("originalFeature").asInstanceOf[String],
        map("stageCoefficients").asInstanceOf[List[String]],
        map("transformations").asInstanceOf[List[Map[String, String]]],
        map("derivedFeature").asInstanceOf[String],
        map("type").asInstanceOf[String]
    )
  }

  case class DataFrameAsInputStream(df: org.apache.spark.sql.DataFrame) extends java.io.InputStream { 
    // val bytesDf = df.rdd.flatMap{ _.mkString("\"","\",\"","\"\n").getBytes("UTF-8")}
    val bytesDf = df.rdd.mapPartitions{ rows => rows.map{ _.mkString("\"","\",\"","\"\n").getBytes("UTF-8")} }.flatMap{ r => r}
    bytesDf.persist
    val numBytesDf = bytesDf.count
    val bytesColumns = df.columns.mkString("",",","\n").getBytes("UTF-8")
    val numBytesColumns = bytesColumns.length
    val numBytes: Long = numBytesDf + numBytesColumns
    val bytes = bytesColumns.toIterator ++ bytesDf.toLocalIterator
    def read(): Int = {
        if(bytes.hasNext) bytes.next.toInt
        else {
          bytesDf.unpersist(true)
          -1
        }
    }
    override def markSupported(): Boolean = false
  }
  
}


