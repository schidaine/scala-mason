A Mason library for scala
=========================

scala-mason is a library for Mason format based on play-json.

## What is Mason ?

Mason is a format describing hypermedia links inside a JSON object.

For more information, please visit https://github.com/JornWildt/Mason

## Getting started

Add scala-mason as a dependency in your project

```scala
libraryDependencies += "io.github.schidaine" %% "scala-mason" % "1.2.0"
```

So far, only Scala 2.13 supported.

TODO: scala 2.12 to be supported.

## Scope of scala-mason

scala-mason 1.2.0 implements Mason with some restrictions:
- supports Scala 2.13 (probably 2.12 because Play Json does, not yet tested),
- is focused on writing Mason (this lib will not help to consume Mason web API),
- following link properties are not yet supported : schema, schemaUrl, template, accept, files and alt,
- doesn't allow a json array at root level (it should be valid if there is no Mason properties at root).

## How to use scala-mason ?
In Play Json, you define `Writes` (or `Format`) with an implicit value in the companion object of your resource.

With scala-mason, you will have to define `MasonWrites` in the same way.

A small DSL helps you, using `$` and `:=`. See `MasonKeyBuilder` to see available property names.

Example:
```scala
import play.api.libs.json._
import schidaine.mason._

case class MyObject(data: String)

object MyObject {
  implicit val masonWrite = new MasonWrite[MyObject] {
    def writes(o: MyObject) =
      Namespaces("ns1" -> "https://localhost/rels") ++
      Json.obj("object" -> o.data) ++
      Controls(
        "self" -> Link($.href := "https://localhost/my-object"),
        "ns1:add-description" -> Link($.href := "https://localhost/my-object/descriptions", $.encoding := JSON)
      )
    }
  }
```

Then, use `Mason` object to manipulate a such resource:
```scala
import schidaine.mason.Mason

val o = MyObject("Tardis")
Mason.toJson(o)
```

As soon as a `MasonWrites[A]` exists, a `MasonWrite[Option[A]]` also exists and `Iterable[A]` can be easily serialized given an array name.
See example below where a Play! application manages HTTP content negotiation:
```scala
import play.api.libs.json.Json
import play.api.mvc._
import schidaine.mason.Mason

class MyController(...) {
  val masonMediaType = "application/vnd.mason+json"
  val AcceptsMason = Accepting(masonMediaType)

  def getOne(...) = {
    val myObject = MyObject("something") // or Some(MyObject("something"))
    ...
    request match {
      case Accepts.Json() => Ok(Json.toJson(myObject))
      case AcceptsMason() => Ok(Mason.toJson(myObject)).as(masonMediaType)
      case _ => NotAcceptable
    }
  }

  def getAll(...) = {
    val objects: List[MyObject] = ...
    ...
    request match {
      case Accepts.Json() => Ok(Json.toJson(objects))
      case AcceptsMason() => Ok(Mason.toJson(objects, "objects")).as(masonMediaType)
      case _ => NotAcceptable
    }
  }
}
```
