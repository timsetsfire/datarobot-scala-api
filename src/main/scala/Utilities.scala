package com.datarobot
import org.json4s._
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
object Utilities {

  implicit val jsonDefaultFormats = DefaultFormats

  def _getDataReady(data: Seq[(String, Any)]) = {
    write(data.toMap)
  }
}
