package com.gopewpew

import models.MonzoStatementRow

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalDateTime

class ParseLineSpec extends AnyFreeSpec with Matchers {
  "parse monzo statement line" in {
    val parse = new ParseMonzoStatementLine(_ => IO.unit)
    val line = "tx_0000A8qgDf4jx9PwIdwXju,01/07/2021,15:17:38,Card payment,Digital Ocean,\uD83D\uDCBB,Entertainment,-4.35,GBP,-6.00,USD,,,,DIGITALOCEAN.COM       +16468274366  NY ,,-4.35,"
    parse(line).unsafeRunSync() shouldBe
      Some(
        MonzoStatementRow(
          LocalDateTime.parse("2021-07-01T15:17:38"),
          "Card payment",
          -4.35))
  }
}
