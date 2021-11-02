import monix.eval.Task
import monix.reactive.Observable
import monix.execution.Ack
import monix.execution.Scheduler
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
      .executeOn(Scheduler.computation(name = "sub-comp-ctx", parallelism = 3))
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
[01:19:14.7680] [run-main-15] Program starts.
[01:19:14.7780] [run-main-15] E: 1
[01:19:14.7840] [sub-comp-ctx-1248] washing dish 1
[01:19:15.7920] [sub-comp-ctx-1248] Dish washed 1.
[01:19:15.7930] [sub-comp-ctx-1248] O:Dish(1,Clean)
[01:19:15.8040] [main-comp-ctx-1249] E: 2
[01:19:15.8060] [sub-comp-ctx-1251] washing dish 2
[01:19:16.8060] [sub-comp-ctx-1251] Dish washed 2.
[01:19:16.8070] [sub-comp-ctx-1251] O:Dish(2,Clean)
[01:19:16.8080] [main-comp-ctx-1249] E: 3
[01:19:16.8100] [sub-comp-ctx-1252] washing dish 3
[01:19:17.8110] [sub-comp-ctx-1252] Dish washed 3.
[01:19:17.8110] [sub-comp-ctx-1252] O:Dish(3,Clean)
[01:19:17.8120] [main-comp-ctx-1249] E: 4
[01:19:17.8150] [sub-comp-ctx-1253] washing dish 4
[01:19:18.8150] [sub-comp-ctx-1253] Dish washed 4.
[01:19:18.8150] [sub-comp-ctx-1253] O:Dish(4,Clean)
[01:19:18.8160] [main-comp-ctx-1249] E: 5
[01:19:18.8190] [sub-comp-ctx-1254] washing dish 5
[01:19:19.8200] [sub-comp-ctx-1254] Dish washed 5.
[01:19:19.8200] [sub-comp-ctx-1254] O:Dish(5,Clean)
[01:19:19.8220] [main-comp-ctx-1249] E: 6
[01:19:19.8240] [sub-comp-ctx-1255] washing dish 6
[01:19:20.8250] [sub-comp-ctx-1255] Dish washed 6.
[01:19:20.8260] [sub-comp-ctx-1255] O:Dish(6,Clean)
[01:19:20.8270] [main-comp-ctx-1249] E: 7
[01:19:20.8290] [sub-comp-ctx-1256] washing dish 7
[01:19:21.8300] [sub-comp-ctx-1256] Dish washed 7.
[01:19:21.8300] [sub-comp-ctx-1256] O:Dish(7,Clean)
[01:19:21.8300] [main-comp-ctx-1249] E: 8
[01:19:21.8320] [sub-comp-ctx-1257] washing dish 8
[01:19:22.8330] [sub-comp-ctx-1257] Dish washed 8.
[01:19:22.8340] [sub-comp-ctx-1257] O:Dish(8,Clean)
[01:19:26.8810] [run-main-15] Program Ends.
*/
