package com.github.timsetsifre.datarobot

import scalaj.http.Multipart
import com.github.timsetsfire.datarobot.Utilities._
import scala.util.Try
import java.io.{File, FileInputStream}
import scalaj.http.MultiPart
import scalaj.http.HttpOptions
import org.json4s._
import org.json4s.jackson.Serialization.{write, writePretty, formats}
import org.json4s.jackson.JsonMethods._
import org.json4s._
import org.json4s.native.JsonMethods
import org.json4s.{DefaultFormats, Extraction, JValue}
import com.github.timsetsfire.datarobot.Utilities._
import com.github.timsetsfire.datarobot.enums.EnumFormats.enumFormats

/**
  * @param Id – I know i know.  Capitalized field, ugh.  Did this the return from api request has this capitalized
  * @param created ISO-8601 string with the time that this calendar was created
  * @param name the name of this calendar. This will be source if no name was specified
  * @param source the filename of the uploaded calendar
  * @param numEvents - number of dates marked as having events in the calendard.  
  * @param numEventTypes the number of distinct eventTypes in this calendar
  * @param calendarStartDate ISO-8601 date string of the earliest event seen in this cal- endar
  * @param calendarEndDate: ISO-8601 date string of the latest event seen in this calendar
  * @param projectId projectIds of projects currently using this calendar
  * @param role the role the requesting user has on this calendar
  * @param multiseriesIdColumns array of multiseries ID column names in calendar file. Currently only one multiseries ID column is supported.
  */
case class Calendar(
    Id: String,
    created: String,
    name: String,
    source: String,
    numEvents: Int,
    numEventTypes: Int,
    calendarStartDate: String,
    calendarEndDate: String,
    projectId: Seq[String],
    role: String,
    multiseriesIdColumns: Seq[String]
) {
  val path = "calendars/"
  override def toString = s"Calendar($name)"

  def delete()(implicit client: DataRobotClient) = {
    client.delete(s"$path$Id/").asString
  }

  def setName(name: String)(implicit client: DataRobotClient) = {
    val data = _getDataReady(Seq(("calendarName", name)))
    val r = client.patch(s"${path}${Id}/", data).asString
    r.code match {
      case 200 => Unit
      case _ =>
        throw new Exception(s"Something went wrong. Response return: ${r.code}")
    }
    Calendar.get(Id)
  }

  /**
    * @todo
    */
  def setAccessControl()(implicit client: DataRobotClient) = ???
  def getAccessControl()(implicit client: DataRobotClient) = ???
}
object Calender {
  val path = "calendars/"
  def create(file: String, name: String)(implicit client: DataRobotClient) = {
    val fs: java.io.InputStream =
      new java.io.FileInputStream("./data/event_calendar_sales.csv")
    val bytesInStream = fs.available
    val dataMP = MultiPart(
      name = "file",
      filename = file,
      mime = "text/csv",
      data = fs,
      numBytes = bytesInStream,
      lenWritten => Unit
    )
    val nameMP = MultiPart(
      name = "name",
      filename = "",
      mime = "text/plain",
      data = name
    )

    val status =
      client.postMulti("calendars/fileUpload/", nameMP, dataMP).asString
    status.code match {
      case 202    => Unit
      case x: Int => throw new Exception(s"$x: ${status.body}")
    }
    val loc =
      Waiter.waitForAsyncResolution(status.headers("location")(0), maxWait)

    val r = client.get(loc.replace(client.endpoint, "")).asString
    parse(r.body).extract[Calendar]
  }

  def get(id: String)(implicit client: DataRobotClient) = {
    val r = client.get(s"calendars/${id}/").asString
    parse(r.body).extract[Calendar]
  }

  def getCalendars()(implicit client: DataRobotClient) = {
    val r = client
      .get(s"calendars/")
      .option(HttpOptions.connTimeout(650))
      .option(HttpOptions.readTimeout(60000))
      .asString
    val JObject(ls) = parse(r.body)
    val JArray(json) = ls(2)._2
    json.map { j => j.extract[Calendar] }
  }

}
