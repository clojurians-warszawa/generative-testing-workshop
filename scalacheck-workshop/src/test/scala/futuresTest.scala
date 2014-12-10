import java.util.concurrent.Executor

import clojurians.warszawa.scalacheck.futures.{FuturesFailure}
import org.scalacheck.{Gen, Arbitrary}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FlatSpec, FunSuite, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.concurrent.{impl, ExecutionContext}
import scala.concurrent.ExecutionContext._

class FuturesUnitTest extends FlatSpec with GeneratorDrivenPropertyChecks  with Matchers with ScalaFutures {

  import Implicits.global

  for (i <- Range(1, 100)) {
    "the test " should s" always pass $i despite race condition" in {
      val result = new FuturesFailure().run()

      whenReady(result) { r => r shouldBe "ok!" }
    }
  }
}

class FuturesFailureSpecification extends FunSuite with GeneratorDrivenPropertyChecks  with Matchers with ScalaFutures {

  def executionContextGen: Gen[ExecutionContext] = Gen.const(ExecutionContext.fromExecutor(null: Executor))

    test("Should work for any ExecutionContext") {
      forAll(executionContextGen) { (ec) =>
        implicit val e: ExecutionContext = ec
        println("running")
        val result = new FuturesFailure().run()
        whenReady(result) { r => r shouldBe "ok!" }
      }
    }

//  implicit val ec = new ExecutionContext {
//    override def execute(runnable: Runnable): Unit = ???
//
//    override def reportFailure(cause: Throwable): Unit = ???
//  }

}

