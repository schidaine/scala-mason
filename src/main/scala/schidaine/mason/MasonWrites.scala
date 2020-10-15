package schidaine.mason

import play.api.libs.json._
import scala.annotation.implicitNotFound

/** Mason serializer: write an implicit to define a serializer for any type */
  @implicitNotFound(
   "No Mason serializer found for type ${A}. Try to implement an implicit MasonWrites for this type."
  )
trait MasonWrites[-A] {

  /** Convertes the 'A' object into a Mason RootObject */
  def writes(o: A): RootObject

}

object MasonWrites {

  def apply[A](f: A => RootObject): MasonWrites[A] = new MasonWrites[A] {
    def writes(o: A): RootObject = f(o)
  }

  /** Mason.toJson(rootObject) equivalent to Json.toJson(rootObject) */
  implicit object RootObjectMasonWrites extends MasonWrites[RootObject] {
    def writes(ro: RootObject) = ro
  }

  import scala.language.implicitConversions

  /** Makes a MasonWrites for type Option[T] as soon as a Mason serializer exists for type T */
  implicit def optionToMasonWrites[T: MasonWrites]: MasonWrites[Option[T]] = {
    MasonWrites[Option[T]] { o =>
      o match {
          case Some(value) => implicitly[MasonWrites[T]].writes(value)
          case None        => RootObject.empty
      }
    }
  }

}

object Mason {

  /** Converts a 'A' object to a [[play.api.libs.json.JsValue]] usig a MasonWrites */
  def toJson[A](o: A)(implicit mw: MasonWrites[A]) =
    Json.toJson(mw.writes(o))

  /** Converts an iterable of 'A' to a [[play.api.libs.json.JsValue]] using a MasonWrites.
    * Meta, Error and Namespaces are merged at root level.
    * All other elements are put in a JsArray.
    * @param i the iterable of 'A'
    * @param to the name of the Json array
    */
  def toJson[A](i: Iterable[A], to: String)(implicit mw: MasonWrites[A]) =
    Json.toJson(mergeIntoArray(i, to))

  def mergeIntoArray[A](from: Iterable[A], to: String)(implicit mw: MasonWrites[A]): RootObject = {
    RootObject.mergeIntoArray(from.map(mw.writes(_)), to)
  }

}
