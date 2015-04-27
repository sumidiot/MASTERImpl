package org.oseraf.bullseye.store


trait EntityIterationSupport[S, ID]
{
  def entities(s: S): Iterable[ID]
}


object EntityIterationSupport {
  implicit def MapHasEntityIterationSupport[K, V]: EntityIterationSupport[Map[K, V], K] =
    new EntityIterationSupport[Map[K, V], K] {
      override def entities(s: Map[K, V]) =
        s.keys
    }
}