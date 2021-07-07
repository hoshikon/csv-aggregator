package com.gopewpew

import models.{MaxAndLast, MonzoStatementRow, YearToMax, YearToMaxAndLast}

import cats.effect.IO

import java.time.LocalDateTime

trait AggregateLines[T, U] extends (fs2.Stream[IO, String] => IO[U]) {
  protected def parseLine: ParseLine[T]
  protected def aggregate(stream: fs2.Stream[IO, T]): IO[U]

  def apply(lines: fs2.Stream[IO, String]): IO[U] = aggregate(lines.evalMapFilter(parseLine))
}

class AggregateMonzoLines(override val parseLine: ParseMonzoStatementLine) extends AggregateLines[MonzoStatementRow, YearToMaxAndLast] {
  private val POT_TRANSFERS = "Pot transfer"

  override protected def aggregate(lines: fs2.Stream[IO, MonzoStatementRow]): IO[YearToMaxAndLast] = {
    lines
      .filter(_.transactionType != POT_TRANSFERS)
      .scan(MonzoStatementRow(LocalDateTime.MIN, "Initial Row", 0)) { (prev, row) =>
        row.copy(amount = row.amount + prev.amount)
      }.tail
      .compile
      .fold(YearToMaxAndLast(Map.empty)){ (acc, row) =>
        acc.update(row.timestamp.getYear)(current => MaxAndLast(current.max max row.amount, row.amount))
      }
  }

}
