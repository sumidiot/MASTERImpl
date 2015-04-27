package org.oseraf.bullseye.store


object EntityStore {

  trait CreateEntityStore[S, ID, Entity] {
    def createEntity(store: S, id: ID, entity: Entity)
  }

  trait EntityCreator[ID, Entity] {
    def createEntity(id: ID, entity: Entity)
  }

  object EntityCreator {
    def creator[S, ID, Entity](store: S)(implicit ev: CreateEntityStore[S, ID, Entity]) =
      new EntityCreator[ID, Entity] {
        override def createEntity(id: ID, entity: Entity) =
          ev.createEntity(store, id, entity)
      }
  }

}

object RelationshipStore {

  trait CreateRelationshipStore[S, ID, Relationship] {
    def createRelationship(store: S, id: ID, relationship: Relationship)
  }

  trait RelationshipCreator[ID, Relationship] {
    def createRelationship(id: ID, relationship: Relationship)
  }

  object RelationshipCreator {
    def creator[S, ID, Relationship](store: S)(implicit ev: CreateRelationshipStore[S, ID, Relationship]) =
      new RelationshipCreator[ID, Relationship] {
        override def createRelationship(id: ID, relationship: Relationship) =
          ev.createRelationship(store, id, relationship)
      }
  }

}

object ERStore {
  import EntityStore._
  import RelationshipStore._

  trait CreateStore[S, EID, Entity, RID, Relationship]
    extends CreateEntityStore[S, EID, Entity]
    with CreateRelationshipStore[S, RID, Relationship]

  trait EntityRelationshipCreator[EID, E, RID, R]
    extends EntityCreator[EID, E]
    with RelationshipCreator[RID, R]

  trait EIDisRIDCreateStore[S, ID, Entity, Relationship]
    extends CreateStore[S, ID, Entity, ID, Relationship]

  trait EIDisRIDCreator[ID, E, R]
    extends EntityRelationshipCreator[ID, E, ID, R]

  trait RelationshipIsEntityCreateStore[S, ID, ER]
    extends EIDisRIDCreateStore[S, ID, ER, ER]
  {
    override def createRelationship(store: S, id: ID, relationship: ER) =
      createEntity(store, id, relationship)
  }

  trait RelationshipIsEntityCreator[ID, ER]
    extends EIDisRIDCreator[ID, ER, ER]
  {
    override def createRelationship(id: ID, relationship: ER) =
      createEntity(id, relationship)
  }

  object CreateRelationshipStore {
    implicit def relationshipAsEntity[S, ID, ER](ev: CreateEntityStore[S, ID, ER]): CreateRelationshipStore[S, ID, ER] = {
      new CreateRelationshipStore[S, ID, ER] {
        override def createRelationship(store: S, id: ID, relationship: ER) =
          ev.createEntity(store, id, relationship)
      }
    }
  }

}




trait ReadEntityStore[S, ID, Entity] {
  def readEntity(store: S, id: ID): Entity
}

trait EntityReader[ID, Entity] {
  def readEntity(id: ID): Entity
}

object EntityReader {
  implicit def reader[S, ID, Entity](store: S)(implicit ev: ReadEntityStore[S, ID, Entity]): EntityReader[ID, Entity] =
    new EntityReader[ID, Entity] {
      override def readEntity(id: ID): Entity =
        ev.readEntity(store, id)
    }
}

trait ReadEntityWithDefaultStore[S, ID, Entity] {
  def readEntity(store: S, id: ID, default: Entity): Entity
}

trait ReadEntityOptionStore[S, ID, Entity] {
  def readOptionEntity(store: S, id: ID): Option[Entity]
}


trait ReadRelationshipStore[S, ID, Relationship] {
  def readRelationship(store: S, id: ID): Relationship
}

trait ReadRelationshipWithDefaultStore[S, ID, Relationship] {
  def readRelationship(store: S, id: ID, default: Relationship): Relationship
}

trait ReadRelationshipOptionStore[S, ID, Relationship] {
  def readOptionRelationship(store: S, id: ID): Option[Relationship]
}


trait ReadStore[S, EID, Entity, RID, Relationship]
  extends ReadEntityStore[S, EID, Entity]
  with ReadRelationshipStore[S, RID, Relationship]

trait ReadWithDefaultStore[S, EID, Entity, RID, Relationship]
  extends ReadEntityWithDefaultStore[S, EID, Entity]
  with ReadRelationshipWithDefaultStore[S, RID, Relationship]

trait ReadOptionStore[S, EID, Entity, RID, Relationship]
  extends ReadEntityOptionStore[S, EID, Entity]
  with ReadRelationshipOptionStore[S, RID, Relationship]

trait EIDisRIDReadStore[S, ID, Entity, Relationship]
  extends ReadStore[S, ID, Entity, ID, Relationship]

trait EIDisRIDReadWithDefaultStore[S, ID, Entity, Relationship]
  extends ReadWithDefaultStore[S, ID, Entity, ID, Relationship]

trait EIDisRIDReadOptionStore[S, ID, Entity, Relationship]
  extends ReadOptionStore[S, ID, Entity, ID, Relationship]

trait RelationshipIsEntityReadStore[S, ID, ER]
  extends EIDisRIDReadStore[S, ID, ER, ER]
{
  override def readRelationship(store: S, id: ID): ER =
    readEntity(store, id)
}

trait RelationshipIsEntityReadWithDefaultStore[S, ID, ER]
  extends EIDisRIDReadWithDefaultStore[S, ID, ER, ER]
{
  override def readRelationship(store: S, id: ID, default: ER): ER =
    readEntity(store, id, default)
}

