package org.oseraf.bullseye.store

trait NeighborhoodSupport[S, ID]
{
  def neighborhood(store: S, id: ID): Iterable[ID]
}
