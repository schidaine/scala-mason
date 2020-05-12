package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.{Error, $, Controls, Link}
import play.api.libs.json._

class ErrorObjectSpec extends AnyFlatSpec with Matchers {

  "An Error object with its properties" must "be converted to JSON without controls" in {
    Json.toJson(ErrorObjectSpec.shared) mustEqual ErrorObjectSpec.expectedJson
  }

  it must "accept an optional property twice, keeping the last one in JSON" in {
    val props = ErrorObjectSpec.shared.properties :+ ($.errorCode := "ALT")
    val error = Error(ErrorObjectSpec.shared.message, props: _*)
    val expectedJson = ErrorObjectSpec.expectedJson.deepMerge(
      Json.obj("@error" -> Json.obj("@code" -> "ALT"))
    )

    Json.toJson(error) mustEqual expectedJson
  }

  it must "accept several controls and merge them in its JSON representation" in {

    val error = Error(
      $.errorMessage := "There is a problem",
      $.relation("self") := Link($.href := "/ignored"),
      $.relation("self") := Link($.href := "/otherself"),
      $.relation("another-org:other") := Link($.href := "/other")
    )

    val expectedJson = Json.obj(
      "@error" -> Json.obj(
        "@message" -> "There is a problem",
        "@controls" -> Json.obj(
          "self" -> Json.obj("href" -> "/otherself"),
          "another-org:other" -> Json.obj("href" -> "/other")
        )
      )
    )

    Json.toJson(error) mustEqual expectedJson
  }

}

object ErrorObjectSpec extends Share {

  val shared = Error(
    $.errorMessage := "There is a big problem",
    $.errorId := "4c4d7b1d-c76c-480e-9829-f94afed8020e",
    $.errorCode := "PROBLEM",
    $.errorMessages := Seq("A detail message","another detail message"),
    $.errorDetails := "message to client developer",
    $.errorHttpStatusCode := 500,
    $.errorTime := "2020-04-01T15:20:50.52Z",
    $.property("specific-prop") := "a specific error message"
  )

  val expectedJson = Json.obj(
    "@error" -> Json.obj(
      "@id" -> "4c4d7b1d-c76c-480e-9829-f94afed8020e",
      "@message" -> "There is a big problem",
      "@code" -> "PROBLEM",
      "@messages" -> Json.arr("A detail message","another detail message"),
      "@details" -> "message to client developer",
      "@httpStatusCode" -> 500,
      "@time" -> "2020-04-01T15:20:50.52Z",
      "specific-prop" -> "a specific error message"
    )
  )

}
