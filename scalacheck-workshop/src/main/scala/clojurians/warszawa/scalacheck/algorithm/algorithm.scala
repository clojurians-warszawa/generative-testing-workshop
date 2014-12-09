package clojurians.warszawa.scalacheck.algorithm

case class Basket(lineItems: Seq[LineItem]) {
  def apply(discounts: Seq[Discount]): Basket = {
    val lineItemsWithDiscounts = for (lineItem <- lineItems) yield {
      val discount = discounts.map {d => lineItem.apply(d) }.filter(_._2 != None).head._2
      lineItem.copy(appliedDiscount = discount)
    }
    Basket(lineItems = lineItemsWithDiscounts)
  }

  def totalPrice = lineItems.map(_.priceValue).sum
  def totalTax = lineItems.map(_.taxValue).sum

}

case class LineItem(id: Gtin, pricePerUnit: BigDecimal, units: Int, tax: BigDecimal, appliedDiscount: Option[Discount] = None) {
  def priceValue = pricePerUnit * units
  def taxValue = tax * priceValue
  
  def apply(discount: Discount): (LineItem, Option[Discount]) = {
    if (discount.applicableTo.contains(this.id)) {
      (this.copy(appliedDiscount = Some(discount)), Some(discount))
    } else {
      (this, None)
    }
  }
}

case class Gtin(value: String)


case class Discount(applicableTo: Set[Gtin], discount: BigDecimal)
