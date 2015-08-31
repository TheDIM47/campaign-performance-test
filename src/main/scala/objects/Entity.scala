package objects

/** Campaign */

case class Target(target: String, attrList: Seq[String])

case class Campaign(campaignName: String, price: Double, targetList: Seq[Target])

/** User */

case class User(user: String, profile: Map[String, String])
