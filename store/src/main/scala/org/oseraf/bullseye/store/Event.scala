package org.oseraf.bullseye.store

import scala.collection.mutable.ArrayBuffer

trait Event

trait EventListener {
  def trigger(event: Event)
}

trait EventDispatcher {
  var listeners = new ArrayBuffer[EventListener]()

  def fire(event: Event) =
    listeners.foreach(_.trigger(event))
}



object CreateEntityEvent
{
  case class Pre[ID, Entity](id: ID, entity: Entity) extends Event
  case class Post[ID, Entity](id: ID, entity: Entity) extends Event
}

trait CreateEntityEventSupport[S, ID, Entity]
  extends CreateEntityStore[S, ID, Entity]
  with EventDispatcher
{
  abstract override def createEntity(store: S, id: ID, entity: Entity) = {
    fireCreating(id, entity)
    super.createEntity(store, id, entity)
    fireCreated(id, entity)
  }

  def fireCreating(id: ID, entity: Entity) =
    fire(CreateEntityEvent.Pre[ID, Entity](id, entity))

  def fireCreated(id: ID, entity: Entity) =
    fire(CreateEntityEvent.Post[ID, Entity](id, entity))

}

trait CreateEntityEventListener[ID, Entity]
  extends EventListener
{
  def handlePre(pre: CreateEntityEvent.Pre[ID, Entity])
  def handlePost(post: CreateEntityEvent.Post[ID, Entity])

  override def trigger(event: Event): Unit = {
    event match {
      case pre: CreateEntityEvent.Pre[ID, Entity] => handlePre(pre)
      case post: CreateEntityEvent.Post[ID, Entity] => handlePost(post)
      case _ =>
    }
  }
}



object UpdateEntityEvent
{
  case class Pre[ID, Entity](id: ID, entityIn: Entity) extends Event
  case class Post[ID, Entity](id: ID, entityIn: Entity, entityOut: Option[Entity]) extends Event
}

trait UpdateEntityEventSupport[S, ID, Entity]
  extends UpdateEntityStore[S, ID, Entity]
  with EventDispatcher
{
  abstract override def updateEntity(store: S, id: ID, entity: Entity) = {
    fireUpdating(id, entity)
    val rv = super.updateEntity(store, id, entity)
    fireUpdated(id, entity, rv)
    rv
  }

  def fireUpdating(id: ID, entity: Entity) =
    fire(UpdateEntityEvent.Pre[ID, Entity](id, entity))

  def fireUpdated(id: ID, entityIn: Entity, entityOut: Option[Entity]) =
    fire(UpdateEntityEvent.Post[ID, Entity](id, entityIn, entityOut))

}

trait UpdateEntityEventListener[ID, Entity]
  extends EventListener
{
  def handlePre(pre: UpdateEntityEvent.Pre[ID, Entity])
  def handlePost(post: UpdateEntityEvent.Post[ID, Entity])

  override def trigger(event: Event): Unit = {
    event match {
      case pre: UpdateEntityEvent.Pre[ID, Entity] => handlePre(pre)
      case post: UpdateEntityEvent.Post[ID, Entity] => handlePost(post)
      case _ =>
    }
  }
}



object DeleteEntityEvent
{
  case class Pre[ID, Entity](id: ID) extends Event
  case class Post[ID, Entity](id: ID, entity: Option[Entity]) extends Event
}

trait DeleteEntityEventSupport[S, ID, Entity]
  extends DeleteEntityStore[S, ID, Entity]
  with EventDispatcher
{
  abstract override def deleteEntity(store: S, id: ID) = {
    fireUpdating(id)
    val rv = super.deleteEntity(store, id)
    fireDeleted(id, rv)
    rv
  }

  def fireUpdating(id: ID) =
    fire(DeleteEntityEvent.Pre[ID, Entity](id))

  def fireDeleted(id: ID, entity: Option[Entity]) =
    fire(DeleteEntityEvent.Post[ID, Entity](id, entity))

}

trait DeleteEntityEventListener[ID, Entity]
  extends EventListener
{
  def handlePre(pre: DeleteEntityEvent.Pre[ID, Entity])
  def handlePost(post: DeleteEntityEvent.Post[ID, Entity])

  override def trigger(event: Event): Unit = {
    event match {
      case pre: DeleteEntityEvent.Pre[ID, Entity] => handlePre(pre)
      case post: DeleteEntityEvent.Post[ID, Entity] => handlePost(post)
      case _ =>
    }
  }
}