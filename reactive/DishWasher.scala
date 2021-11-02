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
  .mapParallelUnordered(1)(d => d.wash)
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
[04:28:24.1240] [run-main-0] Program starts.
[04:28:26.7190] [run-main-0] E: 1
[04:28:26.7410] [main-comp-ctx-133] washing dish 1
[04:28:26.7540] [run-main-0] E: 2
[04:28:26.7550] [run-main-0] E: 3
[04:28:26.9450] [main-comp-ctx-133] Dish washed 1.
[04:28:26.9460] [main-comp-ctx-133] O:Dish(1,Clean)
[04:28:26.9550] [main-comp-ctx-133] washing dish 2
[04:28:27.1560] [main-comp-ctx-133] Dish washed 2.
[04:28:27.1570] [main-comp-ctx-133] O:Dish(2,Clean)
[04:28:27.1580] [main-comp-ctx-133] washing dish 3
[04:28:27.3590] [main-comp-ctx-133] Dish washed 3.
[04:28:27.3600] [main-comp-ctx-133] O:Dish(3,Clean)
[04:28:27.3660] [main-comp-ctx-133] E: 4
[04:28:27.3670] [main-comp-ctx-133] E: 5
[04:28:27.3670] [main-comp-ctx-134] washing dish 4
[04:28:27.3680] [main-comp-ctx-133] E: 6
[04:28:27.5680] [main-comp-ctx-134] Dish washed 4.
[04:28:27.5680] [main-comp-ctx-134] O:Dish(4,Clean)
[04:28:27.5700] [main-comp-ctx-134] washing dish 5
[04:28:27.7700] [main-comp-ctx-134] Dish washed 5.
[04:28:27.7710] [main-comp-ctx-134] O:Dish(5,Clean)
[04:28:27.7720] [main-comp-ctx-134] washing dish 6
[04:28:27.9730] [main-comp-ctx-134] Dish washed 6.
[04:28:27.9730] [main-comp-ctx-134] O:Dish(6,Clean)
[04:28:27.9750] [main-comp-ctx-134] E: 7
[04:28:27.9770] [main-comp-ctx-134] E: 8
[04:28:27.9770] [main-comp-ctx-133] washing dish 7
[04:28:27.9770] [main-comp-ctx-134] E: 9
[04:28:28.1770] [main-comp-ctx-133] Dish washed 7.
[04:28:28.1780] [main-comp-ctx-133] O:Dish(7,Clean)
[04:28:28.1780] [main-comp-ctx-133] washing dish 8
[04:28:28.3800] [main-comp-ctx-133] Dish washed 8.
[04:28:28.3800] [main-comp-ctx-133] O:Dish(8,Clean)
[04:28:28.3810] [main-comp-ctx-133] washing dish 9
[04:28:28.5810] [main-comp-ctx-133] Dish washed 9.
[04:28:28.5820] [main-comp-ctx-133] O:Dish(9,Clean)
[04:28:28.5830] [main-comp-ctx-133] E: 10
[04:28:28.5840] [main-comp-ctx-133] E: 11
[04:28:28.5840] [main-comp-ctx-134] washing dish 10
[04:28:28.5840] [main-comp-ctx-133] E: 12
[04:28:28.7850] [main-comp-ctx-134] Dish washed 10.
[04:28:28.7850] [main-comp-ctx-134] O:Dish(10,Clean)
[04:28:28.7860] [main-comp-ctx-134] washing dish 11
[04:28:28.9870] [main-comp-ctx-134] Dish washed 11.
[04:28:28.9870] [main-comp-ctx-134] O:Dish(11,Clean)
[04:28:28.9880] [main-comp-ctx-134] washing dish 12
[04:28:29.1880] [main-comp-ctx-134] Dish washed 12.
[04:28:29.1880] [main-comp-ctx-134] O:Dish(12,Clean)
[04:28:29.1900] [main-comp-ctx-134] E: 13
[04:28:29.1910] [main-comp-ctx-134] E: 14
[04:28:29.1910] [main-comp-ctx-133] washing dish 13
[04:28:29.1920] [main-comp-ctx-134] E: 15
[04:28:29.3920] [main-comp-ctx-133] Dish washed 13.
[04:28:29.3930] [main-comp-ctx-133] O:Dish(13,Clean)
[04:28:29.3940] [main-comp-ctx-133] washing dish 14
[04:28:29.5940] [main-comp-ctx-133] Dish washed 14.
[04:28:29.5950] [main-comp-ctx-133] O:Dish(14,Clean)
[04:28:29.5960] [main-comp-ctx-133] washing dish 15
[04:28:29.7960] [main-comp-ctx-133] Dish washed 15.
[04:28:29.7960] [main-comp-ctx-133] O:Dish(15,Clean)
[04:28:29.7970] [main-comp-ctx-133] E: 16
[04:28:29.7980] [main-comp-ctx-133] E: 17
[04:28:29.7980] [main-comp-ctx-134] washing dish 16
[04:28:29.7990] [main-comp-ctx-133] E: 18
[04:28:29.9990] [main-comp-ctx-134] Dish washed 16.
[04:28:29.9990] [main-comp-ctx-134] O:Dish(16,Clean)
[04:28:30.0000] [main-comp-ctx-134] washing dish 17
[04:28:30.2000] [main-comp-ctx-134] Dish washed 17.
[04:28:30.2010] [main-comp-ctx-134] O:Dish(17,Clean)
[04:28:30.2010] [main-comp-ctx-134] washing dish 18
[04:28:30.4020] [main-comp-ctx-134] Dish washed 18.
[04:28:30.4020] [main-comp-ctx-134] O:Dish(18,Clean)
[04:28:30.4030] [main-comp-ctx-134] E: 19
[04:28:30.4040] [main-comp-ctx-134] E: 20
[04:28:30.4040] [main-comp-ctx-133] washing dish 19
[04:28:30.4050] [main-comp-ctx-134] E: 21
[04:28:30.6050] [main-comp-ctx-133] Dish washed 19.
[04:28:30.6050] [main-comp-ctx-133] O:Dish(19,Clean)
[04:28:30.6060] [main-comp-ctx-133] washing dish 20
[04:28:30.8070] [main-comp-ctx-133] Dish washed 20.
[04:28:30.8080] [main-comp-ctx-133] O:Dish(20,Clean)
[04:28:30.8090] [main-comp-ctx-133] washing dish 21
[04:28:31.0090] [main-comp-ctx-133] Dish washed 21.
[04:28:31.0100] [main-comp-ctx-133] O:Dish(21,Clean)
[04:28:31.0100] [main-comp-ctx-133] E: 22
[04:28:31.0110] [main-comp-ctx-133] E: 23
[04:28:31.0110] [main-comp-ctx-134] washing dish 22
[04:28:31.0120] [main-comp-ctx-133] E: 24
[04:28:31.2120] [main-comp-ctx-134] Dish washed 22.
[04:28:31.2120] [main-comp-ctx-134] O:Dish(22,Clean)
[04:28:31.2130] [main-comp-ctx-134] washing dish 23
[04:28:31.4130] [main-comp-ctx-134] Dish washed 23.
[04:28:31.4140] [main-comp-ctx-134] O:Dish(23,Clean)
[04:28:31.4140] [main-comp-ctx-134] washing dish 24
[04:28:31.6150] [main-comp-ctx-134] Dish washed 24.
[04:28:31.6160] [main-comp-ctx-134] O:Dish(24,Clean)
[04:28:31.6170] [main-comp-ctx-134] E: 25
[04:28:31.6180] [main-comp-ctx-134] E: 26
[04:28:31.6190] [main-comp-ctx-134] E: 27
[04:28:31.6200] [main-comp-ctx-133] washing dish 25
[04:28:31.8220] [main-comp-ctx-133] Dish washed 25.
[04:28:31.8220] [main-comp-ctx-133] O:Dish(25,Clean)
[04:28:31.8230] [main-comp-ctx-133] washing dish 26
[04:28:32.0230] [main-comp-ctx-133] Dish washed 26.
[04:28:32.0240] [main-comp-ctx-133] O:Dish(26,Clean)
[04:28:32.0240] [main-comp-ctx-133] washing dish 27
[04:28:32.2250] [main-comp-ctx-133] Dish washed 27.
[04:28:32.2250] [main-comp-ctx-133] O:Dish(27,Clean)
[04:28:32.2260] [main-comp-ctx-134] E: 28
[04:28:32.2270] [main-comp-ctx-133] washing dish 28
[04:28:32.2270] [main-comp-ctx-134] E: 29
[04:28:32.2280] [main-comp-ctx-134] E: 30
[04:28:32.4270] [main-comp-ctx-133] Dish washed 28.
[04:28:32.4280] [main-comp-ctx-133] O:Dish(28,Clean)
[04:28:32.4280] [main-comp-ctx-133] washing dish 29
[04:28:32.6290] [main-comp-ctx-133] Dish washed 29.
[04:28:32.6290] [main-comp-ctx-133] O:Dish(29,Clean)
[04:28:32.6300] [main-comp-ctx-133] washing dish 30
[04:28:32.8300] [main-comp-ctx-133] Dish washed 30.
[04:28:32.8300] [main-comp-ctx-133] O:Dish(30,Clean)
[04:28:32.8310] [main-comp-ctx-133] E: 31
[04:28:32.8320] [main-comp-ctx-133] E: 32
[04:28:32.8320] [main-comp-ctx-134] washing dish 31
[04:28:32.8330] [main-comp-ctx-133] E: 33
[04:28:33.0330] [main-comp-ctx-134] Dish washed 31.
[04:28:33.0340] [main-comp-ctx-134] O:Dish(31,Clean)
[04:28:33.0350] [main-comp-ctx-134] washing dish 32
[04:28:33.2350] [main-comp-ctx-134] Dish washed 32.
[04:28:33.2350] [main-comp-ctx-134] O:Dish(32,Clean)
[04:28:33.2360] [main-comp-ctx-134] washing dish 33
[04:28:33.4360] [main-comp-ctx-134] Dish washed 33.
[04:28:33.4360] [main-comp-ctx-134] O:Dish(33,Clean)
[04:28:33.4380] [main-comp-ctx-133] E: 34
[04:28:33.4380] [main-comp-ctx-133] E: 35
[04:28:33.4380] [main-comp-ctx-134] washing dish 34
[04:28:33.4380] [main-comp-ctx-133] E: 36
[04:28:33.6390] [main-comp-ctx-134] Dish washed 34.
[04:28:33.6390] [main-comp-ctx-134] O:Dish(34,Clean)
[04:28:33.6400] [main-comp-ctx-134] washing dish 35
[04:28:33.8410] [main-comp-ctx-134] Dish washed 35.
[04:28:33.8410] [main-comp-ctx-134] O:Dish(35,Clean)
[04:28:33.8420] [main-comp-ctx-134] washing dish 36
[04:28:34.0420] [main-comp-ctx-134] Dish washed 36.
[04:28:34.0430] [main-comp-ctx-134] O:Dish(36,Clean)
[04:28:34.0440] [main-comp-ctx-133] E: 37
[04:28:34.0440] [main-comp-ctx-133] E: 38
[04:28:34.0450] [main-comp-ctx-133] E: 39
[04:28:34.0450] [main-comp-ctx-134] washing dish 37
[04:28:34.2460] [main-comp-ctx-134] Dish washed 37.
[04:28:34.2460] [main-comp-ctx-134] O:Dish(37,Clean)
[04:28:34.2470] [main-comp-ctx-134] washing dish 38
[04:28:34.4480] [main-comp-ctx-134] Dish washed 38.
[04:28:34.4480] [main-comp-ctx-134] O:Dish(38,Clean)
[04:28:34.4490] [main-comp-ctx-134] washing dish 39
[04:28:34.6490] [main-comp-ctx-134] Dish washed 39.
[04:28:34.6500] [main-comp-ctx-134] O:Dish(39,Clean)
[04:28:34.6510] [main-comp-ctx-134] E: 40
[04:28:34.6510] [main-comp-ctx-133] washing dish 40
[04:28:34.6520] [main-comp-ctx-134] E: 41
[04:28:34.6530] [main-comp-ctx-134] E: 42
[04:28:34.8520] [main-comp-ctx-133] Dish washed 40.
[04:28:34.8520] [main-comp-ctx-133] O:Dish(40,Clean)
[04:28:34.8530] [main-comp-ctx-133] washing dish 41
[04:28:35.0540] [main-comp-ctx-133] Dish washed 41.
[04:28:35.0540] [main-comp-ctx-133] O:Dish(41,Clean)
[04:28:35.0550] [main-comp-ctx-133] washing dish 42
[04:28:35.2550] [main-comp-ctx-133] Dish washed 42.
[04:28:35.2550] [main-comp-ctx-133] O:Dish(42,Clean)
[04:28:35.2570] [main-comp-ctx-134] E: 43
[04:28:35.2580] [main-comp-ctx-133] washing dish 43
[04:28:35.2580] [main-comp-ctx-134] E: 44
[04:28:35.2580] [main-comp-ctx-134] E: 45
[04:28:35.4580] [main-comp-ctx-133] Dish washed 43.
[04:28:35.4580] [main-comp-ctx-133] O:Dish(43,Clean)
[04:28:35.4600] [main-comp-ctx-133] washing dish 44
[04:28:35.6600] [main-comp-ctx-133] Dish washed 44.
[04:28:35.6610] [main-comp-ctx-133] O:Dish(44,Clean)
[04:28:35.6620] [main-comp-ctx-133] washing dish 45
[04:28:35.8620] [main-comp-ctx-133] Dish washed 45.
[04:28:35.8630] [main-comp-ctx-133] O:Dish(45,Clean)
[04:28:35.8630] [main-comp-ctx-133] E: 46
[04:28:35.8640] [main-comp-ctx-133] E: 47
[04:28:35.8640] [main-comp-ctx-133] E: 48
[04:28:35.8640] [main-comp-ctx-134] washing dish 46
[04:28:36.0660] [main-comp-ctx-134] Dish washed 46.
[04:28:36.0660] [main-comp-ctx-134] O:Dish(46,Clean)
[04:28:36.0670] [main-comp-ctx-134] washing dish 47
[04:28:36.2670] [main-comp-ctx-134] Dish washed 47.
[04:28:36.2680] [main-comp-ctx-134] O:Dish(47,Clean)
[04:28:36.2680] [main-comp-ctx-134] washing dish 48
[04:28:36.4690] [main-comp-ctx-134] Dish washed 48.
[04:28:36.4690] [main-comp-ctx-134] O:Dish(48,Clean)
[04:28:36.4700] [main-comp-ctx-134] E: 49
[04:28:36.4720] [main-comp-ctx-134] E: 50
[04:28:36.4720] [main-comp-ctx-133] washing dish 49
[04:28:36.4730] [main-comp-ctx-134] E: 51
[04:28:36.6720] [main-comp-ctx-133] Dish washed 49.
[04:28:36.6730] [main-comp-ctx-133] O:Dish(49,Clean)
[04:28:36.6730] [main-comp-ctx-133] washing dish 50
[04:28:36.8740] [main-comp-ctx-133] Dish washed 50.
[04:28:36.8740] [main-comp-ctx-133] O:Dish(50,Clean)
[04:28:36.8750] [main-comp-ctx-133] washing dish 51
[04:28:37.0750] [main-comp-ctx-133] Dish washed 51.
[04:28:37.0760] [main-comp-ctx-133] O:Dish(51,Clean)
[04:28:37.0770] [main-comp-ctx-133] E: 52
[04:28:37.0770] [main-comp-ctx-133] E: 53
[04:28:37.0780] [main-comp-ctx-133] E: 54
[04:28:37.0780] [main-comp-ctx-134] washing dish 52
[04:28:37.2790] [main-comp-ctx-134] Dish washed 52.
[04:28:37.2790] [main-comp-ctx-134] O:Dish(52,Clean)
[04:28:37.2800] [main-comp-ctx-134] washing dish 53
[04:28:37.4800] [main-comp-ctx-134] Dish washed 53.
[04:28:37.4810] [main-comp-ctx-134] O:Dish(53,Clean)
[04:28:37.4810] [main-comp-ctx-134] washing dish 54
[04:28:37.6820] [main-comp-ctx-134] Dish washed 54.
[04:28:37.6820] [main-comp-ctx-134] O:Dish(54,Clean)
[04:28:37.6830] [main-comp-ctx-134] E: 55
[04:28:37.6840] [main-comp-ctx-133] washing dish 55
[04:28:37.6830] [main-comp-ctx-134] E: 56
[04:28:37.6840] [main-comp-ctx-134] E: 57
[04:28:37.8840] [main-comp-ctx-133] Dish washed 55.
[04:28:37.8850] [main-comp-ctx-133] O:Dish(55,Clean)
[04:28:37.8850] [main-comp-ctx-133] washing dish 56
[04:28:38.0860] [main-comp-ctx-133] Dish washed 56.
[04:28:38.0860] [main-comp-ctx-133] O:Dish(56,Clean)
[04:28:38.0870] [main-comp-ctx-133] washing dish 57
[04:28:38.2880] [main-comp-ctx-133] Dish washed 57.
[04:28:38.2880] [main-comp-ctx-133] O:Dish(57,Clean)
[04:28:38.2900] [main-comp-ctx-133] E: 58
[04:28:38.2900] [main-comp-ctx-134] washing dish 58
[04:28:38.2900] [main-comp-ctx-133] E: 59
[04:28:38.2920] [main-comp-ctx-133] E: 60
[04:28:38.4910] [main-comp-ctx-134] Dish washed 58.
[04:28:38.4910] [main-comp-ctx-134] O:Dish(58,Clean)
[04:28:38.4920] [main-comp-ctx-134] washing dish 59
[04:28:38.6920] [main-comp-ctx-134] Dish washed 59.
[04:28:38.6930] [main-comp-ctx-134] O:Dish(59,Clean)
[04:28:38.6930] [main-comp-ctx-134] washing dish 60
[04:28:38.8940] [main-comp-ctx-134] Dish washed 60.
[04:28:38.8940] [main-comp-ctx-134] O:Dish(60,Clean)
[04:28:38.8950] [main-comp-ctx-134] E: 61
[04:28:38.8960] [main-comp-ctx-134] E: 62
[04:28:38.8960] [main-comp-ctx-133] washing dish 61
[04:28:38.8960] [main-comp-ctx-134] E: 63
[04:28:39.0970] [main-comp-ctx-133] Dish washed 61.
[04:28:39.0970] [main-comp-ctx-133] O:Dish(61,Clean)
[04:28:39.0980] [main-comp-ctx-133] washing dish 62
[04:28:39.2980] [main-comp-ctx-133] Dish washed 62.
[04:28:39.2990] [main-comp-ctx-133] O:Dish(62,Clean)
[04:28:39.2990] [main-comp-ctx-133] washing dish 63
[04:28:39.5000] [main-comp-ctx-133] Dish washed 63.
[04:28:39.5000] [main-comp-ctx-133] O:Dish(63,Clean)
[04:28:39.5030] [main-comp-ctx-133] E: 64
[04:28:39.5040] [main-comp-ctx-134] washing dish 64
[04:28:39.5040] [main-comp-ctx-133] E: 65
[04:28:39.5060] [main-comp-ctx-133] E: 66
[04:28:39.7040] [main-comp-ctx-134] Dish washed 64.
[04:28:39.7050] [main-comp-ctx-134] O:Dish(64,Clean)
[04:28:39.7050] [main-comp-ctx-134] washing dish 65
[04:28:39.9060] [main-comp-ctx-134] Dish washed 65.
[04:28:39.9060] [main-comp-ctx-134] O:Dish(65,Clean)
[04:28:39.9060] [main-comp-ctx-134] washing dish 66
[04:28:40.1070] [main-comp-ctx-134] Dish washed 66.
[04:28:40.1070] [main-comp-ctx-134] O:Dish(66,Clean)
[04:28:40.1080] [main-comp-ctx-134] E: 67
[04:28:40.1080] [main-comp-ctx-134] E: 68
[04:28:40.1090] [main-comp-ctx-134] E: 69
[04:28:40.1090] [main-comp-ctx-133] washing dish 67
[04:28:40.3110] [main-comp-ctx-133] Dish washed 67.
[04:28:40.3110] [main-comp-ctx-133] O:Dish(67,Clean)
[04:28:40.3120] [main-comp-ctx-133] washing dish 68
[04:28:40.5120] [main-comp-ctx-133] Dish washed 68.
[04:28:40.5130] [main-comp-ctx-133] O:Dish(68,Clean)
[04:28:40.5140] [main-comp-ctx-133] washing dish 69
[04:28:40.7140] [main-comp-ctx-133] Dish washed 69.
[04:28:40.7150] [main-comp-ctx-133] O:Dish(69,Clean)
[04:28:40.7160] [main-comp-ctx-134] E: 70
[04:28:40.7160] [main-comp-ctx-134] E: 71
[04:28:40.7170] [main-comp-ctx-134] E: 72
[04:28:40.7170] [main-comp-ctx-134] washing dish 70
[04:28:40.9180] [main-comp-ctx-134] Dish washed 70.
[04:28:40.9180] [main-comp-ctx-134] O:Dish(70,Clean)
[04:28:40.9190] [main-comp-ctx-134] washing dish 71
[04:28:41.1190] [main-comp-ctx-134] Dish washed 71.
[04:28:41.1190] [main-comp-ctx-134] O:Dish(71,Clean)
[04:28:41.1200] [main-comp-ctx-134] washing dish 72
[04:28:41.3200] [main-comp-ctx-134] Dish washed 72.
[04:28:41.3210] [main-comp-ctx-134] O:Dish(72,Clean)
[04:28:41.3210] [main-comp-ctx-134] E: 73
[04:28:41.3220] [main-comp-ctx-134] E: 74
[04:28:41.3220] [main-comp-ctx-133] washing dish 73
[04:28:41.3220] [main-comp-ctx-134] E: 75
[04:28:41.5230] [main-comp-ctx-133] Dish washed 73.
[04:28:41.5230] [main-comp-ctx-133] O:Dish(73,Clean)
[04:28:41.5230] [main-comp-ctx-133] washing dish 74
[04:28:41.7240] [main-comp-ctx-133] Dish washed 74.
[04:28:41.7240] [main-comp-ctx-133] O:Dish(74,Clean)
[04:28:41.7250] [main-comp-ctx-133] washing dish 75
[04:28:41.9250] [main-comp-ctx-133] Dish washed 75.
[04:28:41.9250] [main-comp-ctx-133] O:Dish(75,Clean)
[04:28:41.9260] [main-comp-ctx-133] E: 76
[04:28:41.9270] [main-comp-ctx-134] washing dish 76
[04:28:41.9270] [main-comp-ctx-133] E: 77
[04:28:41.9280] [main-comp-ctx-133] E: 78
[04:28:42.1270] [main-comp-ctx-134] Dish washed 76.
[04:28:42.1280] [main-comp-ctx-134] O:Dish(76,Clean)
[04:28:42.1280] [main-comp-ctx-134] washing dish 77
[04:28:42.3290] [main-comp-ctx-134] Dish washed 77.
[04:28:42.3290] [main-comp-ctx-134] O:Dish(77,Clean)
[04:28:42.3300] [main-comp-ctx-134] washing dish 78
[04:28:42.5300] [main-comp-ctx-134] Dish washed 78.
[04:28:42.5310] [main-comp-ctx-134] O:Dish(78,Clean)
[04:28:42.5320] [main-comp-ctx-133] E: 79
[04:28:42.5320] [main-comp-ctx-133] E: 80
[04:28:42.5330] [main-comp-ctx-134] washing dish 79
[04:28:42.5330] [main-comp-ctx-133] E: 81
[04:28:42.7330] [main-comp-ctx-134] Dish washed 79.
[04:28:42.7330] [main-comp-ctx-134] O:Dish(79,Clean)
[04:28:42.7340] [main-comp-ctx-134] washing dish 80
[04:28:42.9340] [main-comp-ctx-134] Dish washed 80.
[04:28:42.9350] [main-comp-ctx-134] O:Dish(80,Clean)
[04:28:42.9350] [main-comp-ctx-134] washing dish 81
[04:28:43.1360] [main-comp-ctx-134] Dish washed 81.
[04:28:43.1360] [main-comp-ctx-134] O:Dish(81,Clean)
[04:28:43.1370] [main-comp-ctx-134] E: 82
[04:28:43.1370] [main-comp-ctx-134] E: 83
[04:28:43.1370] [main-comp-ctx-133] washing dish 82
[04:28:43.1370] [main-comp-ctx-134] E: 84
--- timeout in scastie after 30s ---
*/
