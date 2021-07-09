package com.gopewpew

import cats.implicits.toTraverseOps
import fs2.Stream.StepLeg
import fs2.{Chunk, INothing, Pull}

import scala.collection.SortedSet

object MergeSortedStreams extends {

  private implicit def stepLegOrdering[F[_], T: Ordering]: Ordering[StepLeg[F, T]] = (sl1, sl2) => {
    (sl1.head.head, sl2.head.head) match {
      case (None, None) => 0
      case (Some(_), None) => 1
      case (None, Some(_)) => -1
      case (Some(t1), Some(t2)) => implicitly[Ordering[T]].compare(t1, t2)
    }
  }

  def apply[F[_], T: Ordering](streams: List[fs2.Stream[F, T]]): fs2.Stream[F, T] = {
    val x: Pull[F, INothing, List[Option[StepLeg[F, T]]]] = streams
      .traverse(_.pull.stepLeg)
      val y: Pull[F, INothing, SortedSet[StepLeg[F, T]]] = x.map(stepLegs => SortedSet.from(stepLegs.flatten))
      y.flatMap(toPull[F, T])
      .stream
  }

  private def toPull[F[_], T](sortedStepLegs: SortedSet[StepLeg[F, T]]): Pull[F, T, Unit] = {
    sortedStepLegs.headOption match {
      case Some(stepLeg@StepLegHasNonEmptyChunk(firstChunk, firstValue)) =>
        Pull.output1(firstValue) >> {
          val tailedStepLeg = stepLeg.setHead(firstChunk.drop(1))
          toPull(sortedStepLegs.tail + tailedStepLeg)
        }
      case Some(stepLegWithEmptyChunk) =>
        stepLegWithEmptyChunk.stepLeg
          .flatMap(maybeStepLeg => toPull(sortedStepLegs.tail ++ maybeStepLeg))
      case None => Pull.done
    }
  }

  private object StepLegHasNonEmptyChunk {
    def unapply[F[_], T](stepLeg: StepLeg[F, T]): Option[(Chunk[T], T)] = {
      val firstChunk = stepLeg.head
      firstChunk.head.map(t => (firstChunk, t))
    }
  }

}
