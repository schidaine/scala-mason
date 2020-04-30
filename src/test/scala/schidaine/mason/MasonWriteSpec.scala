package schidaine.mason.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import schidaine.mason._
import play.api.libs.json.Json

class MasonWriteSpec extends AnyFlatSpec with Matchers {

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
          "self" -> Link($.href := s"https://api.mycompany.io/objects/${o.data1}"),
          "my:other" -> Link(
            $.href := "https://api.mycompany.io/objects/other-object",
            $.title := "To another object"
          )
        )
    }

  }

  val expectedNamespace = Json.obj(
    "@namespaces" -> Json.obj(
      "my" -> Json.obj("name" -> "https://api.mycompany.io/rels")))

  def expected(s: String) = Json.obj(
    "data1" -> s,
    "data2" -> true,
    "@controls" -> Json.obj(
      "self" -> Json.obj(
        "href" -> s"https://api.mycompany.io/objects/${s}"
      ),
      "my:other" -> Json.obj(
        "title" -> "To another object",
        "href" -> "https://api.mycompany.io/objects/other-object"
      )
    )
  )

  "Any object with implicit MasonWrite" must "be serialized into Json as a Mason object" in {

    val o = MyObject("first-object", true)
    val exp = expectedNamespace ++ expected("first-object")
    Mason.toJson(o) mustEqual exp

  }

  it must "be serialized when encapsulated in a Option as Some" in {

    val o = Some(MyObject("first-object", true))
    val exp = expectedNamespace ++ expected("first-object")
    Mason.toJson(o) mustEqual exp

  }

  it must "be serialized when encapsulated in a Option as None" in {
    val o: Option[MyObject] = None
    Mason.toJson(o) mustEqual Json.obj()
  }

  it must "be serialized when iterated" in {
    val list = List(
      MyObject("first-object", true),
      MyObject("second-object", true),
      MyObject("third-object", true))

    val exp = expectedNamespace ++
      Json.obj("objects" -> Json.arr(
        expected("first-object"),
        expected("second-object"),
        expected("third-object")
      ))

    Mason.toJson(list, "objects") mustEqual exp

  }

}
