package tests

import generators._
import objects.{User, Campaign, Target}
import org.scalatest.{FlatSpec, Matchers}

class GenTest extends FlatSpec with Matchers {

  "Attribute generator" should "generate valid attribute sequence" in {
    assert(Generator.genAttributes(0, 'Z') == Seq.empty[String])
    assert(Generator.genAttributes(1, 'A') == Seq("A0"))
    assert(Generator.genAttributes(2, 'A') == Seq("A0", "A1"))
    assert(Generator.genAttributes(3, 'C') == Seq("C0", "C1", "C2"))
  }

  "Target generator" should "generate valid Target sequence" in {
    assert(Generator.genTargets(0, 1) == Seq.empty[Target])
    var hasTwo = false
    for(i <- (1 to 100)) {
      val targets = Generator.genTargets(2, i)
      assert(targets != Seq.empty[Target])
      assert(targets.size >= 1)
      val t1 = targets(0)
      assert(t1.target == "attr_A")
      assert(t1.attrList != Seq.empty[String])
      if (targets.size == 2) {
        val t2 = targets(1)
        assert(t2.target == "attr_B")
        assert(t2.attrList != Seq.empty[String])
        hasTwo = true
      }
    }
    assert(hasTwo)
  }

  "Campaign generator" should "generate valid Campaign sequence" in {
    assert(Generator.genCampaign(0, 10, 10) == Seq.empty[Campaign])
    assert(Generator.genCampaign(1, 10, 10) != Seq.empty[Campaign])
    for(i <- (2 to 100)) {
      val campaigns = Generator.genCampaign(i, 2, 2)
      assert(campaigns != Seq.empty[Campaign])
      assert(campaigns.size == i)
      for(k <- (0 until i)) {
        val c = campaigns(k)
        assert(c.campaignName == ("campaign" + (k + 1)))
        assert(c.price > 0)
        assert(c.targetList != Seq.empty[Target])
      }
    }
  }

  "User generator" should "generate valid User" in {
    assert(Generator.genUser(0) == User("u1", Map("attr_A" -> "A1")))
    assert(Generator.genUser(1) == User("u2", Map("attr_A" -> "A2", "attr_B" -> "B2")))
    assert(Generator.genUser(2) == User("u3", Map("attr_A" -> "A3", "attr_B" -> "B3", "attr_C" -> "C3")))
    val u200 = Generator.genUser(199)
    assert(u200.user == "u200")
    assert(u200.profile.size == 1 + (199 % 26))
    val u201 = Generator.genUser(200)
    assert(u201.user == "u201")
    assert(u201.profile.size == 1 + (200 % 26))
    assert(u201.profile.get("attr_A") == Some("A1"))
    assert(u201.profile.get("attr_B") == Some("B1"))
  }

}
