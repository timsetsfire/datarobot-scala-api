package com.datarobot

object Implicits { 
    implicit def toOption[T](x: T) = Some(x)
    implicit def toLong(x: Int) = x.toLong
}