package schidaine.mason

/** Mason values : HttpVerbs, Encodings and so on */
object MasonValues extends MasonValues

/** Mason values : HttpVerbs, Encodings and so on */
trait MasonValues extends Encodings with HttpVerbs with MediaType

/** Standard HTTP Verbs */
object HttpVerbs extends HttpVerbs

/** Standard HTTP Verbs */
trait HttpVerbs {
  val GET     = "GET"
  val POST    = "POST"
  val PUT     = "PUT"
  val PATCH   = "PATCH"
  val DELETE  = "DELETE"
  val HEAD    = "HEAD"
  val OPTIONS = "OPTIONS"
}

/** Standard Encodings */
object Encodings extends Encodings

/** Standard Encodings */
trait Encodings {
  val NONE           = "none"
  val JSON           = "json"
  val JSON_AND_FILES = "json+files"
  val RAW            = "raw"
}

/** Mason media type */
object MediaType extends MediaType

/** Mason media type */
trait MediaType {
  val VND_MASON = "application/vnd.mason+json"
}
