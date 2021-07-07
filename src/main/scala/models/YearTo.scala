package com.gopewpew
package models

trait YearTo[T] {
  def value: Map[Int, T]
  def update(year: Int)(f: T => T): YearTo[T]
}

case class YearToMax(value: Map[Int, BigDecimal]) extends YearTo[BigDecimal] {
  override def update(year: Int)(f: BigDecimal => BigDecimal): YearToMax = {
    val currentMax = value.get(year)
      .orElse(value.get(year - 1))
      .getOrElse[BigDecimal](0)
    val newMax = f(currentMax)
    YearToMax(value + (year -> newMax))
  }
}

//case class YearToMax(value: Map[Int, BigDecimal]) extends YearTo[BigDecimal] {
//  override def update(year: Int)(f: BigDecimal => BigDecimal): YearToMax =
//    YearToMax(value + (year -> f(value.getOrElse(year, 0))))
//}

case class YearToMaxAndLast(value: Map[Int, MaxAndLast]) extends YearTo[MaxAndLast] {
  override def update(year: Int)(f: MaxAndLast => MaxAndLast): YearToMaxAndLast = {
    val currentMaxAndLast = value.get(year)
      .orElse(value.get(year - 1).map(ml => ml.copy(max = ml.last)))
      .getOrElse(MaxAndLast(Int.MinValue, 0))
    val newMaxAndLast = f(currentMaxAndLast)
    YearToMaxAndLast(value + (year -> newMaxAndLast))
  }
}

case class MaxAndLast(max: BigDecimal, last: BigDecimal) {
  def update(amount: BigDecimal): MaxAndLast = MaxAndLast(this.max max amount, amount)
}
