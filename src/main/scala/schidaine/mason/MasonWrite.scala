package schidaine.mason

import play.api.libs.json._
import scala.annotation.implicitNotFound

/** Mason serializer: write an implicit to define a serializer for any type */
  @implicitNotFound(
   "No Mason serializer found for type ${A}. Try to implement an implicit MasonWrite for this type."
  )
trait MasonWrite[-A] {

  /** Convertes the 'A' object into a Mason RootObject */
  def writes(o: A): RootObject

}

object MasonWrite {

  def apply[A](f: A => RootObject): MasonWrite[A] = new MasonWrite[A] {
    def writes(o: A): RootObject = f(o)
  }

  import scala.language.implicitConversions

  /** Makes a MasonWrite for type Option[T] as soon as a Mason serializer exists for type T */
  implicit def optionToMasonWrite[T: MasonWrite]: MasonWrite[Option[T]] = {
    MasonWrite[Option[T]] { o =>
      o match {
          case Some(value) => implicitly[MasonWrite[T]].writes(value)
          case None        => RootObject.empty
      }
    }
  }

}

object Mason {

  /** Converts a 'A' object to a [[play.api.libs.json.JsValue]] usig a MasonWrite */
  def toJson[A](o: A)(implicit mw: MasonWrite[A]) =
    Json.toJson(mw.writes(o))

  /** Converts an iterable of 'A' to a [[play.api.libs.json.JsValue]] using a MasonWrite.
    * Meta, Error and Namespaces are merged at root level.
    * All other elements are put in a JsArray.
    * @param i the iterable of 'A'
    * @param to the name of the Json array
    */
  def toJson[A](i: Iterable[A], to: String)(implicit mw: MasonWrite[A]) =
    Json.toJson(mergeIntoArray(i, to))

  def mergeIntoArray[A](from: Iterable[A], to: String)(implicit mw: MasonWrite[A]): RootObject = {
    RootObject.mergeIntoArray(from.map(mw.writes(_)), to)
  }

}
