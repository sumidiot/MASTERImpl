package org.oseraf.bullseye.store

import java.util.UUID
import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}


trait IDGenerator[ID] {
  def generateID(): ID
}

trait EntityBasedIDGenerator[ID, Entity] {
  def generateID(entity: Entity): ID
}

object IDGenerator {
  // provide a sensible default generator
  def apply(): IDGenerator[String] = {
    new IDGenerator[String] {
      // a sensible default uses inherently meaningless ids
      override def generateID() =
        UUID.randomUUID().toString
    }
  }

  def unsafeSequentialInt(): IDGenerator[Int] = {
    new IDGenerator[Int] {
      var count = 0
      override def generateID() = {
        val id = count
        count += 1
        id
      }
    }
  }

  def unsafeSequentialLong(): IDGenerator[Long] = {
    new IDGenerator[Long] {
      var count = 0l
      override def generateID() = {
        val id = count
        count += 1l
        id
      }
    }
  }

  def atomicSequentialInt(): IDGenerator[Int] = {
    new IDGenerator[Int] {
      val counter = new AtomicInteger()
      override def generateID() = {
        counter.getAndIncrement
      }
    }
  }

  def atomicSequentialLong(): IDGenerator[Long] = {
    new IDGenerator[Long] {
      val counter = new AtomicLong()
      override def generateID() = {
        counter.getAndIncrement
      }
    }
  }
}