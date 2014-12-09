import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.{Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

object CandyMachineProperties extends org.scalacheck.Properties("CommandsLevelDB") {

  property("Never breaks.") = CandyMachineSpecification.property()

}

object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State(machineState: MachineState, nInsertedCoins: Int)
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def invariants(state: State) = {
    val machineState = state.machineState
    val b1 = machineState.products.map.size + machineState.delivered.products.size == initialProducts.size
    if (!b1)
      println("products: " + machineState.products.map.values + " + " + machineState.delivered.products + " = " + initialProducts)

    val b2 = machineState.delivered.products.map(_.value).sum == machineState.internalPocket.coins.size
    if (!b2)
      println("coins: " + machineState.delivered.products.map(_.value).sum + " = " + machineState.internalPocket.coins.size)


    val b3 = machineState.internalPocket.coins.size + machineState.deliveredCoinsPocket.coins.size + machineState.temporarilyDepositedPocket.coins.size == state.nInsertedCoins
    if (!b3)
      println("coins2: " + machineState.internalPocket.coins.size + "+" + machineState.deliveredCoinsPocket.coins.size + "+" + machineState.temporarilyDepositedPocket.coins.size + "==" + state.nInsertedCoins)
    Prop(b1) && Prop(b2) && Prop(b3)
  }

  def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean =
    true

  private val initialProducts = Map(1 -> Product("Coke", 3), 2 -> Product("Pepsi", 2))

  private val initialMachineState = MachineState(
    internalPocket = Pocket(Nil),
    temporarilyDepositedPocket = Pocket(Nil),
    deliveredCoinsPocket = Pocket(Nil),
    delivered = DeliveryBox(Nil),
    products = Products(initialProducts))

  def genInitialState: Gen[State] = Gen.oneOf(Seq(CandyMachineSpecification.State(initialMachineState, 0)))

  def newSut(state: State): Sut = new Machine(state.machineState)


  def genCommand(state: State): Gen[Command] =
    Gen.frequency(
      (1, Gen.const(ChooseProduct(1).asInstanceOf[Command])),
      (2, Gen.const(InsertCoin().asInstanceOf[Command])))


  case class ChooseProduct(number: Int) extends  UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = invariants(state) && success

    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
      println("nextState " + sut.state)
      state.copy(machineState = sut.state)
    }

    def preCondition(state: State): Boolean = true
  }

  case class InsertCoin() extends UnitCommand {
    def run(sut: Sut): Result = sut.insertCoin(Coin())

    def postCondition(state: State, success: Boolean): Prop = invariants(state) && success


    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
      println("nextState " + sut.state)
      State(sut.state, nInsertedCoins = state.nInsertedCoins + 1)
    }

    def preCondition(state: State): Boolean = true
  }


}