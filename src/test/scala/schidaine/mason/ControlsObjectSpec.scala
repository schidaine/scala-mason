package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.{Controls, Link, $, HttpVerbs, Encodings}
import play.api.libs.json.{Json, JsObject}

class ControlsObjectSpec extends AnyFlatSpec with Matchers {

  "An Controls object with links and their properties" must "be converted to JSON" in {
    Json.toJson(ControlsObjectSpec.shared) mustEqual ControlsObjectSpec.expectedJson
  }

  "An empty Controls object" must "be converted to JSON" in {

    val ctrl = Controls()

    val expectedJson = Json.obj(
      "@controls" -> JsObject.empty
    )

    Json.toJson(ctrl) mustEqual expectedJson
  }

}

object ControlsObjectSpec extends HttpVerbs with Encodings with Share {

  val shared = Controls(
    "my-org:test" -> Link(
      $.href := "/test",
      $.isHrefTemplate := false,
      $.method := PUT,
      $.encoding := JSON,
      $.title := "Control's title",
      $.description := "Control's description",
      $.output := Seq("application/vnd.mason+json")
    ),
    "self" -> Link(
      $.href := "/myself"
    )
  )

  val expectedJson = Json.obj(
    "@controls" -> Json.obj(
      "my-org:test" -> Json.obj(
        "title" -> "Control's title",
        "description" -> "Control's description",
        "href" -> "/test",
        "isHrefTemplate" -> false,
        "method" -> "PUT",
        "encoding" -> "json",
        "output" -> Json.arr("application/vnd.mason+json")
      ),
      "self" -> Json.obj(
        "href" -> "/myself"
      )
    )
  )

}
