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
case class DeliveryBox(products: List[Product])

class Machine(var state: MachineState) {
  def chooseProduct(number: Int) = {
    println("chooseProduct: " + number)
    state = (state.copy(chosenProduct = Some(state.products.map(number))))
  }
  def insertCoin(coin: Coin) = {
    println("insertCoin: " + coin)
    state.chosenProduct match {
      case None => state = state.copy(deliveredCoinsPocket = state.deliveredCoinsPocket.addCoin(coin))
      case Some(product) => {
        state = (state.copy(temporarilyDepositedPocket = state.temporarilyDepositedPocket.addCoin(coin)))
        if (product.value <= state.temporarilyDepositedPocket.coins.size) {
          releaseProduct(product)
        }
      }
    }


  }
  def releaseCoins() = state = state.copy(temporarilyDepositedPocket = Pocket(Nil))

  def releaseProduct(product: Product) = {
    state = state.copy(
      temporarilyDepositedPocket = Pocket(Nil),
      internalPocket = state.internalPocket.addCoins(state.temporarilyDepositedPocket.coins),
      delivered = DeliveryBox(product::state.delivered.products))
  }


}
