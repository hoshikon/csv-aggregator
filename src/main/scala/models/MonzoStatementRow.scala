package com.gopewpew
package models

import java.time.LocalDateTime

case class MonzoStatementRow(timestamp: LocalDateTime, transactionType: String, amount: BigDecimal)
