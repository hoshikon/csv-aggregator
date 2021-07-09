package com.gopewpew

import fs2.Pure
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class MergeSortedStreamsSpec extends AnyFreeSpec with Matchers {
  "merges sorted streams" in {
    val stream1 = fs2.Stream.emits[Pure, Int](Seq(1, 4, 7))
    val stream2 = fs2.Stream.emits[Pure, Int](Seq(2, 3, 8, 10))
    val stream3 = fs2.Stream.emits[Pure, Int](Seq(5, 6, 9))
    MergeSortedStreams(List(stream1, stream2, stream3)).toList shouldBe (1 to 10).toList
  }







}
