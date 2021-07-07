package com.gopewpew

import cats.effect.unsafe.implicits.global
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.nio.file.Paths

class ReadFileSpec extends AnyFreeSpec with Matchers {
  "read a file as a stream" in {
    val path = Paths.get("src/test/resources/test.csv")
    ReadFile(path).compile.toList.unsafeRunSync() shouldBe List("this", "is", "a", "test")
  }
  "handle empty file" in {
    val path = Paths.get("src/test/resources/empty.csv")
    ReadFile(path).compile.toList.unsafeRunSync() shouldBe List()
  }
}