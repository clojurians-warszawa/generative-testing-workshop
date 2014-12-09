package clojurians.warszawa.scalacheck.statefull


case class MachineState(
                         internalPocket: Pocket,
                         temporarilyDepositedPocket: Pocket,
                         deliveredCoinsPocket: Pocket,
                         delivered: DeliveryBox,
                         products: Products,
                         chosenProduct: Option[Product] = None)
case class Products(map: Map[Int, Product])
case class Product(name: String, value: Int)
case class Pocket(coins: List[Coin]) {
  def addCoin(coin: Coin): Pocket = this.copy(coins = coin::coins)
  def addCoins(newCoins: List[Coin]): Pocket = this.copy(coins = newCoins:::coins)
}
case class Coin()
case class DeliveryBox(product: Option[Product])

class Machine(var state: MachineState) {
  def chooseProduct(number: Int) = {
    println("chooseProduct: " + number)
    state = (state.copy(chosenProduct = Some(state.products.map(number))))
  }
  def insertCoin(coin: Coin) = {
    state.chosenProduct match {
      case None => state = state.copy(deliveredCoinsPocket = state.deliveredCoinsPocket.addCoin(coin))
      case Some(product) => {
        state = (state.copy(temporarilyDepositedPocket = state.temporarilyDepositedPocket.addCoin(coin)))
        if (product.value <= state.temporarilyDepositedPocket.coins.size) {
          releaseProduct()
        }
      }
    }


  }
  def releaseCoins() = state = state.copy(temporarilyDepositedPocket = Pocket(Nil))

  def releaseProduct() = {
    state = state.copy(temporarilyDepositedPocket = Pocket(Nil), internalPocket = state.internalPocket.addCoins(state.temporarilyDepositedPocket.coins))
  }


}
