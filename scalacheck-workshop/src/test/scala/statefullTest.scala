import clojurians.warszawa.scalacheck.statefull._
import org.scalacheck.{Prop, Gen}
import org.scalatest.FunSuite
import org.scalatest.Matchers
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

class statefullTest extends FunSuite with GeneratorDrivenPropertyChecks  with Matchers {

  test("test") {
    CandyMachineSpecification.property().check
  }
}


object CandyMachineSpecification extends org.scalacheck.commands.Commands {

  case class State(n: Int)
  type Sut = Machine

  def destroySut(sut: Sut): Unit = ()

  def initialPreCondition(state: State): Boolean = true

  def canCreateNewSut(newState: State, initSuts: Traversable[State], runningSuts: Traversable[Sut]): Boolean =
    true

  def genInitialState: Gen[State] = Gen.oneOf(Seq(CandyMachineSpecification.State(1)))

  def newSut(state: State): Sut = {
    new Machine(MachineState(

      Pocket(Nil),
      Pocket(Nil),
      Pocket(Nil),
      DeliveryBox(None),
      Products(Map()))
    )
  }

  def genCommand(state: State): Gen[Command] = Gen.oneOf(ChooseProduct(1).asInstanceOf[Command], ChooseProduct(2).asInstanceOf[Command])

  case class ChooseProduct(number: Int) extends UnitCommand {

    def run(sut: Sut): Result = sut.chooseProduct(number)

    def postCondition(state: State, success: Boolean): Prop = success

    def nextState(state: State): State = state
    def preCondition(state: State): Boolean = true
  }
}