package schidaine

/** Manage your REST resources with Mason.
  *
  * More about HATEOAS : [[https://en.wikipedia.org/wiki/HATEOAS]]
  *
  * Mason specs can be found here : [[https://github.com/JornWildt/Mason/blob/master/Documentation/Mason-draft-2.md]]
  *
  * This library uses play-json : [[https://github.com/playframework/play-json]]
  *
  * As for play-json, write an implicit [[schidaine.mason.MasonWrite]] in a companion object of your resource.
  *
  * Example :
  * {{{
  *   import play.api.libs.json._
  *   import schidaine.mason._
  *
  *   case class MyObject(data: String)
  *   object MyObject extends MasonValues {
  *     implicit val masonWrite = new MasonWrite[MyObject] {
  *       def writes(o: MyObject) =
  *         Namespaces("ns1" -> "https://localhost/rels") ++
  *         Json.obj("object" -> o.data) ++
  *         Controls(
  *           "self" -> Link($.href := "https://localhost/my-object"),
  *           "ns1:add-description" -> Link($.href := "https://localhost/my-object/descriptions", $.encoding := JSON)
  *         )
  *     }
  *   }
  * }}}
  *
  * Use [[schidaine.mason.Mason]] object to manipulate a such resource.
  *
  * Example :
  * {{{
  *   val o = MyObject("Tardis")
  *   Mason.toJson(o)
  * }}}
  *
  * See [[schidaine.mason.MasonKeyBuilder]] to see available properties (Mason specs are not yet fully implemented).
  */
package object mason {
  /** A shortcut to [[schidaine.mason.MasonKeyBuilder]], useful to create Mason Properties */
  val $ = MasonKeyBuilder
}
