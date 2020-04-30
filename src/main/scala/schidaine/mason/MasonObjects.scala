package schidaine.mason

import play.api.libs.json._

/** Trait for objects that can contain "@controls" structure */
trait Controllable {

  /** Adds a Controls object to this object
    * @param ctrl a Controls object
    * @return a new Controllable object
    */
  def &(ctrl: Controls): Controllable

}

/** Represents a link object
  *
  * Example :
  * {{{
  *   val link = Link($.href := "http://my.domain.io/my-resource", $.method := POST)
  * }}}
  *
  * @param href a mandatory href property
  * @param properties optional link properties ; in case of duplicated property names, the last one override the others.
  */
case class Link(href: LinkProperty.Href, properties: LinkProperty.OptionalLinkProperty*)

/** Serializer for [[schidaine.mason.Link]] */
object Link {
  implicit val linkWrites = new OWrites[Link] {
    def writes(l: Link) = l.properties.foldLeft(Json.toJsObject(l.href)) {
      (acc,e) => acc ++ Json.toJsObject(e)
    }
  }
}

/** Represents a @controls object as a collection of references.
  *
  * Example:
  *  {{{
  *  val ctrl = Controls("self" -> Link($.href := "http://my.domain.io/my-resource"))
  *  }}}
  */
case class Controls(refs: (String, Link)*) extends MasonObject

/** Serializer for [[schidaine.mason.Controls]] */
object Controls {
  implicit val controlWrites = new OWrites[Controls] {
    def writes(c: Controls) = Json.obj(
      // "@controls" -> c.refs
      "@controls" -> c.refs.foldLeft(JsObject.empty) { (acc,e) =>
        acc ++ Json.obj(e._1 -> Json.toJsObject(e._2))
      }
    )
  }
}

/** Represents a @namespaces object
  * @param curies a list of (prefix, URI) elements
  */
case class Namespaces(curies: (String, String)*) extends MasonObject

/** Serializer for [[schidaine.mason.Namespaces]] */
object Namespaces {
  implicit val nsWrites = new OWrites[Namespaces] {
    def writes(ns: Namespaces) = Json.obj(
      "@namespaces" -> ns.curies.foldLeft(JsObject.empty) { (acc,e) =>
        acc ++ Json.obj(e._1 -> Json.obj("name" -> e._2))
      }
    )
  }
}

/** Represents a @meta object, only present in the root object
  * @param properties zero or more meta properties ; in case of duplicated property names, the last one override the others.
  */
case class Meta(properties: MetaProperty*) extends MasonObject with Controllable {

  def &(ctrl: Controls): Meta = {
    val props = properties :+ MetaProperty.Ctrls(ctrl)
    Meta(props: _*)
  }

}

/** Serializer for [[schidaine.mason.Meta]] */
object Meta {
  implicit val metaWrites = new OWrites[Meta] {
    def writes(m: Meta) = Json.obj(
      "@meta" -> m.properties.foldLeft(JsObject.empty) { (acc, e) =>
        acc.deepMerge(Json.toJsObject(e))
      }
    )
  }
}

/** Represents a @error object, only present in the root object
  * @param message a mandatory errorMessage
  * @param properties optional properties ; in case of duplicated property names, the last one override the others.
  */
case class Error(message: ErrorProperty.Message, properties: ErrorProperty.OptionalErrorProperty*) extends MasonObject with Controllable {

  def &(ctrl: Controls): Error = {
    val props = properties :+ ErrorProperty.Ctrls(ctrl)
    Error(this.message, props: _*)
  }

}

/** Serializer for [[schidaine.mason.Error]] */
object Error {
  implicit val errorWrites = new OWrites[Error] {
    def writes(m: Error) = Json.obj(
      "@error" -> m.properties.foldLeft(Json.toJsObject(m.message)) { (acc, e) =>
        acc.deepMerge(Json.toJsObject(e))
      }
    )
  }

}


