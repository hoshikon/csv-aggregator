package com.gopewpew

import cats.effect.IO
import models.MonzoStatementRow

import java.time.LocalDateTime
import scala.util.Try

trait ParseLine[T]  extends (String => IO[Option[T]])

class ParseMonzoStatementLine(logger: String => IO[Unit]) extends ParseLine[MonzoStatementRow] {
  override def apply(line: String): IO[Option[MonzoStatementRow]] = {
    line match {
      case ValidMonzoLine(monzoStatementRow) => IO.pure(Some(monzoStatementRow))
      case _ => logger(s"Invalid line: $line") *> IO.pure(None)
    }
  }
}

object ValidMonzoLine {
  private val DateRegex = "([0-9]{2})/([0-9]{2})/([0-9]{4})".r
  private val TimeRegex = "([0-9]{2}):([0-9]{2}):([0-9]{2})".r

  def unapply(line: String): Option[MonzoStatementRow] = {
    line.replaceAll("\"(.*)\"", "").split(" *, *").toList match {
      case _ :: DateRegex(dayStr, monthStr, yearStr) :: TimeRegex(hourStr, minuteStr, secondStr) :: transactionType :: _ :: _ :: _ :: amountStr :: _ =>
        for {
          year <- yearStr.toIntOption
          month <- monthStr.toIntOption
          day <- dayStr.toIntOption
          hour <- hourStr.toIntOption
          minute <- minuteStr.toIntOption
          second <- secondStr.toIntOption
          timestamp = LocalDateTime.of(year, month, day, hour, minute, second)
          amount <- Try(BigDecimal(amountStr)).toOption
        } yield MonzoStatementRow(timestamp, transactionType, amount)
      case _ => None
    }
  }
}

