package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.JsonObject
import play.api.libs.json._

class JsonObjectSpec extends AnyFlatSpec with Matchers {

  val json1 = Json.obj(
    "main"    -> "data",
    "details" -> Json.obj(
      "data1" -> 1,
      "data2" -> 2
    ),
    "array"   -> Json.arr("arr1", "arr2")
  )

  "A Json object" must "act as a Mason object" in {
    Json.toJson(JsonObject(json1)) mustEqual json1
  }

}
