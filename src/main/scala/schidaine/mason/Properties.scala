package schidaine.mason

import play.api.libs.json._

/** A basic Mason property */
trait Property extends Any

trait PropertyBuilder[T] {
  def apply(e: T) : Property
  def :=(e: T) = apply(e)
}

/** Properties available in a link description */
sealed trait LinkProperty extends Any with Property

object LinkProperty {

  case class Href(val value: String) extends AnyVal with LinkProperty

  object Href extends PropertyBuilder[String] {

    def apply(value: String) : Href = new Href(value)

    override def :=(e: String): Href = apply(e)

    implicit val hrefWrites = new OWrites[Href] {
      def writes(href: Href): JsObject = Json.obj("href" -> href.value)
    }
  }

  sealed trait OptionalLinkProperty extends Any with LinkProperty

  case class Title(val value: String) extends AnyVal with OptionalLinkProperty
  case class Description(val value: String) extends AnyVal with OptionalLinkProperty
  case class IsHrefTemplate(val value: Boolean) extends AnyVal with OptionalLinkProperty
  case class Method(value: String) extends OptionalLinkProperty with HttpVerbs {
      override def toString() = value match {
        case POST|PUT|PATCH|DELETE|OPTIONS|HEAD => value
        case _ => GET
      }
  }
  case class Encoding(value: String) extends OptionalLinkProperty with Encodings {
      override def toString() = value match {
        case NONE|JSON|JSON_AND_FILES|RAW => value
        case _ => NONE
      }
  }
  case class Output(val value: Seq[String]) extends AnyVal with OptionalLinkProperty

  implicit val lpWrites = new OWrites[OptionalLinkProperty] {
    def writes(property: OptionalLinkProperty): JsObject = property match {
      case Title(t) => Json.obj("title" -> t)
      case Description(d) => Json.obj("description" -> d)
      case IsHrefTemplate(b) => Json.obj("isHrefTemplate" -> b)
      case m @ Method(_) =>  Json.obj("method" -> m.toString)
      case e @ Encoding(_) => Json.obj("encoding" -> e.toString)
      case Output(o) => Json.obj("output" -> o)
    }
  }

  trait LinkPropertyBuilder[T] extends PropertyBuilder[T] {
    def apply(value: T) : OptionalLinkProperty
    override def :=(e: T): OptionalLinkProperty = apply(e)
  }

  object Title extends LinkPropertyBuilder[String]
  object Description extends LinkPropertyBuilder[String]
  object IsHrefTemplate extends LinkPropertyBuilder[Boolean]
  object Method extends LinkPropertyBuilder[String]
  object Encoding extends LinkPropertyBuilder[String]
  object Output extends LinkPropertyBuilder[Seq[String]]

}

/** Properties available in @meta object */
sealed trait MetaProperty extends Any with Property

object MetaProperty {
  case class Title(val t: String) extends AnyVal with MetaProperty
  case class Description(val d: String) extends AnyVal with MetaProperty
  case class Ctrls(ctrl: Controls) extends MetaProperty

  implicit val metaPropWrites = new OWrites[MetaProperty] {
    def writes(property: MetaProperty) = property match {
      case Title(t)       => Json.obj("@title" -> t)
      case Description(d) => Json.obj("@description" -> d)
      case Ctrls(c)        => Json.toJsObject(c)
      case GenericProperty(name,value) => Json.obj(name -> value)
    }
  }

  trait MetaPropertyBuilder[T] extends PropertyBuilder[T] {
    def apply(value: T) : MetaProperty
    override def :=(value: T) : MetaProperty = apply(value)
  }
  object Title extends MetaPropertyBuilder[String]
  object Description extends MetaPropertyBuilder[String]
}

/** Properties available in @error object */
sealed trait ErrorProperty extends Any with Property

object ErrorProperty {

  case class Message(val value: String) extends AnyVal with ErrorProperty

