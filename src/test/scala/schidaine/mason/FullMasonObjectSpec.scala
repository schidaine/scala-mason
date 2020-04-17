package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason._
import play.api.libs.json.Json

class FullMasonObjectSpec extends AnyFlatSpec with Matchers {

  "A Mason object with its sub-objects" must "be converted to JSON" in {

    val mason =
      NamespacesObjectSpec.shared ++
      Json.obj("data" -> "a small data") ++
      ControlsObjectSpec.shared ++
      MetaObjectSpec.shared

    val expectedJson =
      NamespacesObjectSpec.expectedJson ++
      Json.obj("data" -> "a small data") ++
      ControlsObjectSpec.expectedJson ++
      MetaObjectSpec.expectedJson

    Json.toJson(mason) mustEqual expectedJson
  }

  "A Mason object with an error object" must "be converted to JSON" in {

    import MasonObject._ // so we can start mason value with a JsObject

    val mason =
      Json.obj("data" -> "a small data") ++ ErrorObjectSpec.shared
    val expectedJson =
      Json.obj("data" -> "a small data") ++
      ErrorObjectSpec.expectedJson

    Json.toJson(mason) mustEqual expectedJson

  }

}
