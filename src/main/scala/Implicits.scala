package com.datarobot
import com.datarobot.enums.EnumFormats.enumFormats
import org.json4s.DefaultFormats

object Implicits { 
    implicit val jsonDefaultFormats = DefaultFormats ++ enumFormats
    implicit def toOption[T](x: T) = Some(x)
    implicit def toLong(x: Int) = x.toLong
}