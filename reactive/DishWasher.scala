import monix.eval.Task
import monix.reactive.Observable
import monix.execution.Ack
import monix.execution.Scheduler.Implicits.global
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
  def wash = {
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
  .map(_.wash)
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
  )

Thread.sleep(12000)
println(s"$log Program Ends.")

/* Output
[00:48:42.0090] [run-main-9c] Program starts.
[00:48:42.0180] [run-main-9c] E: 1
[00:48:42.0210] [run-main-9c] washing dish 1
[00:48:43.0260] [run-main-9c] Dish washed 1.
[00:48:43.0260] [run-main-9c] O:Dish(1,Clean)
[00:48:43.0280] [run-main-9c] E: 2
[00:48:43.0290] [run-main-9c] washing dish 2
[00:48:44.0290] [run-main-9c] Dish washed 2.
[00:48:44.0290] [run-main-9c] O:Dish(2,Clean)
[00:48:44.0300] [run-main-9c] E: 3
[00:48:44.0300] [run-main-9c] washing dish 3
[00:48:45.0300] [run-main-9c] Dish washed 3.
[00:48:45.0310] [run-main-9c] O:Dish(3,Clean)
[00:48:45.0310] [run-main-9c] E: 4
[00:48:45.0320] [run-main-9c] washing dish 4
[00:48:46.0320] [run-main-9c] Dish washed 4.
[00:48:46.0330] [run-main-9c] O:Dish(4,Clean)
[00:48:46.0330] [run-main-9c] E: 5
[00:48:46.0330] [run-main-9c] washing dish 5
[00:48:47.0340] [run-main-9c] Dish washed 5.
[00:48:47.0340] [run-main-9c] O:Dish(5,Clean)
[00:48:47.0350] [run-main-9c] E: 6
[00:48:47.0350] [run-main-9c] washing dish 6
[00:48:48.0360] [run-main-9c] Dish washed 6.
[00:48:48.0360] [run-main-9c] O:Dish(6,Clean)
[00:48:48.0360] [run-main-9c] E: 7
[00:48:48.0370] [run-main-9c] washing dish 7
[00:48:49.0370] [run-main-9c] Dish washed 7.
[00:48:49.0370] [run-main-9c] O:Dish(7,Clean)
[00:48:49.0380] [run-main-9c] E: 8
[00:48:49.0380] [run-main-9c] washing dish 8
[00:48:50.0380] [run-main-9c] Dish washed 8.
[00:48:50.0390] [run-main-9c] O:Dish(8,Clean)
[00:49:02.0970] [run-main-9c] Program Ends.
*/
