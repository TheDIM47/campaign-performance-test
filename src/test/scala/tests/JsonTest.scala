package tests

import generators.Generator
import objects.{User, Campaign, Target}
import org.scalatest.{FlatSpec, Matchers}

class JsonTest extends FlatSpec with Matchers {

  import org.json4s._
  import org.json4s.jackson.JsonMethods._
  import org.json4s.jackson.Serialization

  implicit val formats = Serialization.formats(NoTypeHints)

  "(De-)Serialization" should "work for all classes" in {
    val list = List(
      Target("t", List("A", "B", "C")),
      Campaign("cmp", 123.45, Seq(
        Target("t1", List("A1", "B1", "C1")),
        Target("t2", List("A2", "B2", "C2")))
      ),
      User("u3", Map("attr_A" -> "A3", "attr_B" -> "B3", "attr_C" -> "C3"))
    )

    for (x <- list) {
      val s = compact(render(Extraction.decompose(x)))
      val y = parse(s).extract[x.type]
      assert(x == y)
    }
  }

  "Auto (De-)Serialization" should "work for all generated classes" in {
    for(c <- (2 to 50)) {
      val t = 1 + Math.round((Generator.MaxTargets - 1) * Math.random()).toInt
      val a = 1 + Math.round((Generator.MaxAttributes - 1) * Math.random()).toInt
      val p = Generator.genCampaign(c, t, a)
      p.foreach(x => {
        val s = compact(render(Extraction.decompose(x)))
        val y = parse(s).extract[Campaign]
        assert(x == y)
      })
    }
  }

}
