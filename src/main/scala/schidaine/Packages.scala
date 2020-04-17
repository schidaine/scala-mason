package schidaine

package object mason {
  /**
   * A shortcut to MasonKeyBuilder, useful to create Mason Properties
   * $.title = "link title" to create a title property of a Mason link
   * See MasonKeyBuilder for more details.
   */
  val $ = MasonKeyBuilder
}
