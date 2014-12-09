import clojurians.warszawa.scalacheck.algorithm.{Discount, Basket}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class algorithmTest extends FunSuite with GeneratorDrivenPropertyChecks  with Matchers {

  test("application of single") {

  }
}


//object applyDiscountsMonteCarlo extends ((Basket, Seq[Discount]) => Basket) {
//  override def apply(basket: Basket, discounts: Seq[Discount]): Basket = {
//    val applicability = (for (lineItem <- basket.lineItems; discount <- discounts) yield {
//      if (discount.applicableTo(lineItem.id)) {
//        Some((discount, lineItem))
//      } else {
//        None
//      }
//    }).flatten
//
//
//  }
//}