import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.{Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

object CandyMachineProperties extends org.scalacheck.Properties("CandyMachine") {

  property("Maintains invariants.") = CandyMachineSpecification.property()

}

object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State(machineState: MachineState)
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def invariants(state: State): Prop = true

  def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean =
    true

  val initialProducts = Map(1 -> Product("Coke", value = 3), 2 -> Product("Pepsi", value = 2))

  val initialMachineState = MachineState(internalPocket = Pocket(Nil),
       temporarilyDepositedPocket = Pocket(Nil),
       deliveredCoinsPocket = Pocket(Nil),
       delivered = DeliveryBox(Nil),
       products = Products(initialProducts))

  def genInitialState: Gen[State] = Gen.oneOf(Seq(CandyMachineSpecification.State(initialMachineState)))

  def newSut(state: State): Sut = new Machine(state.machineState)



  def genCommand(state: State): Gen[Command] =
    Gen.oneOf(ChooseProduct(1).asInstanceOf[Command], ChooseProduct(2).asInstanceOf[Command])

  case class ChooseProduct(number: Int) extends UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = invariants(state) && success

    def nextState(state: State): State = {
      val sut: Sut = newSut(state)
      run(sut)
      state.copy(machineState = sut.state)
    }

    def preCondition(state: State): Boolean = true
  }
}