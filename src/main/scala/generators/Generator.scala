package generators

import objects.{User, Campaign, Target}

object Generator {
  val MaxCampaigns = 1000
  val MaxTargets = 26
  val MaxAttributes = 100

  private val FIRST_CHAR = 'A'.asInstanceOf[Int]
  private val MAX_CHAR = 'Z'.asInstanceOf[Int]
  private val CHINTERVAL = 1 + MAX_CHAR - FIRST_CHAR

  def genAttributes(attrs: Int, ch: Char): Seq[String] = (0 until attrs).map(i => s"$ch$i")

  def genTargets(nTargets: Int, maxAttrs: Int): Seq[Target] = {
    (0 until nTargets).map(target => {
      val ch = ((target % CHINTERVAL) + FIRST_CHAR).asInstanceOf[Char]
      val nAttrs = 1 + Math.round((maxAttrs - 1) * Math.random()).toInt
      Target("attr_" + ch, genAttributes(nAttrs, ch))
    })
  }

  def genCampaign(c: Int, t: Int, a: Int): Seq[Campaign] = {
    (1 to c).map(campaign => {
      val name = s"campaign$campaign"
      val price = 1.0 + Math.round(1000.0 * Math.random()) / 100.0
      val nTargets = 1 + Math.round((t - 1) * Math.random()).toInt
      val targets = genTargets(nTargets, a)
      Campaign(name, price, targets)
    })
  }

  def genUser(t: Int): User = {
    val nAttrs = t % CHINTERVAL
    val attrs = (0 to nAttrs).map(x => {
      val ch = ((x % CHINTERVAL) + FIRST_CHAR).asInstanceOf[Char]
      val i = 1 + (t % 200)
      (s"attr_$ch" -> s"$ch$i")
    })
    User(s"u${t + 1}", attrs.toMap[String, String])
  }

}
