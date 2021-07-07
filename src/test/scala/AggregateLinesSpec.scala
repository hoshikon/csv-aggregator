package com.gopewpew

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.gopewpew.models.{MaxAndLast, YearToMaxAndLast}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class AggregateLinesSpec extends AnyFreeSpec with Matchers {
  "aggregate Monzo statement lines" in {
    val parseLine = new ParseMonzoStatementLine(IO.println)
    val aggregate = new AggregateMonzoLines(parseLine)

    val lines = fs2.Stream.emits[IO, String](Seq(
      // 2019
      "tx_00009rSNwmzi0e0CeHn91N,28/01/2019,10:14:07,Faster payment,BARRE&HOSHI,,,1000.00,GBP,1000.00,GBP,saving,,,saving,,,1000.00",
      "tx_00009sjKItoGIhA8e49y4X,06/03/2019,12:15:29,Card payment,Kano Udon,🍲,Eating out,-10.10,GBP,-10.10,GBP,,Liverpool Street,,KANO                   LONDON        GBR,,-10.10,",
      "tx_00009sjKIxKzBU7ZaQUdzl,06/03/2019,12:15:29,Pot transfer,COIN JAR Pot,,,-0.90,GBP,-0.90,GBP,,,,Round up,,-0.90,",
      "tx_00009sjKNM3NEVG425LJHl,06/03/2019,12:16:17,Pot transfer,COIN JAR Pot,,,10.00,GBP,10.00,GBP,,,,,,,10.00",
      "tx_00009sjo70SojSEf46Meaw,06/03/2019,17:49:29,Card payment,Paper & Script,📰,Groceries,-3.99,GBP,-3.99,GBP,,6 Liverpool Street,,PAPER & SCRIPT         LIVERPOOL4590 GBR,,-3.99,",
      "tx_00009sjo758nLKB2wE2rEw,06/03/2019,17:49:30,Pot transfer,COIN JAR Pot,,,-0.01,GBP,-0.01,GBP,,,,Round up,,-0.01,",
      // 2020
      "tx_00009spGXMVbA3jpElkT6Q,09/03/2020,09:01:32,Card payment,Caffè Nero,☕,Eating out,-4.95,GBP,-4.95,GBP,,UNIT 1 WINCHESTER HO,,CAFFE NERO 12 WINCHEST LONDON        GBR,,-4.95,",
      "tx_00009spGXQOIj8cD2H51jl,09/03/2020,09:01:33,Pot transfer,COIN JAR Pot,,,-0.05,GBP,-0.05,GBP,,,,Round up,,-0.05,",
      "tx_00009spGYnNsixjIbgRSYk,09/03/2020,09:01:48,Pot transfer,COIN JAR Pot,,,13.75,GBP,13.75,GBP,,,,,,,13.75",
      "tx_00009spGZl94X9adz24PdC,09/03/2020,09:01:58,Card payment,Caffè Nero,☕,Eating out,-4.95,GBP,-4.95,GBP,,UNIT 1 WINCHESTER HO,,CAFFE NERO 12 WINCHEST LONDON        GBR,,-4.95,",
      "tx_00009spGZpH1BXToQCMfpp,09/03/2020,09:01:59,Pot transfer,COIN JAR Pot,,,-0.05,GBP,-0.05,GBP,,,,Round up,,-0.05,",
      "tx_00009spXLVsiBadRRdkxsX,09/03/2020,12:09:53,Card payment,Donburi,🍲,Eating out,-8.20,GBP,-8.20,GBP,,9 Ludgate Square,,PFJ                    LONDON        GBR,,-8.20,",
      "tx_00009spXLbGIBMHAyRddxZ,09/03/2020,12:09:54,Pot transfer,COIN JAR Pot,,,-0.80,GBP,-0.80,GBP,,,,Round up,,-0.80,",
      // 2021
      "tx_00009t56PyDvAEboTsl2B7,17/03/2021,00:23:32,Card payment,Caffè Nero,☕,Eating out,4.95,GBP,4.95,GBP,,UNIT 1 WINCHESTER HO,,CAFFE NERO 12 WINCHEST LONDON        GBR,,,4.95",
      "tx_00009t6B60ej6Yy3A6Yahu,17/03/2021,12:50:40,Pot transfer,Savings Pot,,,200.00,GBP,200.00,GBP,,,,,,,200.00",
      "tx_00009t7sjwTyWzW9calXXt,18/03/2021,08:34:25,Faster payment,BARRE&HOSHI,,Finances,-200.00,GBP,-200.00,GBP,Sent from Monzo,,,Sent from Monzo,,-200.00,",
      "tx_00009tOIn7pkUTCH25UHYX,26/03/2021,06:41:42,Faster payment,BARRE&HOSHI,,,1000.00,GBP,1000.00,GBP,saving,,,saving,,,1000.00",
    ))

    aggregate(lines).unsafeRunSync() shouldBe YearToMaxAndLast(Map(
      2019 -> MaxAndLast(1000.00, 985.91),
      2020 -> MaxAndLast(985.91, 967.81),
      2021 -> MaxAndLast(1772.76, 1772.76)
    ))
  }

}
