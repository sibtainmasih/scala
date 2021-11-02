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
    Thread.sleep(200)
    Dish(id, Clean)
  }
}

val stream = Observable
  .fromIterable(1 to 100)
  .doOnNext { v =>
    Task {
      println(s"$log E: $v")
    }
  }
  .map(Dish(_))  
  .mapParallelUnordered(5)(d => d.wash)
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

Thread.sleep(15000)
println(s"$log Program Ends.")


/* Output
[05:02:56.4940] [run-main-77] Program starts.
[05:02:57.1220] [run-main-77] E: 1
[05:02:57.1610] [run-main-77] E: 2
[05:02:57.1610] [run-main-77] E: 3
[05:02:57.1620] [run-main-77] E: 4
[05:02:57.1650] [run-main-77] E: 5
[05:02:57.1680] [run-main-77] E: 6
[05:02:57.1780] [main-comp-ctx-3835] washing dish 1
[05:02:57.1780] [main-comp-ctx-3836] washing dish 2
[05:02:57.1780] [main-comp-ctx-3839] washing dish 4
[05:02:57.1790] [main-comp-ctx-3837] washing dish 3
[05:02:57.1780] [main-comp-ctx-3838] washing dish 5
[05:02:57.3920] [main-comp-ctx-3838] washing dish 6
[05:02:57.3930] [main-comp-ctx-3837] Dish washed 3.
[05:02:57.3930] [main-comp-ctx-3835] E: 7
[05:02:57.3940] [main-comp-ctx-3835] E: 8
[05:02:57.3950] [main-comp-ctx-3835] E: 9
[05:02:57.3950] [main-comp-ctx-3836] washing dish 8
[05:02:57.3940] [main-comp-ctx-3837] O:Dish(3,Clean)
[05:02:57.3950] [main-comp-ctx-3835] E: 10
[05:02:57.3950] [main-comp-ctx-3839] washing dish 7
[05:02:57.3960] [main-comp-ctx-3835] E: 11
[05:02:57.3960] [main-comp-ctx-3837] Dish washed 4.
[05:02:57.3970] [main-comp-ctx-3835] washing dish 9
[05:02:57.3970] [main-comp-ctx-3837] O:Dish(4,Clean)
[05:02:57.3970] [main-comp-ctx-3837] Dish washed 5.
[05:02:57.3980] [main-comp-ctx-3837] O:Dish(5,Clean)
[05:02:57.3980] [main-comp-ctx-3837] Dish washed 1.
[05:02:57.3980] [main-comp-ctx-3837] O:Dish(1,Clean)
[05:02:57.3990] [main-comp-ctx-3837] Dish washed 2.
[05:02:57.3990] [main-comp-ctx-3837] O:Dish(2,Clean)
[05:02:57.4000] [main-comp-ctx-3837] washing dish 10
[05:02:57.5930] [main-comp-ctx-3838] Dish washed 6.
[05:02:57.5930] [main-comp-ctx-3838] O:Dish(6,Clean)
[05:02:57.5940] [main-comp-ctx-3838] washing dish 11
[05:02:57.5960] [main-comp-ctx-3836] Dish washed 8.
[05:02:57.5970] [main-comp-ctx-3839] E: 12
[05:02:57.5960] [main-comp-ctx-3836] O:Dish(8,Clean)
[05:02:57.5970] [main-comp-ctx-3836] Dish washed 7.
[05:02:57.5970] [main-comp-ctx-3836] O:Dish(7,Clean)
[05:02:57.5980] [main-comp-ctx-3836] Dish washed 9.
[05:02:57.5980] [main-comp-ctx-3836] O:Dish(9,Clean)
[05:02:57.5990] [main-comp-ctx-3839] E: 13
[05:02:57.5990] [main-comp-ctx-3839] E: 14
[05:02:57.6000] [main-comp-ctx-3835] washing dish 13
[05:02:57.6000] [main-comp-ctx-3839] E: 15
[05:02:57.5990] [main-comp-ctx-3836] washing dish 12
[05:02:57.6020] [main-comp-ctx-3837] Dish washed 10.
[05:02:57.6010] [main-comp-ctx-3839] washing dish 14
[05:02:57.6020] [main-comp-ctx-3837] O:Dish(10,Clean)
[05:02:57.6030] [main-comp-ctx-3837] washing dish 15
[05:02:57.7960] [main-comp-ctx-3838] Dish washed 11.
[05:02:57.7960] [main-comp-ctx-3838] O:Dish(11,Clean)
[05:02:57.7970] [main-comp-ctx-3838] E: 16
[05:02:57.7970] [main-comp-ctx-3838] E: 17
[05:02:57.7980] [main-comp-ctx-3838] washing dish 16
[05:02:57.8010] [main-comp-ctx-3835] Dish washed 13.
[05:02:57.8010] [main-comp-ctx-3835] O:Dish(13,Clean)
[05:02:57.8030] [main-comp-ctx-3839] Dish washed 14.
[05:02:57.8030] [main-comp-ctx-3839] O:Dish(14,Clean)
[05:02:57.8040] [main-comp-ctx-3839] E: 18
[05:02:57.8040] [main-comp-ctx-3839] E: 19
[05:02:57.8050] [main-comp-ctx-3839] washing dish 18
[05:02:57.8020] [main-comp-ctx-3835] washing dish 17
[05:02:57.8060] [main-comp-ctx-3836] Dish washed 12.
[05:02:57.8060] [main-comp-ctx-3836] O:Dish(12,Clean)
[05:02:57.8070] [main-comp-ctx-3836] washing dish 19
[05:02:57.8080] [main-comp-ctx-3837] Dish washed 15.
[05:02:57.8080] [main-comp-ctx-3837] O:Dish(15,Clean)
[05:02:57.8080] [main-comp-ctx-3837] E: 20
[05:02:57.8080] [main-comp-ctx-3837] E: 21
[05:02:57.8090] [main-comp-ctx-3837] washing dish 20
[05:02:57.9990] [main-comp-ctx-3838] Dish washed 16.
[05:02:58.0000] [main-comp-ctx-3838] O:Dish(16,Clean)
[05:02:58.0010] [main-comp-ctx-3838] washing dish 21
[05:02:58.0060] [main-comp-ctx-3839] Dish washed 18.
[05:02:58.0060] [main-comp-ctx-3839] O:Dish(18,Clean)
[05:02:58.0060] [main-comp-ctx-3839] Dish washed 17.
[05:02:58.0060] [main-comp-ctx-3835] E: 22
[05:02:58.0060] [main-comp-ctx-3839] O:Dish(17,Clean)
[05:02:58.0070] [main-comp-ctx-3835] E: 23
[05:02:58.0070] [main-comp-ctx-3839] washing dish 22
[05:02:58.0070] [main-comp-ctx-3835] E: 24
[05:02:58.0080] [main-comp-ctx-3836] Dish washed 19.
[05:02:58.0080] [main-comp-ctx-3836] O:Dish(19,Clean)
[05:02:58.0080] [main-comp-ctx-3835] E: 25
[05:02:58.0080] [main-comp-ctx-3836] washing dish 23
[05:02:58.0090] [main-comp-ctx-3835] washing dish 24
[05:02:58.0100] [main-comp-ctx-3837] Dish washed 20.
[05:02:58.0100] [main-comp-ctx-3837] O:Dish(20,Clean)
[05:02:58.0110] [main-comp-ctx-3837] washing dish 25
[05:02:58.2010] [main-comp-ctx-3838] Dish washed 21.
[05:02:58.2010] [main-comp-ctx-3838] O:Dish(21,Clean)
[05:02:58.2020] [main-comp-ctx-3838] E: 26
[05:02:58.2030] [main-comp-ctx-3838] E: 27
[05:02:58.2040] [main-comp-ctx-3838] washing dish 26
[05:02:58.2080] [main-comp-ctx-3839] Dish washed 22.
[05:02:58.2080] [main-comp-ctx-3839] O:Dish(22,Clean)
[05:02:58.2090] [main-comp-ctx-3839] washing dish 27
[05:02:58.2090] [main-comp-ctx-3836] Dish washed 23.
[05:02:58.2090] [main-comp-ctx-3835] E: 28
[05:02:58.2090] [main-comp-ctx-3836] O:Dish(23,Clean)
[05:02:58.2100] [main-comp-ctx-3836] Dish washed 24.
[05:02:58.2100] [main-comp-ctx-3835] E: 29
[05:02:58.2100] [main-comp-ctx-3836] O:Dish(24,Clean)
[05:02:58.2100] [main-comp-ctx-3835] E: 30
[05:02:58.2100] [main-comp-ctx-3836] washing dish 28
[05:02:58.2110] [main-comp-ctx-3835] washing dish 29
[05:02:58.2120] [main-comp-ctx-3837] Dish washed 25.
[05:02:58.2120] [main-comp-ctx-3837] O:Dish(25,Clean)
[05:02:58.2130] [main-comp-ctx-3837] washing dish 30
[05:02:58.4040] [main-comp-ctx-3838] Dish washed 26.
[05:02:58.4050] [main-comp-ctx-3838] O:Dish(26,Clean)
[05:02:58.4060] [main-comp-ctx-3838] E: 31
[05:02:58.4060] [main-comp-ctx-3838] E: 32
[05:02:58.4070] [main-comp-ctx-3838] washing dish 31
[05:02:58.4100] [main-comp-ctx-3839] Dish washed 27.
[05:02:58.4100] [main-comp-ctx-3839] O:Dish(27,Clean)
[05:02:58.4110] [main-comp-ctx-3839] washing dish 32
[05:02:58.4110] [main-comp-ctx-3836] Dish washed 28.
[05:02:58.4110] [main-comp-ctx-3836] O:Dish(28,Clean)
[05:02:58.4120] [main-comp-ctx-3836] Dish washed 29.
[05:02:58.4120] [main-comp-ctx-3835] E: 33
[05:02:58.4120] [main-comp-ctx-3836] O:Dish(29,Clean)
[05:02:58.4120] [main-comp-ctx-3835] E: 34
[05:02:58.4120] [main-comp-ctx-3836] washing dish 33
[05:02:58.4130] [main-comp-ctx-3835] E: 35
[05:02:58.4130] [main-comp-ctx-3835] washing dish 34
[05:02:58.4140] [main-comp-ctx-3837] Dish washed 30.
[05:02:58.4140] [main-comp-ctx-3837] O:Dish(30,Clean)
[05:02:58.4140] [main-comp-ctx-3837] washing dish 35
[05:02:58.6080] [main-comp-ctx-3838] Dish washed 31.
[05:02:58.6080] [main-comp-ctx-3838] O:Dish(31,Clean)
[05:02:58.6090] [main-comp-ctx-3838] E: 36
[05:02:58.6100] [main-comp-ctx-3838] E: 37
[05:02:58.6120] [main-comp-ctx-3839] Dish washed 32.
[05:02:58.6120] [main-comp-ctx-3839] O:Dish(32,Clean)
[05:02:58.6130] [main-comp-ctx-3839] washing dish 37
[05:02:58.6130] [main-comp-ctx-3836] Dish washed 33.
[05:02:58.6130] [main-comp-ctx-3838] washing dish 36
[05:02:58.6130] [main-comp-ctx-3836] O:Dish(33,Clean)
[05:02:58.6140] [main-comp-ctx-3836] Dish washed 34.
[05:02:58.6140] [main-comp-ctx-3835] E: 38
[05:02:58.6140] [main-comp-ctx-3836] O:Dish(34,Clean)
[05:02:58.6140] [main-comp-ctx-3835] E: 39
[05:02:58.6140] [main-comp-ctx-3836] washing dish 38
[05:02:58.6150] [main-comp-ctx-3835] E: 40
[05:02:58.6150] [main-comp-ctx-3837] Dish washed 35.
[05:02:58.6150] [main-comp-ctx-3835] E: 41
[05:02:58.6160] [main-comp-ctx-3837] O:Dish(35,Clean)
[05:02:58.6160] [main-comp-ctx-3837] washing dish 39
[05:02:58.6160] [main-comp-ctx-3835] washing dish 40
[05:02:58.8140] [main-comp-ctx-3839] Dish washed 37.
[05:02:58.8140] [main-comp-ctx-3838] washing dish 41
[05:02:58.8140] [main-comp-ctx-3839] O:Dish(37,Clean)
[05:02:58.8150] [main-comp-ctx-3839] Dish washed 36.
[05:02:58.8150] [main-comp-ctx-3836] E: 42
[05:02:58.8150] [main-comp-ctx-3839] O:Dish(36,Clean)
[05:02:58.8160] [main-comp-ctx-3836] E: 43
[05:02:58.8160] [main-comp-ctx-3839] Dish washed 38.
[05:02:58.8160] [main-comp-ctx-3836] E: 44
[05:02:58.8160] [main-comp-ctx-3839] O:Dish(38,Clean)
[05:02:58.8160] [main-comp-ctx-3839] washing dish 42
[05:02:58.8170] [main-comp-ctx-3836] washing dish 43
[05:02:58.8170] [main-comp-ctx-3837] Dish washed 39.
[05:02:58.8170] [main-comp-ctx-3835] washing dish 44
[05:02:58.8170] [main-comp-ctx-3837] O:Dish(39,Clean)
[05:02:58.8170] [main-comp-ctx-3837] Dish washed 40.
[05:02:58.8180] [main-comp-ctx-3837] O:Dish(40,Clean)
[05:02:58.8180] [main-comp-ctx-3837] E: 45
[05:02:58.8190] [main-comp-ctx-3837] E: 46
[05:02:58.8190] [main-comp-ctx-3837] washing dish 45
[05:02:59.0150] [main-comp-ctx-3838] Dish washed 41.
[05:02:59.0150] [main-comp-ctx-3838] O:Dish(41,Clean)
[05:02:59.0160] [main-comp-ctx-3838] washing dish 46
[05:02:59.0170] [main-comp-ctx-3839] Dish washed 42.
[05:02:59.0170] [main-comp-ctx-3839] O:Dish(42,Clean)
[05:02:59.0170] [main-comp-ctx-3836] E: 47
[05:02:59.0180] [main-comp-ctx-3839] Dish washed 43.
[05:02:59.0180] [main-comp-ctx-3836] E: 48
[05:02:59.0180] [main-comp-ctx-3835] washing dish 47
[05:02:59.0180] [main-comp-ctx-3839] O:Dish(43,Clean)
[05:02:59.0180] [main-comp-ctx-3836] E: 49
[05:02:59.0180] [main-comp-ctx-3839] Dish washed 44.
[05:02:59.0190] [main-comp-ctx-3839] O:Dish(44,Clean)
[05:02:59.0190] [main-comp-ctx-3836] E: 50
[05:02:59.0190] [main-comp-ctx-3839] washing dish 48
[05:02:59.0200] [main-comp-ctx-3837] Dish washed 45.
[05:02:59.0200] [main-comp-ctx-3837] O:Dish(45,Clean)
[05:02:59.0210] [main-comp-ctx-3837] washing dish 50
[05:02:59.0200] [main-comp-ctx-3836] washing dish 49
[05:02:59.2170] [main-comp-ctx-3838] Dish washed 46.
[05:02:59.2170] [main-comp-ctx-3838] O:Dish(46,Clean)
[05:02:59.2190] [main-comp-ctx-3838] E: 51
[05:02:59.2190] [main-comp-ctx-3835] Dish washed 47.
[05:02:59.2190] [main-comp-ctx-3838] E: 52
[05:02:59.2190] [main-comp-ctx-3835] O:Dish(47,Clean)
[05:02:59.2200] [main-comp-ctx-3835] washing dish 51
[05:02:59.2200] [main-comp-ctx-3838] E: 53
[05:02:59.2200] [main-comp-ctx-3839] Dish washed 48.
[05:02:59.2210] [main-comp-ctx-3838] E: 54
[05:02:59.2210] [main-comp-ctx-3839] O:Dish(48,Clean)
[05:02:59.2210] [main-comp-ctx-3839] washing dish 52
[05:02:59.2220] [main-comp-ctx-3837] Dish washed 50.
[05:02:59.2220] [main-comp-ctx-3838] washing dish 53
[05:02:59.2220] [main-comp-ctx-3837] O:Dish(50,Clean)
[05:02:59.2240] [main-comp-ctx-3837] washing dish 54
[05:02:59.2240] [main-comp-ctx-3836] Dish washed 49.
[05:02:59.2250] [main-comp-ctx-3836] O:Dish(49,Clean)
[05:02:59.2250] [main-comp-ctx-3836] E: 55
[05:02:59.2250] [main-comp-ctx-3836] E: 56
[05:02:59.2260] [main-comp-ctx-3836] washing dish 55
[05:02:59.4210] [main-comp-ctx-3835] Dish washed 51.
[05:02:59.4210] [main-comp-ctx-3835] O:Dish(51,Clean)
[05:02:59.4210] [main-comp-ctx-3835] washing dish 56
[05:02:59.4220] [main-comp-ctx-3839] Dish washed 52.
[05:02:59.4230] [main-comp-ctx-3839] O:Dish(52,Clean)
[05:02:59.4230] [main-comp-ctx-3839] Dish washed 53.
[05:02:59.4230] [main-comp-ctx-3838] E: 57
[05:02:59.4230] [main-comp-ctx-3839] O:Dish(53,Clean)
[05:02:59.4240] [main-comp-ctx-3839] washing dish 57
[05:02:59.4230] [main-comp-ctx-3838] E: 58
[05:02:59.4240] [main-comp-ctx-3838] E: 59
[05:02:59.4250] [main-comp-ctx-3837] Dish washed 54.
[05:02:59.4250] [main-comp-ctx-3838] washing dish 58
[05:02:59.4250] [main-comp-ctx-3837] O:Dish(54,Clean)
[05:02:59.4260] [main-comp-ctx-3837] washing dish 59
[05:02:59.4260] [main-comp-ctx-3836] Dish washed 55.
[05:02:59.4270] [main-comp-ctx-3836] O:Dish(55,Clean)
[05:02:59.4270] [main-comp-ctx-3836] E: 60
[05:02:59.4270] [main-comp-ctx-3836] E: 61
[05:02:59.4280] [main-comp-ctx-3836] washing dish 60
[05:02:59.6220] [main-comp-ctx-3835] Dish washed 56.
[05:02:59.6230] [main-comp-ctx-3835] O:Dish(56,Clean)
[05:02:59.6230] [main-comp-ctx-3835] washing dish 61
[05:02:59.6240] [main-comp-ctx-3839] Dish washed 57.
[05:02:59.6240] [main-comp-ctx-3839] O:Dish(57,Clean)
[05:02:59.6250] [main-comp-ctx-3839] E: 62
[05:02:59.6250] [main-comp-ctx-3839] E: 63
[05:02:59.6260] [main-comp-ctx-3839] washing dish 62
[05:02:59.6270] [main-comp-ctx-3838] Dish washed 58.
[05:02:59.6270] [main-comp-ctx-3837] washing dish 63
[05:02:59.6270] [main-comp-ctx-3838] O:Dish(58,Clean)
[05:02:59.6280] [main-comp-ctx-3838] Dish washed 59.
[05:02:59.6280] [main-comp-ctx-3836] E: 64
[05:02:59.6290] [main-comp-ctx-3838] O:Dish(59,Clean)
[05:02:59.6290] [main-comp-ctx-3836] E: 65
[05:02:59.6290] [main-comp-ctx-3836] E: 66
[05:02:59.6290] [main-comp-ctx-3838] Dish washed 60.
[05:02:59.6300] [main-comp-ctx-3838] O:Dish(60,Clean)
[05:02:59.6300] [main-comp-ctx-3836] washing dish 64
[05:02:59.6310] [main-comp-ctx-3838] washing dish 65
[05:02:59.8240] [main-comp-ctx-3835] Dish washed 61.
[05:02:59.8240] [main-comp-ctx-3835] O:Dish(61,Clean)
[05:02:59.8250] [main-comp-ctx-3835] washing dish 66
[05:02:59.8270] [main-comp-ctx-3839] Dish washed 62.
[05:02:59.8280] [main-comp-ctx-3837] E: 67
[05:02:59.8280] [main-comp-ctx-3837] E: 68
[05:02:59.8290] [main-comp-ctx-3837] E: 69
[05:02:59.8300] [main-comp-ctx-3837] washing dish 67
[05:02:59.8300] [main-comp-ctx-3839] O:Dish(62,Clean)
[05:02:59.8310] [main-comp-ctx-3839] Dish washed 63.
[05:02:59.8310] [main-comp-ctx-3839] O:Dish(63,Clean)
[05:02:59.8310] [main-comp-ctx-3839] Dish washed 64.
[05:02:59.8310] [main-comp-ctx-3836] washing dish 69
[05:02:59.8310] [main-comp-ctx-3839] O:Dish(64,Clean)
[05:02:59.8320] [main-comp-ctx-3839] E: 70
[05:02:59.8320] [main-comp-ctx-3839] washing dish 68
[05:02:59.8320] [main-comp-ctx-3838] Dish washed 65.
[05:02:59.8320] [main-comp-ctx-3838] O:Dish(65,Clean)
[05:02:59.8330] [main-comp-ctx-3838] washing dish 70
[05:03:00.0260] [main-comp-ctx-3835] Dish washed 66.
[05:03:00.0260] [main-comp-ctx-3835] O:Dish(66,Clean)
[05:03:00.0260] [main-comp-ctx-3835] E: 71
[05:03:00.0270] [main-comp-ctx-3835] E: 72
[05:03:00.0280] [main-comp-ctx-3835] washing dish 71
[05:03:00.0310] [main-comp-ctx-3837] Dish washed 67.
[05:03:00.0310] [main-comp-ctx-3837] O:Dish(67,Clean)
[05:03:00.0320] [main-comp-ctx-3837] washing dish 72
[05:03:00.0320] [main-comp-ctx-3836] Dish washed 69.
[05:03:00.0320] [main-comp-ctx-3836] O:Dish(69,Clean)
[05:03:00.0330] [main-comp-ctx-3836] E: 73
[05:03:00.0330] [main-comp-ctx-3836] E: 74
[05:03:00.0330] [main-comp-ctx-3839] Dish washed 68.
[05:03:00.0330] [main-comp-ctx-3836] E: 75
[05:03:00.0330] [main-comp-ctx-3839] O:Dish(68,Clean)
[05:03:00.0340] [main-comp-ctx-3839] washing dish 73
[05:03:00.0340] [main-comp-ctx-3836] washing dish 74
[05:03:00.0340] [main-comp-ctx-3838] Dish washed 70.
[05:03:00.0350] [main-comp-ctx-3838] O:Dish(70,Clean)
[05:03:00.0350] [main-comp-ctx-3838] washing dish 75
[05:03:00.2290] [main-comp-ctx-3835] Dish washed 71.
[05:03:00.2290] [main-comp-ctx-3835] O:Dish(71,Clean)
[05:03:00.2300] [main-comp-ctx-3835] E: 76
[05:03:00.2300] [main-comp-ctx-3835] E: 77
[05:03:00.2310] [main-comp-ctx-3835] washing dish 76
[05:03:00.2320] [main-comp-ctx-3837] Dish washed 72.
[05:03:00.2330] [main-comp-ctx-3837] O:Dish(72,Clean)
[05:03:00.2330] [main-comp-ctx-3837] washing dish 77
[05:03:00.2340] [main-comp-ctx-3839] Dish washed 73.
[05:03:00.2350] [main-comp-ctx-3836] E: 78
[05:03:00.2350] [main-comp-ctx-3839] O:Dish(73,Clean)
[05:03:00.2350] [main-comp-ctx-3836] E: 79
[05:03:00.2350] [main-comp-ctx-3839] Dish washed 74.
[05:03:00.2350] [main-comp-ctx-3836] E: 80
[05:03:00.2360] [main-comp-ctx-3838] washing dish 78
[05:03:00.2350] [main-comp-ctx-3839] O:Dish(74,Clean)
[05:03:00.2360] [main-comp-ctx-3839] Dish washed 75.
[05:03:00.2360] [main-comp-ctx-3836] E: 81
[05:03:00.2360] [main-comp-ctx-3839] O:Dish(75,Clean)
[05:03:00.2370] [main-comp-ctx-3836] washing dish 79
[05:03:00.2380] [main-comp-ctx-3839] washing dish 80
[05:03:00.4320] [main-comp-ctx-3835] Dish washed 76.
[05:03:00.4320] [main-comp-ctx-3835] O:Dish(76,Clean)
[05:03:00.4330] [main-comp-ctx-3835] washing dish 81
[05:03:00.4340] [main-comp-ctx-3837] Dish washed 77.
[05:03:00.4340] [main-comp-ctx-3837] O:Dish(77,Clean)
[05:03:00.4350] [main-comp-ctx-3837] E: 82
[05:03:00.4350] [main-comp-ctx-3837] E: 83
[05:03:00.4360] [main-comp-ctx-3838] Dish washed 78.
[05:03:00.4370] [main-comp-ctx-3838] O:Dish(78,Clean)
[05:03:00.4370] [main-comp-ctx-3837] washing dish 82
[05:03:00.4370] [main-comp-ctx-3838] washing dish 83
[05:03:00.4380] [main-comp-ctx-3836] Dish washed 79.
[05:03:00.4380] [main-comp-ctx-3836] O:Dish(79,Clean)
[05:03:00.4380] [main-comp-ctx-3836] Dish washed 80.
[05:03:00.4380] [main-comp-ctx-3839] E: 84
[05:03:00.4380] [main-comp-ctx-3836] O:Dish(80,Clean)
[05:03:00.4380] [main-comp-ctx-3839] E: 85
[05:03:00.4380] [main-comp-ctx-3836] washing dish 84
[05:03:00.4390] [main-comp-ctx-3839] E: 86
[05:03:00.4400] [main-comp-ctx-3839] washing dish 85
[05:03:00.6340] [main-comp-ctx-3835] Dish washed 81.
[05:03:00.6340] [main-comp-ctx-3835] O:Dish(81,Clean)
[05:03:00.6360] [main-comp-ctx-3835] washing dish 86
[05:03:00.6370] [main-comp-ctx-3837] Dish washed 82.
[05:03:00.6370] [main-comp-ctx-3837] O:Dish(82,Clean)
[05:03:00.6380] [main-comp-ctx-3838] E: 87
[05:03:00.6380] [main-comp-ctx-3837] Dish washed 83.
[05:03:00.6380] [main-comp-ctx-3837] O:Dish(83,Clean)
[05:03:00.6380] [main-comp-ctx-3838] E: 88
[05:03:00.6380] [main-comp-ctx-3837] washing dish 87
[05:03:00.6390] [main-comp-ctx-3838] E: 89
[05:03:00.6390] [main-comp-ctx-3838] washing dish 88
[05:03:00.6400] [main-comp-ctx-3839] washing dish 89
[05:03:00.6400] [main-comp-ctx-3836] Dish washed 84.
[05:03:00.6410] [main-comp-ctx-3836] O:Dish(84,Clean)
[05:03:00.6410] [main-comp-ctx-3836] Dish washed 85.
[05:03:00.6410] [main-comp-ctx-3836] O:Dish(85,Clean)
[05:03:00.6420] [main-comp-ctx-3836] E: 90
[05:03:00.6420] [main-comp-ctx-3836] E: 91
[05:03:00.6430] [main-comp-ctx-3836] washing dish 90
[05:03:00.8370] [main-comp-ctx-3835] Dish washed 86.
[05:03:00.8370] [main-comp-ctx-3835] O:Dish(86,Clean)
[05:03:00.8380] [main-comp-ctx-3835] washing dish 91
[05:03:00.8390] [main-comp-ctx-3837] Dish washed 87.
[05:03:00.8390] [main-comp-ctx-3837] O:Dish(87,Clean)
[05:03:00.8400] [main-comp-ctx-3837] E: 92
[05:03:00.8410] [main-comp-ctx-3837] E: 93
[05:03:00.8410] [main-comp-ctx-3838] Dish washed 88.
[05:03:00.8410] [main-comp-ctx-3839] washing dish 92
[05:03:00.8410] [main-comp-ctx-3838] O:Dish(88,Clean)
[05:03:00.8420] [main-comp-ctx-3838] Dish washed 89.
[05:03:00.8410] [main-comp-ctx-3837] E: 94
[05:03:00.8420] [main-comp-ctx-3838] O:Dish(89,Clean)
[05:03:00.8420] [main-comp-ctx-3837] E: 95
[05:03:00.8420] [main-comp-ctx-3838] washing dish 93
[05:03:00.8430] [main-comp-ctx-3837] washing dish 94
[05:03:00.8430] [main-comp-ctx-3836] Dish washed 90.
[05:03:00.8440] [main-comp-ctx-3836] O:Dish(90,Clean)
[05:03:00.8440] [main-comp-ctx-3836] washing dish 95
[05:03:01.0390] [main-comp-ctx-3835] Dish washed 91.
[05:03:01.0400] [main-comp-ctx-3835] O:Dish(91,Clean)
[05:03:01.0410] [main-comp-ctx-3835] E: 96
[05:03:01.0420] [main-comp-ctx-3835] E: 97
[05:03:01.0420] [main-comp-ctx-3839] Dish washed 92.
[05:03:01.0420] [main-comp-ctx-3839] O:Dish(92,Clean)
[05:03:01.0420] [main-comp-ctx-3835] E: 98
[05:03:01.0430] [main-comp-ctx-3839] washing dish 96
[05:03:01.0430] [main-comp-ctx-3838] Dish washed 93.
[05:03:01.0430] [main-comp-ctx-3838] O:Dish(93,Clean)
[05:03:01.0440] [main-comp-ctx-3838] Dish washed 94.
[05:03:01.0440] [main-comp-ctx-3835] washing dish 97
[05:03:01.0440] [main-comp-ctx-3837] washing dish 98
[05:03:01.0450] [main-comp-ctx-3836] E: 99
[05:03:01.0440] [main-comp-ctx-3838] O:Dish(94,Clean)
[05:03:01.0450] [main-comp-ctx-3836] E: 100
[05:03:01.0460] [main-comp-ctx-3838] Dish washed 95.
[05:03:01.0470] [main-comp-ctx-3838] O:Dish(95,Clean)
[05:03:01.0480] [main-comp-ctx-3838] washing dish 99
[05:03:01.0530] [main-comp-ctx-3836] washing dish 100
[05:03:01.2440] [main-comp-ctx-3839] Dish washed 96.
[05:03:01.2440] [main-comp-ctx-3839] O:Dish(96,Clean)
[05:03:01.2450] [main-comp-ctx-3839] Dish washed 97.
[05:03:01.2450] [main-comp-ctx-3839] O:Dish(97,Clean)
[05:03:01.2460] [main-comp-ctx-3839] Dish washed 98.
[05:03:01.2460] [main-comp-ctx-3839] O:Dish(98,Clean)
[05:03:01.2480] [main-comp-ctx-3835] Dish washed 99.
[05:03:01.2490] [main-comp-ctx-3835] O:Dish(99,Clean)
[05:03:01.2530] [main-comp-ctx-3835] Dish washed 100.
[05:03:01.2540] [main-comp-ctx-3835] O:Dish(100,Clean)
[05:03:12.2980] [run-main-77] Program Ends.
