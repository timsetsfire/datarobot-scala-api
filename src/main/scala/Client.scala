package com.datarobot
import scalaj.http._

class DataRobotClient(token: String, val endpoint: String) {

  val auth = ("Authorization",s"token ${token}")

  def get(url: String, contentType: String = "application/json") = {
    Http(s"${endpoint}${url}").headers(
        auth,
        ("Content-Type", contentType)
    )
  }
  def delete(url: String, contentType: String = "application/json") = {
    Http(s"${endpoint}${url}").headers(
      auth,
      ("Content-Type", contentType)
    ).method("DELETE")
  }

  def postForm(url: String, data: Seq[(String,String)], contentType: String = "application/json") = {
    Http(s"${endpoint}${url}").headers(
      auth,
      ("Content-Type", contentType)
    ).postForm(data)
  }
  def postData(url: String, data: String, contentType: String = "application/json") = {
    Http(s"${endpoint}${url}").headers(
      auth,
      ("Content-Type", contentType)
    ).postData(data)
  }
  def postMulti(url: String, mp: scalaj.http.MultiPart*) = {
    Http(s"${endpoint}${url}").headers(
      auth,
      ("Content-Type", "multipart/form-data")
    ).postMulti(mp:_*)
  }

  def patch(url: String, data: String, contentType: String = "application/json") = {
    postData(url, data, contentType).method("PATCH")
  }

  def update(url: String, data: String, contentType: String = "application/json") = {
    postData(url, data, contentType).method("UPDATE")
  }

}
object DataRobotClient {
  def apply(token: String, endpoint: String) = new DataRobotClient(token, endpoint)
}
