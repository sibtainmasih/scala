import monix.eval.Task
import monix.reactive.{Observable, OverflowStrategy}
import monix.execution.Ack
import monix.execution.Scheduler
import monix.execution.ExecutionModel.AlwaysAsyncExecution
import scala.concurrent.duration._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");
def timestamp = formatter.format(LocalDateTime.now)
def log = s"[$timestamp] [${Thread.currentThread.getName}]"

println(s"$log Program starts.")

sealed trait DishStatus
case object Used extends DishStatus
case object Clean extends DishStatus

case class Dish(id: Long, status: DishStatus = Used) {
  def wash = Task {
    println(s"$log washing dish $id")
    Thread.sleep(1000)
    Dish(id, Clean)
  }
}

val stream = Observable
  .fromIterable(1 to 8)
  .doOnNext { v =>
    Task {
      println(s"$log E: $v")
    }
  }
  .map(Dish(_))
  .flatMap(d =>
    Observable
      .fromTask(d.wash)
      .observeOn(
        Scheduler.io(name = "sub-io-ctx", executionModel = AlwaysAsyncExecution),
        OverflowStrategy.Unbounded
      )
  )
  .doOnNext { d =>
    Task {
      println(s"$log Dish washed ${d.id}.")
    }
  }

stream
  .subscribe(
    nextFn = { v =>
      println(s"$log O:$v")
      Ack.Continue
    }
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 5))

Thread.sleep(12000)
println(s"$log Program Ends.")

/* Output
[01:50:09.7820] [run-main-20] Program starts.
[01:50:09.7920] [run-main-20] E: 1
[01:50:09.7980] [run-main-20] washing dish 1
[01:50:10.8110] [sub-io-ctx-1625] Dish washed 1.
[01:50:10.8120] [sub-io-ctx-1625] O:Dish(1,Clean)
[01:50:10.8160] [main-comp-ctx-1626] E: 2
[01:50:10.8170] [main-comp-ctx-1626] washing dish 2
[01:50:11.8190] [sub-io-ctx-1628] Dish washed 2.
[01:50:11.8190] [sub-io-ctx-1628] O:Dish(2,Clean)
[01:50:11.8220] [main-comp-ctx-1626] E: 3
[01:50:11.8220] [main-comp-ctx-1626] washing dish 3
[01:50:12.8240] [sub-io-ctx-1629] Dish washed 3.
[01:50:12.8250] [sub-io-ctx-1629] O:Dish(3,Clean)
[01:50:12.8260] [main-comp-ctx-1626] E: 4
[01:50:12.8270] [main-comp-ctx-1626] washing dish 4
[01:50:13.8280] [sub-io-ctx-1630] Dish washed 4.
[01:50:13.8280] [sub-io-ctx-1630] O:Dish(4,Clean)
[01:50:13.8300] [main-comp-ctx-1626] E: 5
[01:50:13.8300] [main-comp-ctx-1626] washing dish 5
[01:50:14.8320] [sub-io-ctx-1631] Dish washed 5.
[01:50:14.8330] [sub-io-ctx-1631] O:Dish(5,Clean)
[01:50:14.8340] [main-comp-ctx-1626] E: 6
[01:50:14.8350] [main-comp-ctx-1626] washing dish 6
[01:50:15.8360] [sub-io-ctx-1632] Dish washed 6.
[01:50:15.8370] [sub-io-ctx-1632] O:Dish(6,Clean)
[01:50:15.8390] [main-comp-ctx-1626] E: 7
[01:50:15.8400] [main-comp-ctx-1626] washing dish 7
[01:50:16.8420] [sub-io-ctx-1633] Dish washed 7.
[01:50:16.8420] [sub-io-ctx-1633] O:Dish(7,Clean)
[01:50:16.8430] [main-comp-ctx-1626] E: 8
[01:50:16.8440] [main-comp-ctx-1626] washing dish 8
[01:50:17.8460] [sub-io-ctx-1634] Dish washed 8.
[01:50:17.8460] [sub-io-ctx-1634] O:Dish(8,Clean)
[01:50:22.9090] [run-main-20] Program Ends.
*/
