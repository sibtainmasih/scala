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
  .asyncBoundary(OverflowStrategy.BackPressure(50))
  .mapParallelUnordered(2)(d => d.wash)
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
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 2))

Thread.sleep(30000)
println(s"$log Program Ends.")

/* Output
[04:35:27.0720] [run-main-7] Program starts.
[04:35:29.4780] [run-main-7] E: 1
[04:35:29.5160] [run-main-7] E: 2
[04:35:29.5170] [run-main-7] E: 3
[04:35:29.5170] [run-main-7] E: 4
[04:35:29.5170] [run-main-7] E: 5
[04:35:29.5180] [run-main-7] E: 6
[04:35:29.5180] [run-main-7] E: 7
[04:35:29.5190] [run-main-7] E: 8
[04:35:29.5190] [run-main-7] E: 9
[04:35:29.5200] [run-main-7] E: 10
[04:35:29.5200] [run-main-7] E: 11
[04:35:29.5200] [run-main-7] E: 12
[04:35:29.5230] [run-main-7] E: 13
[04:35:29.5250] [run-main-7] E: 14
[04:35:29.5280] [run-main-7] E: 15
[04:35:29.5290] [run-main-7] E: 16
[04:35:29.5300] [run-main-7] E: 17
[04:35:29.5320] [run-main-7] E: 18
[04:35:29.5350] [run-main-7] E: 19
[04:35:29.5370] [run-main-7] E: 20
[04:35:29.5380] [run-main-7] E: 21
[04:35:29.5380] [run-main-7] E: 22
[04:35:29.5390] [run-main-7] E: 23
[04:35:29.5400] [run-main-7] E: 24
[04:35:29.5410] [run-main-7] E: 25
[04:35:29.5410] [run-main-7] E: 26
[04:35:29.5420] [run-main-7] E: 27
[04:35:29.5430] [run-main-7] E: 28
[04:35:29.5430] [run-main-7] E: 29
[04:35:29.5430] [run-main-7] E: 30
[04:35:29.5440] [run-main-7] E: 31
[04:35:29.5440] [run-main-7] E: 32
[04:35:29.5450] [run-main-7] E: 33
[04:35:29.5450] [run-main-7] E: 34
[04:35:29.5450] [run-main-7] E: 35
[04:35:29.5450] [run-main-7] E: 36
[04:35:29.5470] [run-main-7] E: 37
[04:35:29.5470] [main-comp-ctx-302] washing dish 1
[04:35:29.5470] [main-comp-ctx-303] washing dish 2
[04:35:29.5480] [run-main-7] E: 38
[04:35:29.5480] [run-main-7] E: 39
[04:35:29.5490] [run-main-7] E: 40
[04:35:29.5490] [run-main-7] E: 41
[04:35:29.5490] [run-main-7] E: 42
[04:35:29.5490] [run-main-7] E: 43
[04:35:29.5500] [run-main-7] E: 44
[04:35:29.5500] [run-main-7] E: 45
[04:35:29.5500] [run-main-7] E: 46
[04:35:29.5500] [run-main-7] E: 47
[04:35:29.5510] [run-main-7] E: 48
[04:35:29.5510] [run-main-7] E: 49
[04:35:29.5510] [run-main-7] E: 50
[04:35:29.5520] [run-main-7] E: 51
[04:35:29.5520] [run-main-7] E: 52
[04:35:29.5530] [run-main-7] E: 53
[04:35:29.5530] [run-main-7] E: 54
[04:35:29.5530] [run-main-7] E: 55
[04:35:29.5540] [run-main-7] E: 56
[04:35:29.5540] [run-main-7] E: 57
[04:35:29.5540] [run-main-7] E: 58
[04:35:29.5550] [run-main-7] E: 59
[04:35:29.5550] [run-main-7] E: 60
[04:35:29.5550] [run-main-7] E: 61
[04:35:29.5560] [run-main-7] E: 62
[04:35:29.5580] [run-main-7] E: 63
[04:35:29.5590] [run-main-7] E: 64
[04:35:29.5590] [run-main-7] E: 65
[04:35:29.5590] [run-main-7] E: 66
[04:35:29.5610] [run-main-7] E: 67
[04:35:29.7560] [main-comp-ctx-303] Dish washed 1.
[04:35:29.7570] [main-comp-ctx-303] O:Dish(1,Clean)
[04:35:29.7590] [main-comp-ctx-303] Dish washed 2.
[04:35:29.7600] [main-comp-ctx-303] O:Dish(2,Clean)
[04:35:29.7600] [main-comp-ctx-303] washing dish 3
[04:35:29.7670] [main-comp-ctx-302] washing dish 4
[04:35:29.9660] [main-comp-ctx-303] Dish washed 3.
[04:35:29.9660] [main-comp-ctx-303] O:Dish(3,Clean)
[04:35:29.9670] [main-comp-ctx-303] washing dish 5
[04:35:29.9680] [main-comp-ctx-302] Dish washed 4.
[04:35:29.9680] [main-comp-ctx-302] O:Dish(4,Clean)
[04:35:29.9690] [main-comp-ctx-302] washing dish 6
[04:35:30.1690] [main-comp-ctx-303] Dish washed 5.
[04:35:30.1690] [main-comp-ctx-303] O:Dish(5,Clean)
[04:35:30.1710] [main-comp-ctx-303] Dish washed 6.
[04:35:30.1710] [main-comp-ctx-302] washing dish 7
[04:35:30.1710] [main-comp-ctx-303] O:Dish(6,Clean)
[04:35:30.1720] [main-comp-ctx-303] washing dish 8
[04:35:30.3720] [main-comp-ctx-302] Dish washed 7.
[04:35:30.3720] [main-comp-ctx-302] O:Dish(7,Clean)
[04:35:30.3730] [main-comp-ctx-303] washing dish 9
[04:35:30.3730] [main-comp-ctx-302] Dish washed 8.
[04:35:30.3730] [main-comp-ctx-302] O:Dish(8,Clean)
[04:35:30.3740] [main-comp-ctx-302] washing dish 10
[04:35:30.5730] [main-comp-ctx-303] Dish washed 9.
[04:35:30.5740] [main-comp-ctx-303] O:Dish(9,Clean)
[04:35:30.5750] [main-comp-ctx-303] Dish washed 10.
[04:35:30.5750] [main-comp-ctx-303] O:Dish(10,Clean)
[04:35:30.5750] [main-comp-ctx-302] washing dish 11
[04:35:30.5760] [main-comp-ctx-303] washing dish 12
[04:35:30.7760] [main-comp-ctx-302] Dish washed 11.
[04:35:30.7770] [main-comp-ctx-302] O:Dish(11,Clean)
[04:35:30.7770] [main-comp-ctx-303] washing dish 13
[04:35:30.7770] [main-comp-ctx-302] Dish washed 12.
[04:35:30.7780] [main-comp-ctx-302] O:Dish(12,Clean)
[04:35:30.7790] [main-comp-ctx-302] washing dish 14
[04:35:30.9780] [main-comp-ctx-303] Dish washed 13.
[04:35:30.9780] [main-comp-ctx-303] O:Dish(13,Clean)
[04:35:30.9790] [main-comp-ctx-302] Dish washed 14.
[04:35:30.9800] [main-comp-ctx-302] O:Dish(14,Clean)
[04:35:30.9800] [main-comp-ctx-303] washing dish 15
[04:35:30.9800] [main-comp-ctx-302] washing dish 16
[04:35:31.1800] [main-comp-ctx-303] Dish washed 15.
[04:35:31.1800] [main-comp-ctx-302] washing dish 17
[04:35:31.1810] [main-comp-ctx-303] O:Dish(15,Clean)
[04:35:31.1810] [main-comp-ctx-303] Dish washed 16.
[04:35:31.1810] [main-comp-ctx-303] O:Dish(16,Clean)
[04:35:31.1810] [main-comp-ctx-303] washing dish 18
[04:35:31.3810] [main-comp-ctx-302] Dish washed 17.
[04:35:31.3820] [main-comp-ctx-302] O:Dish(17,Clean)
[04:35:31.3820] [main-comp-ctx-303] washing dish 19
[04:35:31.3830] [main-comp-ctx-302] Dish washed 18.
[04:35:31.3830] [main-comp-ctx-302] O:Dish(18,Clean)
[04:35:31.3840] [main-comp-ctx-302] washing dish 20
[04:35:31.5830] [main-comp-ctx-303] Dish washed 19.
[04:35:31.5840] [main-comp-ctx-303] O:Dish(19,Clean)
[04:35:31.5840] [main-comp-ctx-303] washing dish 21
[04:35:31.5860] [main-comp-ctx-302] Dish washed 20.
[04:35:31.5870] [main-comp-ctx-302] O:Dish(20,Clean)
[04:35:31.5890] [main-comp-ctx-302] washing dish 22
[04:35:31.7850] [main-comp-ctx-303] Dish washed 21.
[04:35:31.7850] [main-comp-ctx-303] O:Dish(21,Clean)
[04:35:31.7860] [main-comp-ctx-303] washing dish 23
[04:35:31.7900] [main-comp-ctx-302] Dish washed 22.
[04:35:31.7910] [main-comp-ctx-302] O:Dish(22,Clean)
[04:35:31.7920] [main-comp-ctx-302] washing dish 24
[04:35:31.9870] [main-comp-ctx-303] Dish washed 23.
[04:35:31.9870] [main-comp-ctx-303] O:Dish(23,Clean)
[04:35:31.9880] [main-comp-ctx-303] washing dish 25
[04:35:31.9930] [main-comp-ctx-302] Dish washed 24.
[04:35:31.9940] [main-comp-ctx-302] O:Dish(24,Clean)
[04:35:31.9950] [main-comp-ctx-302] washing dish 26
[04:35:32.1890] [main-comp-ctx-303] Dish washed 25.
[04:35:32.1890] [main-comp-ctx-303] O:Dish(25,Clean)
[04:35:32.1900] [main-comp-ctx-303] washing dish 27
[04:35:32.1960] [main-comp-ctx-302] Dish washed 26.
[04:35:32.1960] [main-comp-ctx-302] O:Dish(26,Clean)
[04:35:32.1970] [main-comp-ctx-302] washing dish 28
[04:35:32.3910] [main-comp-ctx-303] Dish washed 27.
[04:35:32.3910] [main-comp-ctx-303] O:Dish(27,Clean)
[04:35:32.3920] [main-comp-ctx-303] washing dish 29
[04:35:32.3980] [main-comp-ctx-302] Dish washed 28.
[04:35:32.3980] [main-comp-ctx-302] O:Dish(28,Clean)
[04:35:32.3990] [main-comp-ctx-302] washing dish 30
[04:35:32.5930] [main-comp-ctx-303] Dish washed 29.
[04:35:32.5930] [main-comp-ctx-303] O:Dish(29,Clean)
[04:35:32.5940] [main-comp-ctx-303] washing dish 31
[04:35:32.6000] [main-comp-ctx-302] Dish washed 30.
[04:35:32.6000] [main-comp-ctx-302] O:Dish(30,Clean)
[04:35:32.6010] [main-comp-ctx-302] washing dish 32
[04:35:32.7950] [main-comp-ctx-303] Dish washed 31.
[04:35:32.7950] [main-comp-ctx-303] O:Dish(31,Clean)
[04:35:32.7970] [main-comp-ctx-303] washing dish 33
[04:35:32.8020] [main-comp-ctx-302] Dish washed 32.
[04:35:32.8020] [main-comp-ctx-302] O:Dish(32,Clean)
[04:35:32.8030] [main-comp-ctx-302] washing dish 34
[04:35:32.9980] [main-comp-ctx-303] Dish washed 33.
[04:35:32.9980] [main-comp-ctx-303] O:Dish(33,Clean)
[04:35:32.9990] [main-comp-ctx-303] washing dish 35
[04:35:33.0040] [main-comp-ctx-302] Dish washed 34.
[04:35:33.0040] [main-comp-ctx-302] O:Dish(34,Clean)
[04:35:33.0050] [main-comp-ctx-302] washing dish 36
[04:35:33.2000] [main-comp-ctx-303] Dish washed 35.
[04:35:33.2010] [main-comp-ctx-303] O:Dish(35,Clean)
[04:35:33.2020] [main-comp-ctx-303] washing dish 37
[04:35:33.2060] [main-comp-ctx-302] Dish washed 36.
[04:35:33.2060] [main-comp-ctx-302] O:Dish(36,Clean)
[04:35:33.2070] [main-comp-ctx-302] washing dish 38
[04:35:33.4030] [main-comp-ctx-303] Dish washed 37.
[04:35:33.4040] [main-comp-ctx-303] O:Dish(37,Clean)
[04:35:33.4050] [main-comp-ctx-303] washing dish 39
[04:35:33.4080] [main-comp-ctx-302] Dish washed 38.
[04:35:33.4080] [main-comp-ctx-302] O:Dish(38,Clean)
[04:35:33.4090] [main-comp-ctx-302] washing dish 40
[04:35:33.6060] [main-comp-ctx-303] Dish washed 39.
[04:35:33.6060] [main-comp-ctx-303] O:Dish(39,Clean)
[04:35:33.6070] [main-comp-ctx-303] washing dish 41
[04:35:33.6090] [main-comp-ctx-302] Dish washed 40.
[04:35:33.6100] [main-comp-ctx-302] O:Dish(40,Clean)
[04:35:33.6110] [main-comp-ctx-302] washing dish 42
[04:35:33.8080] [main-comp-ctx-303] Dish washed 41.
[04:35:33.8090] [main-comp-ctx-303] O:Dish(41,Clean)
[04:35:33.8100] [main-comp-ctx-303] washing dish 43
[04:35:33.8120] [main-comp-ctx-302] Dish washed 42.
[04:35:33.8120] [main-comp-ctx-302] O:Dish(42,Clean)
[04:35:33.8140] [main-comp-ctx-302] washing dish 44
[04:35:34.0110] [main-comp-ctx-303] Dish washed 43.
[04:35:34.0110] [main-comp-ctx-303] O:Dish(43,Clean)
[04:35:34.0130] [main-comp-ctx-303] washing dish 45
[04:35:34.0150] [main-comp-ctx-302] Dish washed 44.
[04:35:34.0150] [main-comp-ctx-302] O:Dish(44,Clean)
[04:35:34.0170] [main-comp-ctx-302] washing dish 46
[04:35:34.2130] [main-comp-ctx-303] Dish washed 45.
[04:35:34.2140] [main-comp-ctx-303] O:Dish(45,Clean)
[04:35:34.2150] [main-comp-ctx-303] washing dish 47
[04:35:34.2180] [main-comp-ctx-302] Dish washed 46.
[04:35:34.2180] [main-comp-ctx-302] O:Dish(46,Clean)
[04:35:34.2190] [main-comp-ctx-302] washing dish 48
[04:35:34.4160] [main-comp-ctx-303] Dish washed 47.
[04:35:34.4170] [main-comp-ctx-303] O:Dish(47,Clean)
[04:35:34.4170] [main-comp-ctx-303] washing dish 49
[04:35:34.4200] [main-comp-ctx-302] Dish washed 48.
[04:35:34.4210] [main-comp-ctx-302] O:Dish(48,Clean)
[04:35:34.4220] [main-comp-ctx-302] washing dish 50
[04:35:34.6180] [main-comp-ctx-303] Dish washed 49.
[04:35:34.6180] [main-comp-ctx-303] O:Dish(49,Clean)
[04:35:34.6190] [main-comp-ctx-303] washing dish 51
[04:35:34.6230] [main-comp-ctx-302] Dish washed 50.
[04:35:34.6230] [main-comp-ctx-302] O:Dish(50,Clean)
[04:35:34.6240] [main-comp-ctx-302] washing dish 52
[04:35:34.8200] [main-comp-ctx-303] Dish washed 51.
[04:35:34.8200] [main-comp-ctx-303] O:Dish(51,Clean)
[04:35:34.8210] [main-comp-ctx-303] washing dish 53
[04:35:34.8250] [main-comp-ctx-302] Dish washed 52.
[04:35:34.8250] [main-comp-ctx-302] O:Dish(52,Clean)
[04:35:34.8260] [main-comp-ctx-302] washing dish 54
[04:35:35.0210] [main-comp-ctx-303] Dish washed 53.
[04:35:35.0220] [main-comp-ctx-303] O:Dish(53,Clean)
[04:35:35.0230] [main-comp-ctx-303] washing dish 55
[04:35:35.0280] [main-comp-ctx-302] Dish washed 54.
[04:35:35.0280] [main-comp-ctx-302] O:Dish(54,Clean)
[04:35:35.0300] [main-comp-ctx-302] washing dish 56
[04:35:35.2240] [main-comp-ctx-303] Dish washed 55.
[04:35:35.2240] [main-comp-ctx-303] O:Dish(55,Clean)
[04:35:35.2250] [main-comp-ctx-303] washing dish 57
[04:35:35.2310] [main-comp-ctx-302] Dish washed 56.
[04:35:35.2310] [main-comp-ctx-302] O:Dish(56,Clean)
[04:35:35.2330] [main-comp-ctx-302] washing dish 58
[04:35:35.4250] [main-comp-ctx-303] Dish washed 57.
[04:35:35.4250] [main-comp-ctx-303] O:Dish(57,Clean)
[04:35:35.4260] [main-comp-ctx-303] washing dish 59
[04:35:35.4340] [main-comp-ctx-302] Dish washed 58.
[04:35:35.4340] [main-comp-ctx-302] O:Dish(58,Clean)
[04:35:35.4350] [main-comp-ctx-302] washing dish 60
[04:35:35.6270] [main-comp-ctx-303] Dish washed 59.
[04:35:35.6270] [main-comp-ctx-303] O:Dish(59,Clean)
[04:35:35.6280] [main-comp-ctx-303] washing dish 61
[04:35:35.6350] [main-comp-ctx-302] Dish washed 60.
[04:35:35.6350] [main-comp-ctx-302] O:Dish(60,Clean)
[04:35:35.6370] [main-comp-ctx-302] washing dish 62
[04:35:35.8290] [main-comp-ctx-303] Dish washed 61.
[04:35:35.8290] [main-comp-ctx-303] O:Dish(61,Clean)
[04:35:35.8300] [main-comp-ctx-303] washing dish 63
[04:35:35.8380] [main-comp-ctx-302] Dish washed 62.
[04:35:35.8380] [main-comp-ctx-302] O:Dish(62,Clean)
[04:35:35.8390] [main-comp-ctx-302] washing dish 64
[04:35:36.0310] [main-comp-ctx-303] Dish washed 63.
[04:35:36.0310] [main-comp-ctx-303] O:Dish(63,Clean)
[04:35:36.0320] [main-comp-ctx-303] washing dish 65
[04:35:36.0400] [main-comp-ctx-302] Dish washed 64.
[04:35:36.0400] [main-comp-ctx-302] O:Dish(64,Clean)
[04:35:36.0410] [main-comp-ctx-302] washing dish 66
[04:35:36.2330] [main-comp-ctx-303] Dish washed 65.
[04:35:36.2330] [main-comp-ctx-303] O:Dish(65,Clean)
[04:35:36.2340] [main-comp-ctx-303] washing dish 67
[04:35:36.2420] [main-comp-ctx-302] Dish washed 66.
[04:35:36.2430] [main-comp-ctx-302] O:Dish(66,Clean)
[04:35:36.2440] [main-comp-ctx-302] E: 68
[04:35:36.2440] [main-comp-ctx-302] E: 69
[04:35:36.2450] [main-comp-ctx-302] E: 70
[04:35:36.2460] [main-comp-ctx-302] E: 71
[04:35:36.2470] [main-comp-ctx-302] E: 72
[04:35:36.2480] [main-comp-ctx-302] E: 73
[04:35:36.2480] [main-comp-ctx-302] E: 74
[04:35:36.2490] [main-comp-ctx-302] E: 75
[04:35:36.2500] [main-comp-ctx-302] E: 76
[04:35:36.2500] [main-comp-ctx-302] E: 77
[04:35:36.2510] [main-comp-ctx-302] E: 78
[04:35:36.2510] [main-comp-ctx-302] E: 79
[04:35:36.2520] [main-comp-ctx-302] E: 80
[04:35:36.2520] [main-comp-ctx-302] E: 81
[04:35:36.2540] [main-comp-ctx-302] E: 82
[04:35:36.2540] [main-comp-ctx-302] E: 83
[04:35:36.2550] [main-comp-ctx-302] E: 84
[04:35:36.2550] [main-comp-ctx-302] E: 85
[04:35:36.2550] [main-comp-ctx-302] E: 86
[04:35:36.2560] [main-comp-ctx-302] E: 87
[04:35:36.2560] [main-comp-ctx-302] E: 88
[04:35:36.2570] [main-comp-ctx-302] E: 89
[04:35:36.2570] [main-comp-ctx-302] E: 90
[04:35:36.2570] [main-comp-ctx-302] E: 91
[04:35:36.2580] [main-comp-ctx-302] E: 92
[04:35:36.2580] [main-comp-ctx-302] E: 93
[04:35:36.2590] [main-comp-ctx-302] E: 94
[04:35:36.2590] [main-comp-ctx-302] E: 95
[04:35:36.2600] [main-comp-ctx-302] E: 96
[04:35:36.2600] [main-comp-ctx-302] E: 97
[04:35:36.2610] [main-comp-ctx-302] E: 98
[04:35:36.2610] [main-comp-ctx-302] E: 99
[04:35:36.2620] [main-comp-ctx-302] E: 100
[04:35:36.2660] [main-comp-ctx-302] washing dish 68
[04:35:36.4350] [main-comp-ctx-303] Dish washed 67.
[04:35:36.4350] [main-comp-ctx-303] O:Dish(67,Clean)
[04:35:36.4360] [main-comp-ctx-303] washing dish 69
[04:35:36.4660] [main-comp-ctx-302] Dish washed 68.
[04:35:36.4670] [main-comp-ctx-302] O:Dish(68,Clean)
[04:35:36.4680] [main-comp-ctx-302] washing dish 70
[04:35:36.6370] [main-comp-ctx-303] Dish washed 69.
[04:35:36.6380] [main-comp-ctx-303] O:Dish(69,Clean)
[04:35:36.6390] [main-comp-ctx-303] washing dish 71
[04:35:36.6690] [main-comp-ctx-302] Dish washed 70.
[04:35:36.6690] [main-comp-ctx-302] O:Dish(70,Clean)
[04:35:36.6700] [main-comp-ctx-302] washing dish 72
[04:35:36.8400] [main-comp-ctx-303] Dish washed 71.
[04:35:36.8410] [main-comp-ctx-303] O:Dish(71,Clean)
[04:35:36.8410] [main-comp-ctx-303] washing dish 73
[04:35:36.8710] [main-comp-ctx-302] Dish washed 72.
[04:35:36.8710] [main-comp-ctx-302] O:Dish(72,Clean)
[04:35:36.8720] [main-comp-ctx-302] washing dish 74
[04:35:37.0420] [main-comp-ctx-303] Dish washed 73.
[04:35:37.0420] [main-comp-ctx-303] O:Dish(73,Clean)
[04:35:37.0430] [main-comp-ctx-303] washing dish 75
[04:35:37.0720] [main-comp-ctx-302] Dish washed 74.
[04:35:37.0720] [main-comp-ctx-302] O:Dish(74,Clean)
[04:35:37.0740] [main-comp-ctx-302] washing dish 76
[04:35:37.2430] [main-comp-ctx-303] Dish washed 75.
[04:35:37.2440] [main-comp-ctx-303] O:Dish(75,Clean)
[04:35:37.2450] [main-comp-ctx-303] washing dish 77
[04:35:37.2740] [main-comp-ctx-302] Dish washed 76.
[04:35:37.2740] [main-comp-ctx-302] O:Dish(76,Clean)
[04:35:37.2750] [main-comp-ctx-302] washing dish 78
[04:35:37.4450] [main-comp-ctx-303] Dish washed 77.
[04:35:37.4460] [main-comp-ctx-303] O:Dish(77,Clean)
[04:35:37.4470] [main-comp-ctx-303] washing dish 79
[04:35:37.4760] [main-comp-ctx-302] Dish washed 78.
[04:35:37.4760] [main-comp-ctx-302] O:Dish(78,Clean)
[04:35:37.4770] [main-comp-ctx-302] washing dish 80
[04:35:37.6480] [main-comp-ctx-303] Dish washed 79.
[04:35:37.6480] [main-comp-ctx-303] O:Dish(79,Clean)
[04:35:37.6490] [main-comp-ctx-303] washing dish 81
[04:35:37.6780] [main-comp-ctx-302] Dish washed 80.
[04:35:37.6780] [main-comp-ctx-302] O:Dish(80,Clean)
[04:35:37.6790] [main-comp-ctx-302] washing dish 82
[04:35:37.8490] [main-comp-ctx-303] Dish washed 81.
[04:35:37.8500] [main-comp-ctx-303] O:Dish(81,Clean)
[04:35:37.8500] [main-comp-ctx-303] washing dish 83
[04:35:37.8800] [main-comp-ctx-302] Dish washed 82.
[04:35:37.8800] [main-comp-ctx-302] O:Dish(82,Clean)
[04:35:37.8810] [main-comp-ctx-302] washing dish 84
[04:35:38.0510] [main-comp-ctx-303] Dish washed 83.
[04:35:38.0510] [main-comp-ctx-303] O:Dish(83,Clean)
[04:35:38.0530] [main-comp-ctx-303] washing dish 85
[04:35:38.0820] [main-comp-ctx-302] Dish washed 84.
[04:35:38.0830] [main-comp-ctx-302] O:Dish(84,Clean)
[04:35:38.0840] [main-comp-ctx-302] washing dish 86
[04:35:38.2530] [main-comp-ctx-303] Dish washed 85.
[04:35:38.2540] [main-comp-ctx-303] O:Dish(85,Clean)
[04:35:38.2550] [main-comp-ctx-303] washing dish 87
[04:35:38.2840] [main-comp-ctx-302] Dish washed 86.
[04:35:38.2850] [main-comp-ctx-302] O:Dish(86,Clean)
[04:35:38.2850] [main-comp-ctx-302] washing dish 88
[04:35:38.4550] [main-comp-ctx-303] Dish washed 87.
[04:35:38.4560] [main-comp-ctx-303] O:Dish(87,Clean)
[04:35:38.4560] [main-comp-ctx-303] washing dish 89
[04:35:38.4860] [main-comp-ctx-302] Dish washed 88.
[04:35:38.4860] [main-comp-ctx-302] O:Dish(88,Clean)
[04:35:38.4870] [main-comp-ctx-302] washing dish 90
[04:35:38.6570] [main-comp-ctx-303] Dish washed 89.
[04:35:38.6570] [main-comp-ctx-303] O:Dish(89,Clean)
[04:35:38.6570] [main-comp-ctx-303] washing dish 91
[04:35:38.6870] [main-comp-ctx-302] Dish washed 90.
[04:35:38.6870] [main-comp-ctx-302] O:Dish(90,Clean)
[04:35:38.6880] [main-comp-ctx-302] washing dish 92
[04:35:38.8580] [main-comp-ctx-303] Dish washed 91.
[04:35:38.8580] [main-comp-ctx-303] O:Dish(91,Clean)
[04:35:38.8590] [main-comp-ctx-303] washing dish 93
[04:35:38.8890] [main-comp-ctx-302] Dish washed 92.
[04:35:38.8890] [main-comp-ctx-302] O:Dish(92,Clean)
[04:35:38.8890] [main-comp-ctx-302] washing dish 94
[04:35:39.0600] [main-comp-ctx-303] Dish washed 93.
[04:35:39.0600] [main-comp-ctx-303] O:Dish(93,Clean)
[04:35:39.0610] [main-comp-ctx-303] washing dish 95
[04:35:39.0900] [main-comp-ctx-302] Dish washed 94.
[04:35:39.0900] [main-comp-ctx-302] O:Dish(94,Clean)
[04:35:39.0910] [main-comp-ctx-302] washing dish 96
[04:35:39.2610] [main-comp-ctx-303] Dish washed 95.
[04:35:39.2620] [main-comp-ctx-303] O:Dish(95,Clean)
[04:35:39.2620] [main-comp-ctx-303] washing dish 97
[04:35:39.2920] [main-comp-ctx-302] Dish washed 96.
[04:35:39.2920] [main-comp-ctx-302] O:Dish(96,Clean)
[04:35:39.2920] [main-comp-ctx-302] washing dish 98
[04:35:39.4630] [main-comp-ctx-303] Dish washed 97.
[04:35:39.4630] [main-comp-ctx-303] O:Dish(97,Clean)
[04:35:39.4690] [main-comp-ctx-303] washing dish 99
[04:35:39.4930] [main-comp-ctx-302] Dish washed 98.
[04:35:39.4930] [main-comp-ctx-302] O:Dish(98,Clean)
[04:35:39.4940] [main-comp-ctx-302] washing dish 100
[04:35:39.6700] [main-comp-ctx-303] Dish washed 99.
[04:35:39.6700] [main-comp-ctx-303] O:Dish(99,Clean)
[04:35:39.6940] [main-comp-ctx-303] Dish washed 100.
[04:35:39.6950] [main-comp-ctx-303] O:Dish(100,Clean)
*/
