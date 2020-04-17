package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason.{Error, $, Controls, Link}
import play.api.libs.json._

class ErrorObjectSpec extends AnyFlatSpec with Matchers {

  "An Error object with its properties" must "be converted to JSON without controls" in {
    Json.toJson(ErrorObjectSpec.shared) mustEqual ErrorObjectSpec.expectedJson
  }

  it must "be converted to JSON with some controls" in {
    val error = ErrorObjectSpec.shared & ControlsObjectSpec.shared
    val expectedError = ErrorObjectSpec.expectedJson.deepMerge(
      Json.obj("@error" -> ControlsObjectSpec.expectedJson)
    )

    Json.toJson(error) mustEqual expectedError
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
    val error =
      ErrorObjectSpec.shared &
      ControlsObjectSpec.shared &
      Controls("another-org:other" -> Link($.href := "/other")) &
      Controls("self" -> Link($.href := "/otherself"))
    val expectedJson = ErrorObjectSpec.expectedJson
      .deepMerge(Json.obj("@error" -> ControlsObjectSpec.expectedJson))
      .deepMerge(Json.obj("@error" -> Json.obj(
                            "@controls" -> Json.obj(
                              "another-org:other" -> Json.obj("href" -> "/other"),
                              "self" -> Json.obj("href" -> "/otherself")))))

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
