package com.datarobot
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import org.apache.spark.sql.DataFrame
object Utilities {

  implicit val jsonDefaultFormats = DefaultFormats

  def _getDataReady(data: Seq[(String, Any)]) = {
    write(data.toMap)
  }


  object Waiter {
    def waitForAsyncResolution(url: String, maxWait: Int=600000)(implicit client: DataRobotClient) = {
      val baseTime = System.currentTimeMillis
      
      def asyncHelper(r: String, maxWait: Int )(implicit client: DataRobotClient): IndexedSeq[String] = {
        val resp = client.get(r).asString
        if(System.currentTimeMillis - baseTime > maxWait) throw new Exception("timeout")
        else {
          if(resp.code == 303) {
          resp.headers("location")
          } else if (resp.code != 200) {
            throw new Exception(s"The server gave an unexpected response. Status Code ${resp.code}: ${resp.body}")
          } else {
          asyncHelper(r, maxWait)
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

  case class DataFrameAsInputStream(df: DataFrame) extends java.io.InputStream { 
    private val columns = df.columns.mkString("",",","\n").getBytes
    private val data = df.rdd.map{ _.mkString("\"","\",\"","\"\n")}.flatMap(_.getBytes("UTF-8"))
    override val available = data.count.toInt + columns.length
    var bytes = columns.toIterator ++ data.toLocalIterator
    def read(): Int = {
      if(bytes.hasNext) bytes.next.toInt
      else -1
    }
    override def markSupported(): Boolean = false
}
  
}


