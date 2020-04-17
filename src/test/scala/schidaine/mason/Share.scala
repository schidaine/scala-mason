package schidaine.mason.test

import schidaine.mason.MasonObject
import play.api.libs.json.JsObject

trait Share {

  val shared: MasonObject
  val expectedJson: JsObject

}
