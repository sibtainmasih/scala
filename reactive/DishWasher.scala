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
  .mergeMap(d => Observable.fromTask(d.wash))
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

Thread.sleep(30000)
println(s"$log Program Ends.")

/* Output
[05:45:18.3060] [run-main-0] Program starts.
[05:45:20.7050] [run-main-0] E: 1
[05:45:20.7220] [run-main-0] washing dish 1
[05:45:20.9470] [main-comp-ctx-139] Dish washed 1.
[05:45:20.9470] [main-comp-ctx-139] O:Dish(1,Clean)
[05:45:20.9620] [run-main-0] E: 2
[05:45:20.9620] [run-main-0] washing dish 2
[05:45:21.1630] [main-comp-ctx-139] Dish washed 2.
[05:45:21.1630] [run-main-0] E: 3
[05:45:21.1640] [run-main-0] washing dish 3
[05:45:21.1650] [main-comp-ctx-139] O:Dish(2,Clean)
[05:45:21.3650] [main-comp-ctx-139] Dish washed 3.
[05:45:21.3650] [run-main-0] E: 4
[05:45:21.3650] [main-comp-ctx-139] O:Dish(3,Clean)
[05:45:21.3650] [run-main-0] washing dish 4
[05:45:21.5660] [main-comp-ctx-139] Dish washed 4.
[05:45:21.5660] [run-main-0] E: 5
[05:45:21.5670] [main-comp-ctx-139] O:Dish(4,Clean)
[05:45:21.5670] [run-main-0] washing dish 5
[05:45:21.7670] [run-main-0] E: 6
[05:45:21.7670] [main-comp-ctx-139] Dish washed 5.
[05:45:21.7680] [run-main-0] washing dish 6
[05:45:21.7680] [main-comp-ctx-139] O:Dish(5,Clean)
[05:45:21.9680] [run-main-0] E: 7
[05:45:21.9680] [main-comp-ctx-139] Dish washed 6.
[05:45:21.9690] [run-main-0] washing dish 7
[05:45:21.9690] [main-comp-ctx-139] O:Dish(6,Clean)
[05:45:22.1690] [run-main-0] E: 8
[05:45:22.1700] [run-main-0] washing dish 8
[05:45:22.1690] [main-comp-ctx-139] Dish washed 7.
[05:45:22.1710] [main-comp-ctx-139] O:Dish(7,Clean)
[05:45:22.3700] [main-comp-ctx-139] Dish washed 8.
[05:45:22.3700] [run-main-0] E: 9
[05:45:22.3710] [run-main-0] washing dish 9
[05:45:22.3720] [main-comp-ctx-139] O:Dish(8,Clean)
[05:45:22.5720] [run-main-0] E: 10
[05:45:22.5730] [run-main-0] washing dish 10
[05:45:22.5720] [main-comp-ctx-139] Dish washed 9.
[05:45:22.5750] [main-comp-ctx-139] O:Dish(9,Clean)
[05:45:22.7740] [run-main-0] E: 11
[05:45:22.7740] [main-comp-ctx-139] Dish washed 10.
[05:45:22.7740] [run-main-0] washing dish 11
[05:45:22.7740] [main-comp-ctx-139] O:Dish(10,Clean)
[05:45:22.9750] [main-comp-ctx-139] Dish washed 11.
[05:45:22.9750] [run-main-0] E: 12
[05:45:22.9760] [main-comp-ctx-139] O:Dish(11,Clean)
[05:45:22.9760] [run-main-0] washing dish 12
[05:45:23.1770] [main-comp-ctx-139] Dish washed 12.
[05:45:23.1770] [run-main-0] E: 13
[05:45:23.1780] [main-comp-ctx-139] O:Dish(12,Clean)
[05:45:23.1780] [run-main-0] washing dish 13
[05:45:23.3790] [run-main-0] E: 14
[05:45:23.3790] [run-main-0] washing dish 14
[05:45:23.3790] [main-comp-ctx-139] Dish washed 13.
[05:45:23.3800] [main-comp-ctx-139] O:Dish(13,Clean)
[05:45:23.5800] [run-main-0] E: 15
[05:45:23.5800] [main-comp-ctx-139] Dish washed 14.
[05:45:23.5800] [run-main-0] washing dish 15
[05:45:23.5810] [main-comp-ctx-139] O:Dish(14,Clean)
[05:45:23.7810] [run-main-0] E: 16
[05:45:23.7810] [main-comp-ctx-139] Dish washed 15.
[05:45:23.7820] [main-comp-ctx-139] O:Dish(15,Clean)
[05:45:23.7840] [run-main-0] washing dish 16
[05:45:23.9860] [run-main-0] E: 17
[05:45:23.9860] [main-comp-ctx-139] Dish washed 16.
[05:45:23.9860] [run-main-0] washing dish 17
[05:45:23.9880] [main-comp-ctx-139] O:Dish(16,Clean)
[05:45:24.1890] [main-comp-ctx-139] Dish washed 17.
[05:45:24.1890] [main-comp-ctx-139] O:Dish(17,Clean)
[05:45:24.1890] [run-main-0] E: 18
[05:45:24.1900] [run-main-0] washing dish 18
[05:45:24.3900] [run-main-0] E: 19
[05:45:24.3910] [run-main-0] washing dish 19
[05:45:24.3910] [main-comp-ctx-139] Dish washed 18.
[05:45:24.3910] [main-comp-ctx-139] O:Dish(18,Clean)
[05:45:24.5920] [run-main-0] E: 20
[05:45:24.5920] [main-comp-ctx-139] Dish washed 19.
[05:45:24.5920] [run-main-0] washing dish 20
[05:45:24.5920] [main-comp-ctx-139] O:Dish(19,Clean)
[05:45:24.7930] [run-main-0] E: 21
[05:45:24.7930] [run-main-0] washing dish 21
[05:45:24.7930] [main-comp-ctx-139] Dish washed 20.
[05:45:24.7940] [main-comp-ctx-139] O:Dish(20,Clean)
[05:45:24.9940] [run-main-0] E: 22
[05:45:24.9940] [main-comp-ctx-139] Dish washed 21.
[05:45:24.9940] [run-main-0] washing dish 22
[05:45:24.9950] [main-comp-ctx-139] O:Dish(21,Clean)
[05:45:25.1950] [run-main-0] E: 23
[05:45:25.1960] [run-main-0] washing dish 23
[05:45:25.1950] [main-comp-ctx-139] Dish washed 22.
[05:45:25.1970] [main-comp-ctx-139] O:Dish(22,Clean)
[05:45:25.3960] [run-main-0] E: 24
[05:45:25.3970] [main-comp-ctx-139] Dish washed 23.
[05:45:25.3970] [run-main-0] washing dish 24
[05:45:25.3980] [main-comp-ctx-139] O:Dish(23,Clean)
[05:45:25.5980] [run-main-0] E: 25
[05:45:25.5980] [main-comp-ctx-139] Dish washed 24.
[05:45:25.5990] [run-main-0] washing dish 25
[05:45:25.5990] [main-comp-ctx-139] O:Dish(24,Clean)
[05:45:25.7990] [run-main-0] E: 26
[05:45:25.8000] [main-comp-ctx-139] Dish washed 25.
[05:45:25.8000] [run-main-0] washing dish 26
[05:45:25.8000] [main-comp-ctx-139] O:Dish(25,Clean)
[05:45:26.0010] [run-main-0] E: 27
[05:45:26.0010] [main-comp-ctx-139] Dish washed 26.
[05:45:26.0010] [run-main-0] washing dish 27
[05:45:26.0010] [main-comp-ctx-139] O:Dish(26,Clean)
[05:45:26.2020] [run-main-0] E: 28
[05:45:26.2020] [main-comp-ctx-139] Dish washed 27.
[05:45:26.2020] [run-main-0] washing dish 28
[05:45:26.2020] [main-comp-ctx-139] O:Dish(27,Clean)
[05:45:26.4030] [run-main-0] E: 29
[05:45:26.4040] [run-main-0] washing dish 29
[05:45:26.4030] [main-comp-ctx-139] Dish washed 28.
[05:45:26.4040] [main-comp-ctx-139] O:Dish(28,Clean)
[05:45:26.6050] [main-comp-ctx-139] Dish washed 29.
[05:45:26.6050] [run-main-0] E: 30
[05:45:26.6050] [main-comp-ctx-139] O:Dish(29,Clean)
[05:45:26.6060] [run-main-0] washing dish 30
[05:45:26.8060] [run-main-0] E: 31
[05:45:26.8060] [main-comp-ctx-139] Dish washed 30.
[05:45:26.8070] [run-main-0] washing dish 31
[05:45:26.8080] [main-comp-ctx-139] O:Dish(30,Clean)
[05:45:27.0080] [run-main-0] E: 32
[05:45:27.0090] [run-main-0] washing dish 32
[05:45:27.0100] [main-comp-ctx-139] Dish washed 31.
[05:45:27.0100] [main-comp-ctx-139] O:Dish(31,Clean)
[05:45:27.2090] [main-comp-ctx-139] Dish washed 32.
[05:45:27.2090] [run-main-0] E: 33
[05:45:27.2100] [main-comp-ctx-139] O:Dish(32,Clean)
[05:45:27.2130] [run-main-0] washing dish 33
[05:45:27.4130] [run-main-0] E: 34
[05:45:27.4130] [main-comp-ctx-139] Dish washed 33.
[05:45:27.4140] [run-main-0] washing dish 34
[05:45:27.4140] [main-comp-ctx-139] O:Dish(33,Clean)
[05:45:27.6140] [run-main-0] E: 35
[05:45:27.6140] [main-comp-ctx-139] Dish washed 34.
[05:45:27.6150] [run-main-0] washing dish 35
[05:45:27.6150] [main-comp-ctx-139] O:Dish(34,Clean)
[05:45:27.8150] [run-main-0] E: 36
[05:45:27.8160] [run-main-0] washing dish 36
[05:45:27.8150] [main-comp-ctx-139] Dish washed 35.
[05:45:27.8160] [main-comp-ctx-139] O:Dish(35,Clean)
[05:45:28.0160] [run-main-0] E: 37
[05:45:28.0160] [main-comp-ctx-139] Dish washed 36.
[05:45:28.0170] [run-main-0] washing dish 37
[05:45:28.0170] [main-comp-ctx-139] O:Dish(36,Clean)
[05:45:28.2170] [main-comp-ctx-139] Dish washed 37.
[05:45:28.2170] [run-main-0] E: 38
[05:45:28.2180] [main-comp-ctx-139] O:Dish(37,Clean)
[05:45:28.2180] [run-main-0] washing dish 38
[05:45:28.4180] [run-main-0] E: 39
[05:45:28.4180] [main-comp-ctx-139] Dish washed 38.
[05:45:28.4190] [run-main-0] washing dish 39
[05:45:28.4190] [main-comp-ctx-139] O:Dish(38,Clean)
[05:45:28.6200] [main-comp-ctx-139] Dish washed 39.
[05:45:28.6200] [run-main-0] E: 40
[05:45:28.6200] [main-comp-ctx-139] O:Dish(39,Clean)
[05:45:28.6200] [run-main-0] washing dish 40
[05:45:28.8210] [run-main-0] E: 41
[05:45:28.8210] [main-comp-ctx-139] Dish washed 40.
[05:45:28.8210] [run-main-0] washing dish 41
[05:45:28.8210] [main-comp-ctx-139] O:Dish(40,Clean)
[05:45:29.0220] [main-comp-ctx-139] Dish washed 41.
[05:45:29.0220] [run-main-0] E: 42
[05:45:29.0220] [main-comp-ctx-139] O:Dish(41,Clean)
[05:45:29.0220] [run-main-0] washing dish 42
[05:45:29.2230] [main-comp-ctx-139] Dish washed 42.
[05:45:29.2230] [run-main-0] E: 43
[05:45:29.2230] [main-comp-ctx-139] O:Dish(42,Clean)
[05:45:29.2240] [run-main-0] washing dish 43
[05:45:29.4250] [run-main-0] E: 44
[05:45:29.4250] [main-comp-ctx-139] Dish washed 43.
[05:45:29.4260] [run-main-0] washing dish 44
[05:45:29.4260] [main-comp-ctx-139] O:Dish(43,Clean)
[05:45:29.6260] [run-main-0] E: 45
[05:45:29.6260] [main-comp-ctx-139] Dish washed 44.
[05:45:29.6270] [run-main-0] washing dish 45
[05:45:29.6270] [main-comp-ctx-139] O:Dish(44,Clean)
[05:45:29.8280] [run-main-0] E: 46
[05:45:29.8280] [main-comp-ctx-139] Dish washed 45.
[05:45:29.8280] [run-main-0] washing dish 46
[05:45:29.8280] [main-comp-ctx-139] O:Dish(45,Clean)
[05:45:30.0290] [run-main-0] E: 47
[05:45:30.0290] [main-comp-ctx-139] Dish washed 46.
[05:45:30.0290] [run-main-0] washing dish 47
[05:45:30.0290] [main-comp-ctx-139] O:Dish(46,Clean)
[05:45:30.2300] [main-comp-ctx-139] Dish washed 47.
[05:45:30.2300] [run-main-0] E: 48
[05:45:30.2300] [main-comp-ctx-139] O:Dish(47,Clean)
[05:45:30.2310] [run-main-0] washing dish 48
[05:45:30.4320] [main-comp-ctx-139] Dish washed 48.
[05:45:30.4320] [run-main-0] E: 49
[05:45:30.4320] [main-comp-ctx-139] O:Dish(48,Clean)
[05:45:30.4330] [run-main-0] washing dish 49
[05:45:30.6340] [main-comp-ctx-139] Dish washed 49.
[05:45:30.6340] [run-main-0] E: 50
[05:45:30.6340] [main-comp-ctx-139] O:Dish(49,Clean)
[05:45:30.6340] [run-main-0] washing dish 50
[05:45:30.8360] [main-comp-ctx-139] Dish washed 50.
[05:45:30.8360] [run-main-0] E: 51
[05:45:30.8360] [main-comp-ctx-139] O:Dish(50,Clean)
[05:45:30.8360] [run-main-0] washing dish 51
[05:45:31.0370] [run-main-0] E: 52
[05:45:31.0370] [main-comp-ctx-139] Dish washed 51.
[05:45:31.0390] [run-main-0] washing dish 52
[05:45:31.0390] [main-comp-ctx-139] O:Dish(51,Clean)
[05:45:31.2400] [main-comp-ctx-139] Dish washed 52.
[05:45:31.2400] [run-main-0] E: 53
[05:45:31.2400] [main-comp-ctx-139] O:Dish(52,Clean)
[05:45:31.2400] [run-main-0] washing dish 53
[05:45:31.4410] [run-main-0] E: 54
[05:45:31.4410] [main-comp-ctx-139] Dish washed 53.
[05:45:31.4410] [run-main-0] washing dish 54
[05:45:31.4410] [main-comp-ctx-139] O:Dish(53,Clean)
[05:45:31.6420] [run-main-0] E: 55
[05:45:31.6420] [main-comp-ctx-139] Dish washed 54.
[05:45:31.6420] [run-main-0] washing dish 55
[05:45:31.6430] [main-comp-ctx-139] O:Dish(54,Clean)
[05:45:31.8430] [run-main-0] E: 56
[05:45:31.8440] [run-main-0] washing dish 56
[05:45:31.8440] [main-comp-ctx-139] Dish washed 55.
[05:45:31.8440] [main-comp-ctx-139] O:Dish(55,Clean)
[05:45:32.0440] [run-main-0] E: 57
[05:45:32.0450] [run-main-0] washing dish 57
[05:45:32.0450] [main-comp-ctx-139] Dish washed 56.
[05:45:32.0450] [main-comp-ctx-139] O:Dish(56,Clean)
[05:45:32.2450] [run-main-0] E: 58
[05:45:32.2450] [main-comp-ctx-139] Dish washed 57.
[05:45:32.2460] [run-main-0] washing dish 58
[05:45:32.2460] [main-comp-ctx-139] O:Dish(57,Clean)
[05:45:32.4470] [run-main-0] E: 59
[05:45:32.4470] [main-comp-ctx-139] Dish washed 58.
[05:45:32.4470] [run-main-0] washing dish 59
[05:45:32.4470] [main-comp-ctx-139] O:Dish(58,Clean)
[05:45:32.6480] [run-main-0] E: 60
[05:45:32.6480] [main-comp-ctx-139] Dish washed 59.
[05:45:32.6480] [run-main-0] washing dish 60
[05:45:32.6480] [main-comp-ctx-139] O:Dish(59,Clean)
[05:45:32.8490] [run-main-0] E: 61
[05:45:32.8490] [main-comp-ctx-139] Dish washed 60.
[05:45:32.8490] [run-main-0] washing dish 61
[05:45:32.8490] [main-comp-ctx-139] O:Dish(60,Clean)
[05:45:33.0500] [run-main-0] E: 62
[05:45:33.0500] [main-comp-ctx-139] Dish washed 61.
[05:45:33.0500] [run-main-0] washing dish 62
[05:45:33.0500] [main-comp-ctx-139] O:Dish(61,Clean)
[05:45:33.2510] [main-comp-ctx-139] Dish washed 62.
[05:45:33.2510] [run-main-0] E: 63
[05:45:33.2520] [main-comp-ctx-139] O:Dish(62,Clean)
[05:45:33.2520] [run-main-0] washing dish 63
[05:45:33.4530] [main-comp-ctx-139] Dish washed 63.
[05:45:33.4530] [run-main-0] E: 64
[05:45:33.4530] [main-comp-ctx-139] O:Dish(63,Clean)
[05:45:33.4530] [run-main-0] washing dish 64
[05:45:33.6540] [run-main-0] E: 65
[05:45:33.6540] [main-comp-ctx-139] Dish washed 64.
[05:45:33.6540] [main-comp-ctx-139] O:Dish(64,Clean)
[05:45:33.6550] [run-main-0] washing dish 65
[05:45:33.8550] [run-main-0] E: 66
[05:45:33.8560] [main-comp-ctx-139] Dish washed 65.
[05:45:33.8560] [run-main-0] washing dish 66
[05:45:33.8560] [main-comp-ctx-139] O:Dish(65,Clean)
[05:45:34.0560] [run-main-0] E: 67
[05:45:34.0560] [main-comp-ctx-139] Dish washed 66.
[05:45:34.0570] [run-main-0] washing dish 67
[05:45:34.0570] [main-comp-ctx-139] O:Dish(66,Clean)
[05:45:34.2570] [run-main-0] E: 68
[05:45:34.2570] [main-comp-ctx-139] Dish washed 67.
[05:45:34.2580] [run-main-0] washing dish 68
[05:45:34.2580] [main-comp-ctx-139] O:Dish(67,Clean)
[05:45:34.4580] [run-main-0] E: 69
[05:45:34.4580] [main-comp-ctx-139] Dish washed 68.
[05:45:34.4590] [run-main-0] washing dish 69
[05:45:34.4590] [main-comp-ctx-139] O:Dish(68,Clean)
[05:45:34.6600] [main-comp-ctx-139] Dish washed 69.
[05:45:34.6600] [run-main-0] E: 70
[05:45:34.6600] [main-comp-ctx-139] O:Dish(69,Clean)
[05:45:34.6600] [run-main-0] washing dish 70
[05:45:34.8610] [run-main-0] E: 71
[05:45:34.8610] [main-comp-ctx-139] Dish washed 70.
[05:45:34.8610] [run-main-0] washing dish 71
[05:45:34.8610] [main-comp-ctx-139] O:Dish(70,Clean)
[05:45:35.0620] [run-main-0] E: 72
[05:45:35.0620] [main-comp-ctx-139] Dish washed 71.
[05:45:35.0620] [run-main-0] washing dish 72
[05:45:35.0620] [main-comp-ctx-139] O:Dish(71,Clean)
[05:45:35.2630] [run-main-0] E: 73
[05:45:35.2630] [main-comp-ctx-139] Dish washed 72.
[05:45:35.2630] [run-main-0] washing dish 73
[05:45:35.2630] [main-comp-ctx-139] O:Dish(72,Clean)
[05:45:35.4640] [run-main-0] E: 74
[05:45:35.4640] [main-comp-ctx-139] Dish washed 73.
[05:45:35.4640] [run-main-0] washing dish 74
[05:45:35.4640] [main-comp-ctx-139] O:Dish(73,Clean)
[05:45:35.6650] [main-comp-ctx-139] Dish washed 74.
[05:45:35.6650] [run-main-0] E: 75
[05:45:35.6650] [main-comp-ctx-139] O:Dish(74,Clean)
[05:45:35.6660] [run-main-0] washing dish 75
[05:45:35.8670] [main-comp-ctx-139] Dish washed 75.
[05:45:35.8670] [run-main-0] E: 76
[05:45:35.8670] [main-comp-ctx-139] O:Dish(75,Clean)
[05:45:35.8670] [run-main-0] washing dish 76
[05:45:36.0680] [run-main-0] E: 77
[05:45:36.0680] [main-comp-ctx-139] Dish washed 76.
[05:45:36.0690] [run-main-0] washing dish 77
[05:45:36.0690] [main-comp-ctx-139] O:Dish(76,Clean)
[05:45:36.2690] [main-comp-ctx-139] Dish washed 77.
[05:45:36.2690] [run-main-0] E: 78
[05:45:36.2700] [main-comp-ctx-139] O:Dish(77,Clean)
[05:45:36.2700] [run-main-0] washing dish 78
[05:45:36.4710] [main-comp-ctx-139] Dish washed 78.
[05:45:36.4710] [run-main-0] E: 79
[05:45:36.4710] [main-comp-ctx-139] O:Dish(78,Clean)
[05:45:36.4710] [run-main-0] washing dish 79
[05:45:36.6720] [run-main-0] E: 80
[05:45:36.6720] [main-comp-ctx-139] Dish washed 79.
[05:45:36.6720] [run-main-0] washing dish 80
[05:45:36.6720] [main-comp-ctx-139] O:Dish(79,Clean)
[05:45:36.8730] [run-main-0] E: 81
[05:45:36.8730] [main-comp-ctx-139] Dish washed 80.
[05:45:36.8730] [run-main-0] washing dish 81
[05:45:36.8730] [main-comp-ctx-139] O:Dish(80,Clean)
[05:45:37.0740] [run-main-0] E: 82
[05:45:37.0740] [main-comp-ctx-139] Dish washed 81.
[05:45:37.0740] [run-main-0] washing dish 82
[05:45:37.0740] [main-comp-ctx-139] O:Dish(81,Clean)
[05:45:37.2750] [run-main-0] E: 83
[05:45:37.2750] [main-comp-ctx-139] Dish washed 82.
[05:45:37.2760] [run-main-0] washing dish 83
[05:45:37.2760] [main-comp-ctx-139] O:Dish(82,Clean)
[05:45:37.4760] [run-main-0] E: 84
[05:45:37.4770] [run-main-0] washing dish 84
[05:45:37.4760] [main-comp-ctx-139] Dish washed 83.
[05:45:37.4770] [main-comp-ctx-139] O:Dish(83,Clean)
[05:45:37.6770] [run-main-0] E: 85
[05:45:37.6770] [main-comp-ctx-139] Dish washed 84.
[05:45:37.6780] [run-main-0] washing dish 85
[05:45:37.6780] [main-comp-ctx-139] O:Dish(84,Clean)
--- output limited due to scastie timeout ---
*/
