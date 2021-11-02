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
  .asyncBoundary(OverflowStrategy.BackPressure(20))
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
[02:23:18.5390] [run-main-2] Program starts.
[02:23:18.5560] [run-main-2] E: 1
[02:23:18.5620] [run-main-2] E: 2
[02:23:18.5620] [run-main-2] E: 3
[02:23:18.5620] [run-main-2] E: 4
[02:23:18.5620] [run-main-2] E: 5
[02:23:18.5630] [run-main-2] E: 6
[02:23:18.5630] [run-main-2] E: 7
[02:23:18.5630] [run-main-2] E: 8
[02:23:18.5630] [run-main-2] E: 9
[02:23:18.5630] [run-main-2] E: 10
[02:23:18.5630] [run-main-2] E: 11
[02:23:18.5640] [run-main-2] E: 12
[02:23:18.5640] [run-main-2] E: 13
[02:23:18.5650] [run-main-2] E: 14
[02:23:18.5650] [run-main-2] E: 15
[02:23:18.5650] [run-main-2] E: 16
[02:23:18.5650] [run-main-2] E: 17
[02:23:18.5660] [run-main-2] E: 18
[02:23:18.5660] [run-main-2] E: 19
[02:23:18.5660] [run-main-2] E: 20
[02:23:18.5660] [run-main-2] E: 21
[02:23:18.5670] [run-main-2] E: 22
[02:23:18.5670] [run-main-2] E: 23
[02:23:18.5670] [run-main-2] E: 24
[02:23:18.5670] [run-main-2] E: 25
[02:23:18.5670] [run-main-2] E: 26
[02:23:18.5680] [run-main-2] E: 27
[02:23:18.5680] [run-main-2] E: 28
[02:23:18.5680] [run-main-2] E: 29
[02:23:18.5690] [run-main-2] E: 30
[02:23:18.5690] [run-main-2] E: 31
[02:23:18.5690] [run-main-2] E: 32
[02:23:18.5700] [run-main-2] E: 33
[02:23:18.5770] [main-comp-ctx-195] washing dish 1
[02:23:18.6820] [main-comp-ctx-195] Dish washed 1.
[02:23:18.6830] [main-comp-ctx-195] O:Dish(1,Clean)
[02:23:18.6830] [main-comp-ctx-195] washing dish 2
[02:23:18.7840] [main-comp-ctx-195] Dish washed 2.
[02:23:18.7840] [main-comp-ctx-195] O:Dish(2,Clean)
[02:23:18.7840] [main-comp-ctx-195] washing dish 3
[02:23:18.8850] [main-comp-ctx-195] Dish washed 3.
[02:23:18.8850] [main-comp-ctx-195] O:Dish(3,Clean)
[02:23:18.8850] [main-comp-ctx-195] washing dish 4
[02:23:18.9860] [main-comp-ctx-195] Dish washed 4.
[02:23:18.9860] [main-comp-ctx-195] O:Dish(4,Clean)
[02:23:18.9860] [main-comp-ctx-195] washing dish 5
[02:23:19.0870] [main-comp-ctx-195] Dish washed 5.
[02:23:19.0870] [main-comp-ctx-195] O:Dish(5,Clean)
[02:23:19.0870] [main-comp-ctx-195] washing dish 6
[02:23:19.1880] [main-comp-ctx-195] Dish washed 6.
[02:23:19.1880] [main-comp-ctx-195] O:Dish(6,Clean)
[02:23:19.1880] [main-comp-ctx-195] washing dish 7
[02:23:19.2890] [main-comp-ctx-195] Dish washed 7.
[02:23:19.2890] [main-comp-ctx-195] O:Dish(7,Clean)
[02:23:19.2890] [main-comp-ctx-195] washing dish 8
[02:23:19.3900] [main-comp-ctx-195] Dish washed 8.
[02:23:19.3900] [main-comp-ctx-195] O:Dish(8,Clean)
[02:23:19.3900] [main-comp-ctx-195] washing dish 9
[02:23:19.4910] [main-comp-ctx-195] Dish washed 9.
[02:23:19.4910] [main-comp-ctx-195] O:Dish(9,Clean)
[02:23:19.4910] [main-comp-ctx-195] washing dish 10
[02:23:19.5910] [main-comp-ctx-195] Dish washed 10.
[02:23:19.5920] [main-comp-ctx-195] O:Dish(10,Clean)
[02:23:19.5920] [main-comp-ctx-195] washing dish 11
[02:23:19.6920] [main-comp-ctx-195] Dish washed 11.
[02:23:19.6920] [main-comp-ctx-195] O:Dish(11,Clean)
[02:23:19.6930] [main-comp-ctx-195] washing dish 12
[02:23:19.7930] [main-comp-ctx-195] Dish washed 12.
[02:23:19.7930] [main-comp-ctx-195] O:Dish(12,Clean)
[02:23:19.7930] [main-comp-ctx-195] washing dish 13
[02:23:19.8940] [main-comp-ctx-195] Dish washed 13.
[02:23:19.8940] [main-comp-ctx-195] O:Dish(13,Clean)
[02:23:19.8940] [main-comp-ctx-195] washing dish 14
[02:23:19.9950] [main-comp-ctx-195] Dish washed 14.
[02:23:19.9950] [main-comp-ctx-195] O:Dish(14,Clean)
[02:23:19.9950] [main-comp-ctx-195] washing dish 15
[02:23:20.0960] [main-comp-ctx-195] Dish washed 15.
[02:23:20.0960] [main-comp-ctx-195] O:Dish(15,Clean)
[02:23:20.0960] [main-comp-ctx-195] washing dish 16
[02:23:20.1970] [main-comp-ctx-195] Dish washed 16.
[02:23:20.1970] [main-comp-ctx-195] O:Dish(16,Clean)
[02:23:20.2010] [main-comp-ctx-195] washing dish 17
[02:23:20.3010] [main-comp-ctx-195] Dish washed 17.
[02:23:20.3020] [main-comp-ctx-195] O:Dish(17,Clean)
[02:23:20.3020] [main-comp-ctx-195] washing dish 18
[02:23:20.4020] [main-comp-ctx-195] Dish washed 18.
[02:23:20.4030] [main-comp-ctx-195] O:Dish(18,Clean)
[02:23:20.4030] [main-comp-ctx-195] washing dish 19
[02:23:20.5030] [main-comp-ctx-195] Dish washed 19.
[02:23:20.5030] [main-comp-ctx-195] O:Dish(19,Clean)
[02:23:20.5040] [main-comp-ctx-195] washing dish 20
[02:23:20.6040] [main-comp-ctx-195] Dish washed 20.
[02:23:20.6040] [main-comp-ctx-195] O:Dish(20,Clean)
[02:23:20.6050] [main-comp-ctx-195] washing dish 21
[02:23:20.7060] [main-comp-ctx-195] Dish washed 21.
[02:23:20.7060] [main-comp-ctx-195] O:Dish(21,Clean)
[02:23:20.7060] [main-comp-ctx-195] washing dish 22
[02:23:20.8060] [main-comp-ctx-195] Dish washed 22.
[02:23:20.8060] [main-comp-ctx-195] O:Dish(22,Clean)
[02:23:20.8070] [main-comp-ctx-195] washing dish 23
[02:23:20.9070] [main-comp-ctx-195] Dish washed 23.
[02:23:20.9070] [main-comp-ctx-195] O:Dish(23,Clean)
[02:23:20.9070] [main-comp-ctx-195] washing dish 24
[02:23:21.0070] [main-comp-ctx-195] Dish washed 24.
[02:23:21.0070] [main-comp-ctx-195] O:Dish(24,Clean)
[02:23:21.0080] [main-comp-ctx-195] washing dish 25
[02:23:21.1080] [main-comp-ctx-195] Dish washed 25.
[02:23:21.1090] [main-comp-ctx-195] O:Dish(25,Clean)
[02:23:21.1090] [main-comp-ctx-195] washing dish 26
[02:23:21.2090] [main-comp-ctx-195] Dish washed 26.
[02:23:21.2090] [main-comp-ctx-195] O:Dish(26,Clean)
[02:23:21.2100] [main-comp-ctx-195] washing dish 27
[02:23:21.3100] [main-comp-ctx-195] Dish washed 27.
[02:23:21.3100] [main-comp-ctx-195] O:Dish(27,Clean)
[02:23:21.3110] [main-comp-ctx-195] washing dish 28
[02:23:21.4110] [main-comp-ctx-195] Dish washed 28.
[02:23:21.4110] [main-comp-ctx-195] O:Dish(28,Clean)
[02:23:21.4110] [main-comp-ctx-195] washing dish 29
[02:23:21.5120] [main-comp-ctx-195] Dish washed 29.
[02:23:21.5120] [main-comp-ctx-195] O:Dish(29,Clean)
[02:23:21.5120] [main-comp-ctx-195] washing dish 30
[02:23:21.6120] [main-comp-ctx-195] Dish washed 30.
[02:23:21.6120] [main-comp-ctx-195] O:Dish(30,Clean)
[02:23:21.6130] [main-comp-ctx-195] washing dish 31
[02:23:21.7130] [main-comp-ctx-195] Dish washed 31.
[02:23:21.7130] [main-comp-ctx-195] O:Dish(31,Clean)
[02:23:21.7130] [main-comp-ctx-195] washing dish 32
[02:23:21.8140] [main-comp-ctx-195] Dish washed 32.
[02:23:21.8140] [main-comp-ctx-195] O:Dish(32,Clean)
[02:23:21.8140] [main-comp-ctx-195] washing dish 33
[02:23:21.9140] [main-comp-ctx-195] Dish washed 33.
[02:23:21.9150] [main-comp-ctx-195] O:Dish(33,Clean)
[02:23:21.9200] [main-comp-ctx-196] E: 34
[02:23:21.9200] [main-comp-ctx-195] washing dish 34
[02:23:21.9200] [main-comp-ctx-196] E: 35
[02:23:21.9210] [main-comp-ctx-196] E: 36
[02:23:21.9210] [main-comp-ctx-196] E: 37
[02:23:21.9220] [main-comp-ctx-196] E: 38
[02:23:21.9240] [main-comp-ctx-196] E: 39
[02:23:21.9240] [main-comp-ctx-196] E: 40
[02:23:21.9240] [main-comp-ctx-196] E: 41
[02:23:21.9240] [main-comp-ctx-196] E: 42
[02:23:21.9240] [main-comp-ctx-196] E: 43
[02:23:21.9240] [main-comp-ctx-196] E: 44
[02:23:21.9240] [main-comp-ctx-196] E: 45
[02:23:21.9240] [main-comp-ctx-196] E: 46
[02:23:21.9250] [main-comp-ctx-196] E: 47
[02:23:21.9250] [main-comp-ctx-196] E: 48
[02:23:21.9250] [main-comp-ctx-196] E: 49
[02:23:21.9250] [main-comp-ctx-196] E: 50
[02:23:21.9250] [main-comp-ctx-196] E: 51
[02:23:21.9250] [main-comp-ctx-196] E: 52
[02:23:21.9250] [main-comp-ctx-196] E: 53
[02:23:21.9250] [main-comp-ctx-196] E: 54
[02:23:21.9260] [main-comp-ctx-196] E: 55
[02:23:21.9260] [main-comp-ctx-196] E: 56
[02:23:21.9260] [main-comp-ctx-196] E: 57
[02:23:21.9260] [main-comp-ctx-196] E: 58
[02:23:21.9260] [main-comp-ctx-196] E: 59
[02:23:21.9260] [main-comp-ctx-196] E: 60
[02:23:21.9260] [main-comp-ctx-196] E: 61
[02:23:21.9280] [main-comp-ctx-196] E: 62
[02:23:21.9280] [main-comp-ctx-196] E: 63
[02:23:21.9280] [main-comp-ctx-196] E: 64
[02:23:21.9280] [main-comp-ctx-196] E: 65
[02:23:21.9290] [main-comp-ctx-196] E: 66
[02:23:22.0210] [main-comp-ctx-195] Dish washed 34.
[02:23:22.0210] [main-comp-ctx-195] O:Dish(34,Clean)
[02:23:22.0210] [main-comp-ctx-195] washing dish 35
[02:23:22.1220] [main-comp-ctx-195] Dish washed 35.
[02:23:22.1220] [main-comp-ctx-195] O:Dish(35,Clean)
[02:23:22.1220] [main-comp-ctx-195] washing dish 36
[02:23:22.2230] [main-comp-ctx-195] Dish washed 36.
[02:23:22.2230] [main-comp-ctx-195] O:Dish(36,Clean)
[02:23:22.2230] [main-comp-ctx-195] washing dish 37
[02:23:22.3230] [main-comp-ctx-195] Dish washed 37.
[02:23:22.3240] [main-comp-ctx-195] O:Dish(37,Clean)
[02:23:22.3240] [main-comp-ctx-195] washing dish 38
[02:23:22.4240] [main-comp-ctx-195] Dish washed 38.
[02:23:22.4240] [main-comp-ctx-195] O:Dish(38,Clean)
[02:23:22.4250] [main-comp-ctx-195] washing dish 39
[02:23:22.5250] [main-comp-ctx-195] Dish washed 39.
[02:23:22.5250] [main-comp-ctx-195] O:Dish(39,Clean)
[02:23:22.5260] [main-comp-ctx-195] washing dish 40
[02:23:22.6270] [main-comp-ctx-195] Dish washed 40.
[02:23:22.6270] [main-comp-ctx-195] O:Dish(40,Clean)
[02:23:22.6280] [main-comp-ctx-195] washing dish 41
[02:23:22.7280] [main-comp-ctx-195] Dish washed 41.
[02:23:22.7280] [main-comp-ctx-195] O:Dish(41,Clean)
[02:23:22.7300] [main-comp-ctx-195] washing dish 42
[02:23:22.8300] [main-comp-ctx-195] Dish washed 42.
[02:23:22.8300] [main-comp-ctx-195] O:Dish(42,Clean)
[02:23:22.8310] [main-comp-ctx-195] washing dish 43
[02:23:22.9310] [main-comp-ctx-195] Dish washed 43.
[02:23:22.9310] [main-comp-ctx-195] O:Dish(43,Clean)
[02:23:22.9320] [main-comp-ctx-195] washing dish 44
[02:23:23.0330] [main-comp-ctx-195] Dish washed 44.
[02:23:23.0340] [main-comp-ctx-195] O:Dish(44,Clean)
[02:23:23.0340] [main-comp-ctx-195] washing dish 45
[02:23:23.1340] [main-comp-ctx-195] Dish washed 45.
[02:23:23.1350] [main-comp-ctx-195] O:Dish(45,Clean)
[02:23:23.1350] [main-comp-ctx-195] washing dish 46
[02:23:23.2350] [main-comp-ctx-195] Dish washed 46.
[02:23:23.2350] [main-comp-ctx-195] O:Dish(46,Clean)
[02:23:23.2360] [main-comp-ctx-195] washing dish 47
[02:23:23.3360] [main-comp-ctx-195] Dish washed 47.
[02:23:23.3360] [main-comp-ctx-195] O:Dish(47,Clean)
[02:23:23.3360] [main-comp-ctx-195] washing dish 48
[02:23:23.4370] [main-comp-ctx-195] Dish washed 48.
[02:23:23.4370] [main-comp-ctx-195] O:Dish(48,Clean)
[02:23:23.4370] [main-comp-ctx-195] washing dish 49
[02:23:23.5370] [main-comp-ctx-195] Dish washed 49.
[02:23:23.5380] [main-comp-ctx-195] O:Dish(49,Clean)
[02:23:23.5380] [main-comp-ctx-195] washing dish 50
[02:23:23.6380] [main-comp-ctx-195] Dish washed 50.
[02:23:23.6380] [main-comp-ctx-195] O:Dish(50,Clean)
[02:23:23.6390] [main-comp-ctx-195] washing dish 51
[02:23:23.7400] [main-comp-ctx-195] Dish washed 51.
[02:23:23.7400] [main-comp-ctx-195] O:Dish(51,Clean)
[02:23:23.7400] [main-comp-ctx-195] washing dish 52
[02:23:23.8410] [main-comp-ctx-195] Dish washed 52.
[02:23:23.8410] [main-comp-ctx-195] O:Dish(52,Clean)
[02:23:23.8410] [main-comp-ctx-195] washing dish 53
[02:23:23.9410] [main-comp-ctx-195] Dish washed 53.
[02:23:23.9420] [main-comp-ctx-195] O:Dish(53,Clean)
[02:23:23.9420] [main-comp-ctx-195] washing dish 54
[02:23:24.0420] [main-comp-ctx-195] Dish washed 54.
[02:23:24.0420] [main-comp-ctx-195] O:Dish(54,Clean)
[02:23:24.0430] [main-comp-ctx-195] washing dish 55
[02:23:24.1430] [main-comp-ctx-195] Dish washed 55.
[02:23:24.1430] [main-comp-ctx-195] O:Dish(55,Clean)
[02:23:24.1430] [main-comp-ctx-195] washing dish 56
[02:23:24.2440] [main-comp-ctx-195] Dish washed 56.
[02:23:24.2440] [main-comp-ctx-195] O:Dish(56,Clean)
[02:23:24.2440] [main-comp-ctx-195] washing dish 57
[02:23:24.3450] [main-comp-ctx-195] Dish washed 57.
[02:23:24.3450] [main-comp-ctx-195] O:Dish(57,Clean)
[02:23:24.3450] [main-comp-ctx-195] washing dish 58
[02:23:24.4460] [main-comp-ctx-195] Dish washed 58.
[02:23:24.4460] [main-comp-ctx-195] O:Dish(58,Clean)
[02:23:24.4460] [main-comp-ctx-195] washing dish 59
[02:23:24.5470] [main-comp-ctx-195] Dish washed 59.
[02:23:24.5470] [main-comp-ctx-195] O:Dish(59,Clean)
[02:23:24.5470] [main-comp-ctx-195] washing dish 60
[02:23:24.6480] [main-comp-ctx-195] Dish washed 60.
[02:23:24.6490] [main-comp-ctx-195] O:Dish(60,Clean)
[02:23:24.6490] [main-comp-ctx-195] washing dish 61
[02:23:24.7500] [main-comp-ctx-195] Dish washed 61.
[02:23:24.7500] [main-comp-ctx-195] O:Dish(61,Clean)
[02:23:24.7510] [main-comp-ctx-195] washing dish 62
[02:23:24.8520] [main-comp-ctx-195] Dish washed 62.
[02:23:24.8520] [main-comp-ctx-195] O:Dish(62,Clean)
[02:23:24.8520] [main-comp-ctx-195] washing dish 63
[02:23:24.9530] [main-comp-ctx-195] Dish washed 63.
[02:23:24.9530] [main-comp-ctx-195] O:Dish(63,Clean)
[02:23:24.9530] [main-comp-ctx-195] washing dish 64
[02:23:25.0540] [main-comp-ctx-195] Dish washed 64.
[02:23:25.0550] [main-comp-ctx-195] O:Dish(64,Clean)
[02:23:25.0550] [main-comp-ctx-195] washing dish 65
[02:23:25.1550] [main-comp-ctx-195] Dish washed 65.
[02:23:25.1550] [main-comp-ctx-195] O:Dish(65,Clean)
[02:23:25.1560] [main-comp-ctx-195] washing dish 66
[02:23:25.2560] [main-comp-ctx-195] Dish washed 66.
[02:23:25.2560] [main-comp-ctx-195] O:Dish(66,Clean)
[02:23:25.2570] [main-comp-ctx-195] E: 67
[02:23:25.2570] [main-comp-ctx-195] E: 68
[02:23:25.2570] [main-comp-ctx-196] washing dish 67
[02:23:25.2570] [main-comp-ctx-195] E: 69
[02:23:25.2570] [main-comp-ctx-195] E: 70
[02:23:25.2570] [main-comp-ctx-195] E: 71
[02:23:25.2570] [main-comp-ctx-195] E: 72
[02:23:25.2580] [main-comp-ctx-195] E: 73
[02:23:25.2580] [main-comp-ctx-195] E: 74
[02:23:25.2580] [main-comp-ctx-195] E: 75
[02:23:25.2580] [main-comp-ctx-195] E: 76
[02:23:25.2580] [main-comp-ctx-195] E: 77
[02:23:25.2580] [main-comp-ctx-195] E: 78
[02:23:25.2580] [main-comp-ctx-195] E: 79
[02:23:25.2590] [main-comp-ctx-195] E: 80
[02:23:25.2590] [main-comp-ctx-195] E: 81
[02:23:25.2590] [main-comp-ctx-195] E: 82
[02:23:25.2600] [main-comp-ctx-195] E: 83
[02:23:25.2610] [main-comp-ctx-195] E: 84
[02:23:25.2610] [main-comp-ctx-195] E: 85
[02:23:25.2620] [main-comp-ctx-195] E: 86
[02:23:25.2620] [main-comp-ctx-195] E: 87
[02:23:25.2620] [main-comp-ctx-195] E: 88
[02:23:25.2630] [main-comp-ctx-195] E: 89
[02:23:25.2630] [main-comp-ctx-195] E: 90
[02:23:25.2640] [main-comp-ctx-195] E: 91
[02:23:25.2640] [main-comp-ctx-195] E: 92
[02:23:25.2640] [main-comp-ctx-195] E: 93
[02:23:25.2650] [main-comp-ctx-195] E: 94
[02:23:25.2650] [main-comp-ctx-195] E: 95
[02:23:25.2650] [main-comp-ctx-195] E: 96
[02:23:25.2650] [main-comp-ctx-195] E: 97
[02:23:25.2660] [main-comp-ctx-195] E: 98
[02:23:25.2660] [main-comp-ctx-195] E: 99
[02:23:25.3570] [main-comp-ctx-196] Dish washed 67.
[02:23:25.3580] [main-comp-ctx-196] O:Dish(67,Clean)
[02:23:25.3580] [main-comp-ctx-196] washing dish 68
[02:23:25.4580] [main-comp-ctx-196] Dish washed 68.
[02:23:25.4580] [main-comp-ctx-196] O:Dish(68,Clean)
[02:23:25.4590] [main-comp-ctx-196] washing dish 69
[02:23:25.5590] [main-comp-ctx-196] Dish washed 69.
[02:23:25.5590] [main-comp-ctx-196] O:Dish(69,Clean)
[02:23:25.5600] [main-comp-ctx-196] washing dish 70
[02:23:25.6600] [main-comp-ctx-196] Dish washed 70.
[02:23:25.6600] [main-comp-ctx-196] O:Dish(70,Clean)
[02:23:25.6600] [main-comp-ctx-196] washing dish 71
[02:23:25.7610] [main-comp-ctx-196] Dish washed 71.
[02:23:25.7610] [main-comp-ctx-196] O:Dish(71,Clean)
[02:23:25.7610] [main-comp-ctx-196] washing dish 72
[02:23:25.8610] [main-comp-ctx-196] Dish washed 72.
[02:23:25.8620] [main-comp-ctx-196] O:Dish(72,Clean)
[02:23:25.8620] [main-comp-ctx-196] washing dish 73
[02:23:25.9620] [main-comp-ctx-196] Dish washed 73.
[02:23:25.9620] [main-comp-ctx-196] O:Dish(73,Clean)
[02:23:25.9630] [main-comp-ctx-196] washing dish 74
[02:23:26.0630] [main-comp-ctx-196] Dish washed 74.
[02:23:26.0630] [main-comp-ctx-196] O:Dish(74,Clean)
[02:23:26.0640] [main-comp-ctx-196] washing dish 75
[02:23:26.1640] [main-comp-ctx-196] Dish washed 75.
[02:23:26.1640] [main-comp-ctx-196] O:Dish(75,Clean)
[02:23:26.1640] [main-comp-ctx-196] washing dish 76
[02:23:26.2650] [main-comp-ctx-196] Dish washed 76.
[02:23:26.2820] [main-comp-ctx-196] O:Dish(76,Clean)
[02:23:26.2820] [main-comp-ctx-196] washing dish 77
[02:23:26.3820] [main-comp-ctx-196] Dish washed 77.
[02:23:26.3830] [main-comp-ctx-196] O:Dish(77,Clean)
[02:23:26.3830] [main-comp-ctx-196] washing dish 78
[02:23:26.4830] [main-comp-ctx-196] Dish washed 78.
[02:23:26.4840] [main-comp-ctx-196] O:Dish(78,Clean)
[02:23:26.4840] [main-comp-ctx-196] washing dish 79
[02:23:26.5840] [main-comp-ctx-196] Dish washed 79.
[02:23:26.5850] [main-comp-ctx-196] O:Dish(79,Clean)
[02:23:26.5850] [main-comp-ctx-196] washing dish 80
[02:23:26.6850] [main-comp-ctx-196] Dish washed 80.
[02:23:26.6860] [main-comp-ctx-196] O:Dish(80,Clean)
[02:23:26.6860] [main-comp-ctx-196] washing dish 81
[02:23:26.7860] [main-comp-ctx-196] Dish washed 81.
[02:23:26.7860] [main-comp-ctx-196] O:Dish(81,Clean)
[02:23:26.7870] [main-comp-ctx-196] washing dish 82
[02:23:26.8870] [main-comp-ctx-196] Dish washed 82.
[02:23:26.8870] [main-comp-ctx-196] O:Dish(82,Clean)
[02:23:26.8880] [main-comp-ctx-196] washing dish 83
[02:23:26.9880] [main-comp-ctx-196] Dish washed 83.
[02:23:26.9880] [main-comp-ctx-196] O:Dish(83,Clean)
[02:23:26.9900] [main-comp-ctx-196] washing dish 84
[02:23:27.0900] [main-comp-ctx-196] Dish washed 84.
[02:23:27.0910] [main-comp-ctx-196] O:Dish(84,Clean)
[02:23:27.0910] [main-comp-ctx-196] washing dish 85
[02:23:27.1920] [main-comp-ctx-196] Dish washed 85.
[02:23:27.1920] [main-comp-ctx-196] O:Dish(85,Clean)
[02:23:27.1920] [main-comp-ctx-196] washing dish 86
[02:23:27.2930] [main-comp-ctx-196] Dish washed 86.
[02:23:27.2930] [main-comp-ctx-196] O:Dish(86,Clean)
[02:23:27.2930] [main-comp-ctx-196] washing dish 87
[02:23:27.3940] [main-comp-ctx-196] Dish washed 87.
[02:23:27.3940] [main-comp-ctx-196] O:Dish(87,Clean)
[02:23:27.3950] [main-comp-ctx-196] washing dish 88
[02:23:27.4950] [main-comp-ctx-196] Dish washed 88.
[02:23:27.4960] [main-comp-ctx-196] O:Dish(88,Clean)
[02:23:27.4970] [main-comp-ctx-196] washing dish 89
[02:23:27.5970] [main-comp-ctx-196] Dish washed 89.
[02:23:27.5970] [main-comp-ctx-196] O:Dish(89,Clean)
[02:23:27.5980] [main-comp-ctx-196] washing dish 90
[02:23:27.6980] [main-comp-ctx-196] Dish washed 90.
[02:23:27.6990] [main-comp-ctx-196] O:Dish(90,Clean)
[02:23:27.7000] [main-comp-ctx-196] washing dish 91
[02:23:27.8000] [main-comp-ctx-196] Dish washed 91.
[02:23:27.8010] [main-comp-ctx-196] O:Dish(91,Clean)
[02:23:27.8020] [main-comp-ctx-196] washing dish 92
[02:23:27.9020] [main-comp-ctx-196] Dish washed 92.
[02:23:27.9030] [main-comp-ctx-196] O:Dish(92,Clean)
[02:23:27.9040] [main-comp-ctx-196] washing dish 93
[02:23:28.0050] [main-comp-ctx-196] Dish washed 93.
[02:23:28.0050] [main-comp-ctx-196] O:Dish(93,Clean)
[02:23:28.0060] [main-comp-ctx-196] washing dish 94
[02:23:28.1060] [main-comp-ctx-196] Dish washed 94.
[02:23:28.1070] [main-comp-ctx-196] O:Dish(94,Clean)
[02:23:28.1070] [main-comp-ctx-196] washing dish 95
[02:23:28.2070] [main-comp-ctx-196] Dish washed 95.
[02:23:28.2070] [main-comp-ctx-196] O:Dish(95,Clean)
[02:23:28.2080] [main-comp-ctx-196] washing dish 96
[02:23:28.3080] [main-comp-ctx-196] Dish washed 96.
[02:23:28.3080] [main-comp-ctx-196] O:Dish(96,Clean)
[02:23:28.3090] [main-comp-ctx-196] washing dish 97
[02:23:28.4090] [main-comp-ctx-196] Dish washed 97.
[02:23:28.4090] [main-comp-ctx-196] O:Dish(97,Clean)
[02:23:28.4100] [main-comp-ctx-196] washing dish 98
[02:23:28.5100] [main-comp-ctx-196] Dish washed 98.
[02:23:28.5100] [main-comp-ctx-196] O:Dish(98,Clean)
[02:23:28.5100] [main-comp-ctx-196] washing dish 99
[02:23:28.6110] [main-comp-ctx-196] Dish washed 99.
[02:23:28.6110] [main-comp-ctx-196] O:Dish(99,Clean)
[02:23:28.6130] [main-comp-ctx-196] E: 100
[02:23:28.6140] [main-comp-ctx-195] washing dish 100
[02:23:28.7150] [main-comp-ctx-195] Dish washed 100.
[02:23:28.7150] [main-comp-ctx-195] O:Dish(100,Clean)
[02:23:30.7110] [run-main-2] Program Ends.
*/
