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
      Ack.Continue21.4
    }
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 2))

Thread.sleep(30000)
println(s"$log Program Ends.")


/* Output
[04:05:18.5840] [run-main-0] Program starts.
[04:05:21.4080] [run-main-0] E: 1
[04:05:21.4600] [run-main-0] E: 2
[04:05:21.4600] [run-main-0] E: 3
[04:05:21.4940] [main-comp-ctx-140] washing dish 1
[04:05:21.4940] [main-comp-ctx-141] washing dish 2
[04:05:21.5140] [run-main-0] E: 4
[04:05:21.5150] [run-main-0] E: 5
[04:05:21.5150] [run-main-0] E: 6
[04:05:21.7050] [main-comp-ctx-140] washing dish 3
[04:05:21.7060] [main-comp-ctx-141] Dish washed 1.
[04:05:21.7070] [main-comp-ctx-141] O:Dish(1,Clean)
[04:05:21.7100] [main-comp-ctx-141] Dish washed 2.
[04:05:21.7100] [main-comp-ctx-141] O:Dish(2,Clean)
[04:05:21.7110] [main-comp-ctx-141] washing dish 4
[04:05:21.9050] [main-comp-ctx-140] Dish washed 3.
[04:05:21.9060] [main-comp-ctx-140] O:Dish(3,Clean)
[04:05:21.9060] [main-comp-ctx-140] washing dish 5
[04:05:21.9120] [main-comp-ctx-141] washing dish 6
[04:05:22.1070] [main-comp-ctx-140] E: 7
[04:05:22.1080] [main-comp-ctx-140] E: 8
[04:05:22.1080] [main-comp-ctx-140] E: 9
[04:05:22.1090] [main-comp-ctx-140] washing dish 7
[04:05:22.1130] [main-comp-ctx-141] Dish washed 4.
[04:05:22.1130] [main-comp-ctx-141] O:Dish(4,Clean)
[04:05:22.1140] [main-comp-ctx-141] Dish washed 5.
[04:05:22.1140] [main-comp-ctx-141] O:Dish(5,Clean)
[04:05:22.1140] [main-comp-ctx-141] Dish washed 6.
[04:05:22.1150] [main-comp-ctx-141] O:Dish(6,Clean)
[04:05:22.1150] [main-comp-ctx-141] washing dish 8
[04:05:22.3100] [main-comp-ctx-140] washing dish 9
[04:05:22.3160] [main-comp-ctx-141] E: 10
[04:05:22.3160] [main-comp-ctx-141] E: 11
[04:05:22.3170] [main-comp-ctx-141] E: 12
[04:05:22.3180] [main-comp-ctx-141] washing dish 10
[04:05:22.5110] [main-comp-ctx-140] Dish washed 7.
[04:05:22.5110] [main-comp-ctx-140] O:Dish(7,Clean)
[04:05:22.5120] [main-comp-ctx-140] Dish washed 8.
[04:05:22.5120] [main-comp-ctx-140] O:Dish(8,Clean)
[04:05:22.5120] [main-comp-ctx-140] Dish washed 9.
[04:05:22.5120] [main-comp-ctx-140] O:Dish(9,Clean)
[04:05:22.5130] [main-comp-ctx-140] washing dish 11
[04:05:22.5180] [main-comp-ctx-141] washing dish 12
[04:05:22.7140] [main-comp-ctx-140] E: 13
[04:05:22.7140] [main-comp-ctx-140] E: 14
[04:05:22.7150] [main-comp-ctx-140] E: 15
[04:05:22.7160] [main-comp-ctx-140] washing dish 13
[04:05:22.7190] [main-comp-ctx-141] Dish washed 10.
[04:05:22.7190] [main-comp-ctx-141] O:Dish(10,Clean)
[04:05:22.7200] [main-comp-ctx-141] Dish washed 11.
[04:05:22.7210] [main-comp-ctx-141] O:Dish(11,Clean)
[04:05:22.7220] [main-comp-ctx-141] Dish washed 12.
[04:05:22.7220] [main-comp-ctx-141] O:Dish(12,Clean)
[04:05:22.7230] [main-comp-ctx-141] washing dish 14
[04:05:22.9170] [main-comp-ctx-140] washing dish 15
[04:05:22.9240] [main-comp-ctx-141] E: 16
[04:05:22.9240] [main-comp-ctx-141] E: 17
[04:05:22.9250] [main-comp-ctx-141] E: 18
[04:05:22.9250] [main-comp-ctx-141] washing dish 16
[04:05:23.1170] [main-comp-ctx-140] Dish washed 13.
[04:05:23.1180] [main-comp-ctx-140] O:Dish(13,Clean)
[04:05:23.1180] [main-comp-ctx-140] Dish washed 14.
[04:05:23.1190] [main-comp-ctx-140] O:Dish(14,Clean)
[04:05:23.1190] [main-comp-ctx-140] Dish washed 15.
[04:05:23.1190] [main-comp-ctx-140] O:Dish(15,Clean)
[04:05:23.1200] [main-comp-ctx-140] washing dish 17
[04:05:23.1260] [main-comp-ctx-141] washing dish 18
[04:05:23.3210] [main-comp-ctx-140] E: 19
[04:05:23.3210] [main-comp-ctx-140] E: 20
[04:05:23.3210] [main-comp-ctx-140] E: 21
[04:05:23.3220] [main-comp-ctx-140] washing dish 19
[04:05:23.3270] [main-comp-ctx-141] Dish washed 16.
[04:05:23.3270] [main-comp-ctx-141] O:Dish(16,Clean)
[04:05:23.3280] [main-comp-ctx-141] Dish washed 17.
[04:05:23.3290] [main-comp-ctx-141] O:Dish(17,Clean)
[04:05:23.3300] [main-comp-ctx-141] Dish washed 18.
[04:05:23.3300] [main-comp-ctx-141] O:Dish(18,Clean)
[04:05:23.3310] [main-comp-ctx-141] washing dish 20
[04:05:23.5230] [main-comp-ctx-140] washing dish 21
[04:05:23.5310] [main-comp-ctx-141] E: 22
[04:05:23.5320] [main-comp-ctx-141] E: 23
[04:05:23.5320] [main-comp-ctx-141] E: 24
[04:05:23.5330] [main-comp-ctx-141] washing dish 22
[04:05:23.7240] [main-comp-ctx-140] Dish washed 19.
[04:05:23.7240] [main-comp-ctx-140] O:Dish(19,Clean)
[04:05:23.7240] [main-comp-ctx-140] Dish washed 20.
[04:05:23.7240] [main-comp-ctx-140] O:Dish(20,Clean)
[04:05:23.7250] [main-comp-ctx-140] Dish washed 21.
[04:05:23.7250] [main-comp-ctx-140] O:Dish(21,Clean)
[04:05:23.7260] [main-comp-ctx-140] washing dish 23
[04:05:23.7340] [main-comp-ctx-141] washing dish 24
[04:05:23.9260] [main-comp-ctx-140] E: 25
[04:05:23.9270] [main-comp-ctx-140] E: 26
[04:05:23.9270] [main-comp-ctx-140] E: 27
[04:05:23.9280] [main-comp-ctx-140] washing dish 25
[04:05:23.9350] [main-comp-ctx-141] Dish washed 22.
[04:05:23.9350] [main-comp-ctx-141] O:Dish(22,Clean)
[04:05:23.9360] [main-comp-ctx-141] Dish washed 23.
[04:05:23.9360] [main-comp-ctx-141] O:Dish(23,Clean)
[04:05:23.9370] [main-comp-ctx-141] Dish washed 24.
[04:05:23.9370] [main-comp-ctx-141] O:Dish(24,Clean)
[04:05:23.9380] [main-comp-ctx-141] washing dish 26
[04:05:24.1290] [main-comp-ctx-140] washing dish 27
[04:05:24.1390] [main-comp-ctx-141] E: 28
[04:05:24.1390] [main-comp-ctx-141] E: 29
[04:05:24.1400] [main-comp-ctx-141] E: 30
[04:05:24.1420] [main-comp-ctx-141] washing dish 28
[04:05:24.3290] [main-comp-ctx-140] Dish washed 25.
[04:05:24.3300] [main-comp-ctx-140] O:Dish(25,Clean)
[04:05:24.3300] [main-comp-ctx-140] Dish washed 26.
[04:05:24.3300] [main-comp-ctx-140] O:Dish(26,Clean)
[04:05:24.3310] [main-comp-ctx-140] Dish washed 27.
[04:05:24.3310] [main-comp-ctx-140] O:Dish(27,Clean)
[04:05:24.3320] [main-comp-ctx-140] washing dish 29
[04:05:24.3430] [main-comp-ctx-141] washing dish 30
[04:05:24.5320] [main-comp-ctx-140] E: 31
[04:05:24.5330] [main-comp-ctx-140] E: 32
[04:05:24.5330] [main-comp-ctx-140] E: 33
[04:05:24.5340] [main-comp-ctx-140] washing dish 31
[04:05:24.5440] [main-comp-ctx-141] Dish washed 28.
[04:05:24.5440] [main-comp-ctx-141] O:Dish(28,Clean)
[04:05:24.5450] [main-comp-ctx-141] Dish washed 29.
[04:05:24.5460] [main-comp-ctx-141] O:Dish(29,Clean)
[04:05:24.5470] [main-comp-ctx-141] Dish washed 30.
[04:05:24.5480] [main-comp-ctx-141] O:Dish(30,Clean)
[04:05:24.5480] [main-comp-ctx-141] washing dish 32
[04:05:24.7340] [main-comp-ctx-140] washing dish 33
[04:05:24.7490] [main-comp-ctx-141] E: 34
[04:05:24.7500] [main-comp-ctx-141] E: 35
[04:05:24.7500] [main-comp-ctx-141] E: 36
[04:05:24.7510] [main-comp-ctx-141] washing dish 34
[04:05:24.9350] [main-comp-ctx-140] Dish washed 31.
[04:05:24.9350] [main-comp-ctx-140] O:Dish(31,Clean)
[04:05:24.9360] [main-comp-ctx-140] Dish washed 32.
[04:05:24.9360] [main-comp-ctx-140] O:Dish(32,Clean)
[04:05:24.9370] [main-comp-ctx-140] Dish washed 33.
[04:05:24.9370] [main-comp-ctx-140] O:Dish(33,Clean)
[04:05:24.9380] [main-comp-ctx-140] washing dish 35
[04:05:24.9520] [main-comp-ctx-141] washing dish 36
[04:05:25.1390] [main-comp-ctx-140] E: 37
[04:05:25.1400] [main-comp-ctx-140] E: 38
[04:05:25.1400] [main-comp-ctx-140] E: 39
[04:05:25.1410] [main-comp-ctx-140] washing dish 37
[04:05:25.1530] [main-comp-ctx-141] Dish washed 34.
[04:05:25.1530] [main-comp-ctx-141] O:Dish(34,Clean)
[04:05:25.1540] [main-comp-ctx-141] Dish washed 35.
[04:05:25.1540] [main-comp-ctx-141] O:Dish(35,Clean)
[04:05:25.1540] [main-comp-ctx-141] Dish washed 36.
[04:05:25.1550] [main-comp-ctx-141] O:Dish(36,Clean)
[04:05:25.1550] [main-comp-ctx-141] washing dish 38
[04:05:25.3420] [main-comp-ctx-140] washing dish 39
[04:05:25.3560] [main-comp-ctx-141] E: 40
[04:05:25.3570] [main-comp-ctx-141] E: 41
[04:05:25.3570] [main-comp-ctx-141] E: 42
[04:05:25.3580] [main-comp-ctx-141] washing dish 40
[04:05:25.5430] [main-comp-ctx-140] Dish washed 37.
[04:05:25.5430] [main-comp-ctx-140] O:Dish(37,Clean)
[04:05:25.5440] [main-comp-ctx-140] Dish washed 38.
[04:05:25.5440] [main-comp-ctx-140] O:Dish(38,Clean)
[04:05:25.5440] [main-comp-ctx-140] Dish washed 39.
[04:05:25.5440] [main-comp-ctx-140] O:Dish(39,Clean)
[04:05:25.5450] [main-comp-ctx-140] washing dish 41
[04:05:25.5590] [main-comp-ctx-141] washing dish 42
[04:05:25.7450] [main-comp-ctx-140] E: 43
[04:05:25.7470] [main-comp-ctx-140] E: 44
[04:05:25.7470] [main-comp-ctx-140] E: 45
[04:05:25.7500] [main-comp-ctx-140] washing dish 43
[04:05:25.7600] [main-comp-ctx-141] Dish washed 40.
[04:05:25.7610] [main-comp-ctx-141] O:Dish(40,Clean)
[04:05:25.7610] [main-comp-ctx-141] Dish washed 41.
[04:05:25.7610] [main-comp-ctx-141] O:Dish(41,Clean)
[04:05:25.7610] [main-comp-ctx-141] Dish washed 42.
[04:05:25.7620] [main-comp-ctx-141] O:Dish(42,Clean)
[04:05:25.7620] [main-comp-ctx-141] washing dish 44
[04:05:25.9510] [main-comp-ctx-140] washing dish 45
[04:05:25.9630] [main-comp-ctx-141] E: 46
[04:05:25.9640] [main-comp-ctx-141] E: 47
[04:05:25.9640] [main-comp-ctx-141] E: 48
[04:05:25.9680] [main-comp-ctx-141] washing dish 46
[04:05:26.1520] [main-comp-ctx-140] Dish washed 43.
[04:05:26.1520] [main-comp-ctx-140] O:Dish(43,Clean)
[04:05:26.1520] [main-comp-ctx-140] Dish washed 44.
[04:05:26.1530] [main-comp-ctx-140] O:Dish(44,Clean)
[04:05:26.1540] [main-comp-ctx-140] Dish washed 45.
[04:05:26.1550] [main-comp-ctx-140] O:Dish(45,Clean)
[04:05:26.1560] [main-comp-ctx-140] washing dish 47
[04:05:26.1680] [main-comp-ctx-141] washing dish 48
[04:05:26.3580] [main-comp-ctx-140] E: 49
[04:05:26.3580] [main-comp-ctx-140] E: 50
[04:05:26.3590] [main-comp-ctx-140] E: 51
[04:05:26.3620] [main-comp-ctx-140] washing dish 49
[04:05:26.3690] [main-comp-ctx-141] Dish washed 46.
[04:05:26.3690] [main-comp-ctx-141] O:Dish(46,Clean)
[04:05:26.3690] [main-comp-ctx-141] Dish washed 47.
[04:05:26.3690] [main-comp-ctx-141] O:Dish(47,Clean)
[04:05:26.3700] [main-comp-ctx-141] Dish washed 48.
[04:05:26.3700] [main-comp-ctx-141] O:Dish(48,Clean)
[04:05:26.3700] [main-comp-ctx-141] washing dish 50
[04:05:26.5620] [main-comp-ctx-140] washing dish 51
[04:05:26.5710] [main-comp-ctx-141] E: 52
[04:05:26.5720] [main-comp-ctx-141] E: 53
[04:05:26.5720] [main-comp-ctx-141] E: 54
[04:05:26.5740] [main-comp-ctx-141] washing dish 52
[04:05:26.7630] [main-comp-ctx-140] Dish washed 49.
[04:05:26.7640] [main-comp-ctx-140] O:Dish(49,Clean)
[04:05:26.7650] [main-comp-ctx-140] Dish washed 50.
[04:05:26.7650] [main-comp-ctx-140] O:Dish(50,Clean)
[04:05:26.7660] [main-comp-ctx-140] Dish washed 51.
[04:05:26.7670] [main-comp-ctx-140] O:Dish(51,Clean)
[04:05:26.7670] [main-comp-ctx-140] washing dish 53
[04:05:26.7740] [main-comp-ctx-141] washing dish 54
[04:05:26.9680] [main-comp-ctx-140] E: 55
[04:05:26.9690] [main-comp-ctx-140] E: 56
[04:05:26.9690] [main-comp-ctx-140] E: 57
[04:05:26.9700] [main-comp-ctx-140] washing dish 55
[04:05:26.9750] [main-comp-ctx-141] Dish washed 52.
[04:05:26.9750] [main-comp-ctx-141] O:Dish(52,Clean)
[04:05:26.9760] [main-comp-ctx-141] Dish washed 53.
[04:05:26.9770] [main-comp-ctx-141] O:Dish(53,Clean)
[04:05:26.9770] [main-comp-ctx-141] Dish washed 54.
[04:05:26.9780] [main-comp-ctx-141] O:Dish(54,Clean)
[04:05:26.9780] [main-comp-ctx-141] washing dish 56
[04:05:27.1710] [main-comp-ctx-140] washing dish 57
[04:05:27.1790] [main-comp-ctx-141] E: 58
[04:05:27.1790] [main-comp-ctx-141] E: 59
[04:05:27.1800] [main-comp-ctx-141] E: 60
[04:05:27.1810] [main-comp-ctx-141] washing dish 58
[04:05:27.3720] [main-comp-ctx-140] Dish washed 55.
[04:05:27.3720] [main-comp-ctx-140] O:Dish(55,Clean)
[04:05:27.3730] [main-comp-ctx-140] Dish washed 56.
[04:05:27.3730] [main-comp-ctx-140] O:Dish(56,Clean)
[04:05:27.3740] [main-comp-ctx-140] Dish washed 57.
[04:05:27.3740] [main-comp-ctx-140] O:Dish(57,Clean)
[04:05:27.3750] [main-comp-ctx-140] washing dish 59
[04:05:27.3820] [main-comp-ctx-141] washing dish 60
[04:05:27.5750] [main-comp-ctx-140] E: 61
[04:05:27.5760] [main-comp-ctx-140] E: 62
[04:05:27.5770] [main-comp-ctx-140] E: 63
[04:05:27.5780] [main-comp-ctx-140] washing dish 61
[04:05:27.5830] [main-comp-ctx-141] Dish washed 58.
[04:05:27.5830] [main-comp-ctx-141] O:Dish(58,Clean)
[04:05:27.5830] [main-comp-ctx-141] Dish washed 59.
[04:05:27.5830] [main-comp-ctx-141] O:Dish(59,Clean)
[04:05:27.5840] [main-comp-ctx-141] Dish washed 60.
[04:05:27.5840] [main-comp-ctx-141] O:Dish(60,Clean)
[04:05:27.5840] [main-comp-ctx-141] washing dish 62
[04:05:27.7780] [main-comp-ctx-140] washing dish 63
[04:05:27.7850] [main-comp-ctx-141] E: 64
[04:05:27.7850] [main-comp-ctx-141] E: 65
[04:05:27.7860] [main-comp-ctx-141] E: 66
[04:05:27.7870] [main-comp-ctx-141] washing dish 64
[04:05:27.9790] [main-comp-ctx-140] Dish washed 61.
[04:05:27.9800] [main-comp-ctx-140] O:Dish(61,Clean)
[04:05:27.9810] [main-comp-ctx-140] Dish washed 62.
[04:05:27.9810] [main-comp-ctx-140] O:Dish(62,Clean)
[04:05:27.9820] [main-comp-ctx-140] Dish washed 63.
[04:05:27.9830] [main-comp-ctx-140] O:Dish(63,Clean)
[04:05:27.9840] [main-comp-ctx-140] washing dish 65
[04:05:27.9880] [main-comp-ctx-141] washing dish 66
[04:05:28.1860] [main-comp-ctx-140] E: 67
[04:05:28.1870] [main-comp-ctx-140] E: 68
[04:05:28.1870] [main-comp-ctx-140] E: 69
[04:05:28.1890] [main-comp-ctx-140] washing dish 67
[04:05:28.1890] [main-comp-ctx-141] Dish washed 64.
[04:05:28.1900] [main-comp-ctx-141] O:Dish(64,Clean)
[04:05:28.1900] [main-comp-ctx-141] Dish washed 65.
[04:05:28.1920] [main-comp-ctx-141] O:Dish(65,Clean)
[04:05:28.1940] [main-comp-ctx-141] Dish washed 66.
[04:05:28.1950] [main-comp-ctx-141] O:Dish(66,Clean)
[04:05:28.1960] [main-comp-ctx-141] washing dish 68
[04:05:28.3890] [main-comp-ctx-140] washing dish 69
[04:05:28.3970] [main-comp-ctx-141] E: 70
[04:05:28.3970] [main-comp-ctx-141] E: 71
[04:05:28.3980] [main-comp-ctx-141] E: 72
[04:05:28.4000] [main-comp-ctx-141] washing dish 70
[04:05:28.5900] [main-comp-ctx-140] Dish washed 67.
[04:05:28.5900] [main-comp-ctx-140] O:Dish(67,Clean)
[04:05:28.5910] [main-comp-ctx-140] Dish washed 68.
[04:05:28.5910] [main-comp-ctx-140] O:Dish(68,Clean)
[04:05:28.5920] [main-comp-ctx-140] Dish washed 69.
[04:05:28.5930] [main-comp-ctx-140] O:Dish(69,Clean)
[04:05:28.5940] [main-comp-ctx-140] washing dish 71
[04:05:28.6000] [main-comp-ctx-141] washing dish 72
[04:05:28.7940] [main-comp-ctx-140] E: 73
[04:05:28.7950] [main-comp-ctx-140] E: 74
[04:05:28.7960] [main-comp-ctx-140] E: 75
[04:05:28.7980] [main-comp-ctx-140] washing dish 73
[04:05:28.8010] [main-comp-ctx-141] Dish washed 70.
[04:05:28.8020] [main-comp-ctx-141] O:Dish(70,Clean)
[04:05:28.8030] [main-comp-ctx-141] Dish washed 71.
[04:05:28.8030] [main-comp-ctx-141] O:Dish(71,Clean)
[04:05:28.8040] [main-comp-ctx-141] Dish washed 72.
[04:05:28.8050] [main-comp-ctx-141] O:Dish(72,Clean)
[04:05:28.8060] [main-comp-ctx-141] washing dish 74
[04:05:28.9990] [main-comp-ctx-140] washing dish 75
[04:05:29.0060] [main-comp-ctx-141] E: 76
[04:05:29.0070] [main-comp-ctx-141] E: 77
[04:05:29.0070] [main-comp-ctx-141] E: 78
[04:05:29.0090] [main-comp-ctx-141] washing dish 76
[04:05:29.2000] [main-comp-ctx-140] Dish washed 73.
[04:05:29.2000] [main-comp-ctx-140] O:Dish(73,Clean)
[04:05:29.2010] [main-comp-ctx-140] Dish washed 74.
[04:05:29.2010] [main-comp-ctx-140] O:Dish(74,Clean)
[04:05:29.2010] [main-comp-ctx-140] Dish washed 75.
[04:05:29.2020] [main-comp-ctx-140] O:Dish(75,Clean)
[04:05:29.2040] [main-comp-ctx-140] washing dish 77
[04:05:29.2100] [main-comp-ctx-141] washing dish 78
[04:05:29.4040] [main-comp-ctx-140] E: 79
[04:05:29.4050] [main-comp-ctx-140] E: 80
[04:05:29.4060] [main-comp-ctx-140] E: 81
[04:05:29.4070] [main-comp-ctx-140] washing dish 79
[04:05:29.4110] [main-comp-ctx-141] Dish washed 76.
[04:05:29.4110] [main-comp-ctx-141] O:Dish(76,Clean)
[04:05:29.4120] [main-comp-ctx-141] Dish washed 77.
[04:05:29.4130] [main-comp-ctx-141] O:Dish(77,Clean)
[04:05:29.4130] [main-comp-ctx-141] Dish washed 78.
[04:05:29.4140] [main-comp-ctx-141] O:Dish(78,Clean)
[04:05:29.4140] [main-comp-ctx-141] washing dish 80
[04:05:29.6080] [main-comp-ctx-140] washing dish 81
[04:05:29.6160] [main-comp-ctx-141] E: 82
[04:05:29.6160] [main-comp-ctx-141] E: 83
[04:05:29.6170] [main-comp-ctx-141] E: 84
[04:05:29.6180] [main-comp-ctx-141] washing dish 82
[04:05:29.8090] [main-comp-ctx-140] Dish washed 79.
[04:05:29.8090] [main-comp-ctx-140] O:Dish(79,Clean)
[04:05:29.8090] [main-comp-ctx-140] Dish washed 80.
[04:05:29.8100] [main-comp-ctx-140] O:Dish(80,Clean)
[04:05:29.8100] [main-comp-ctx-140] Dish washed 81.
[04:05:29.8100] [main-comp-ctx-140] O:Dish(81,Clean)
[04:05:29.8110] [main-comp-ctx-140] washing dish 83
[04:05:29.8190] [main-comp-ctx-141] washing dish 84
[04:05:30.0120] [main-comp-ctx-140] E: 85
[04:05:30.0120] [main-comp-ctx-140] E: 86
[04:05:30.0130] [main-comp-ctx-140] E: 87
[04:05:30.0130] [main-comp-ctx-140] washing dish 85
[04:05:30.0190] [main-comp-ctx-141] Dish washed 82.
[04:05:30.0200] [main-comp-ctx-141] O:Dish(82,Clean)
[04:05:30.0200] [main-comp-ctx-141] Dish washed 83.
[04:05:30.0200] [main-comp-ctx-141] O:Dish(83,Clean)
[04:05:30.0210] [main-comp-ctx-141] Dish washed 84.
[04:05:30.0210] [main-comp-ctx-141] O:Dish(84,Clean)
[04:05:30.0210] [main-comp-ctx-141] washing dish 86
[04:05:30.2140] [main-comp-ctx-140] washing dish 87
[04:05:30.2220] [main-comp-ctx-141] E: 88
[04:05:30.2220] [main-comp-ctx-141] E: 89
[04:05:30.2220] [main-comp-ctx-141] E: 90
[04:05:30.2230] [main-comp-ctx-141] washing dish 88
[04:05:30.4150] [main-comp-ctx-140] Dish washed 85.
[04:05:30.4150] [main-comp-ctx-140] O:Dish(85,Clean)
[04:05:30.4160] [main-comp-ctx-140] Dish washed 86.
[04:05:30.4160] [main-comp-ctx-140] O:Dish(86,Clean)
[04:05:30.4160] [main-comp-ctx-140] Dish washed 87.
[04:05:30.4170] [main-comp-ctx-140] O:Dish(87,Clean)
[04:05:30.4170] [main-comp-ctx-140] washing dish 89
[04:05:30.4240] [main-comp-ctx-141] washing dish 90
[04:05:30.6180] [main-comp-ctx-140] E: 91
[04:05:30.6180] [main-comp-ctx-140] E: 92
[04:05:30.6180] [main-comp-ctx-140] E: 93
[04:05:30.6190] [main-comp-ctx-140] washing dish 91
[04:05:30.6250] [main-comp-ctx-141] Dish washed 88.
[04:05:30.6250] [main-comp-ctx-141] O:Dish(88,Clean)
[04:05:30.6250] [main-comp-ctx-141] Dish washed 89.
[04:05:30.6260] [main-comp-ctx-141] O:Dish(89,Clean)
[04:05:30.6260] [main-comp-ctx-141] Dish washed 90.
[04:05:30.6260] [main-comp-ctx-141] O:Dish(90,Clean)
[04:05:30.6260] [main-comp-ctx-141] washing dish 92
[04:05:30.8200] [main-comp-ctx-140] washing dish 93
[04:05:30.8270] [main-comp-ctx-141] E: 94
[04:05:30.8280] [main-comp-ctx-141] E: 95
[04:05:30.8280] [main-comp-ctx-141] E: 96
[04:05:30.8290] [main-comp-ctx-141] washing dish 94
[04:05:31.0200] [main-comp-ctx-140] Dish washed 91.
[04:05:31.0200] [main-comp-ctx-140] O:Dish(91,Clean)
[04:05:31.0210] [main-comp-ctx-140] Dish washed 92.
[04:05:31.0210] [main-comp-ctx-140] O:Dish(92,Clean)
[04:05:31.0210] [main-comp-ctx-140] Dish washed 93.
[04:05:31.0220] [main-comp-ctx-140] O:Dish(93,Clean)
[04:05:31.0220] [main-comp-ctx-140] washing dish 95
[04:05:31.0290] [main-comp-ctx-141] washing dish 96
[04:05:31.2230] [main-comp-ctx-140] E: 97
[04:05:31.2230] [main-comp-ctx-140] E: 98
[04:05:31.2240] [main-comp-ctx-140] E: 99
[04:05:31.2250] [main-comp-ctx-140] washing dish 97
[04:05:31.2300] [main-comp-ctx-141] Dish washed 94.
[04:05:31.2300] [main-comp-ctx-141] O:Dish(94,Clean)
[04:05:31.2310] [main-comp-ctx-141] Dish washed 95.
[04:05:31.2310] [main-comp-ctx-141] O:Dish(95,Clean)
[04:05:31.2310] [main-comp-ctx-141] Dish washed 96.
[04:05:31.2310] [main-comp-ctx-141] O:Dish(96,Clean)
[04:05:31.2320] [main-comp-ctx-141] washing dish 98
[04:05:31.4260] [main-comp-ctx-140] washing dish 99
[04:05:31.4330] [main-comp-ctx-141] E: 100
[04:05:31.4470] [main-comp-ctx-141] washing dish 100
[04:05:31.6270] [main-comp-ctx-140] Dish washed 97.
[04:05:31.6270] [main-comp-ctx-140] O:Dish(97,Clean)
[04:05:31.6280] [main-comp-ctx-140] Dish washed 98.
[04:05:31.6280] [main-comp-ctx-140] O:Dish(98,Clean)
[04:05:31.6280] [main-comp-ctx-140] Dish washed 99.
[04:05:31.6280] [main-comp-ctx-140] O:Dish(99,Clean)
[04:05:31.6470] [main-comp-ctx-140] Dish washed 100.
[04:05:31.6470] [main-comp-ctx-140] O:Dish(100,Clean)
*/