trait RelationshipIsEntityReadOptionStore[S, ID, ER]
  extends EIDisRIDReadOptionStore[S, ID, ER, ER]
{
  override def readOptionRelationship(store: S, id: ID): Option[ER] =
    readOptionEntity(store, id)
}


trait UpdateEntityStore[S, ID, Entity] {
  def updateEntity(store: S, id: ID, entity: Entity): Option[Entity]
}

trait UpdateRelationshipStore[S, ID, Relationship] {
  def updateRelationship(store: S, id: ID, relationship: Relationship): Option[Relationship]
}

trait UpdateStore[S, EID, Entity, RID, Relationship]
  extends UpdateEntityStore[S, EID, Entity]
  with UpdateRelationshipStore[S, RID, Relationship]

trait EIDisRIDUpdateStore[S, ID, Entity, Relationship]
  extends UpdateStore[S, ID, Entity, ID, Relationship]

trait RelationshipIsEntityUpdateStore[S, ID, ER]
  extends EIDisRIDUpdateStore[S, ID, ER, ER]
{
  override def updateRelationship(store: S, id: ID, relationship: ER): Option[ER] =
    updateEntity(store, id, relationship)
}

object UpdateRelationshipStore {
  implicit def relationshipAsEntity[S, ID, ER](ev: UpdateEntityStore[S, ID, ER]): UpdateRelationshipStore[S, ID, ER] = {
    new UpdateRelationshipStore[S, ID, ER] {
      override def updateRelationship(store: S, id: ID, relationship: ER): Option[ER] =
        ev.updateEntity(store, id, relationship)
    }
  }
}



trait DeleteEntityStore[S, ID, Entity] {
  def deleteEntity(store: S, id: ID): Option[Entity]
}

trait DeleteRelationshipStore[S, ID, Relationship] {
  def deleteRelationship(store: S, id: ID): Option[Relationship]
}

trait DeleteStore[S, EID, Entity, RID, Relationship]
  extends DeleteEntityStore[S, EID, Entity]
  with DeleteRelationshipStore[S, RID, Relationship]

trait EIDisRIDDeleteStore[S, ID, Entity, Relationship]
  extends DeleteStore[S, ID, Entity, ID, Relationship]

trait RelationshipIsEntityDeleteStore[S, ID, ER]
  extends EIDisRIDDeleteStore[S, ID, ER, ER]
{
  override def deleteRelationship(store: S, id: ID): Option[ER] =
    deleteEntity(store, id)
}

object DeleteRelationshipStore {
  implicit def relationshipAsEntity[S, ID, ER](ev: DeleteEntityStore[S, ID, ER]): DeleteRelationshipStore[S, ID, ER] = {
    new DeleteRelationshipStore[S, ID, ER] {
      override def deleteRelationship(store: S, id: ID): Option[ER] =
        ev.deleteEntity(store, id)
    }
  }
}



trait EntityExistenceStore[S, ID] {
  def entityExists(store: S, id: ID): Boolean
}

trait RelationshipExistenceStore[S, ID] {
  def relationshipExists(store: S, id: ID): Boolean
}

trait ExistenceStore[S, EID, RID]
  extends EntityExistenceStore[S, EID]
  with RelationshipExistenceStore[S, RID]

trait EIDisRIDExistenceStore[S, ID]
  extends ExistenceStore[S, ID, ID]
{
  override def relationshipExists(store: S, id: ID) =
    entityExists(store, id)
}

object RelationshipExistenceStore {
  implicit def relationshipAsEntity[S, ID](ev: EntityExistenceStore[S, ID]): RelationshipExistenceStore[S, ID] = {
    new RelationshipExistenceStore[S, ID] {
      override def relationshipExists(store: S, id: ID): Boolean =
        ev.entityExists(store, id)
    }
  }
}




trait CRUDEntityStore[S, ID, Entity]
  extends CreateEntityStore[S, ID, Entity]
  with ReadEntityStore[S, ID, Entity]
  with UpdateEntityStore[S, ID, Entity]
  with DeleteEntityStore[S, ID, Entity]

trait FullEntityStore[S, ID, Entity]
  extends CRUDEntityStore[S, ID, Entity]
  with ReadEntityWithDefaultStore[S, ID, Entity]
  with ReadEntityOptionStore[S, ID, Entity]
  with EntityExistenceStore[S, ID]

trait CRUDRelationshipStore[S, ID, Relationship]
  extends CreateRelationshipStore[S, ID, Relationship]
  with ReadRelationshipStore[S, ID, Relationship]
  with UpdateRelationshipStore[S, ID, Relationship]
  with DeleteRelationshipStore[S, ID, Relationship]

trait FullRelationshipStore[S, ID, Relationship]
  extends CRUDRelationshipStore[S, ID, Relationship]
  with ReadRelationshipWithDefaultStore[S, ID, Relationship]
  with ReadRelationshipOptionStore[S, ID, Relationship]
  with RelationshipExistenceStore[S, ID]

trait CRUDStore[S, EID, Entity, RID, Relationship]
  extends CRUDEntityStore[S, EID, Entity]
  with CRUDRelationshipStore[S, RID, Relationship]

trait EIDisRIDCRUDStore[S, ID, Entity, Relationship]
  extends CRUDStore[S, ID, Entity, ID, Relationship]

trait RelationshipIsEntityCRUDStore[S, ID, ER]
  extends EIDisRIDCRUDStore[S, ID, ER, ER]
