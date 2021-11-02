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
  .asyncBoundary(OverflowStrategy.BackPressure(5))
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
[05:52:43.3350] [run-main-0] Program starts.
[05:52:45.7980] [run-main-0] E: 1
[05:52:45.8320] [main-comp-ctx-131] washing dish 1
[05:52:45.8390] [run-main-0] E: 2
[05:52:45.8390] [run-main-0] E: 3
[05:52:45.8390] [run-main-0] E: 4
[05:52:45.8390] [run-main-0] E: 5
[05:52:45.8390] [run-main-0] E: 6
[05:52:45.8400] [run-main-0] E: 7
[05:52:45.8400] [run-main-0] E: 8
[05:52:45.8400] [run-main-0] E: 9
[05:52:46.0430] [main-comp-ctx-132] Dish washed 1.
[05:52:46.0430] [main-comp-ctx-131] washing dish 2
[05:52:46.0460] [main-comp-ctx-132] O:Dish(1,Clean)
[05:52:46.2440] [main-comp-ctx-131] washing dish 3
[05:52:46.2440] [main-comp-ctx-132] Dish washed 2.
[05:52:46.2450] [main-comp-ctx-132] O:Dish(2,Clean)
[05:52:46.4450] [main-comp-ctx-131] washing dish 4
[05:52:46.4450] [main-comp-ctx-132] Dish washed 3.
[05:52:46.4460] [main-comp-ctx-132] O:Dish(3,Clean)
[05:52:46.6460] [main-comp-ctx-131] washing dish 5
[05:52:46.6460] [main-comp-ctx-132] Dish washed 4.
[05:52:46.6460] [main-comp-ctx-132] O:Dish(4,Clean)
[05:52:46.8470] [main-comp-ctx-132] Dish washed 5.
[05:52:46.8460] [main-comp-ctx-131] washing dish 6
[05:52:46.8470] [main-comp-ctx-132] O:Dish(5,Clean)
[05:52:47.0480] [main-comp-ctx-132] Dish washed 6.
[05:52:47.0480] [main-comp-ctx-131] washing dish 7
[05:52:47.0480] [main-comp-ctx-132] O:Dish(6,Clean)
[05:52:47.2490] [main-comp-ctx-131] washing dish 8
[05:52:47.2490] [main-comp-ctx-132] Dish washed 7.
[05:52:47.2490] [main-comp-ctx-132] O:Dish(7,Clean)
[05:52:47.4490] [main-comp-ctx-131] washing dish 9
[05:52:47.4500] [main-comp-ctx-132] Dish washed 8.
[05:52:47.4500] [main-comp-ctx-132] O:Dish(8,Clean)
[05:52:47.6500] [main-comp-ctx-132] Dish washed 9.
[05:52:47.6510] [main-comp-ctx-132] O:Dish(9,Clean)
[05:52:47.6530] [main-comp-ctx-132] E: 10
[05:52:47.6530] [main-comp-ctx-132] E: 11
[05:52:47.6530] [main-comp-ctx-131] washing dish 10
[05:52:47.6530] [main-comp-ctx-132] E: 12
[05:52:47.6540] [main-comp-ctx-132] E: 13
[05:52:47.6550] [main-comp-ctx-132] E: 14
[05:52:47.6550] [main-comp-ctx-132] E: 15
[05:52:47.6550] [main-comp-ctx-132] E: 16
[05:52:47.6560] [main-comp-ctx-132] E: 17
[05:52:47.6560] [main-comp-ctx-132] E: 18
[05:52:47.8540] [main-comp-ctx-131] washing dish 11
[05:52:47.8540] [main-comp-ctx-132] Dish washed 10.
[05:52:47.8550] [main-comp-ctx-132] O:Dish(10,Clean)
[05:52:48.0550] [main-comp-ctx-131] washing dish 12
[05:52:48.0550] [main-comp-ctx-132] Dish washed 11.
[05:52:48.0560] [main-comp-ctx-132] O:Dish(11,Clean)
[05:52:48.2560] [main-comp-ctx-131] washing dish 13
[05:52:48.2560] [main-comp-ctx-132] Dish washed 12.
[05:52:48.2560] [main-comp-ctx-132] O:Dish(12,Clean)
[05:52:48.4570] [main-comp-ctx-131] washing dish 14
[05:52:48.4570] [main-comp-ctx-132] Dish washed 13.
[05:52:48.4570] [main-comp-ctx-132] O:Dish(13,Clean)
[05:52:48.6570] [main-comp-ctx-131] washing dish 15
[05:52:48.6570] [main-comp-ctx-132] Dish washed 14.
[05:52:48.6580] [main-comp-ctx-132] O:Dish(14,Clean)
[05:52:48.8580] [main-comp-ctx-131] washing dish 16
[05:52:48.8580] [main-comp-ctx-132] Dish washed 15.
[05:52:48.8590] [main-comp-ctx-132] O:Dish(15,Clean)
[05:52:49.0590] [main-comp-ctx-131] washing dish 17
[05:52:49.0590] [main-comp-ctx-132] Dish washed 16.
[05:52:49.0590] [main-comp-ctx-132] O:Dish(16,Clean)
[05:52:49.2600] [main-comp-ctx-131] washing dish 18
[05:52:49.2600] [main-comp-ctx-132] Dish washed 17.
[05:52:49.2610] [main-comp-ctx-132] O:Dish(17,Clean)
[05:52:49.4610] [main-comp-ctx-132] Dish washed 18.
[05:52:49.4610] [main-comp-ctx-132] O:Dish(18,Clean)
[05:52:49.4630] [main-comp-ctx-131] E: 19
[05:52:49.4640] [main-comp-ctx-133] washing dish 19
[05:52:49.4650] [main-comp-ctx-131] E: 20
[05:52:49.4660] [main-comp-ctx-131] E: 21
[05:52:49.4660] [main-comp-ctx-131] E: 22
[05:52:49.4670] [main-comp-ctx-131] E: 23
[05:52:49.4670] [main-comp-ctx-131] E: 24
[05:52:49.4670] [main-comp-ctx-131] E: 25
[05:52:49.4700] [main-comp-ctx-131] E: 26
[05:52:49.4700] [main-comp-ctx-131] E: 27
[05:52:49.6650] [main-comp-ctx-131] Dish washed 19.
[05:52:49.6650] [main-comp-ctx-133] washing dish 20
[05:52:49.6660] [main-comp-ctx-131] O:Dish(19,Clean)
[05:52:49.8660] [main-comp-ctx-133] washing dish 21
[05:52:49.8670] [main-comp-ctx-131] Dish washed 20.
[05:52:49.8670] [main-comp-ctx-131] O:Dish(20,Clean)
[05:52:50.0670] [main-comp-ctx-133] washing dish 22
[05:52:50.0670] [main-comp-ctx-131] Dish washed 21.
[05:52:50.0680] [main-comp-ctx-131] O:Dish(21,Clean)
[05:52:50.2680] [main-comp-ctx-131] Dish washed 22.
[05:52:50.2680] [main-comp-ctx-133] washing dish 23
[05:52:50.2680] [main-comp-ctx-131] O:Dish(22,Clean)
[05:52:50.4690] [main-comp-ctx-133] washing dish 24
[05:52:50.4690] [main-comp-ctx-131] Dish washed 23.
[05:52:50.4690] [main-comp-ctx-131] O:Dish(23,Clean)
[05:52:50.6690] [main-comp-ctx-133] washing dish 25
[05:52:50.6700] [main-comp-ctx-131] Dish washed 24.
[05:52:50.6700] [main-comp-ctx-131] O:Dish(24,Clean)
[05:52:50.8700] [main-comp-ctx-133] washing dish 26
[05:52:50.8700] [main-comp-ctx-131] Dish washed 25.
[05:52:50.8710] [main-comp-ctx-131] O:Dish(25,Clean)
[05:52:51.0710] [main-comp-ctx-133] washing dish 27
[05:52:51.0710] [main-comp-ctx-131] Dish washed 26.
[05:52:51.0720] [main-comp-ctx-131] O:Dish(26,Clean)
[05:52:51.2720] [main-comp-ctx-131] Dish washed 27.
[05:52:51.2720] [main-comp-ctx-131] O:Dish(27,Clean)
[05:52:51.2730] [main-comp-ctx-135] E: 28
[05:52:51.2740] [main-comp-ctx-131] washing dish 28
[05:52:51.2740] [main-comp-ctx-135] E: 29
[05:52:51.2740] [main-comp-ctx-135] E: 30
[05:52:51.2750] [main-comp-ctx-135] E: 31
[05:52:51.2750] [main-comp-ctx-135] E: 32
[05:52:51.2760] [main-comp-ctx-135] E: 33
[05:52:51.2770] [main-comp-ctx-135] E: 34
[05:52:51.2780] [main-comp-ctx-135] E: 35
[05:52:51.2790] [main-comp-ctx-135] E: 36
[05:52:51.4750] [main-comp-ctx-135] Dish washed 28.
[05:52:51.4750] [main-comp-ctx-135] O:Dish(28,Clean)
[05:52:51.4750] [main-comp-ctx-131] washing dish 29
[05:52:51.6770] [main-comp-ctx-131] washing dish 30
[05:52:51.6770] [main-comp-ctx-135] Dish washed 29.
[05:52:51.6770] [main-comp-ctx-135] O:Dish(29,Clean)
[05:52:51.8770] [main-comp-ctx-131] washing dish 31
[05:52:51.8780] [main-comp-ctx-135] Dish washed 30.
[05:52:51.8780] [main-comp-ctx-135] O:Dish(30,Clean)
[05:52:52.0780] [main-comp-ctx-131] washing dish 32
[05:52:52.0780] [main-comp-ctx-135] Dish washed 31.
[05:52:52.0790] [main-comp-ctx-135] O:Dish(31,Clean)
[05:52:52.2790] [main-comp-ctx-131] washing dish 33
[05:52:52.2790] [main-comp-ctx-135] Dish washed 32.
[05:52:52.2800] [main-comp-ctx-135] O:Dish(32,Clean)
[05:52:52.4800] [main-comp-ctx-131] washing dish 34
[05:52:52.4800] [main-comp-ctx-135] Dish washed 33.
[05:52:52.4800] [main-comp-ctx-135] O:Dish(33,Clean)
[05:52:52.6810] [main-comp-ctx-135] Dish washed 34.
[05:52:52.6810] [main-comp-ctx-131] washing dish 35
[05:52:52.6820] [main-comp-ctx-135] O:Dish(34,Clean)
[05:52:52.8820] [main-comp-ctx-131] washing dish 36
[05:52:52.8820] [main-comp-ctx-135] Dish washed 35.
[05:52:52.8830] [main-comp-ctx-135] O:Dish(35,Clean)
[05:52:53.0830] [main-comp-ctx-135] Dish washed 36.
[05:52:53.0830] [main-comp-ctx-135] O:Dish(36,Clean)
[05:52:53.0830] [main-comp-ctx-133] E: 37
[05:52:53.0840] [main-comp-ctx-133] E: 38
[05:52:53.0840] [main-comp-ctx-135] washing dish 37
[05:52:53.0850] [main-comp-ctx-133] E: 39
[05:52:53.0850] [main-comp-ctx-133] E: 40
[05:52:53.0860] [main-comp-ctx-133] E: 41
[05:52:53.0860] [main-comp-ctx-133] E: 42
[05:52:53.0870] [main-comp-ctx-133] E: 43
[05:52:53.0870] [main-comp-ctx-133] E: 44
[05:52:53.0880] [main-comp-ctx-133] E: 45
[05:52:53.2850] [main-comp-ctx-135] washing dish 38
[05:52:53.2850] [main-comp-ctx-133] Dish washed 37.
[05:52:53.2860] [main-comp-ctx-133] O:Dish(37,Clean)
[05:52:53.4860] [main-comp-ctx-135] washing dish 39
[05:52:53.4860] [main-comp-ctx-133] Dish washed 38.
[05:52:53.4860] [main-comp-ctx-133] O:Dish(38,Clean)
[05:52:53.6860] [main-comp-ctx-133] Dish washed 39.
[05:52:53.6860] [main-comp-ctx-135] washing dish 40
[05:52:53.6870] [main-comp-ctx-133] O:Dish(39,Clean)
[05:52:53.8880] [main-comp-ctx-135] washing dish 41
[05:52:53.8880] [main-comp-ctx-133] Dish washed 40.
[05:52:53.8880] [main-comp-ctx-133] O:Dish(40,Clean)
[05:52:54.0880] [main-comp-ctx-133] Dish washed 41.
[05:52:54.0880] [main-comp-ctx-135] washing dish 42
[05:52:54.0890] [main-comp-ctx-133] O:Dish(41,Clean)
[05:52:54.2890] [main-comp-ctx-133] Dish washed 42.
[05:52:54.2890] [main-comp-ctx-135] washing dish 43
[05:52:54.2900] [main-comp-ctx-133] O:Dish(42,Clean)
[05:52:54.4900] [main-comp-ctx-135] washing dish 44
[05:52:54.4900] [main-comp-ctx-133] Dish washed 43.
[05:52:54.4910] [main-comp-ctx-133] O:Dish(43,Clean)
[05:52:54.6910] [main-comp-ctx-135] washing dish 45
[05:52:54.6910] [main-comp-ctx-133] Dish washed 44.
[05:52:54.6920] [main-comp-ctx-133] O:Dish(44,Clean)
[05:52:54.8920] [main-comp-ctx-135] E: 46
[05:52:54.8920] [main-comp-ctx-135] E: 47
[05:52:54.8920] [main-comp-ctx-133] Dish washed 45.
[05:52:54.8930] [main-comp-ctx-135] E: 48
[05:52:54.8930] [main-comp-ctx-131] washing dish 46
[05:52:54.8930] [main-comp-ctx-133] O:Dish(45,Clean)
[05:52:54.8930] [main-comp-ctx-135] E: 49
[05:52:54.8940] [main-comp-ctx-135] E: 50
[05:52:54.8940] [main-comp-ctx-135] E: 51
[05:52:54.8940] [main-comp-ctx-135] E: 52
[05:52:54.8950] [main-comp-ctx-135] E: 53
[05:52:54.8950] [main-comp-ctx-135] E: 54
[05:52:55.0940] [main-comp-ctx-131] washing dish 47
[05:52:55.0940] [main-comp-ctx-135] Dish washed 46.
[05:52:55.0940] [main-comp-ctx-135] O:Dish(46,Clean)
[05:52:55.2950] [main-comp-ctx-131] washing dish 48
[05:52:55.2950] [main-comp-ctx-135] Dish washed 47.
[05:52:55.2950] [main-comp-ctx-135] O:Dish(47,Clean)
[05:52:55.4950] [main-comp-ctx-135] Dish washed 48.
[05:52:55.4950] [main-comp-ctx-131] washing dish 49
[05:52:55.4960] [main-comp-ctx-135] O:Dish(48,Clean)
[05:52:55.6960] [main-comp-ctx-135] Dish washed 49.
[05:52:55.6960] [main-comp-ctx-131] washing dish 50
[05:52:55.6980] [main-comp-ctx-135] O:Dish(49,Clean)
[05:52:55.8980] [main-comp-ctx-131] washing dish 51
[05:52:55.8980] [main-comp-ctx-135] Dish washed 50.
[05:52:55.8990] [main-comp-ctx-135] O:Dish(50,Clean)
[05:52:56.0990] [main-comp-ctx-135] Dish washed 51.
[05:52:56.0990] [main-comp-ctx-131] washing dish 52
[05:52:56.0990] [main-comp-ctx-135] O:Dish(51,Clean)
[05:52:56.3010] [main-comp-ctx-131] washing dish 53
[05:52:56.3010] [main-comp-ctx-135] Dish washed 52.
[05:52:56.3010] [main-comp-ctx-135] O:Dish(52,Clean)
[05:52:56.5010] [main-comp-ctx-131] washing dish 54
[05:52:56.5010] [main-comp-ctx-135] Dish washed 53.
[05:52:56.5020] [main-comp-ctx-135] O:Dish(53,Clean)
[05:52:56.7020] [main-comp-ctx-135] Dish washed 54.
[05:52:56.7020] [main-comp-ctx-131] E: 55
[05:52:56.7020] [main-comp-ctx-135] O:Dish(54,Clean)
[05:52:56.7030] [main-comp-ctx-131] E: 56
[05:52:56.7040] [main-comp-ctx-133] washing dish 55
[05:52:56.7050] [main-comp-ctx-131] E: 57
[05:52:56.7050] [main-comp-ctx-131] E: 58
[05:52:56.7060] [main-comp-ctx-131] E: 59
[05:52:56.7060] [main-comp-ctx-131] E: 60
[05:52:56.7080] [main-comp-ctx-131] E: 61
[05:52:56.7080] [main-comp-ctx-131] E: 62
[05:52:56.7090] [main-comp-ctx-131] E: 63
[05:52:56.9040] [main-comp-ctx-133] washing dish 56
[05:52:56.9040] [main-comp-ctx-131] Dish washed 55.
[05:52:56.9050] [main-comp-ctx-131] O:Dish(55,Clean)
[05:52:57.1050] [main-comp-ctx-131] Dish washed 56.
[05:52:57.1050] [main-comp-ctx-133] washing dish 57
[05:52:57.1060] [main-comp-ctx-131] O:Dish(56,Clean)
[05:52:57.3060] [main-comp-ctx-131] Dish washed 57.
[05:52:57.3060] [main-comp-ctx-133] washing dish 58
[05:52:57.3070] [main-comp-ctx-131] O:Dish(57,Clean)
[05:52:57.5080] [main-comp-ctx-133] washing dish 59
[05:52:57.5070] [main-comp-ctx-131] Dish washed 58.
[05:52:57.5080] [main-comp-ctx-131] O:Dish(58,Clean)
[05:52:57.7080] [main-comp-ctx-131] Dish washed 59.
[05:52:57.7080] [main-comp-ctx-133] washing dish 60
[05:52:57.7090] [main-comp-ctx-131] O:Dish(59,Clean)
[05:52:57.9090] [main-comp-ctx-131] Dish washed 60.
[05:52:57.9100] [main-comp-ctx-133] washing dish 61
[05:52:57.9100] [main-comp-ctx-131] O:Dish(60,Clean)
[05:52:58.1110] [main-comp-ctx-131] Dish washed 61.
[05:52:58.1110] [main-comp-ctx-133] washing dish 62
[05:52:58.1110] [main-comp-ctx-131] O:Dish(61,Clean)
[05:52:58.3120] [main-comp-ctx-131] Dish washed 62.
[05:52:58.3120] [main-comp-ctx-133] washing dish 63
[05:52:58.3120] [main-comp-ctx-131] O:Dish(62,Clean)
[05:52:58.5130] [main-comp-ctx-135] E: 64
[05:52:58.5130] [main-comp-ctx-131] Dish washed 63.
[05:52:58.5140] [main-comp-ctx-131] O:Dish(63,Clean)
[05:52:58.5140] [main-comp-ctx-133] washing dish 64
[05:52:58.5140] [main-comp-ctx-135] E: 65
[05:52:58.5140] [main-comp-ctx-135] E: 66
[05:52:58.5140] [main-comp-ctx-135] E: 67
[05:52:58.5140] [main-comp-ctx-135] E: 68
[05:52:58.5150] [main-comp-ctx-135] E: 69
[05:52:58.5150] [main-comp-ctx-135] E: 70
[05:52:58.5160] [main-comp-ctx-135] E: 71
[05:52:58.5160] [main-comp-ctx-135] E: 72
[05:52:58.7150] [main-comp-ctx-135] Dish washed 64.
[05:52:58.7150] [main-comp-ctx-133] washing dish 65
[05:52:58.7150] [main-comp-ctx-135] O:Dish(64,Clean)
[05:52:58.9160] [main-comp-ctx-135] Dish washed 65.
[05:52:58.9160] [main-comp-ctx-133] washing dish 66
[05:52:58.9170] [main-comp-ctx-135] O:Dish(65,Clean)
[05:52:59.1170] [main-comp-ctx-133] washing dish 67
[05:52:59.1170] [main-comp-ctx-135] Dish washed 66.
[05:52:59.1180] [main-comp-ctx-135] O:Dish(66,Clean)
[05:52:59.3180] [main-comp-ctx-133] washing dish 68
[05:52:59.3180] [main-comp-ctx-135] Dish washed 67.
[05:52:59.3180] [main-comp-ctx-135] O:Dish(67,Clean)
[05:52:59.5190] [main-comp-ctx-133] washing dish 69
[05:52:59.5190] [main-comp-ctx-135] Dish washed 68.
[05:52:59.5190] [main-comp-ctx-135] O:Dish(68,Clean)
[05:52:59.7190] [main-comp-ctx-133] washing dish 70
[05:52:59.7190] [main-comp-ctx-135] Dish washed 69.
[05:52:59.7200] [main-comp-ctx-135] O:Dish(69,Clean)
[05:52:59.9200] [main-comp-ctx-133] washing dish 71
[05:52:59.9200] [main-comp-ctx-135] Dish washed 70.
[05:52:59.9210] [main-comp-ctx-135] O:Dish(70,Clean)
[05:53:00.1210] [main-comp-ctx-133] washing dish 72
[05:53:00.1210] [main-comp-ctx-135] Dish washed 71.
[05:53:00.1210] [main-comp-ctx-135] O:Dish(71,Clean)
[05:53:00.3220] [main-comp-ctx-135] Dish washed 72.
[05:53:00.3220] [main-comp-ctx-133] E: 73
[05:53:00.3220] [main-comp-ctx-135] O:Dish(72,Clean)
[05:53:00.3220] [main-comp-ctx-133] E: 74
[05:53:00.3230] [main-comp-ctx-135] washing dish 73
[05:53:00.3230] [main-comp-ctx-133] E: 75
[05:53:00.3230] [main-comp-ctx-133] E: 76
[05:53:00.3230] [main-comp-ctx-133] E: 77
[05:53:00.3240] [main-comp-ctx-133] E: 78
[05:53:00.3240] [main-comp-ctx-133] E: 79
[05:53:00.3240] [main-comp-ctx-133] E: 80
[05:53:00.3250] [main-comp-ctx-133] E: 81
[05:53:00.5230] [main-comp-ctx-135] washing dish 74
[05:53:00.5230] [main-comp-ctx-133] Dish washed 73.
[05:53:00.5240] [main-comp-ctx-133] O:Dish(73,Clean)
[05:53:00.7240] [main-comp-ctx-133] Dish washed 74.
[05:53:00.7240] [main-comp-ctx-135] washing dish 75
[05:53:00.7240] [main-comp-ctx-133] O:Dish(74,Clean)
[05:53:00.9250] [main-comp-ctx-135] washing dish 76
[05:53:00.9250] [main-comp-ctx-133] Dish washed 75.
[05:53:00.9260] [main-comp-ctx-133] O:Dish(75,Clean)
[05:53:01.1260] [main-comp-ctx-135] washing dish 77
[05:53:01.1260] [main-comp-ctx-133] Dish washed 76.
[05:53:01.1260] [main-comp-ctx-133] O:Dish(76,Clean)
[05:53:01.3260] [main-comp-ctx-133] Dish washed 77.
[05:53:01.3260] [main-comp-ctx-135] washing dish 78
[05:53:01.3270] [main-comp-ctx-133] O:Dish(77,Clean)
[05:53:01.5270] [main-comp-ctx-135] washing dish 79
[05:53:01.5270] [main-comp-ctx-133] Dish washed 78.
[05:53:01.5280] [main-comp-ctx-133] O:Dish(78,Clean)
[05:53:01.7280] [main-comp-ctx-135] washing dish 80
[05:53:01.7290] [main-comp-ctx-133] Dish washed 79.
[05:53:01.7290] [main-comp-ctx-133] O:Dish(79,Clean)
[05:53:01.9290] [main-comp-ctx-135] washing dish 81
[05:53:01.9290] [main-comp-ctx-133] Dish washed 80.
[05:53:01.9290] [main-comp-ctx-133] O:Dish(80,Clean)
[05:53:02.1300] [main-comp-ctx-133] Dish washed 81.
[05:53:02.1300] [main-comp-ctx-131] E: 82
[05:53:02.1300] [main-comp-ctx-133] O:Dish(81,Clean)
[05:53:02.1310] [main-comp-ctx-131] E: 83
[05:53:02.1310] [main-comp-ctx-133] washing dish 82
[05:53:02.1310] [main-comp-ctx-131] E: 84
[05:53:02.1320] [main-comp-ctx-131] E: 85
[05:53:02.1320] [main-comp-ctx-131] E: 86
[05:53:02.1320] [main-comp-ctx-131] E: 87
[05:53:02.1330] [main-comp-ctx-131] E: 88
[05:53:02.1330] [main-comp-ctx-131] E: 89
[05:53:02.1340] [main-comp-ctx-131] E: 90
[05:53:02.3320] [main-comp-ctx-133] washing dish 83
[05:53:02.3320] [main-comp-ctx-131] Dish washed 82.
[05:53:02.3330] [main-comp-ctx-131] O:Dish(82,Clean)
[05:53:02.5330] [main-comp-ctx-133] washing dish 84
[05:53:02.5330] [main-comp-ctx-131] Dish washed 83.
[05:53:02.5330] [main-comp-ctx-131] O:Dish(83,Clean)
--- output limited due to scastie timeout ---
*/
