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
  def wash = {
    println(s"$log washing dish $id")
    Thread.sleep(100)
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
  .asyncBoundary(OverflowStrategy.Unbounded)
  .map(d => d.wash)
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
[02:14:58.6510] [run-main-1] Program starts.
[02:14:58.6640] [run-main-1] E: 1
[02:14:58.6690] [run-main-1] E: 2
[02:14:58.6690] [run-main-1] E: 3
[02:14:58.6690] [run-main-1] E: 4
[02:14:58.6700] [main-comp-ctx-160] washing dish 1
[02:14:58.6690] [run-main-1] E: 5
[02:14:58.6710] [run-main-1] E: 6
[02:14:58.6710] [run-main-1] E: 7
[02:14:58.6710] [run-main-1] E: 8
[02:14:58.6710] [run-main-1] E: 9
[02:14:58.6710] [run-main-1] E: 10
[02:14:58.6720] [run-main-1] E: 11
[02:14:58.6720] [run-main-1] E: 12
[02:14:58.6720] [run-main-1] E: 13
[02:14:58.6720] [run-main-1] E: 14
[02:14:58.6720] [run-main-1] E: 15
[02:14:58.6730] [run-main-1] E: 16
[02:14:58.6730] [run-main-1] E: 17
[02:14:58.6730] [run-main-1] E: 18
[02:14:58.6730] [run-main-1] E: 19
[02:14:58.6730] [run-main-1] E: 20
[02:14:58.6740] [run-main-1] E: 21
[02:14:58.6740] [run-main-1] E: 22
[02:14:58.6750] [run-main-1] E: 23
[02:14:58.6750] [run-main-1] E: 24
[02:14:58.6760] [run-main-1] E: 25
[02:14:58.6760] [run-main-1] E: 26
[02:14:58.6760] [run-main-1] E: 27
[02:14:58.6760] [run-main-1] E: 28
[02:14:58.6760] [run-main-1] E: 29
[02:14:58.6770] [run-main-1] E: 30
[02:14:58.6770] [run-main-1] E: 31
[02:14:58.6770] [run-main-1] E: 32
[02:14:58.6770] [run-main-1] E: 33
[02:14:58.6770] [run-main-1] E: 34
[02:14:58.6780] [run-main-1] E: 35
[02:14:58.6780] [run-main-1] E: 36
[02:14:58.6780] [run-main-1] E: 37
[02:14:58.6790] [run-main-1] E: 38
[02:14:58.6790] [run-main-1] E: 39
[02:14:58.6790] [run-main-1] E: 40
[02:14:58.6790] [run-main-1] E: 41
[02:14:58.6800] [run-main-1] E: 42
[02:14:58.6800] [run-main-1] E: 43
[02:14:58.6810] [run-main-1] E: 44
[02:14:58.6810] [run-main-1] E: 45
[02:14:58.6820] [run-main-1] E: 46
[02:14:58.6820] [run-main-1] E: 47
[02:14:58.6820] [run-main-1] E: 48
[02:14:58.6840] [run-main-1] E: 49
[02:14:58.6840] [run-main-1] E: 50
[02:14:58.6850] [run-main-1] E: 51
[02:14:58.6860] [run-main-1] E: 52
[02:14:58.6860] [run-main-1] E: 53
[02:14:58.6870] [run-main-1] E: 54
[02:14:58.6870] [run-main-1] E: 55
[02:14:58.6880] [run-main-1] E: 56
[02:14:58.6890] [run-main-1] E: 57
[02:14:58.6900] [run-main-1] E: 58
[02:14:58.6900] [run-main-1] E: 59
[02:14:58.6910] [run-main-1] E: 60
[02:14:58.6910] [run-main-1] E: 61
[02:14:58.6920] [run-main-1] E: 62
[02:14:58.6920] [run-main-1] E: 63
[02:14:58.6930] [run-main-1] E: 64
[02:14:58.6930] [run-main-1] E: 65
[02:14:58.6930] [run-main-1] E: 66
[02:14:58.6940] [run-main-1] E: 67
[02:14:58.6940] [run-main-1] E: 68
[02:14:58.6950] [run-main-1] E: 69
[02:14:58.6950] [run-main-1] E: 70
[02:14:58.6950] [run-main-1] E: 71
[02:14:58.6960] [run-main-1] E: 72
[02:14:58.6960] [run-main-1] E: 73
[02:14:58.6960] [run-main-1] E: 74
[02:14:58.6960] [run-main-1] E: 75
[02:14:58.6960] [run-main-1] E: 76
[02:14:58.6970] [run-main-1] E: 77
[02:14:58.6970] [run-main-1] E: 78
[02:14:58.6970] [run-main-1] E: 79
[02:14:58.6990] [run-main-1] E: 80
[02:14:58.6990] [run-main-1] E: 81
[02:14:58.6990] [run-main-1] E: 82
[02:14:58.7000] [run-main-1] E: 83
[02:14:58.7000] [run-main-1] E: 84
[02:14:58.7000] [run-main-1] E: 85
[02:14:58.7000] [run-main-1] E: 86
[02:14:58.7020] [run-main-1] E: 87
[02:14:58.7030] [run-main-1] E: 88
[02:14:58.7030] [run-main-1] E: 89
[02:14:58.7030] [run-main-1] E: 90
[02:14:58.7030] [run-main-1] E: 91
[02:14:58.7040] [run-main-1] E: 92
[02:14:58.7040] [run-main-1] E: 93
[02:14:58.7040] [run-main-1] E: 94
[02:14:58.7040] [run-main-1] E: 95
[02:14:58.7050] [run-main-1] E: 96
[02:14:58.7050] [run-main-1] E: 97
[02:14:58.7050] [run-main-1] E: 98
[02:14:58.7050] [run-main-1] E: 99
[02:14:58.7060] [run-main-1] E: 100
[02:14:58.7760] [main-comp-ctx-160] Dish washed 1.
[02:14:58.7770] [main-comp-ctx-160] O:Dish(1,Clean)
[02:14:58.7770] [main-comp-ctx-160] washing dish 2
[02:14:58.8780] [main-comp-ctx-160] Dish washed 2.
[02:14:58.8780] [main-comp-ctx-160] O:Dish(2,Clean)
[02:14:58.8780] [main-comp-ctx-160] washing dish 3
[02:14:58.9790] [main-comp-ctx-160] Dish washed 3.
[02:14:58.9790] [main-comp-ctx-160] O:Dish(3,Clean)
[02:14:58.9790] [main-comp-ctx-160] washing dish 4
[02:14:59.0800] [main-comp-ctx-160] Dish washed 4.
[02:14:59.0800] [main-comp-ctx-160] O:Dish(4,Clean)
[02:14:59.0810] [main-comp-ctx-160] washing dish 5
[02:14:59.1810] [main-comp-ctx-160] Dish washed 5.
[02:14:59.1810] [main-comp-ctx-160] O:Dish(5,Clean)
[02:14:59.1820] [main-comp-ctx-160] washing dish 6
[02:14:59.2820] [main-comp-ctx-160] Dish washed 6.
[02:14:59.2830] [main-comp-ctx-160] O:Dish(6,Clean)
[02:14:59.2830] [main-comp-ctx-160] washing dish 7
[02:14:59.3830] [main-comp-ctx-160] Dish washed 7.
[02:14:59.3840] [main-comp-ctx-160] O:Dish(7,Clean)
[02:14:59.3840] [main-comp-ctx-160] washing dish 8
[02:14:59.4850] [main-comp-ctx-160] Dish washed 8.
[02:14:59.4850] [main-comp-ctx-160] O:Dish(8,Clean)
[02:14:59.4850] [main-comp-ctx-160] washing dish 9
[02:14:59.5860] [main-comp-ctx-160] Dish washed 9.
[02:14:59.5890] [main-comp-ctx-160] O:Dish(9,Clean)
[02:14:59.5910] [main-comp-ctx-160] washing dish 10
[02:14:59.6920] [main-comp-ctx-160] Dish washed 10.
[02:14:59.6920] [main-comp-ctx-160] O:Dish(10,Clean)
[02:14:59.6930] [main-comp-ctx-160] washing dish 11
[02:14:59.7930] [main-comp-ctx-160] Dish washed 11.
[02:14:59.7930] [main-comp-ctx-160] O:Dish(11,Clean)
[02:14:59.7940] [main-comp-ctx-160] washing dish 12
[02:14:59.8940] [main-comp-ctx-160] Dish washed 12.
[02:14:59.8950] [main-comp-ctx-160] O:Dish(12,Clean)
[02:14:59.8970] [main-comp-ctx-160] washing dish 13
[02:14:59.9980] [main-comp-ctx-160] Dish washed 13.
[02:14:59.9980] [main-comp-ctx-160] O:Dish(13,Clean)
[02:14:59.9990] [main-comp-ctx-160] washing dish 14
[02:15:00.0990] [main-comp-ctx-160] Dish washed 14.
[02:15:00.0990] [main-comp-ctx-160] O:Dish(14,Clean)
[02:15:00.1000] [main-comp-ctx-160] washing dish 15
[02:15:00.2000] [main-comp-ctx-160] Dish washed 15.
[02:15:00.2000] [main-comp-ctx-160] O:Dish(15,Clean)
[02:15:00.2010] [main-comp-ctx-160] washing dish 16
[02:15:00.3010] [main-comp-ctx-160] Dish washed 16.
[02:15:00.3010] [main-comp-ctx-160] O:Dish(16,Clean)
[02:15:00.3020] [main-comp-ctx-160] washing dish 17
[02:15:00.4020] [main-comp-ctx-160] Dish washed 17.
[02:15:00.4020] [main-comp-ctx-160] O:Dish(17,Clean)
[02:15:00.4030] [main-comp-ctx-160] washing dish 18
[02:15:00.5030] [main-comp-ctx-160] Dish washed 18.
[02:15:00.5040] [main-comp-ctx-160] O:Dish(18,Clean)
[02:15:00.5040] [main-comp-ctx-160] washing dish 19
[02:15:00.6050] [main-comp-ctx-160] Dish washed 19.
[02:15:00.6050] [main-comp-ctx-160] O:Dish(19,Clean)
[02:15:00.6060] [main-comp-ctx-160] washing dish 20
[02:15:00.7060] [main-comp-ctx-160] Dish washed 20.
[02:15:00.7070] [main-comp-ctx-160] O:Dish(20,Clean)
[02:15:00.7070] [main-comp-ctx-160] washing dish 21
[02:15:00.8080] [main-comp-ctx-160] Dish washed 21.
[02:15:00.8080] [main-comp-ctx-160] O:Dish(21,Clean)
[02:15:00.8090] [main-comp-ctx-160] washing dish 22
[02:15:00.9090] [main-comp-ctx-160] Dish washed 22.
[02:15:00.9090] [main-comp-ctx-160] O:Dish(22,Clean)
[02:15:00.9100] [main-comp-ctx-160] washing dish 23
[02:15:01.0110] [main-comp-ctx-160] Dish washed 23.
[02:15:01.0110] [main-comp-ctx-160] O:Dish(23,Clean)
[02:15:01.0120] [main-comp-ctx-160] washing dish 24
[02:15:01.1130] [main-comp-ctx-160] Dish washed 24.
[02:15:01.1130] [main-comp-ctx-160] O:Dish(24,Clean)
[02:15:01.1140] [main-comp-ctx-160] washing dish 25
[02:15:01.2160] [main-comp-ctx-160] Dish washed 25.
[02:15:01.2160] [main-comp-ctx-160] O:Dish(25,Clean)
[02:15:01.2170] [main-comp-ctx-160] washing dish 26
[02:15:01.3170] [main-comp-ctx-160] Dish washed 26.
[02:15:01.3180] [main-comp-ctx-160] O:Dish(26,Clean)
[02:15:01.3180] [main-comp-ctx-160] washing dish 27
[02:15:01.4190] [main-comp-ctx-160] Dish washed 27.
[02:15:01.4190] [main-comp-ctx-160] O:Dish(27,Clean)
[02:15:01.4190] [main-comp-ctx-160] washing dish 28
[02:15:01.5200] [main-comp-ctx-160] Dish washed 28.
[02:15:01.5200] [main-comp-ctx-160] O:Dish(28,Clean)
[02:15:01.5210] [main-comp-ctx-160] washing dish 29
[02:15:01.6210] [main-comp-ctx-160] Dish washed 29.
[02:15:01.6210] [main-comp-ctx-160] O:Dish(29,Clean)
[02:15:01.6220] [main-comp-ctx-160] washing dish 30
[02:15:01.7220] [main-comp-ctx-160] Dish washed 30.
[02:15:01.7230] [main-comp-ctx-160] O:Dish(30,Clean)
[02:15:01.7230] [main-comp-ctx-160] washing dish 31
[02:15:01.8240] [main-comp-ctx-160] Dish washed 31.
[02:15:01.8240] [main-comp-ctx-160] O:Dish(31,Clean)
[02:15:01.8250] [main-comp-ctx-160] washing dish 32
[02:15:01.9250] [main-comp-ctx-160] Dish washed 32.
[02:15:01.9250] [main-comp-ctx-160] O:Dish(32,Clean)
[02:15:01.9260] [main-comp-ctx-160] washing dish 33
[02:15:02.0260] [main-comp-ctx-160] Dish washed 33.
[02:15:02.0260] [main-comp-ctx-160] O:Dish(33,Clean)
[02:15:02.0270] [main-comp-ctx-160] washing dish 34
[02:15:02.1270] [main-comp-ctx-160] Dish washed 34.
[02:15:02.1270] [main-comp-ctx-160] O:Dish(34,Clean)
[02:15:02.1280] [main-comp-ctx-160] washing dish 35
[02:15:02.2280] [main-comp-ctx-160] Dish washed 35.
[02:15:02.2290] [main-comp-ctx-160] O:Dish(35,Clean)
[02:15:02.2290] [main-comp-ctx-160] washing dish 36
[02:15:02.3300] [main-comp-ctx-160] Dish washed 36.
[02:15:02.3300] [main-comp-ctx-160] O:Dish(36,Clean)
[02:15:02.3310] [main-comp-ctx-160] washing dish 37
[02:15:02.4320] [main-comp-ctx-160] Dish washed 37.
[02:15:02.4330] [main-comp-ctx-160] O:Dish(37,Clean)
[02:15:02.4330] [main-comp-ctx-160] washing dish 38
[02:15:02.5340] [main-comp-ctx-160] Dish washed 38.
[02:15:02.5340] [main-comp-ctx-160] O:Dish(38,Clean)
[02:15:02.5350] [main-comp-ctx-160] washing dish 39
[02:15:02.6360] [main-comp-ctx-160] Dish washed 39.
[02:15:02.6360] [main-comp-ctx-160] O:Dish(39,Clean)
[02:15:02.6380] [main-comp-ctx-160] washing dish 40
[02:15:02.7380] [main-comp-ctx-160] Dish washed 40.
[02:15:02.7390] [main-comp-ctx-160] O:Dish(40,Clean)
[02:15:02.7400] [main-comp-ctx-160] washing dish 41
[02:15:02.8410] [main-comp-ctx-160] Dish washed 41.
[02:15:02.8410] [main-comp-ctx-160] O:Dish(41,Clean)
[02:15:02.8430] [main-comp-ctx-160] washing dish 42
[02:15:02.9440] [main-comp-ctx-160] Dish washed 42.
[02:15:02.9440] [main-comp-ctx-160] O:Dish(42,Clean)
[02:15:02.9460] [main-comp-ctx-160] washing dish 43
[02:15:03.0480] [main-comp-ctx-160] Dish washed 43.
[02:15:03.0480] [main-comp-ctx-160] O:Dish(43,Clean)
[02:15:03.0490] [main-comp-ctx-160] washing dish 44
[02:15:03.1500] [main-comp-ctx-160] Dish washed 44.
[02:15:03.1510] [main-comp-ctx-160] O:Dish(44,Clean)
[02:15:03.1520] [main-comp-ctx-160] washing dish 45
[02:15:03.2530] [main-comp-ctx-160] Dish washed 45.
[02:15:03.2540] [main-comp-ctx-160] O:Dish(45,Clean)
[02:15:03.2560] [main-comp-ctx-160] washing dish 46
[02:15:03.3570] [main-comp-ctx-160] Dish washed 46.
[02:15:03.3570] [main-comp-ctx-160] O:Dish(46,Clean)
[02:15:03.3590] [main-comp-ctx-160] washing dish 47
[02:15:03.4590] [main-comp-ctx-160] Dish washed 47.
[02:15:03.4600] [main-comp-ctx-160] O:Dish(47,Clean)
[02:15:03.4600] [main-comp-ctx-160] washing dish 48
[02:15:03.5610] [main-comp-ctx-160] Dish washed 48.
[02:15:03.5610] [main-comp-ctx-160] O:Dish(48,Clean)
[02:15:03.5620] [main-comp-ctx-160] washing dish 49
[02:15:03.6630] [main-comp-ctx-160] Dish washed 49.
[02:15:03.6630] [main-comp-ctx-160] O:Dish(49,Clean)
[02:15:03.6640] [main-comp-ctx-160] washing dish 50
[02:15:03.7650] [main-comp-ctx-160] Dish washed 50.
[02:15:03.7660] [main-comp-ctx-160] O:Dish(50,Clean)
[02:15:03.7660] [main-comp-ctx-160] washing dish 51
[02:15:03.8670] [main-comp-ctx-160] Dish washed 51.
[02:15:03.8680] [main-comp-ctx-160] O:Dish(51,Clean)
[02:15:03.8690] [main-comp-ctx-160] washing dish 52
[02:15:03.9690] [main-comp-ctx-160] Dish washed 52.
[02:15:03.9700] [main-comp-ctx-160] O:Dish(52,Clean)
[02:15:03.9710] [main-comp-ctx-160] washing dish 53
[02:15:04.0720] [main-comp-ctx-160] Dish washed 53.
[02:15:04.0730] [main-comp-ctx-160] O:Dish(53,Clean)
[02:15:04.0740] [main-comp-ctx-160] washing dish 54
[02:15:04.1740] [main-comp-ctx-160] Dish washed 54.
[02:15:04.1750] [main-comp-ctx-160] O:Dish(54,Clean)
[02:15:04.1750] [main-comp-ctx-160] washing dish 55
[02:15:04.2750] [main-comp-ctx-160] Dish washed 55.
[02:15:04.2760] [main-comp-ctx-160] O:Dish(55,Clean)
[02:15:04.2760] [main-comp-ctx-160] washing dish 56
[02:15:04.3760] [main-comp-ctx-160] Dish washed 56.
[02:15:04.3770] [main-comp-ctx-160] O:Dish(56,Clean)
[02:15:04.3770] [main-comp-ctx-160] washing dish 57
[02:15:04.4770] [main-comp-ctx-160] Dish washed 57.
[02:15:04.4770] [main-comp-ctx-160] O:Dish(57,Clean)
[02:15:04.4780] [main-comp-ctx-160] washing dish 58
[02:15:04.5780] [main-comp-ctx-160] Dish washed 58.
[02:15:04.5780] [main-comp-ctx-160] O:Dish(58,Clean)
[02:15:04.5790] [main-comp-ctx-160] washing dish 59
[02:15:04.6790] [main-comp-ctx-160] Dish washed 59.
[02:15:04.6790] [main-comp-ctx-160] O:Dish(59,Clean)
[02:15:04.6800] [main-comp-ctx-160] washing dish 60
[02:15:04.7800] [main-comp-ctx-160] Dish washed 60.
[02:15:04.7800] [main-comp-ctx-160] O:Dish(60,Clean)
[02:15:04.7810] [main-comp-ctx-160] washing dish 61
[02:15:04.8810] [main-comp-ctx-160] Dish washed 61.
[02:15:04.8820] [main-comp-ctx-160] O:Dish(61,Clean)
[02:15:04.8820] [main-comp-ctx-160] washing dish 62
[02:15:04.9820] [main-comp-ctx-160] Dish washed 62.
[02:15:04.9830] [main-comp-ctx-160] O:Dish(62,Clean)
[02:15:04.9830] [main-comp-ctx-160] washing dish 63
[02:15:05.0830] [main-comp-ctx-160] Dish washed 63.
[02:15:05.0840] [main-comp-ctx-160] O:Dish(63,Clean)
[02:15:05.0850] [main-comp-ctx-160] washing dish 64
[02:15:05.1850] [main-comp-ctx-160] Dish washed 64.
[02:15:05.1850] [main-comp-ctx-160] O:Dish(64,Clean)
[02:15:05.1860] [main-comp-ctx-160] washing dish 65
[02:15:05.2870] [main-comp-ctx-160] Dish washed 65.
[02:15:05.2870] [main-comp-ctx-160] O:Dish(65,Clean)
[02:15:05.2880] [main-comp-ctx-160] washing dish 66
[02:15:05.3880] [main-comp-ctx-160] Dish washed 66.
[02:15:05.3880] [main-comp-ctx-160] O:Dish(66,Clean)
[02:15:05.3890] [main-comp-ctx-160] washing dish 67
[02:15:05.4890] [main-comp-ctx-160] Dish washed 67.
[02:15:05.4890] [main-comp-ctx-160] O:Dish(67,Clean)
[02:15:05.4890] [main-comp-ctx-160] washing dish 68
[02:15:05.5900] [main-comp-ctx-160] Dish washed 68.
[02:15:05.5900] [main-comp-ctx-160] O:Dish(68,Clean)
[02:15:05.5910] [main-comp-ctx-160] washing dish 69
[02:15:05.6910] [main-comp-ctx-160] Dish washed 69.
[02:15:05.6920] [main-comp-ctx-160] O:Dish(69,Clean)
[02:15:05.6930] [main-comp-ctx-160] washing dish 70
[02:15:05.7940] [main-comp-ctx-160] Dish washed 70.
[02:15:05.7940] [main-comp-ctx-160] O:Dish(70,Clean)
[02:15:05.7950] [main-comp-ctx-160] washing dish 71
[02:15:05.8950] [main-comp-ctx-160] Dish washed 71.
[02:15:05.8960] [main-comp-ctx-160] O:Dish(71,Clean)
[02:15:05.8960] [main-comp-ctx-160] washing dish 72
[02:15:05.9970] [main-comp-ctx-160] Dish washed 72.
[02:15:05.9970] [main-comp-ctx-160] O:Dish(72,Clean)
[02:15:05.9990] [main-comp-ctx-160] washing dish 73
[02:15:06.0990] [main-comp-ctx-160] Dish washed 73.
[02:15:06.1000] [main-comp-ctx-160] O:Dish(73,Clean)
[02:15:06.1010] [main-comp-ctx-160] washing dish 74
[02:15:06.2010] [main-comp-ctx-160] Dish washed 74.
[02:15:06.2020] [main-comp-ctx-160] O:Dish(74,Clean)
[02:15:06.2020] [main-comp-ctx-160] washing dish 75
[02:15:06.3030] [main-comp-ctx-160] Dish washed 75.
[02:15:06.3030] [main-comp-ctx-160] O:Dish(75,Clean)
[02:15:06.3040] [main-comp-ctx-160] washing dish 76
[02:15:06.4040] [main-comp-ctx-160] Dish washed 76.
[02:15:06.4040] [main-comp-ctx-160] O:Dish(76,Clean)
[02:15:06.4050] [main-comp-ctx-160] washing dish 77
[02:15:06.5060] [main-comp-ctx-160] Dish washed 77.
[02:15:06.5060] [main-comp-ctx-160] O:Dish(77,Clean)
[02:15:06.5070] [main-comp-ctx-160] washing dish 78
[02:15:06.6070] [main-comp-ctx-160] Dish washed 78.
[02:15:06.6070] [main-comp-ctx-160] O:Dish(78,Clean)
[02:15:06.6080] [main-comp-ctx-160] washing dish 79
[02:15:06.7090] [main-comp-ctx-160] Dish washed 79.
[02:15:06.7090] [main-comp-ctx-160] O:Dish(79,Clean)
[02:15:06.7090] [main-comp-ctx-160] washing dish 80
[02:15:06.8100] [main-comp-ctx-160] Dish washed 80.
[02:15:06.8100] [main-comp-ctx-160] O:Dish(80,Clean)
[02:15:06.8100] [main-comp-ctx-160] washing dish 81
[02:15:06.9110] [main-comp-ctx-160] Dish washed 81.
[02:15:06.9110] [main-comp-ctx-160] O:Dish(81,Clean)
[02:15:06.9110] [main-comp-ctx-160] washing dish 82
[02:15:07.0120] [main-comp-ctx-160] Dish washed 82.
[02:15:07.0120] [main-comp-ctx-160] O:Dish(82,Clean)
[02:15:07.0120] [main-comp-ctx-160] washing dish 83
[02:15:07.1130] [main-comp-ctx-160] Dish washed 83.
[02:15:07.1130] [main-comp-ctx-160] O:Dish(83,Clean)
[02:15:07.1140] [main-comp-ctx-160] washing dish 84
[02:15:07.2140] [main-comp-ctx-160] Dish washed 84.
[02:15:07.2150] [main-comp-ctx-160] O:Dish(84,Clean)
[02:15:07.2150] [main-comp-ctx-160] washing dish 85
[02:15:07.3150] [main-comp-ctx-160] Dish washed 85.
[02:15:07.3160] [main-comp-ctx-160] O:Dish(85,Clean)
[02:15:07.3160] [main-comp-ctx-160] washing dish 86
[02:15:07.4170] [main-comp-ctx-160] Dish washed 86.
[02:15:07.4170] [main-comp-ctx-160] O:Dish(86,Clean)
[02:15:07.4180] [main-comp-ctx-160] washing dish 87
[02:15:07.5180] [main-comp-ctx-160] Dish washed 87.
[02:15:07.5180] [main-comp-ctx-160] O:Dish(87,Clean)
[02:15:07.5190] [main-comp-ctx-160] washing dish 88
[02:15:07.6200] [main-comp-ctx-160] Dish washed 88.
[02:15:07.6200] [main-comp-ctx-160] O:Dish(88,Clean)
[02:15:07.6210] [main-comp-ctx-160] washing dish 89
[02:15:07.7210] [main-comp-ctx-160] Dish washed 89.
[02:15:07.7210] [main-comp-ctx-160] O:Dish(89,Clean)
[02:15:07.7220] [main-comp-ctx-160] washing dish 90
[02:15:07.8220] [main-comp-ctx-160] Dish washed 90.
[02:15:07.8220] [main-comp-ctx-160] O:Dish(90,Clean)
[02:15:07.8220] [main-comp-ctx-160] washing dish 91
[02:15:07.9230] [main-comp-ctx-160] Dish washed 91.
[02:15:07.9230] [main-comp-ctx-160] O:Dish(91,Clean)
[02:15:07.9230] [main-comp-ctx-160] washing dish 92
[02:15:08.0240] [main-comp-ctx-160] Dish washed 92.
[02:15:08.0240] [main-comp-ctx-160] O:Dish(92,Clean)
[02:15:08.0240] [main-comp-ctx-160] washing dish 93
[02:15:08.1250] [main-comp-ctx-160] Dish washed 93.
[02:15:08.1250] [main-comp-ctx-160] O:Dish(93,Clean)
[02:15:08.1250] [main-comp-ctx-160] washing dish 94
[02:15:08.2260] [main-comp-ctx-160] Dish washed 94.
[02:15:08.2260] [main-comp-ctx-160] O:Dish(94,Clean)
[02:15:08.2260] [main-comp-ctx-160] washing dish 95
[02:15:08.3270] [main-comp-ctx-160] Dish washed 95.
[02:15:08.3270] [main-comp-ctx-160] O:Dish(95,Clean)
[02:15:08.3270] [main-comp-ctx-160] washing dish 96
[02:15:08.4270] [main-comp-ctx-160] Dish washed 96.
[02:15:08.4280] [main-comp-ctx-160] O:Dish(96,Clean)
[02:15:08.4280] [main-comp-ctx-160] washing dish 97
[02:15:08.5280] [main-comp-ctx-160] Dish washed 97.
[02:15:08.5280] [main-comp-ctx-160] O:Dish(97,Clean)
[02:15:08.5290] [main-comp-ctx-160] washing dish 98
[02:15:08.6290] [main-comp-ctx-160] Dish washed 98.
[02:15:08.6290] [main-comp-ctx-160] O:Dish(98,Clean)
[02:15:08.6300] [main-comp-ctx-160] washing dish 99
[02:15:08.7300] [main-comp-ctx-160] Dish washed 99.
[02:15:08.7300] [main-comp-ctx-160] O:Dish(99,Clean)
[02:15:08.7300] [main-comp-ctx-160] washing dish 100
[02:15:08.8310] [main-comp-ctx-160] Dish washed 100.
[02:15:08.8310] [main-comp-ctx-160] O:Dish(100,Clean)
[02:15:10.8030] [run-main-1] Program Ends.
*/
