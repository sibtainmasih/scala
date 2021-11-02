import monix.eval.Task
import monix.reactive.Observable
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
      .executeOn(
        Scheduler.io(name = "sub-io-ctx", executionModel = AlwaysAsyncExecution)
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
[01:24:56.7340] [run-main-17] Program starts.
[01:24:56.7440] [run-main-17] E: 1
[01:24:56.7520] [sub-io-ctx-1334] washing dish 1
[01:24:57.7610] [sub-io-ctx-1334] Dish washed 1.
[01:24:57.7620] [sub-io-ctx-1334] O:Dish(1,Clean)
[01:24:57.7680] [main-comp-ctx-1335] E: 2
[01:24:57.7720] [sub-io-ctx-1338] washing dish 2
[01:24:58.7730] [sub-io-ctx-1338] Dish washed 2.
[01:24:58.7730] [sub-io-ctx-1338] O:Dish(2,Clean)
[01:24:58.7760] [main-comp-ctx-1335] E: 3
[01:24:58.7810] [sub-io-ctx-1340] washing dish 3
[01:24:59.7820] [sub-io-ctx-1340] Dish washed 3.
[01:24:59.7820] [sub-io-ctx-1340] O:Dish(3,Clean)
[01:24:59.7830] [main-comp-ctx-1335] E: 4
[01:24:59.7880] [sub-io-ctx-1342] washing dish 4
[01:25:00.7880] [sub-io-ctx-1342] Dish washed 4.
[01:25:00.7890] [sub-io-ctx-1342] O:Dish(4,Clean)
[01:25:00.7900] [main-comp-ctx-1335] E: 5
[01:25:00.7930] [sub-io-ctx-1344] washing dish 5
[01:25:01.7940] [sub-io-ctx-1344] Dish washed 5.
[01:25:01.7950] [sub-io-ctx-1344] O:Dish(5,Clean)
[01:25:01.7960] [main-comp-ctx-1335] E: 6
[01:25:01.8000] [sub-io-ctx-1346] washing dish 6
[01:25:02.8000] [sub-io-ctx-1346] Dish washed 6.
[01:25:02.8000] [sub-io-ctx-1346] O:Dish(6,Clean)
[01:25:02.8010] [main-comp-ctx-1335] E: 7
[01:25:02.8050] [sub-io-ctx-1348] washing dish 7
[01:25:03.8050] [sub-io-ctx-1348] Dish washed 7.
[01:25:03.8050] [sub-io-ctx-1348] O:Dish(7,Clean)
[01:25:03.8060] [main-comp-ctx-1335] E: 8
[01:25:03.8100] [sub-io-ctx-1350] washing dish 8
[01:25:04.8110] [sub-io-ctx-1350] Dish washed 8.
[01:25:04.8110] [sub-io-ctx-1350] O:Dish(8,Clean)
[01:25:08.7990] [run-main-17] Program Ends.
*/
