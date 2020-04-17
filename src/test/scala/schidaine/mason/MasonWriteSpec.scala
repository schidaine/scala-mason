package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason._
import play.api.libs.json.Json

class MasonWriteSpec extends AnyFlatSpec with Matchers {

  "Any object with implicit MasonWrite" must "be serialized into Json as a Mason object" in {

    case class MyObject(data1: String, data2: Boolean)

    object MyObject {

      implicit val mwrite = new MasonWrite[MyObject] {
        def writes(o: MyObject) =
          Namespaces("my" -> "https://api.mycompany.io/rels") ++
          Json.obj(
            "data1" -> o.data1,
            "data2" -> o.data2
          ) ++
          Controls(
            "self" -> Link($.href := "https://api.mycompny.io/my-object"),
            "my:other" -> Link(
              $.href := "https://api.mycompny.io/other-object",
              $.title := "To another object"
            )
          )
      }

    }

    val o = MyObject("important data", true)

    val expected = Json.obj(
      "@namespaces" -> Json.obj("my" -> Json.obj("name" -> "https://api.mycompany.io/rels")),
      "data1" -> "important data",
      "data2" -> true,
      "@controls" -> Json.obj(
        "self" -> Json.obj(
          "href" -> "https://api.mycompny.io/my-object"
        ),
        "my:other" -> Json.obj(
          "title" -> "To another object",
          "href" -> "https://api.mycompny.io/other-object"
        )
      )
    )

    Mason.toJson(o) mustEqual expected


  }

}
