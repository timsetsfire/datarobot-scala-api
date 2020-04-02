
package com.datarobot

class Test(x: Int) { 
    implicit val id = x

    def f(x: Int) = Test.f(x)
}
object Test { 
    def f(x: Int)(implicit y: Int) = x*y
}