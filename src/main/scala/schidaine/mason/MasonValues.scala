package schidaine.mason

/**
 * Mason values : HttpVerbs, Encodings and so on
 */
object MasonValues extends Encodings with HttpVerbs

/**
 * Mason values : HttpVerbs, Encodings and so on
 */
trait MasonValues extends Encodings with HttpVerbs

/**
 * Standard Encodgins
 */
object Encodings extends Encodings

/**
 * Standard HTTP Verbs
 */
trait HttpVerbs {
  val GET     = "GET"
  val POST    = "POST"
  val PUT     = "PUT"
  val PATCH   = "PATCH"
  val DELETE  = "DELETE"
  val HEAD    = "HEAD"
  val OPTIONS = "OPTIONS"
}

/**
 * Standard Encodings
 */
trait Encodings {
  val NONE           = "none"
  val JSON           = "json"
  val JSON_AND_FILES = "json+files"
  val RAW            = "raw"
}
