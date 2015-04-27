package org.oseraf.bullseye.store.impl.pair

import org.oseraf.bullseye.store.{CreateEntityStore, EIDisRIDCreateStore}


object PairAsERStore {
  trait StorePair[S, ES, RS] {
    def entities(s: S): ES
    def relationships(s: S): RS
  }
  implicit def PairAsStorePair[S]: StorePair[(S, S), S, S] =
    new StorePair[(S, S), S, S] {
      override def entities(pair: (S, S)): S = pair._1
      override def relationships(pair: (S, S)): S = pair._2
    }

  implicit def pairAsCreateStore[P, S, ID, ER](ev: CreateEntityStore[S, ID, ER])(implicit pair: StorePair[P, S, S]): EIDisRIDCreateStore[P, ID, ER, ER] = {
    new EIDisRIDCreateStore[P, ID, ER, ER] {
      override def createEntity(store: P, id: ID, entity: ER) =
        ev.createEntity(pair.entities(store), id, entity)
      override def createRelationship(store: P, id: ID, entity: ER) =
        ev.createEntity(pair.relationships(store), id, entity)
    }
  }
}
