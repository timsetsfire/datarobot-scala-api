package com.datarobot

object Implicits { 
    implicit def toOption[T](x: T) = Some(x)
}