package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason._
import play.api.libs.json.Json

class RootObjectSpec extends AnyFlatSpec with Matchers {

  val root1 =
    NamespacesObjectSpec.shared ++
    Json.obj("data" -> "a small data 1") ++
    ControlsObjectSpec.shared ++
    MetaObjectSpec.shared

  val root2 =
    NamespacesObjectSpec.shared ++
    Json.obj("data" -> "a small data 2") ++
    ControlsObjectSpec.shared ++
    MetaObjectSpec.shared

  "A Root object with mason properties" must "be converted to JSON" in {

    val expectedJson =
      NamespacesObjectSpec.expectedJson ++
      Json.obj("data" -> "a small data 1") ++
      ControlsObjectSpec.expectedJson ++
      MetaObjectSpec.expectedJson

    Json.toJson(root1) mustEqual expectedJson
  }

  it must "be merged with another RootObject" in {
    val expectedJson =
      NamespacesObjectSpec.expectedJson ++
      Json.obj("data" -> "a small data 1") ++
      Json.obj("data" -> "a small data 2") ++
      ControlsObjectSpec.expectedJson ++
      MetaObjectSpec.expectedJson

    Json.toJson(root1 ++ root2) mustEqual expectedJson
  }

  it must "be merged into array with another RootObject" in {
    val expectedJson =
      NamespacesObjectSpec.expectedJson ++
      Json.obj("array" ->
        (Json.obj("data" -> "a small data 1") ++ ControlsObjectSpec.expectedJson,
        Json.obj("data" -> "a small data 2") ++ ControlsObjectSpec.expectedJson)) ++
      MetaObjectSpec.expectedJson

      Json.toJson(RootObject.mergeIntoArray(List(root1, root2), "array")) mustEqual expectedJson
  }

  it must "be merged into array with an empty RootObject" in {
    val expectedJson =
      NamespacesObjectSpec.expectedJson ++
      Json.obj("array" -> Json.arr(
        Json.obj("data" -> "a small data 1") ++ ControlsObjectSpec.expectedJson)) ++
      MetaObjectSpec.expectedJson

    Json.toJson(RootObject.mergeIntoArray(List(root1, RootObject.empty), "array")) mustEqual expectedJson
  }

  "An empty RootObject" must "be merged into array with another empty RootObject" in {
    Json.toJson(RootObject.mergeIntoArray(List(RootObject.empty, RootObject.empty), "empty")) mustEqual
      Json.obj("empty" -> Json.arr())
  }

  "A Root object with an error object" must "be converted to JSON" in {

    import MasonObject._ // so we can start mason value with a JsObject

    val root =
      Json.obj("data" -> "a small data") ++ ErrorObjectSpec.shared
    val expectedJson =
      Json.obj("data" -> "a small data") ++
      ErrorObjectSpec.expectedJson

    Json.toJson(root) mustEqual expectedJson

  }

}
