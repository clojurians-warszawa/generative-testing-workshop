package clojurians.warszawa.scalacheck.statefull


case class MachineState(
                         internalPocket: Pocket,
                         temporarilyDepositedPocket: Pocket,
                         deliveredCoinsPocket: Pocket,
                         delivered: DeliveryBox,
                         products: Products,
                         chosenProductMachineIdOpt: Option[Int] = None)
case class Products(map: Map[Int, Product])
case class Product(name: String, value: Int)
case class Pocket(coins: List[Coin]) {
  def addCoin(coin: Coin): Pocket = this.copy(coins = coin::coins)
  def addCoins(newCoins: List[Coin]): Pocket = this.copy(coins = newCoins:::coins)
}
case class Coin()
case class DeliveryBox(product: List[Product])

class Machine(var state: MachineState) {

  def chooseProduct(number: Int) = {
    println("chooseProduct: " + number)
    state = (state.copy(chosenProductMachineIdOpt = Some(number)))
  }

  def insertCoin(coin: Coin) = {
    state.chosenProductMachineIdOpt match {
      case None => state = state.copy(deliveredCoinsPocket = state.deliveredCoinsPocket.addCoin(coin))
      case Some(chosenProductMachineId) => {
        state = (state.copy(temporarilyDepositedPocket = state.temporarilyDepositedPocket.addCoin(coin)))
        if (state.products.map(chosenProductMachineId).value <= state.temporarilyDepositedPocket.coins.size) {
          releaseProduct(chosenProductMachineId)
        }
      }
    }
  }

  def releaseCoins() = state = state.copy(temporarilyDepositedPocket = Pocket(Nil))

  def releaseProduct(chosenProductMachineId: Int) = {
    state = state.copy(
      temporarilyDepositedPocket = Pocket(Nil),
      internalPocket = state.internalPocket.addCoins(state.temporarilyDepositedPocket.coins))
  }

}