/** Any JsObject as a MasonObject */
case class JsonObject(value: JsObject) extends MasonObject

/** Serializer and helpers for [[schidaine.mason.JsonObject]] */
object JsonObject {
  implicit val jsoWrites = new OWrites[JsonObject] {
    def writes(o: JsonObject) = o.value
  }
}

/** Represents any Mason object such as @controls, @meta, @error, @namespaces */
sealed trait MasonObject {

  /** Creates a new Root object from this object and another MasonObject */
  final def ++(mo: MasonObject): RootObject = RootObject(this) ++ mo

}

/** Implicit conversions and serializer for [[schidaine.mason.MasonObject]] instances.
  *
  *  {{{
  *  import MasonObject._
  *  val mason =
  *    Json.obj("data" -> "a small data") ++
  *    Error($.errorMessage := "There is a big problem")
  *  }}}
  */
object MasonObject {

  import scala.language.implicitConversions
  implicit def jsoToMasonObject(jso: JsObject) = JsonObject(jso)

  implicit val moWrites = new OWrites[MasonObject] {
    def writes(mo: MasonObject) = mo match {
      case c @ Controls(_*)     => Json.toJsObject(c)
      case n @ Namespaces(_*)   => Json.toJsObject(n)
      case m @ Meta(_*)         => Json.toJsObject(m)
      case e @ Error(_, _*)     => Json.toJsObject(e)
      case JsonObject(v)        => v
    }
  }

}

/** Factories and serialiser for [[schidaine.mason.RootObject]] */
object RootObject {

  /** Creates a RootObject with mason objects */
  def apply(objects: MasonObject*) = new RootObject(objects.toList)

  /** Creates an empty RootObject, which will give '{}' once serialized  */
  def empty = new RootObject(List.empty)

  implicit val roWrites = new OWrites[RootObject] {
    def writes(ro: RootObject) = ro.underlying.foldLeft(JsObject.empty) {
      (acc,e) => acc.deepMerge(Json.toJsObject(e))
    }
  }

  /** Merges elements of an iterable of RootObject into a new RootObject.
    * Each mason "root only" properties are merged at root level.
    * For each RootObject, JsonObjects and controls are extracted and put as a single object in a Json array.
    * @param objects an iterable of RootObject
    * @param arrayName the name of the new Json array
    * @return a new RootObject
    */
  def mergeIntoArray(objects: Iterable[RootObject], arrayName: String): RootObject = {
    val (masonProps, arrContent) =
      objects.foldLeft(List.empty[MasonObject],List.empty[RootObject]) { (acc,e) =>
        val elems = if (e.jsonAndControls.isEmpty) acc._2 else RootObject(e.jsonAndControls) :: acc._2
        (acc._1 ++ e.masonElements, elems)
      }
    val body = JsonObject(Json.obj(arrayName -> arrContent.reverse)) // keep the order, a Json array is ordered
    RootObject(masonProps) ++ body
  }

}

/** Represents the root of a Mason object */
case class RootObject(underlying: List[MasonObject]) extends Controllable {

  /** Separates JsonObjects and Controls in jsonAndControls and all other mason objects in masonElements (@meta, @error, @namespaces) */
  private lazy val (jsonAndControls, masonElements) = underlying.partition { _ match {
      case JsonObject(_) | Controls(_*) => true
      case _ => false
    }
  }

  /** Adds (merges) a Mason object to the root object
    * @param mo MasonObject to be merged
    * @return a new RootObject
    */
  def ++(mo: MasonObject): RootObject = {
    RootObject(mo :: underlying)
  }

  /** Merges this object with that.
    * Each MasonObject is merge at root level.
    * @param that a RootObject
    * @return a new RootObject
    */
  def ++(that: RootObject): RootObject = {
    RootObject(underlying ++ that.underlying)
  }

  def &(ctrl: Controls): RootObject = this ++ ctrl

}
