package org.oseraf.bullseye.store.impl.map

import org.oseraf.bullseye.store._

import scala.collection.mutable

object MapIsEntityStore {

  implicit def MutableMapIsCreateEntityStore[K, V]: CreateEntityStore[mutable.Map[K, V], K, V] =
    new CreateEntityStore[mutable.Map[K, V], K, V] {
      override def createEntity(store: mutable.Map[K, V], id: K, entity: V) =
        store.put(id, entity)
    }

  implicit def MutableMapIsEntityCreator[K, V](m: mutable.Map[K, V]): EntityCreator[K, V] =
    EntityCreator.creator(m)

  implicit def MapIsReadEntityStore[K, V]: ReadEntityStore[Map[K, V], K, V] =
    new ReadEntityStore[Map[K, V], K, V] {
      override def readEntity(store: Map[K, V], id: K): V =
        store(id)
    }

  implicit def MapIsReadEntityWithDefaultStore[K, V]: ReadEntityWithDefaultStore[Map[K, V], K, V] =
    new ReadEntityWithDefaultStore[Map[K, V], K, V] {
      override def readEntity(store: Map[K, V], id: K, default: V): V =
        store.getOrElse(id, default)
    }

  implicit def MapIsReadOptionEntityStore[K, V]: ReadEntityOptionStore[Map[K, V], K, V] =
    new ReadEntityOptionStore[Map[K, V], K, V] {
      override def readOptionEntity(store: Map[K, V], id: K): Option[V] =
        store.get(id)
    }

  implicit def MutableMapIsUpdateEntityStore[K, V]: UpdateEntityStore[mutable.Map[K, V], K, V] =
    new UpdateEntityStore[mutable.Map[K, V], K, V] {
      override def updateEntity(store: mutable.Map[K, V], id: K, entity: V): Option[V] =
        store.put(id, entity)
    }

  implicit def MutableMapIsDeleteEntityStore[K, V]: DeleteEntityStore[mutable.Map[K, V], K, V] =
    new DeleteEntityStore[mutable.Map[K, V], K, V] {
      override def deleteEntity(store: mutable.Map[K, V], id: K): Option[V] =
        store.remove(id)
    }

  implicit def MapIsEntityExistenceStore[K, V]: EntityExistenceStore[Map[K, V], K] =
    new EntityExistenceStore[Map[K, V], K] {
      override def entityExists(store: Map[K, V], id: K): Boolean =
        store.contains(id)
    }

}
