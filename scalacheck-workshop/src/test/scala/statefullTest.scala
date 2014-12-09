import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.{Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

class statefullTest extends FunSuite with GeneratorDrivenPropertyChecks  with Matchers {

  private val initialProducts = Map(1 -> Product("Coke", 3), 2 -> Product("Pepsi", 2))

  private val initialMachineState = MachineState(
    internalPocket = Pocket(Nil),
    temporarilyDepositedPocket = Pocket(Nil),
    deliveredCoinsPocket = Pocket(Nil),
    delivered = DeliveryBox(Nil),
    products = Products(initialProducts))

  test("test") {
    val m = new Machine(initialMachineState)
    m.chooseProduct(1)
    m.insertCoin(Coin())
    println(m.state)

  }
}

object CandyMachineProperties extends org.scalacheck.Properties("CommandsLevelDB") {

  property("Never breaks.") = CandyMachineSpecification.property()

}

object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State(machineState: MachineState)
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def invariants(machineState: MachineState) = {
    val b2 = machineState.delivered.products.map(_.value).sum == machineState.internalPocket.coins.size
    if (!b2)
      println("coins: " + machineState.delivered.products.map(_.value).sum + " = " + machineState.internalPocket.coins.size)
    Prop(b2)
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

  def genInitialState: Gen[State] = Gen.oneOf(Seq(CandyMachineSpecification.State(initialMachineState)))

  def newSut(state: State): Sut = new Machine(state.machineState)


  def genCommand(state: State): Gen[Command] =
    Gen.frequency(
      (1, Gen.const(ChooseProduct(1).asInstanceOf[Command])),
      (2, Gen.const(InsertCoin().asInstanceOf[Command])))


  case class ChooseProduct(number: Int) extends  UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = invariants(state.machineState) && success

    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
      println("nextState " + sut.state)
      State(sut.state)
    }

    def preCondition(state: State): Boolean = true
  }

  case class InsertCoin() extends UnitCommand {
    def run(sut: Sut): Result = sut.insertCoin(Coin())

    def postCondition(state: State, success: Boolean): Prop = invariants(state.machineState) && success


    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
      println("nextState " + sut.state)
      State(sut.state)
    }

    def preCondition(state: State): Boolean = true
  }


}