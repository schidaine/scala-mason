package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.Namespaces
import play.api.libs.json.{Json, JsObject}

class NamespacesObjectSpec extends AnyFlatSpec with Matchers {



  "An Namespaces object" must "be converted to JSON" in {
    Json.toJson(NamespacesObjectSpec.shared) mustEqual NamespacesObjectSpec.expectedJson
  }

  it must "be aggregated with another Namespace in a JSON root object" in {
    val other = Namespaces("yet-another-ns" -> "http://yet.another.org/relations#")
    val expected = NamespacesObjectSpec.expectedJson deepMerge
      Json.obj(
        "@namespaces" -> Json.obj(
          "yet-another-ns" -> Json.obj(
            "name" -> "http://yet.another.org/relations#"
          )
        )
      )
    Json.toJson(NamespacesObjectSpec.shared ++ other) mustEqual expected
  }

  "An empty Namespaces object" must "be converted to JSON" in {

    val ns = Namespaces()

    val expectedJson = Json.obj(
      "@namespaces" -> JsObject.empty
    )

    Json.toJson(ns) mustEqual expectedJson
  }

}

object NamespacesObjectSpec extends Share {

  val shared = Namespaces(
    "my-org" -> "http://my.organization.org/rel-types",
    "another-org" -> "http://another.org/rels"
  )

  val expectedJson = Json.obj(
    "@namespaces" -> Json.obj(
      "my-org" -> Json.obj(
        "name" -> "http://my.organization.org/rel-types"
      ),
      "another-org" -> Json.obj(
        "name" -> "http://another.org/rels"
      )
    )
  )
}
