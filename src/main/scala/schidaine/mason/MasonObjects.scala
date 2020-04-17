package schidaine.mason

import play.api.libs.json._

/**
 * Trait for objects that can contain "@controls" structure
 */
trait Controllable {

  /**
   * Adds Controls object to this object
   */
  def &(ctrl: Controls): Controllable

}

/**
 * Represents a @namespaces object, containing a list of (prefix, URI) elements
 */
case class Namespaces(curies: (String, String)*) extends MasonObject

object Namespaces {
  implicit val nsWrites = new OWrites[Namespaces] {
    def writes(ns: Namespaces) = Json.obj(
      "@namespaces" -> ns.curies.foldLeft(JsObject.empty) { (acc,e) =>
        acc ++ Json.obj(e._1 -> Json.obj("name" -> e._2))
      }
    )
  }
}

/**
 * Represents a @meta object, only present in the root object
 * Properties with the same name are accepted, the last one overide the others.
 */
case class Meta(properties: MetaProperty*) extends MasonObject with Controllable {

  def &(ctrl: Controls): Meta = {
    val props = properties :+ MetaProperty.Ctrls(ctrl)
    Meta(props: _*)
  }

}

object Meta {
  implicit val metaWrites = new OWrites[Meta] {
    def writes(m: Meta) = Json.obj(
      "@meta" -> m.properties.foldLeft(JsObject.empty) { (acc, e) =>
        acc.deepMerge(Json.toJsObject(e))
      }
    )
  }
}

/**
 * Represents a @error object, only present in the root object
 * message is mandaory, other properties are not
 * Optional properties with the same name are accepted, the last one overide the others.
 */
case class Error(message: ErrorProperty.Message, properties: ErrorProperty.OptionalErrorProperty*) extends MasonObject with Controllable {

  def &(ctrl: Controls): Error = {
    val props = properties :+ ErrorProperty.Ctrls(ctrl)
    Error(this.message, props: _*)
  }

}

object Error {
  implicit val metaWrites = new OWrites[Error] {
    def writes(m: Error) = Json.obj(
      "@error" -> m.properties.foldLeft(Json.toJsObject(m.message)) { (acc, e) =>
        acc.deepMerge(Json.toJsObject(e))
      }
    )
  }
}

/**
 * Represents a link object
 * href is mandatory, other properties are not
 * Properties with the same name are accepted, the last one overide the others.
 */
case class Link(href: LinkProperty.Href, properties: LinkProperty.OptionalLinkProperty*)

object Link {

  implicit val linkWrites = new OWrites[Link] {
    def writes(l: Link) = l.properties.foldLeft(Json.toJsObject(l.href)) {
      (acc,e) => acc ++ Json.toJsObject(e)
    }
  }

}

/**
 * Represents a @controls object as a collection of references.
 * A reference is a link associated to a relation name.
 */
case class Controls(refs: (String, Link)*) extends MasonObject

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

/**
 * Any JsObject as a MasonObject
 */
 case class JsonObject(value: JsObject) extends MasonObject

 object JsonObject {

   implicit val jsoWrites = new OWrites[JsonObject] {
     def writes(o: JsonObject) = o.value
   }
 }

/**
 * Represents any Mason object such as @controls, @meta, @error, @namespaces
 */
sealed trait MasonObject {

  /**
   * Creates a new Root object from this object and parameter
   */
  final def ++(mo: MasonObject): RootObject = RootObject(this) ++ mo

}

object MasonObject {

  import scala.language.implicitConversions
  implicit def jsoToMasonObject(jso: JsObject) = JsonObject(jso)

  implicit val moWrites = new OWrites[MasonObject] {
    def writes(mo: MasonObject) = mo match {
      case n @ Namespaces(_*) => Json.toJsObject(n)
      case m @ Meta(_*)       => Json.toJsObject(m)
      case e @ Error(_, _*)   => Json.toJsObject(e)
      case c @ Controls(_*)   => Json.toJsObject(c)
      case JsonObject(v)      => v
    }
  }

}


/**
 * Represents the root of a Mason object
 */
case class RootObject(private val underlying: List[MasonObject]) {

  /**
   * List is used for performance when ++ function is called.
   * Use apply method to create a RootObject
   */

   /**
    * Adds object to the root object
    */
  def ++(mo: MasonObject): RootObject = {
    RootObject(mo :: underlying) // need to be optimized with a mutable structure ?
  }

}

object RootObject {

  // This to avoid RootObject(List(...)) at RootObject creation
  def apply(objects: MasonObject*) = new RootObject(objects.toList)

  implicit val roWrites = new OWrites[RootObject] {
    def writes(ro: RootObject) = ro.underlying.foldLeft(JsObject.empty) {
      (acc,e) => acc.deepMerge(Json.toJsObject(e))
    }
  }

}
