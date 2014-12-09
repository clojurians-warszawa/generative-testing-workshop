import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.Test.TestCallback
import org.scalacheck.{Test, Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

object CandyMachineProperties extends org.scalacheck.Properties("CommandsLevelDB") {

  CandyMachineSpecification.property().check(new Test.Parameters {
    val minSuccessfulTests: Int = 10000
    val minSize: Int = 0
    val maxSize: Int = Gen.Parameters.default.size
    val rng: scala.util.Random = Gen.Parameters.default.rng
    val workers: Int = 1
    val testCallback: TestCallback = new TestCallback {}
    val maxDiscardRatio: Float = 5
    val customClassLoader: Option[ClassLoader] = None
  })

}

object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State(machineState: MachineState, initialProducts: Map[Int, Product], nInsertedCoins: Int)
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def invariants(state: State) = {
    val machineState = state.machineState
    val b1 = machineState.products.map.size + machineState.delivered.products.size == state.initialProducts.size
    val b2 = machineState.delivered.products.map(_.value).sum == machineState.internalPocket.coins.size
    val b3 = machineState.internalPocket.coins.size + machineState.deliveredCoinsPocket.coins.size + machineState.temporarilyDepositedPocket.coins.size == state.nInsertedCoins

    Prop(b1) :| s"Number of products in the environment is constant: ${machineState.products.map.size} + ${machineState.delivered.products.size} = ${state.initialProducts.size}" &&
      Prop(b2) :| "Number of coins in the machine internalPocket is equal to value of products delivered" &&
      Prop(b3) :| "Sum of number of coins in the machine (in all pockets) is equal to number of coins inserted into the machine"
  }

  def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean =
    true

  def initialProductsGen: Gen[Map[Int,Product]] = Gen.mapOf[Int, Product](for (key <- Gen.posNum[Byte].map(_.toInt);
                                product <- productGen) yield (key, product))

  private val initialMachineStateGen = for (initialProducts <- initialProductsGen) yield
    MachineState(
      internalPocket = Pocket(Nil),
      temporarilyDepositedPocket = Pocket(Nil),
      deliveredCoinsPocket = Pocket(Nil),
      delivered = DeliveryBox(Nil),
      products = Products(initialProducts))

  def genInitialState: Gen[State] = for (initialMachineState <- initialMachineStateGen) yield
    CandyMachineSpecification.State(initialMachineState, initialMachineState.products.map, 0)

  def newSut(state: State): Sut = new Machine(state.machineState)


  def genCommand(state: State): Gen[Command] = {
    val keys = state.initialProducts.keys.toList

    Gen.frequency((keys.size * 5, Gen.const(InsertCoin().asInstanceOf[Command]))::
      keys.map(ChooseProduct).map(x => (1, Gen.const(x.asInstanceOf[Command]))): _*)
  }


  case class ChooseProduct(number: Int) extends  UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = invariants(state) && success

    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
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
      state.copy(sut.state, nInsertedCoins = state.nInsertedCoins + 1)
    }

    def preCondition(state: State): Boolean = true
  }

  def productGen: Gen[Product] = {
    for (name <- Gen.alphaStr; value <- Gen.chooseNum(1, 10)) yield Product(name, value)
  }


}