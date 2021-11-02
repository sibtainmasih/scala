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
  .asyncBoundary(OverflowStrategy.BackPressure(2))
  .mapParallelUnordered(10)(d => d.wash)
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
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 1))

Thread.sleep(30000)
println(s"$log Program Ends.")

/* Output
[03:57:24.1660] [run-main-b1] Program starts.
[03:57:24.1810] [run-main-b1] E: 1
[03:57:24.1900] [run-main-b1] E: 2
[03:57:24.1900] [run-main-b1] E: 3
[03:57:24.2010] [main-comp-ctx-6140] washing dish 1
[03:57:24.4060] [main-comp-ctx-6140] washing dish 2
[03:57:24.6070] [main-comp-ctx-6140] washing dish 3
[03:57:24.8110] [main-comp-ctx-6140] Dish washed 1.
[03:57:24.8120] [main-comp-ctx-6140] O:Dish(1,Clean)
[03:57:24.8160] [main-comp-ctx-6140] Dish washed 2.
[03:57:24.8160] [main-comp-ctx-6140] O:Dish(2,Clean)
[03:57:24.8170] [main-comp-ctx-6140] Dish washed 3.
[03:57:24.8170] [main-comp-ctx-6140] O:Dish(3,Clean)
[03:57:24.8180] [main-comp-ctx-6140] E: 4
[03:57:24.8190] [main-comp-ctx-6140] E: 5
[03:57:24.8190] [main-comp-ctx-6140] E: 6
[03:57:24.8200] [main-comp-ctx-6140] washing dish 4
[03:57:25.0210] [main-comp-ctx-6140] washing dish 5
[03:57:25.2220] [main-comp-ctx-6140] washing dish 6
[03:57:25.4230] [main-comp-ctx-6140] Dish washed 4.
[03:57:25.4230] [main-comp-ctx-6140] O:Dish(4,Clean)
[03:57:25.4240] [main-comp-ctx-6140] Dish washed 5.
[03:57:25.4250] [main-comp-ctx-6140] O:Dish(5,Clean)
[03:57:25.4250] [main-comp-ctx-6140] Dish washed 6.
[03:57:25.4260] [main-comp-ctx-6140] O:Dish(6,Clean)
[03:57:25.4270] [main-comp-ctx-6140] E: 7
[03:57:25.4280] [main-comp-ctx-6140] E: 8
[03:57:25.4280] [main-comp-ctx-6140] E: 9
[03:57:25.4300] [main-comp-ctx-6140] washing dish 7
[03:57:25.6310] [main-comp-ctx-6140] washing dish 8
[03:57:25.8320] [main-comp-ctx-6140] washing dish 9
[03:57:26.0330] [main-comp-ctx-6140] Dish washed 7.
[03:57:26.0330] [main-comp-ctx-6140] O:Dish(7,Clean)
[03:57:26.0340] [main-comp-ctx-6140] Dish washed 8.
[03:57:26.0340] [main-comp-ctx-6140] O:Dish(8,Clean)
[03:57:26.0350] [main-comp-ctx-6140] Dish washed 9.
[03:57:26.0350] [main-comp-ctx-6140] O:Dish(9,Clean)
[03:57:26.0360] [main-comp-ctx-6140] E: 10
[03:57:26.0360] [main-comp-ctx-6140] E: 11
[03:57:26.0370] [main-comp-ctx-6140] E: 12
[03:57:26.0380] [main-comp-ctx-6140] washing dish 10
[03:57:26.2380] [main-comp-ctx-6140] washing dish 11
[03:57:26.4390] [main-comp-ctx-6140] washing dish 12
[03:57:26.6400] [main-comp-ctx-6140] Dish washed 10.
[03:57:26.6400] [main-comp-ctx-6140] O:Dish(10,Clean)
[03:57:26.6400] [main-comp-ctx-6140] Dish washed 11.
[03:57:26.6410] [main-comp-ctx-6140] O:Dish(11,Clean)
[03:57:26.6410] [main-comp-ctx-6140] Dish washed 12.
[03:57:26.6410] [main-comp-ctx-6140] O:Dish(12,Clean)
[03:57:26.6410] [main-comp-ctx-6140] E: 13
[03:57:26.6420] [main-comp-ctx-6140] E: 14
[03:57:26.6420] [main-comp-ctx-6140] E: 15
[03:57:26.6430] [main-comp-ctx-6140] washing dish 13
[03:57:26.8440] [main-comp-ctx-6140] washing dish 14
[03:57:27.0440] [main-comp-ctx-6140] washing dish 15
[03:57:27.2450] [main-comp-ctx-6140] Dish washed 13.
[03:57:27.2450] [main-comp-ctx-6140] O:Dish(13,Clean)
[03:57:27.2460] [main-comp-ctx-6140] Dish washed 14.
[03:57:27.2460] [main-comp-ctx-6140] O:Dish(14,Clean)
[03:57:27.2460] [main-comp-ctx-6140] Dish washed 15.
[03:57:27.2470] [main-comp-ctx-6140] O:Dish(15,Clean)
[03:57:27.2480] [main-comp-ctx-6140] E: 16
[03:57:27.2480] [main-comp-ctx-6140] E: 17
[03:57:27.2490] [main-comp-ctx-6140] E: 18
[03:57:27.2490] [main-comp-ctx-6140] washing dish 16
[03:57:27.4500] [main-comp-ctx-6140] washing dish 17
[03:57:27.6510] [main-comp-ctx-6140] washing dish 18
[03:57:27.8510] [main-comp-ctx-6140] Dish washed 16.
[03:57:27.8520] [main-comp-ctx-6140] O:Dish(16,Clean)
[03:57:27.8520] [main-comp-ctx-6140] Dish washed 17.
[03:57:27.8520] [main-comp-ctx-6140] O:Dish(17,Clean)
[03:57:27.8530] [main-comp-ctx-6140] Dish washed 18.
[03:57:27.8530] [main-comp-ctx-6140] O:Dish(18,Clean)
[03:57:27.8540] [main-comp-ctx-6140] E: 19
[03:57:27.8540] [main-comp-ctx-6140] E: 20
[03:57:27.8540] [main-comp-ctx-6140] E: 21
[03:57:27.8550] [main-comp-ctx-6140] washing dish 19
[03:57:28.0560] [main-comp-ctx-6140] washing dish 20
[03:57:28.2570] [main-comp-ctx-6140] washing dish 21
[03:57:28.4570] [main-comp-ctx-6140] Dish washed 19.
[03:57:28.4570] [main-comp-ctx-6140] O:Dish(19,Clean)
[03:57:28.4580] [main-comp-ctx-6140] Dish washed 20.
[03:57:28.4580] [main-comp-ctx-6140] O:Dish(20,Clean)
[03:57:28.4580] [main-comp-ctx-6140] Dish washed 21.
[03:57:28.4590] [main-comp-ctx-6140] O:Dish(21,Clean)
[03:57:28.4600] [main-comp-ctx-6140] E: 22
[03:57:28.4600] [main-comp-ctx-6140] E: 23
[03:57:28.4600] [main-comp-ctx-6140] E: 24
[03:57:28.4620] [main-comp-ctx-6140] washing dish 22
[03:57:28.6620] [main-comp-ctx-6140] washing dish 23
[03:57:28.8630] [main-comp-ctx-6140] washing dish 24
[03:57:29.0630] [main-comp-ctx-6140] Dish washed 22.
[03:57:29.0640] [main-comp-ctx-6140] O:Dish(22,Clean)
[03:57:29.0640] [main-comp-ctx-6140] Dish washed 23.
[03:57:29.0640] [main-comp-ctx-6140] O:Dish(23,Clean)
[03:57:29.0650] [main-comp-ctx-6140] Dish washed 24.
[03:57:29.0650] [main-comp-ctx-6140] O:Dish(24,Clean)
[03:57:29.0650] [main-comp-ctx-6140] E: 25
[03:57:29.0660] [main-comp-ctx-6140] E: 26
[03:57:29.0670] [main-comp-ctx-6140] E: 27
[03:57:29.0680] [main-comp-ctx-6140] washing dish 25
[03:57:29.2680] [main-comp-ctx-6140] washing dish 26
[03:57:29.4690] [main-comp-ctx-6140] washing dish 27
[03:57:29.6690] [main-comp-ctx-6140] Dish washed 25.
[03:57:29.6700] [main-comp-ctx-6140] O:Dish(25,Clean)
[03:57:29.6700] [main-comp-ctx-6140] Dish washed 26.
[03:57:29.6700] [main-comp-ctx-6140] O:Dish(26,Clean)
[03:57:29.6710] [main-comp-ctx-6140] Dish washed 27.
[03:57:29.6710] [main-comp-ctx-6140] O:Dish(27,Clean)
[03:57:29.6720] [main-comp-ctx-6140] E: 28
[03:57:29.6720] [main-comp-ctx-6140] E: 29
[03:57:29.6720] [main-comp-ctx-6140] E: 30
[03:57:29.6730] [main-comp-ctx-6140] washing dish 28
[03:57:29.8740] [main-comp-ctx-6140] washing dish 29
[03:57:30.0750] [main-comp-ctx-6140] washing dish 30
[03:57:30.2750] [main-comp-ctx-6140] Dish washed 28.
[03:57:30.2750] [main-comp-ctx-6140] O:Dish(28,Clean)
[03:57:30.2760] [main-comp-ctx-6140] Dish washed 29.
[03:57:30.2760] [main-comp-ctx-6140] O:Dish(29,Clean)
[03:57:30.2770] [main-comp-ctx-6140] Dish washed 30.
[03:57:30.2770] [main-comp-ctx-6140] O:Dish(30,Clean)
[03:57:30.2770] [main-comp-ctx-6140] E: 31
[03:57:30.2780] [main-comp-ctx-6140] E: 32
[03:57:30.2780] [main-comp-ctx-6140] E: 33
[03:57:30.2790] [main-comp-ctx-6140] washing dish 31
[03:57:30.4800] [main-comp-ctx-6140] washing dish 32
[03:57:30.6800] [main-comp-ctx-6140] washing dish 33
[03:57:30.8810] [main-comp-ctx-6140] Dish washed 31.
[03:57:30.8810] [main-comp-ctx-6140] O:Dish(31,Clean)
[03:57:30.8810] [main-comp-ctx-6140] Dish washed 32.
[03:57:30.8820] [main-comp-ctx-6140] O:Dish(32,Clean)
[03:57:30.8820] [main-comp-ctx-6140] Dish washed 33.
[03:57:30.8830] [main-comp-ctx-6140] O:Dish(33,Clean)
[03:57:30.8830] [main-comp-ctx-6140] E: 34
[03:57:30.8830] [main-comp-ctx-6140] E: 35
[03:57:30.8840] [main-comp-ctx-6140] E: 36
[03:57:30.8850] [main-comp-ctx-6140] washing dish 34
[03:57:31.0850] [main-comp-ctx-6140] washing dish 35
[03:57:31.2860] [main-comp-ctx-6140] washing dish 36
[03:57:31.4870] [main-comp-ctx-6140] Dish washed 34.
[03:57:31.4870] [main-comp-ctx-6140] O:Dish(34,Clean)
[03:57:31.4870] [main-comp-ctx-6140] Dish washed 35.
[03:57:31.4880] [main-comp-ctx-6140] O:Dish(35,Clean)
[03:57:31.4880] [main-comp-ctx-6140] Dish washed 36.
[03:57:31.4880] [main-comp-ctx-6140] O:Dish(36,Clean)
[03:57:31.4890] [main-comp-ctx-6140] E: 37
[03:57:31.4900] [main-comp-ctx-6140] E: 38
[03:57:31.4900] [main-comp-ctx-6140] E: 39
[03:57:31.4910] [main-comp-ctx-6140] washing dish 37
[03:57:31.6920] [main-comp-ctx-6140] washing dish 38
[03:57:31.8920] [main-comp-ctx-6140] washing dish 39
[03:57:32.0930] [main-comp-ctx-6140] Dish washed 37.
[03:57:32.0930] [main-comp-ctx-6140] O:Dish(37,Clean)
[03:57:32.0930] [main-comp-ctx-6140] Dish washed 38.
[03:57:32.0940] [main-comp-ctx-6140] O:Dish(38,Clean)
[03:57:32.0940] [main-comp-ctx-6140] Dish washed 39.
[03:57:32.0950] [main-comp-ctx-6140] O:Dish(39,Clean)
[03:57:32.0960] [main-comp-ctx-6140] E: 40
[03:57:32.0960] [main-comp-ctx-6140] E: 41
[03:57:32.0960] [main-comp-ctx-6140] E: 42
[03:57:32.0970] [main-comp-ctx-6140] washing dish 40
[03:57:32.2980] [main-comp-ctx-6140] washing dish 41
[03:57:32.4980] [main-comp-ctx-6140] washing dish 42
[03:57:32.6990] [main-comp-ctx-6140] Dish washed 40.
[03:57:32.6990] [main-comp-ctx-6140] O:Dish(40,Clean)
[03:57:32.7000] [main-comp-ctx-6140] Dish washed 41.
[03:57:32.7000] [main-comp-ctx-6140] O:Dish(41,Clean)
[03:57:32.7000] [main-comp-ctx-6140] Dish washed 42.
[03:57:32.7010] [main-comp-ctx-6140] O:Dish(42,Clean)
[03:57:32.7010] [main-comp-ctx-6140] E: 43
[03:57:32.7020] [main-comp-ctx-6140] E: 44
[03:57:32.7020] [main-comp-ctx-6140] E: 45
[03:57:32.7040] [main-comp-ctx-6140] washing dish 43
[03:57:32.9040] [main-comp-ctx-6140] washing dish 44
[03:57:33.1050] [main-comp-ctx-6140] washing dish 45
[03:57:33.3060] [main-comp-ctx-6140] Dish washed 43.
[03:57:33.3060] [main-comp-ctx-6140] O:Dish(43,Clean)
[03:57:33.3060] [main-comp-ctx-6140] Dish washed 44.
[03:57:33.3060] [main-comp-ctx-6140] O:Dish(44,Clean)
[03:57:33.3070] [main-comp-ctx-6140] Dish washed 45.
[03:57:33.3070] [main-comp-ctx-6140] O:Dish(45,Clean)
[03:57:33.3070] [main-comp-ctx-6140] E: 46
[03:57:33.3070] [main-comp-ctx-6140] E: 47
[03:57:33.3080] [main-comp-ctx-6140] E: 48
[03:57:33.3090] [main-comp-ctx-6140] washing dish 46
[03:57:33.5090] [main-comp-ctx-6140] washing dish 47
[03:57:33.7100] [main-comp-ctx-6140] washing dish 48
[03:57:33.9100] [main-comp-ctx-6140] Dish washed 46.
[03:57:33.9110] [main-comp-ctx-6140] O:Dish(46,Clean)
[03:57:33.9110] [main-comp-ctx-6140] Dish washed 47.
[03:57:33.9110] [main-comp-ctx-6140] O:Dish(47,Clean)
[03:57:33.9110] [main-comp-ctx-6140] Dish washed 48.
[03:57:33.9120] [main-comp-ctx-6140] O:Dish(48,Clean)
[03:57:33.9120] [main-comp-ctx-6140] E: 49
[03:57:33.9120] [main-comp-ctx-6140] E: 50
[03:57:33.9130] [main-comp-ctx-6140] E: 51
[03:57:33.9130] [main-comp-ctx-6140] washing dish 49
[03:57:34.1140] [main-comp-ctx-6140] washing dish 50
[03:57:34.3140] [main-comp-ctx-6140] washing dish 51
[03:57:34.5150] [main-comp-ctx-6140] Dish washed 49.
[03:57:34.5150] [main-comp-ctx-6140] O:Dish(49,Clean)
[03:57:34.5160] [main-comp-ctx-6140] Dish washed 50.
[03:57:34.5160] [main-comp-ctx-6140] O:Dish(50,Clean)
[03:57:34.5160] [main-comp-ctx-6140] Dish washed 51.
[03:57:34.5160] [main-comp-ctx-6140] O:Dish(51,Clean)
[03:57:34.5170] [main-comp-ctx-6140] E: 52
[03:57:34.5170] [main-comp-ctx-6140] E: 53
[03:57:34.5170] [main-comp-ctx-6140] E: 54
[03:57:34.5200] [main-comp-ctx-6140] washing dish 52
[03:57:34.7210] [main-comp-ctx-6140] washing dish 53
[03:57:34.9220] [main-comp-ctx-6140] washing dish 54
[03:57:35.1220] [main-comp-ctx-6140] Dish washed 52.
[03:57:35.1230] [main-comp-ctx-6140] O:Dish(52,Clean)
[03:57:35.1230] [main-comp-ctx-6140] Dish washed 53.
[03:57:35.1230] [main-comp-ctx-6140] O:Dish(53,Clean)
[03:57:35.1240] [main-comp-ctx-6140] Dish washed 54.
[03:57:35.1240] [main-comp-ctx-6140] O:Dish(54,Clean)
[03:57:35.1240] [main-comp-ctx-6140] E: 55
[03:57:35.1250] [main-comp-ctx-6140] E: 56
[03:57:35.1250] [main-comp-ctx-6140] E: 57
[03:57:35.1260] [main-comp-ctx-6140] washing dish 55
[03:57:35.3270] [main-comp-ctx-6140] washing dish 56
[03:57:35.5280] [main-comp-ctx-6140] washing dish 57
[03:57:35.7290] [main-comp-ctx-6140] Dish washed 55.
[03:57:35.7290] [main-comp-ctx-6140] O:Dish(55,Clean)
[03:57:35.7300] [main-comp-ctx-6140] Dish washed 56.
[03:57:35.7310] [main-comp-ctx-6140] O:Dish(56,Clean)
[03:57:35.7320] [main-comp-ctx-6140] Dish washed 57.
[03:57:35.7330] [main-comp-ctx-6140] O:Dish(57,Clean)
[03:57:35.7330] [main-comp-ctx-6140] E: 58
[03:57:35.7340] [main-comp-ctx-6140] E: 59
[03:57:35.7350] [main-comp-ctx-6140] E: 60
[03:57:35.7360] [main-comp-ctx-6140] washing dish 58
[03:57:35.9370] [main-comp-ctx-6140] washing dish 59
[03:57:36.1370] [main-comp-ctx-6140] washing dish 60
[03:57:36.3380] [main-comp-ctx-6140] Dish washed 58.
[03:57:36.3380] [main-comp-ctx-6140] O:Dish(58,Clean)
[03:57:36.3390] [main-comp-ctx-6140] Dish washed 59.
[03:57:36.3390] [main-comp-ctx-6140] O:Dish(59,Clean)
[03:57:36.3400] [main-comp-ctx-6140] Dish washed 60.
[03:57:36.3400] [main-comp-ctx-6140] O:Dish(60,Clean)
[03:57:36.3410] [main-comp-ctx-6140] E: 61
[03:57:36.3420] [main-comp-ctx-6140] E: 62
[03:57:36.3430] [main-comp-ctx-6140] E: 63
[03:57:36.3440] [main-comp-ctx-6140] washing dish 61
[03:57:36.5450] [main-comp-ctx-6140] washing dish 62
[03:57:36.7450] [main-comp-ctx-6140] washing dish 63
[03:57:36.9460] [main-comp-ctx-6140] Dish washed 61.
[03:57:36.9460] [main-comp-ctx-6140] O:Dish(61,Clean)
[03:57:36.9470] [main-comp-ctx-6140] Dish washed 62.
[03:57:36.9470] [main-comp-ctx-6140] O:Dish(62,Clean)
[03:57:36.9480] [main-comp-ctx-6140] Dish washed 63.
[03:57:36.9490] [main-comp-ctx-6140] O:Dish(63,Clean)
[03:57:36.9500] [main-comp-ctx-6140] E: 64
[03:57:36.9500] [main-comp-ctx-6140] E: 65
[03:57:36.9510] [main-comp-ctx-6140] E: 66
[03:57:36.9520] [main-comp-ctx-6140] washing dish 64
[03:57:37.1530] [main-comp-ctx-6140] washing dish 65
[03:57:37.3540] [main-comp-ctx-6140] washing dish 66
[03:57:37.5540] [main-comp-ctx-6140] Dish washed 64.
[03:57:37.5550] [main-comp-ctx-6140] O:Dish(64,Clean)
[03:57:37.5550] [main-comp-ctx-6140] Dish washed 65.
[03:57:37.5570] [main-comp-ctx-6140] O:Dish(65,Clean)
[03:57:37.5570] [main-comp-ctx-6140] Dish washed 66.
[03:57:37.5580] [main-comp-ctx-6140] O:Dish(66,Clean)
[03:57:37.5590] [main-comp-ctx-6140] E: 67
[03:57:37.5600] [main-comp-ctx-6140] E: 68
[03:57:37.5610] [main-comp-ctx-6140] E: 69
[03:57:37.5620] [main-comp-ctx-6140] washing dish 67
[03:57:37.7630] [main-comp-ctx-6140] washing dish 68
[03:57:37.9640] [main-comp-ctx-6140] washing dish 69
[03:57:38.1650] [main-comp-ctx-6140] Dish washed 67.
[03:57:38.1650] [main-comp-ctx-6140] O:Dish(67,Clean)
[03:57:38.1660] [main-comp-ctx-6140] Dish washed 68.
[03:57:38.1660] [main-comp-ctx-6140] O:Dish(68,Clean)
[03:57:38.1690] [main-comp-ctx-6140] Dish washed 69.
[03:57:38.1690] [main-comp-ctx-6140] O:Dish(69,Clean)
[03:57:38.1700] [main-comp-ctx-6140] E: 70
[03:57:38.1710] [main-comp-ctx-6140] E: 71
[03:57:38.1720] [main-comp-ctx-6140] E: 72
[03:57:38.1740] [main-comp-ctx-6140] washing dish 70
[03:57:38.3740] [main-comp-ctx-6140] washing dish 71
[03:57:38.5750] [main-comp-ctx-6140] washing dish 72
[03:57:38.7750] [main-comp-ctx-6140] Dish washed 70.
[03:57:38.7760] [main-comp-ctx-6140] O:Dish(70,Clean)
[03:57:38.7760] [main-comp-ctx-6140] Dish washed 71.
[03:57:38.7760] [main-comp-ctx-6140] O:Dish(71,Clean)
[03:57:38.7760] [main-comp-ctx-6140] Dish washed 72.
[03:57:38.7770] [main-comp-ctx-6140] O:Dish(72,Clean)
[03:57:38.7780] [main-comp-ctx-6140] E: 73
[03:57:38.7790] [main-comp-ctx-6140] E: 74
[03:57:38.7790] [main-comp-ctx-6140] E: 75
[03:57:38.7810] [main-comp-ctx-6140] washing dish 73
[03:57:38.9810] [main-comp-ctx-6140] washing dish 74
[03:57:39.1820] [main-comp-ctx-6140] washing dish 75
[03:57:39.3830] [main-comp-ctx-6140] Dish washed 73.
[03:57:39.3830] [main-comp-ctx-6140] O:Dish(73,Clean)
[03:57:39.3840] [main-comp-ctx-6140] Dish washed 74.
[03:57:39.3840] [main-comp-ctx-6140] O:Dish(74,Clean)
[03:57:39.3850] [main-comp-ctx-6140] Dish washed 75.
[03:57:39.3860] [main-comp-ctx-6140] O:Dish(75,Clean)
[03:57:39.3860] [main-comp-ctx-6140] E: 76
[03:57:39.3870] [main-comp-ctx-6140] E: 77
[03:57:39.3880] [main-comp-ctx-6140] E: 78
[03:57:39.3900] [main-comp-ctx-6140] washing dish 76
[03:57:39.5900] [main-comp-ctx-6140] washing dish 77
[03:57:39.7910] [main-comp-ctx-6140] washing dish 78
[03:57:39.9910] [main-comp-ctx-6140] Dish washed 76.
[03:57:39.9920] [main-comp-ctx-6140] O:Dish(76,Clean)
[03:57:39.9930] [main-comp-ctx-6140] Dish washed 77.
[03:57:39.9940] [main-comp-ctx-6140] O:Dish(77,Clean)
[03:57:39.9950] [main-comp-ctx-6140] Dish washed 78.
[03:57:39.9960] [main-comp-ctx-6140] O:Dish(78,Clean)
[03:57:39.9960] [main-comp-ctx-6140] E: 79
[03:57:39.9990] [main-comp-ctx-6140] E: 80
[03:57:39.9990] [main-comp-ctx-6140] E: 81
[03:57:40.0000] [main-comp-ctx-6140] washing dish 79
[03:57:40.2010] [main-comp-ctx-6140] washing dish 80
[03:57:40.4020] [main-comp-ctx-6140] washing dish 81
[03:57:40.6020] [main-comp-ctx-6140] Dish washed 79.
[03:57:40.6020] [main-comp-ctx-6140] O:Dish(79,Clean)
[03:57:40.6030] [main-comp-ctx-6140] Dish washed 80.
[03:57:40.6030] [main-comp-ctx-6140] O:Dish(80,Clean)
[03:57:40.6030] [main-comp-ctx-6140] Dish washed 81.
[03:57:40.6040] [main-comp-ctx-6140] O:Dish(81,Clean)
[03:57:40.6040] [main-comp-ctx-6140] E: 82
[03:57:40.6040] [main-comp-ctx-6140] E: 83
[03:57:40.6050] [main-comp-ctx-6140] E: 84
[03:57:40.6060] [main-comp-ctx-6140] washing dish 82
[03:57:40.8060] [main-comp-ctx-6140] washing dish 83
[03:57:41.0070] [main-comp-ctx-6140] washing dish 84
[03:57:41.2070] [main-comp-ctx-6140] Dish washed 82.
[03:57:41.2080] [main-comp-ctx-6140] O:Dish(82,Clean)
[03:57:41.2080] [main-comp-ctx-6140] Dish washed 83.
[03:57:41.2080] [main-comp-ctx-6140] O:Dish(83,Clean)
[03:57:41.2080] [main-comp-ctx-6140] Dish washed 84.
[03:57:41.2090] [main-comp-ctx-6140] O:Dish(84,Clean)
[03:57:41.2100] [main-comp-ctx-6140] E: 85
[03:57:41.2100] [main-comp-ctx-6140] E: 86
[03:57:41.2110] [main-comp-ctx-6140] E: 87
[03:57:41.2130] [main-comp-ctx-6140] washing dish 85
[03:57:41.4130] [main-comp-ctx-6140] washing dish 86
[03:57:41.6140] [main-comp-ctx-6140] washing dish 87
[03:57:41.8140] [main-comp-ctx-6140] Dish washed 85.
[03:57:41.8150] [main-comp-ctx-6140] O:Dish(85,Clean)
[03:57:41.8150] [main-comp-ctx-6140] Dish washed 86.
[03:57:41.8150] [main-comp-ctx-6140] O:Dish(86,Clean)
[03:57:41.8160] [main-comp-ctx-6140] Dish washed 87.
[03:57:41.8160] [main-comp-ctx-6140] O:Dish(87,Clean)
[03:57:41.8170] [main-comp-ctx-6140] E: 88
[03:57:41.8170] [main-comp-ctx-6140] E: 89
[03:57:41.8180] [main-comp-ctx-6140] E: 90
[03:57:41.8190] [main-comp-ctx-6140] washing dish 88
[03:57:42.0200] [main-comp-ctx-6140] washing dish 89
[03:57:42.2210] [main-comp-ctx-6140] washing dish 90
[03:57:42.4210] [main-comp-ctx-6140] Dish washed 88.
[03:57:42.4210] [main-comp-ctx-6140] O:Dish(88,Clean)
[03:57:42.4220] [main-comp-ctx-6140] Dish washed 89.
[03:57:42.4220] [main-comp-ctx-6140] O:Dish(89,Clean)
[03:57:42.4220] [main-comp-ctx-6140] Dish washed 90.
[03:57:42.4220] [main-comp-ctx-6140] O:Dish(90,Clean)
[03:57:42.4230] [main-comp-ctx-6140] E: 91
[03:57:42.4230] [main-comp-ctx-6140] E: 92
[03:57:42.4230] [main-comp-ctx-6140] E: 93
[03:57:42.4240] [main-comp-ctx-6140] washing dish 91
[03:57:42.6240] [main-comp-ctx-6140] washing dish 92
[03:57:42.8250] [main-comp-ctx-6140] washing dish 93
[03:57:43.0260] [main-comp-ctx-6140] Dish washed 91.
[03:57:43.0260] [main-comp-ctx-6140] O:Dish(91,Clean)
[03:57:43.0270] [main-comp-ctx-6140] Dish washed 92.
[03:57:43.0270] [main-comp-ctx-6140] O:Dish(92,Clean)
[03:57:43.0270] [main-comp-ctx-6140] Dish washed 93.
[03:57:43.0280] [main-comp-ctx-6140] O:Dish(93,Clean)
[03:57:43.0280] [main-comp-ctx-6140] E: 94
[03:57:43.0290] [main-comp-ctx-6140] E: 95
[03:57:43.0290] [main-comp-ctx-6140] E: 96
[03:57:43.0300] [main-comp-ctx-6140] washing dish 94
[03:57:43.2300] [main-comp-ctx-6140] washing dish 95
[03:57:43.4310] [main-comp-ctx-6140] washing dish 96
[03:57:43.6320] [main-comp-ctx-6140] Dish washed 94.
[03:57:43.6320] [main-comp-ctx-6140] O:Dish(94,Clean)
[03:57:43.6320] [main-comp-ctx-6140] Dish washed 95.
[03:57:43.6330] [main-comp-ctx-6140] O:Dish(95,Clean)
[03:57:43.6330] [main-comp-ctx-6140] Dish washed 96.
[03:57:43.6330] [main-comp-ctx-6140] O:Dish(96,Clean)
[03:57:43.6340] [main-comp-ctx-6140] E: 97
[03:57:43.6340] [main-comp-ctx-6140] E: 98
[03:57:43.6350] [main-comp-ctx-6140] E: 99
[03:57:43.6360] [main-comp-ctx-6140] washing dish 97
[03:57:43.8370] [main-comp-ctx-6140] washing dish 98
[03:57:44.0370] [main-comp-ctx-6140] washing dish 99
[03:57:44.2380] [main-comp-ctx-6140] Dish washed 97.
[03:57:44.2380] [main-comp-ctx-6140] O:Dish(97,Clean)
[03:57:44.2390] [main-comp-ctx-6140] Dish washed 98.
[03:57:44.2390] [main-comp-ctx-6140] O:Dish(98,Clean)
[03:57:44.2390] [main-comp-ctx-6140] Dish washed 99.
[03:57:44.2400] [main-comp-ctx-6140] O:Dish(99,Clean)
[03:57:44.2400] [main-comp-ctx-6140] E: 100
[03:57:44.2410] [main-comp-ctx-6140] washing dish 100
[03:57:44.4420] [main-comp-ctx-6140] Dish washed 100.
[03:57:44.4420] [main-comp-ctx-6140] O:Dish(100,Clean)
*/
