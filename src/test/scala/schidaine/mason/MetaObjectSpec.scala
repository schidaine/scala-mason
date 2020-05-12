package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.{Meta, $, Controls, Link}
import play.api.libs.json.{Json, JsObject}

class MetaObjectSpec extends AnyFlatSpec with Matchers {

  "A Meta object with all of its properties" must "be converted to JSON without controls" in {
      Json.toJson(MetaObjectSpec.shared) mustEqual MetaObjectSpec.expectedJson
    }

  it must "accept a property twice, keeping the last one in JSON" in {
    val props = MetaObjectSpec.shared.properties :+ ($.metaTitle := "alterate title")
    val meta = Meta(props: _*)
    val expectedJson = MetaObjectSpec.expectedJson.deepMerge(
      Json.obj("@meta" -> Json.obj("@title" -> "alterate title"))
    )
    Json.toJson(meta) mustEqual expectedJson
  }

  it must "accept several controls and merge them in its JSON representation" in {
    val meta = Meta(
      $.relation("self") := Link($.href := "/ignored"),
      $.relation("self") := Link($.href := "/otherself"),
      $.relation("another-org:other") := Link($.href := "/other")
    )

    val expectedJson = Json.obj(
      "@meta" -> Json.obj(
        "@controls" -> Json.obj(
          "self" -> Json.obj("href" -> "/otherself"),
          "another-org:other" -> Json.obj("href" -> "/other")
        )
      )
    )

    Json.toJson(meta) mustEqual expectedJson
  }

  "An empty Meta object" must "be converted to JSON" in {
    val meta = Meta()

    val expectedJson = Json.obj(
      "@meta" -> JsObject.empty
    )

    Json.toJson(meta) mustEqual expectedJson
  }

}

object MetaObjectSpec extends Share {

  val shared = Meta(
    $.metaTitle := "test",
    $.metaDescription := "a bigger description",
    $.property("extended") := "an extended field"
  )

  val expectedJson = Json.obj(
    "@meta" -> Json.obj(
      "@title" -> "test",
      "@description" -> "a bigger description",
      "extended" -> "an extended field"
    )
  )

}
