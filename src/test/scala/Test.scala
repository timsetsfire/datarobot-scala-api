import org.scalatest.flatspec.AnyFlatSpec
import io.github.timsetsfire.datarobot._
import io.github.timsetsfire.datarobot.Implicits._
import io.github.timsetsfire.datarobot.enums.{CVMethod, ValidationType, ModelingMode, Source}
import org.json4s.jackson.Serialization.writePretty
import java.io.File

class SetSpec extends AnyFlatSpec {

  implicit val client = DataRobotClient("config.yaml")  

  "client.get(\"ping\")" should "return pong" in {
      assert( client.get("ping").asString.body == "{\"response\": \"pong\", \"token\": null}")
  }

  "Project(file: java.io.File, projectName: String)" should "return a project" in {
      val currDir = System.getProperty("user.dir")
      val file = new File(currDir, "src/test/resources/training.csv")
      val project = Project(file, "test project")
      assert( project.getClass.toString == "class io.github.timsetsfire.datarobot.Project")
  }

}


// object Test extends App {



    // val file = new File("training.csv")
    // val project = Project(file, "Lending Club from Spark")
    // project.setTarget("readmitted", maxWait = 300000)
    // project.setWorkerCount(-1)
  
// }
