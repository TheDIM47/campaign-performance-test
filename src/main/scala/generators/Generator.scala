package generators

import objects.{User, Campaign, Target}

object Generator {
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
    for (campaign <- (1 to c))
      yield Campaign(name = s"campaign$campaign",
        price = genPrice,
        targets = genTargets(genTargetLen(t), a)
      )
  }

  def genPrice: Double = Math.round(1.0 + 1000.0 * Math.random()) / 100.0

  def genTargetLen(maxTargets: Int): Int = 1 + Math.round((maxTargets - 1) * Math.random()).toInt

  def genUser(t: Int): User = {
    val nAttrs = (t - 1) % CHINTERVAL
    val attrs = for(x <- (0 to nAttrs)) yield {
      val ch = (x + FIRST_CHAR).asInstanceOf[Char]
      val i: Int = Math.round(Math.random() * 200.0).toInt
      (s"attr_$ch" -> s"$ch$i")
    }
    User(s"u${t}", attrs.toMap[String, String])
  }

}
