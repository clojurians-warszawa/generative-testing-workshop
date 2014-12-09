import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.{Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

//class statefullTest extends FunSuite with GeneratorDrivenPropertyChecks  with Matchers {
//
//  test("test") {
//    CandyMachineSpecification.property().check
//
//  }
//}

object CandyMachineProperties extends org.scalacheck.Properties("CommandsLevelDB") {

  property("Never breaks.") = CandyMachineSpecification.property()

}

object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State()
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean =
    true

  def genInitialState: Gen[State] = Gen.oneOf(Seq(CandyMachineSpecification.State()))

  def newSut(state: State): Sut = {
    new Machine(MachineState(

      internalPocket = Pocket(Nil),
      temporarilyDepositedPocket = Pocket(Nil),
      deliveredCoinsPocket = Pocket(Nil),
      delivered = DeliveryBox(None),
      products = Products(Map(1 -> Product("Coke", 3), 2 -> Product("Pepsi", 2))))
    )
  }

  def C(state: State): Gen[Command] =
    Gen.frequency(
      (1, Gen.const(ChooseProduct(1).asInstanceOf[Command])),
      (2, Gen.const(InsertCoin().asInstanceOf[Command])))


  case class ChooseProduct(number: Int) extends  UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = success

    def nextState(state: State): State = state
    def preCondition(state: State): Boolean = true
  }

  case class InsertCoin() extends UnitCommand {
    def run(sut: Sut): Result = sut.insertCoin(Coin())

    def postCondition(state: State, success: Boolean): Prop = success

    def nextState(state: State): State = state
    def preCondition(state: State): Boolean = true
  }
}