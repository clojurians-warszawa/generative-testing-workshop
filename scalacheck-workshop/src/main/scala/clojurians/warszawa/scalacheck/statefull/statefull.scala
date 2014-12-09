package clojurians.warszawa.scalacheck.statefull


case class MachineState(
                         internalPocket: Pocket,
                         temporarilyDepositedPocket: Pocket,
                         deliveredCoinsPocket: Pocket,
                         delivered: DeliveryBox,
                         products: Products,
                         chosenProduct: Option[Int] = None)
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
    if (state.products.map.keys.toSet.contains(number)) {
      state = (state.copy(chosenProduct = Some(number)))
    }
  }

  def insertCoin(coin: Coin) = {
    println("insertCoin: " + coin)
    state.chosenProduct match {
      case None => state = state.copy(deliveredCoinsPocket = state.deliveredCoinsPocket.addCoin(coin))
      case Some(productMachineId) => {
        state = (state.copy(temporarilyDepositedPocket = state.temporarilyDepositedPocket.addCoin(coin)))
        if (state.products.map(productMachineId).value <= state.temporarilyDepositedPocket.coins.size) {
          releaseProduct(productMachineId)
        }
      }
    }


  }
  def releaseCoins() = state = state.copy(temporarilyDepositedPocket = Pocket(Nil))

  def releaseProduct(productMachineId: Int) = {
    val product: Product = state.products.map(productMachineId)
    state = state.copy(
      temporarilyDepositedPocket = Pocket(Nil),
      internalPocket = state.internalPocket.addCoins(state.temporarilyDepositedPocket.coins),
      delivered = DeliveryBox(product::state.delivered.products),
      products = state.products.copy(state.products.map.filterKeys(_ != productMachineId)),
      chosenProduct = None)
  }


}
