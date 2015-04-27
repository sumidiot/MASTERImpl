package org.oseraf.bullseye.store.impl.map

import org.oseraf.bullseye.store.{EntityCreator, CreateRelationshipStore, CreateEntityStore}

import scala.collection.mutable
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith



object Adder {
  def addEntity[S](s: S)(implicit ev: CreateEntityStore[S, String, String]): Unit =
    ev.createEntity(s, "entity", "X")

  def addRelationship[S](s: S)(implicit ev: CreateRelationshipStore[S, String, String]): Unit =
    ev.createRelationship(s, "relationship", "Y")

}

@RunWith(classOf[JUnitRunner])
class MapIsEntityStoreTest extends Specification {
  val anEntityMap = mutable.Map[String, String]()

  "Adder" should {

    import MapIsEntityStore._
    val createEntityStore = MapIsEntityStore.MutableMapIsCreateEntityStore[String, String]
    import CreateRelationshipStore._

    "have added entity" in {
      Adder.addEntity(anEntityMap)
      anEntityMap.contains("entity") must beTrue
    }

    "have added relationship" in {
      Adder.addRelationship(anEntityMap)(createEntityStore)
      anEntityMap.contains("relationship") must beTrue
    }
  }

  "Creator" should {
    import MapIsEntityStore._
    val creator = EntityCreator.creator(anEntityMap)

    "have created entity" in {
      creator.createEntity("this", "guy")
      anEntityMap.contains("this") must beTrue
    }
  }

  "Pimp" should {
    import MapIsEntityStore._

    "have created entity" in {
      anEntityMap.createEntity("that", "other guy")
      anEntityMap.contains("that") must beTrue
    }
  }
}