  object Message extends PropertyBuilder[String] {

    def apply(value: String) = new Message(value)

    override def :=(e: String) : Message = apply(e)

    implicit val errPropWrites = new OWrites[Message] {
      def writes(m: Message) = Json.obj("@message" -> m.value)
    }
  }

  sealed trait OptionalErrorProperty extends Any with ErrorProperty

  case class Id(val id: String) extends AnyVal with OptionalErrorProperty
  case class Code(val c: String) extends AnyVal with OptionalErrorProperty
  case class Details(val d: String) extends AnyVal with OptionalErrorProperty
  case class Time(val t: String) extends AnyVal with OptionalErrorProperty
  case class Messages(val m: Seq[String]) extends AnyVal with OptionalErrorProperty
  case class HttpStatusCode(val c: Int) extends AnyVal with OptionalErrorProperty
  case class Ctrls(val ctrl: Controls) extends OptionalErrorProperty

  implicit val errorWrites = new OWrites[OptionalErrorProperty] {
    def writes(property: OptionalErrorProperty) = property match {
      case Id(id)             => Json.obj("@id" -> id)
      case Code(c)            => Json.obj("@code" -> c)
      case Details(d)         => Json.obj("@details" -> d)
      case Time(t)            => Json.obj("@time" -> t)
      case Messages(messages) => Json.obj("@messages" -> messages)
      case HttpStatusCode(i)  => Json.obj("@httpStatusCode" -> i)
      case Ctrls(ctrl)        => Json.toJsObject(ctrl)
      case GenericProperty(name,value) => Json.obj(name -> value)
    }
  }

  trait ErrorPropertyBuilder[T] extends PropertyBuilder[T] {
    def apply(value: T) : OptionalErrorProperty
    override def :=(e: T) : OptionalErrorProperty = apply(e)
  }

  object Id extends ErrorPropertyBuilder[String]
  object Code extends ErrorPropertyBuilder[String]
  object Details extends ErrorPropertyBuilder[String]
  object Time extends ErrorPropertyBuilder[String]
  object Messages extends ErrorPropertyBuilder[Seq[String]]
  object HttpStatusCode extends ErrorPropertyBuilder[Int]

}

/** A generic property allowing extension of @meta and @error objects */
case class GenericProperty(val name: String, val value: JsValue) extends MetaProperty with ErrorProperty.OptionalErrorProperty

/** Factory for GenericProperty */
class GenericPropertyBuilder(val propName: String) {
  def :=[T](e: T)(implicit w: Writes[T]) = GenericProperty(propName, Json.toJson(e))
}

/** DSL for Mason
  * {{{
  * Link(
  *   $.href := "/resource",
  *   $.title := "link title"
  * )
  * }}}
  * {{{
  * Meta(
  *   $.metaTitle := "title",
  *   $.metaDescription := "a bigger description",
  *   $.property("extended") := "an extended field"
  * )
  * }}}
  */
trait MasonKeyBuilder {
  val title = LinkProperty.Title
  val description = LinkProperty.Description
  val href = LinkProperty.Href
  val isHrefTemplate = LinkProperty.IsHrefTemplate
  val method = LinkProperty.Method
  val encoding = LinkProperty.Encoding
  val output = LinkProperty.Output

  val metaTitle = MetaProperty.Title
  val metaDescription = MetaProperty.Description

  val errorId = ErrorProperty.Id
  val errorMessage = ErrorProperty.Message
  val errorCode = ErrorProperty.Code
  val errorMessages = ErrorProperty.Messages
  val errorDetails = ErrorProperty.Details
  val errorHttpStatusCode = ErrorProperty.HttpStatusCode
  val errorTime = ErrorProperty.Time

  def property(name: String): GenericPropertyBuilder
}

object MasonKeyBuilder extends MasonKeyBuilder {
  def property(name: String) = new GenericPropertyBuilder(name)
}
