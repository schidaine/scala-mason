package schidaine.mason

import play.api.libs.json._
import scala.annotation.implicitNotFound

/**
 * Mason serializer: write an implicit to define a serializer for any type
 */
  @implicitNotFound(
   "No Mason serializer found for type ${A}. Try to implement an implicit MasonWrites for this type."
  )
trait MasonWrite[-A] {

  /**
   * Convertes the 'A' object into a Mason RootObject
   */
  def writes(o: A): RootObject

}

object Mason {

  def toJson[A](o: A)(implicit mw: MasonWrite[A]) =
    Json.toJson(mw.writes(o))

}
