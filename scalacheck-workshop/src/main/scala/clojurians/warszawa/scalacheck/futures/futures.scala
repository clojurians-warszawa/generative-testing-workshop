package clojurians.warszawa.scalacheck.futures

import scala.concurrent.{ExecutionContext, Future}


class FuturesFailure(implicit val ec: ExecutionContext) {


  def run(): Future[String] = {
    val f2 = Range(1,100).map{ x => Future {
      "ok!"
    }}.toList
    val f1 = Future {
      Thread.sleep(100)
      "error!"
    }
    scala.concurrent.Future.firstCompletedOf(f1::f2)
  }
}
