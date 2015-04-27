package org.oseraf.bullseye.store

import java.util.Comparator


trait AttributeSearchSupport[S, AKey, AVal, ID] {
  def search(store: S, key: AKey, value: AVal): Iterable[ID]
}

trait ScoredAttributeSearchSupport[S, AKey, AVal, ID, Score] {
  def scoredSearch(store: S, key: AKey, value: AVal): Iterable[(ID, Score)]
}


case class SearchableAttribute[AKey](key: AKey, name: String)

trait SearchableAttributes[S, AKey] {
  def searchableAttributes(store: S): Iterable[SearchableAttribute[AKey]]
}


trait AttributeComparator[A, S] {
  def agreement(left: A, right: A): S
}

object AttributeComparator {
  implicit def ComparatorIsAttributeComparator[A](cmp: Comparator[A]): AttributeComparator[A, Int] =
    new AttributeComparator[A, Int] {
      override def agreement(left: A, right: A): Int =
        cmp.compare(left, right)
    }
}

trait BooleanAttributeComparator[A]
  extends AttributeComparator[A, Boolean]

object BooleanAttributeComparator {
  implicit def ComparatorIsBooleanAttributeComparator[A](cmp: Comparator[A]): BooleanAttributeComparator[A] =
    new BooleanAttributeComparator[A] {
      override def agreement(left: A, right: A): Boolean =
        cmp.compare(left, right) == 0
    }
}

trait AgreementAcceptor[S] {
  def accept(score: S): Boolean
}

object AgreementAcceptor {
  def above(threshold: Double): AgreementAcceptor[Double] =
    new AgreementAcceptor[Double] {
      override def accept(score: Double) =
        score > threshold
    }

  def below(threshold: Double): AgreementAcceptor[Double] =
    new AgreementAcceptor[Double] {
      override def accept(score: Double) =
        score > threshold
    }

  def boolean: AgreementAcceptor[Boolean] =
    new AgreementAcceptor[Boolean] {
      override def accept(score: Boolean): Boolean =
        score
    }
}


object AttributeSearchSupport {
  implicit def EntityIterationAllowsBruteForceAttributeSearch[S, AKey, AVal, ID, Entity, Score](
      implicit
        entity: ReadEntityStore[S, ID, Entity],
        attr: ReadEntityStore[Entity, AKey, AVal],
        iteration: EntityIterationSupport[S, ID],
        cmp: AttributeComparator[AVal, Score],
        accept: AgreementAcceptor[Score]
    ): AttributeSearchSupport[S, AKey, AVal, ID] = {
    new AttributeSearchSupport[S, AKey, AVal, ID] {
      override def search(store: S, key: AKey, value: AVal): Iterable[ID] = {
        iteration
          .entities(store)
          .filter(entityId => {
            accept.accept(cmp.agreement(attr.readEntity(entity.readEntity(store, entityId), key), value))
          })
      }
    }
  }

  implicit object StringComparator extends BooleanAttributeComparator[String] {
    override def agreement(left: String, right: String): Boolean =
      left.equals(right)
  }

  implicit object FuzzyStringComparator extends AttributeComparator[String, Double] {
    override def agreement(left: String, right: String): Double =
      left match {
        case s: String if s.equals(right)                            => 1.0
        case s: String if s.equalsIgnoreCase(right)                  => 0.95
        case s: String if s.matches(".*\\b" + right + "\\b.*")       => 0.85
        case s: String if s.matches("(?i).*\\b" + right + "\\b.*")   => 0.80
        case s: String if s.matches(".*\\b" + right + ".*")          => 0.75
        case s: String if s.matches("(?i).*\\b" + right + ".*")      => 0.70
        case _                                                       => 0.0
      }
  }
}

//trait TestMapSupport {
//  def findMe[S](store: S)(implicit search: AttributeSearchSupport[S, String, String, String]) =
//    search.search(store, "name", "Nick")
//
//  val myStore = mutable.Map[String, Map[String, String]]()
//
//  val res = findMe(myStore)
//}
