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
  .asyncBoundary(OverflowStrategy.BackPressure(2))
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
[02:28:38.0740] [run-main-5] Program starts.
[02:28:38.0820] [run-main-5] E: 1
[02:28:38.0870] [run-main-5] E: 2
[02:28:38.0870] [run-main-5] E: 3
[02:28:38.0890] [main-comp-ctx-251] washing dish 1
[02:28:38.1970] [main-comp-ctx-251] Dish washed 1.
[02:28:38.1990] [main-comp-ctx-251] O:Dish(1,Clean)
[02:28:38.2010] [main-comp-ctx-251] washing dish 2
[02:28:38.3020] [main-comp-ctx-251] Dish washed 2.
[02:28:38.3020] [main-comp-ctx-251] O:Dish(2,Clean)
[02:28:38.3030] [main-comp-ctx-251] washing dish 3
[02:28:38.4030] [main-comp-ctx-251] Dish washed 3.
[02:28:38.4030] [main-comp-ctx-251] O:Dish(3,Clean)
[02:28:38.4070] [main-comp-ctx-251] E: 4
[02:28:38.4070] [main-comp-ctx-251] E: 5
[02:28:38.4080] [main-comp-ctx-251] E: 6
[02:28:38.4100] [main-comp-ctx-252] washing dish 4
[02:28:38.5110] [main-comp-ctx-252] Dish washed 4.
[02:28:38.5110] [main-comp-ctx-252] O:Dish(4,Clean)
[02:28:38.5110] [main-comp-ctx-252] washing dish 5
[02:28:38.6120] [main-comp-ctx-252] Dish washed 5.
[02:28:38.6120] [main-comp-ctx-252] O:Dish(5,Clean)
[02:28:38.6120] [main-comp-ctx-252] washing dish 6
[02:28:38.7130] [main-comp-ctx-252] Dish washed 6.
[02:28:38.7130] [main-comp-ctx-252] O:Dish(6,Clean)
[02:28:38.7140] [main-comp-ctx-252] E: 7
[02:28:38.7140] [main-comp-ctx-252] E: 8
[02:28:38.7140] [main-comp-ctx-251] washing dish 7
[02:28:38.7140] [main-comp-ctx-252] E: 9
[02:28:38.8150] [main-comp-ctx-251] Dish washed 7.
[02:28:38.8150] [main-comp-ctx-251] O:Dish(7,Clean)
[02:28:38.8160] [main-comp-ctx-251] washing dish 8
[02:28:38.9160] [main-comp-ctx-251] Dish washed 8.
[02:28:38.9160] [main-comp-ctx-251] O:Dish(8,Clean)
[02:28:38.9170] [main-comp-ctx-251] washing dish 9
[02:28:39.0170] [main-comp-ctx-251] Dish washed 9.
[02:28:39.0170] [main-comp-ctx-251] O:Dish(9,Clean)
[02:28:39.0180] [main-comp-ctx-252] E: 10
[02:28:39.0190] [main-comp-ctx-251] washing dish 10
[02:28:39.0220] [main-comp-ctx-252] E: 11
[02:28:39.0220] [main-comp-ctx-252] E: 12
[02:28:39.1200] [main-comp-ctx-251] Dish washed 10.
[02:28:39.1200] [main-comp-ctx-251] O:Dish(10,Clean)
[02:28:39.1210] [main-comp-ctx-251] washing dish 11
[02:28:39.2220] [main-comp-ctx-251] Dish washed 11.
[02:28:39.2220] [main-comp-ctx-251] O:Dish(11,Clean)
[02:28:39.2230] [main-comp-ctx-251] washing dish 12
[02:28:39.3240] [main-comp-ctx-251] Dish washed 12.
[02:28:39.3240] [main-comp-ctx-251] O:Dish(12,Clean)
[02:28:39.3240] [main-comp-ctx-251] E: 13
[02:28:39.3250] [main-comp-ctx-251] E: 14
[02:28:39.3250] [main-comp-ctx-252] washing dish 13
[02:28:39.3250] [main-comp-ctx-251] E: 15
[02:28:39.4260] [main-comp-ctx-252] Dish washed 13.
[02:28:39.4260] [main-comp-ctx-252] O:Dish(13,Clean)
[02:28:39.4260] [main-comp-ctx-252] washing dish 14
[02:28:39.5260] [main-comp-ctx-252] Dish washed 14.
[02:28:39.5270] [main-comp-ctx-252] O:Dish(14,Clean)
[02:28:39.5270] [main-comp-ctx-252] washing dish 15
[02:28:39.6270] [main-comp-ctx-252] Dish washed 15.
[02:28:39.6280] [main-comp-ctx-252] O:Dish(15,Clean)
[02:28:39.6280] [main-comp-ctx-252] E: 16
[02:28:39.6290] [main-comp-ctx-252] E: 17
[02:28:39.6290] [main-comp-ctx-252] E: 18
[02:28:39.6290] [main-comp-ctx-254] washing dish 16
[02:28:39.7300] [main-comp-ctx-254] Dish washed 16.
[02:28:39.7300] [main-comp-ctx-254] O:Dish(16,Clean)
[02:28:39.7300] [main-comp-ctx-254] washing dish 17
[02:28:39.8310] [main-comp-ctx-254] Dish washed 17.
[02:28:39.8310] [main-comp-ctx-254] O:Dish(17,Clean)
[02:28:39.8310] [main-comp-ctx-254] washing dish 18
[02:28:39.9320] [main-comp-ctx-254] Dish washed 18.
[02:28:39.9320] [main-comp-ctx-254] O:Dish(18,Clean)
[02:28:39.9330] [main-comp-ctx-252] E: 19
[02:28:39.9330] [main-comp-ctx-252] E: 20
[02:28:39.9340] [main-comp-ctx-252] E: 21
[02:28:39.9340] [main-comp-ctx-251] washing dish 19
[02:28:40.0340] [main-comp-ctx-251] Dish washed 19.
[02:28:40.0340] [main-comp-ctx-251] O:Dish(19,Clean)
[02:28:40.0350] [main-comp-ctx-251] washing dish 20
[02:28:40.1350] [main-comp-ctx-251] Dish washed 20.
[02:28:40.1350] [main-comp-ctx-251] O:Dish(20,Clean)
[02:28:40.1360] [main-comp-ctx-251] washing dish 21
[02:28:40.2360] [main-comp-ctx-251] Dish washed 21.
[02:28:40.2360] [main-comp-ctx-251] O:Dish(21,Clean)
[02:28:40.2370] [main-comp-ctx-251] E: 22
[02:28:40.2370] [main-comp-ctx-251] E: 23
[02:28:40.2370] [main-comp-ctx-252] washing dish 22
[02:28:40.2380] [main-comp-ctx-251] E: 24
[02:28:40.3380] [main-comp-ctx-252] Dish washed 22.
[02:28:40.3380] [main-comp-ctx-252] O:Dish(22,Clean)
[02:28:40.3380] [main-comp-ctx-252] washing dish 23
[02:28:40.4380] [main-comp-ctx-252] Dish washed 23.
[02:28:40.4390] [main-comp-ctx-252] O:Dish(23,Clean)
[02:28:40.4390] [main-comp-ctx-252] washing dish 24
[02:28:40.5390] [main-comp-ctx-252] Dish washed 24.
[02:28:40.5400] [main-comp-ctx-252] O:Dish(24,Clean)
[02:28:40.5400] [main-comp-ctx-252] E: 25
[02:28:40.5410] [main-comp-ctx-252] E: 26
[02:28:40.5410] [main-comp-ctx-252] E: 27
[02:28:40.5410] [main-comp-ctx-251] washing dish 25
[02:28:40.6420] [main-comp-ctx-251] Dish washed 25.
[02:28:40.6430] [main-comp-ctx-251] O:Dish(25,Clean)
[02:28:40.6430] [main-comp-ctx-251] washing dish 26
[02:28:40.7440] [main-comp-ctx-251] Dish washed 26.
[02:28:40.7450] [main-comp-ctx-251] O:Dish(26,Clean)
[02:28:40.7450] [main-comp-ctx-251] washing dish 27
[02:28:40.8460] [main-comp-ctx-251] Dish washed 27.
[02:28:40.8460] [main-comp-ctx-251] O:Dish(27,Clean)
[02:28:40.8480] [main-comp-ctx-251] E: 28
[02:28:40.8490] [main-comp-ctx-251] E: 29
[02:28:40.8490] [main-comp-ctx-252] washing dish 28
[02:28:40.8490] [main-comp-ctx-251] E: 30
[02:28:40.9490] [main-comp-ctx-252] Dish washed 28.
[02:28:40.9490] [main-comp-ctx-252] O:Dish(28,Clean)
[02:28:40.9500] [main-comp-ctx-252] washing dish 29
[02:28:41.0500] [main-comp-ctx-252] Dish washed 29.
[02:28:41.0500] [main-comp-ctx-252] O:Dish(29,Clean)
[02:28:41.0510] [main-comp-ctx-252] washing dish 30
[02:28:41.1510] [main-comp-ctx-252] Dish washed 30.
[02:28:41.1510] [main-comp-ctx-252] O:Dish(30,Clean)
[02:28:41.1520] [main-comp-ctx-251] E: 31
[02:28:41.1520] [main-comp-ctx-251] E: 32
[02:28:41.1520] [main-comp-ctx-254] washing dish 31
[02:28:41.1530] [main-comp-ctx-251] E: 33
[02:28:41.2530] [main-comp-ctx-254] Dish washed 31.
[02:28:41.2540] [main-comp-ctx-254] O:Dish(31,Clean)
[02:28:41.2540] [main-comp-ctx-254] washing dish 32
[02:28:41.3540] [main-comp-ctx-254] Dish washed 32.
[02:28:41.3550] [main-comp-ctx-254] O:Dish(32,Clean)
[02:28:41.3550] [main-comp-ctx-254] washing dish 33
[02:28:41.4550] [main-comp-ctx-254] Dish washed 33.
[02:28:41.4550] [main-comp-ctx-254] O:Dish(33,Clean)
[02:28:41.4560] [main-comp-ctx-254] E: 34
[02:28:41.4570] [main-comp-ctx-254] E: 35
[02:28:41.4570] [main-comp-ctx-251] washing dish 34
[02:28:41.4570] [main-comp-ctx-254] E: 36
[02:28:41.5570] [main-comp-ctx-251] Dish washed 34.
[02:28:41.5570] [main-comp-ctx-251] O:Dish(34,Clean)
[02:28:41.5580] [main-comp-ctx-251] washing dish 35
[02:28:41.6580] [main-comp-ctx-251] Dish washed 35.
[02:28:41.6580] [main-comp-ctx-251] O:Dish(35,Clean)
[02:28:41.6590] [main-comp-ctx-251] washing dish 36
[02:28:41.7590] [main-comp-ctx-251] Dish washed 36.
[02:28:41.7590] [main-comp-ctx-251] O:Dish(36,Clean)
[02:28:41.7600] [main-comp-ctx-251] E: 37
[02:28:41.7600] [main-comp-ctx-251] E: 38
[02:28:41.7600] [main-comp-ctx-254] washing dish 37
[02:28:41.7600] [main-comp-ctx-251] E: 39
[02:28:41.8610] [main-comp-ctx-254] Dish washed 37.
[02:28:41.8610] [main-comp-ctx-254] O:Dish(37,Clean)
[02:28:41.8610] [main-comp-ctx-254] washing dish 38
[02:28:41.9620] [main-comp-ctx-254] Dish washed 38.
[02:28:41.9620] [main-comp-ctx-254] O:Dish(38,Clean)
[02:28:41.9620] [main-comp-ctx-254] washing dish 39
[02:28:42.0650] [main-comp-ctx-254] Dish washed 39.
[02:28:42.0650] [main-comp-ctx-254] O:Dish(39,Clean)
[02:28:42.0650] [main-comp-ctx-251] E: 40
[02:28:42.0660] [main-comp-ctx-251] E: 41
[02:28:42.0660] [main-comp-ctx-252] washing dish 40
[02:28:42.0660] [main-comp-ctx-251] E: 42
[02:28:42.1660] [main-comp-ctx-252] Dish washed 40.
[02:28:42.1660] [main-comp-ctx-252] O:Dish(40,Clean)
[02:28:42.1670] [main-comp-ctx-252] washing dish 41
[02:28:42.2670] [main-comp-ctx-252] Dish washed 41.
[02:28:42.2670] [main-comp-ctx-252] O:Dish(41,Clean)
[02:28:42.2680] [main-comp-ctx-252] washing dish 42
[02:28:42.3680] [main-comp-ctx-252] Dish washed 42.
[02:28:42.3680] [main-comp-ctx-252] O:Dish(42,Clean)
[02:28:42.3690] [main-comp-ctx-251] E: 43
[02:28:42.3700] [main-comp-ctx-254] washing dish 43
[02:28:42.3700] [main-comp-ctx-251] E: 44
[02:28:42.3700] [main-comp-ctx-251] E: 45
[02:28:42.4700] [main-comp-ctx-254] Dish washed 43.
[02:28:42.4700] [main-comp-ctx-254] O:Dish(43,Clean)
[02:28:42.4700] [main-comp-ctx-254] washing dish 44
[02:28:42.5710] [main-comp-ctx-254] Dish washed 44.
[02:28:42.5710] [main-comp-ctx-254] O:Dish(44,Clean)
[02:28:42.5720] [main-comp-ctx-254] washing dish 45
[02:28:42.6720] [main-comp-ctx-254] Dish washed 45.
[02:28:42.6720] [main-comp-ctx-254] O:Dish(45,Clean)
[02:28:42.6730] [main-comp-ctx-254] E: 46
[02:28:42.6730] [main-comp-ctx-251] washing dish 46
[02:28:42.6740] [main-comp-ctx-254] E: 47
[02:28:42.6750] [main-comp-ctx-254] E: 48
[02:28:42.7740] [main-comp-ctx-251] Dish washed 46.
[02:28:42.7750] [main-comp-ctx-251] O:Dish(46,Clean)
[02:28:42.7750] [main-comp-ctx-251] washing dish 47
[02:28:42.8760] [main-comp-ctx-251] Dish washed 47.
[02:28:42.8760] [main-comp-ctx-251] O:Dish(47,Clean)
[02:28:42.8770] [main-comp-ctx-251] washing dish 48
[02:28:42.9770] [main-comp-ctx-251] Dish washed 48.
[02:28:42.9770] [main-comp-ctx-251] O:Dish(48,Clean)
[02:28:42.9780] [main-comp-ctx-251] E: 49
[02:28:42.9780] [main-comp-ctx-251] E: 50
[02:28:42.9780] [main-comp-ctx-251] E: 51
[02:28:42.9790] [main-comp-ctx-254] washing dish 49
[02:28:43.0790] [main-comp-ctx-254] Dish washed 49.
[02:28:43.0790] [main-comp-ctx-254] O:Dish(49,Clean)
[02:28:43.0800] [main-comp-ctx-254] washing dish 50
[02:28:43.1800] [main-comp-ctx-254] Dish washed 50.
[02:28:43.1800] [main-comp-ctx-254] O:Dish(50,Clean)
[02:28:43.1800] [main-comp-ctx-254] washing dish 51
[02:28:43.2810] [main-comp-ctx-254] Dish washed 51.
[02:28:43.2810] [main-comp-ctx-254] O:Dish(51,Clean)
[02:28:43.2820] [main-comp-ctx-251] E: 52
[02:28:43.2820] [main-comp-ctx-251] E: 53
[02:28:43.2820] [main-comp-ctx-254] washing dish 52
[02:28:43.2830] [main-comp-ctx-251] E: 54
[02:28:43.3830] [main-comp-ctx-254] Dish washed 52.
[02:28:43.3830] [main-comp-ctx-254] O:Dish(52,Clean)
[02:28:43.3830] [main-comp-ctx-254] washing dish 53
[02:28:43.4840] [main-comp-ctx-254] Dish washed 53.
[02:28:43.4840] [main-comp-ctx-254] O:Dish(53,Clean)
[02:28:43.4840] [main-comp-ctx-254] washing dish 54
[02:28:43.5850] [main-comp-ctx-254] Dish washed 54.
[02:28:43.5850] [main-comp-ctx-254] O:Dish(54,Clean)
[02:28:43.5850] [main-comp-ctx-251] E: 55
[02:28:43.5860] [main-comp-ctx-251] E: 56
[02:28:43.5860] [main-comp-ctx-254] washing dish 55
[02:28:43.5860] [main-comp-ctx-251] E: 57
[02:28:43.6870] [main-comp-ctx-254] Dish washed 55.
[02:28:43.6870] [main-comp-ctx-254] O:Dish(55,Clean)
[02:28:43.6870] [main-comp-ctx-254] washing dish 56
[02:28:43.7880] [main-comp-ctx-254] Dish washed 56.
[02:28:43.7880] [main-comp-ctx-254] O:Dish(56,Clean)
[02:28:43.7890] [main-comp-ctx-254] washing dish 57
[02:28:43.8890] [main-comp-ctx-254] Dish washed 57.
[02:28:43.8890] [main-comp-ctx-254] O:Dish(57,Clean)
[02:28:43.8900] [main-comp-ctx-251] E: 58
[02:28:43.8910] [main-comp-ctx-251] E: 59
[02:28:43.8910] [main-comp-ctx-254] washing dish 58
[02:28:43.8910] [main-comp-ctx-251] E: 60
[02:28:43.9920] [main-comp-ctx-254] Dish washed 58.
[02:28:43.9920] [main-comp-ctx-254] O:Dish(58,Clean)
[02:28:43.9930] [main-comp-ctx-254] washing dish 59
[02:28:44.0930] [main-comp-ctx-254] Dish washed 59.
[02:28:44.0940] [main-comp-ctx-254] O:Dish(59,Clean)
[02:28:44.0940] [main-comp-ctx-254] washing dish 60
[02:28:44.1940] [main-comp-ctx-254] Dish washed 60.
[02:28:44.1950] [main-comp-ctx-254] O:Dish(60,Clean)
[02:28:44.1950] [main-comp-ctx-251] E: 61
[02:28:44.1960] [main-comp-ctx-251] E: 62
[02:28:44.1960] [main-comp-ctx-254] washing dish 61
[02:28:44.1960] [main-comp-ctx-251] E: 63
[02:28:44.2960] [main-comp-ctx-254] Dish washed 61.
[02:28:44.2960] [main-comp-ctx-254] O:Dish(61,Clean)
[02:28:44.2970] [main-comp-ctx-254] washing dish 62
[02:28:44.3970] [main-comp-ctx-254] Dish washed 62.
[02:28:44.3970] [main-comp-ctx-254] O:Dish(62,Clean)
[02:28:44.3980] [main-comp-ctx-254] washing dish 63
[02:28:44.4980] [main-comp-ctx-254] Dish washed 63.
[02:28:44.4980] [main-comp-ctx-254] O:Dish(63,Clean)
[02:28:44.4990] [main-comp-ctx-251] E: 64
[02:28:44.4990] [main-comp-ctx-251] E: 65
[02:28:44.4990] [main-comp-ctx-254] washing dish 64
[02:28:44.5000] [main-comp-ctx-251] E: 66
[02:28:44.6000] [main-comp-ctx-254] Dish washed 64.
[02:28:44.6010] [main-comp-ctx-254] O:Dish(64,Clean)
[02:28:44.6020] [main-comp-ctx-254] washing dish 65
[02:28:44.7030] [main-comp-ctx-254] Dish washed 65.
[02:28:44.7030] [main-comp-ctx-254] O:Dish(65,Clean)
[02:28:44.7030] [main-comp-ctx-254] washing dish 66
[02:28:44.8040] [main-comp-ctx-254] Dish washed 66.
[02:28:44.8040] [main-comp-ctx-254] O:Dish(66,Clean)
[02:28:44.8050] [main-comp-ctx-251] E: 67
[02:28:44.8060] [main-comp-ctx-251] E: 68
[02:28:44.8060] [main-comp-ctx-252] washing dish 67
[02:28:44.8060] [main-comp-ctx-251] E: 69
[02:28:44.9060] [main-comp-ctx-252] Dish washed 67.
[02:28:44.9070] [main-comp-ctx-252] O:Dish(67,Clean)
[02:28:44.9070] [main-comp-ctx-252] washing dish 68
[02:28:45.0070] [main-comp-ctx-252] Dish washed 68.
[02:28:45.0070] [main-comp-ctx-252] O:Dish(68,Clean)
[02:28:45.0080] [main-comp-ctx-252] washing dish 69
[02:28:45.1080] [main-comp-ctx-252] Dish washed 69.
[02:28:45.1080] [main-comp-ctx-252] O:Dish(69,Clean)
[02:28:45.1090] [main-comp-ctx-251] E: 70
[02:28:45.1090] [main-comp-ctx-251] E: 71
[02:28:45.1090] [main-comp-ctx-252] washing dish 70
[02:28:45.1100] [main-comp-ctx-251] E: 72
[02:28:45.2100] [main-comp-ctx-252] Dish washed 70.
[02:28:45.2100] [main-comp-ctx-252] O:Dish(70,Clean)
[02:28:45.2100] [main-comp-ctx-252] washing dish 71
[02:28:45.3110] [main-comp-ctx-252] Dish washed 71.
[02:28:45.3110] [main-comp-ctx-252] O:Dish(71,Clean)
[02:28:45.3110] [main-comp-ctx-252] washing dish 72
[02:28:45.4120] [main-comp-ctx-252] Dish washed 72.
[02:28:45.4120] [main-comp-ctx-252] O:Dish(72,Clean)
[02:28:45.4120] [main-comp-ctx-251] E: 73
[02:28:45.4120] [main-comp-ctx-251] E: 74
[02:28:45.4120] [main-comp-ctx-254] washing dish 73
[02:28:45.4130] [main-comp-ctx-251] E: 75
[02:28:45.5130] [main-comp-ctx-254] Dish washed 73.
[02:28:45.5130] [main-comp-ctx-254] O:Dish(73,Clean)
[02:28:45.5140] [main-comp-ctx-254] washing dish 74
[02:28:45.6140] [main-comp-ctx-254] Dish washed 74.
[02:28:45.6140] [main-comp-ctx-254] O:Dish(74,Clean)
[02:28:45.6150] [main-comp-ctx-254] washing dish 75
[02:28:45.7150] [main-comp-ctx-254] Dish washed 75.
[02:28:45.7150] [main-comp-ctx-254] O:Dish(75,Clean)
[02:28:45.7160] [main-comp-ctx-254] E: 76
[02:28:45.7160] [main-comp-ctx-254] E: 77
[02:28:45.7160] [main-comp-ctx-251] washing dish 76
[02:28:45.7170] [main-comp-ctx-254] E: 78
[02:28:45.8170] [main-comp-ctx-251] Dish washed 76.
[02:28:45.8170] [main-comp-ctx-251] O:Dish(76,Clean)
[02:28:45.8170] [main-comp-ctx-251] washing dish 77
[02:28:45.9180] [main-comp-ctx-251] Dish washed 77.
[02:28:45.9180] [main-comp-ctx-251] O:Dish(77,Clean)
[02:28:45.9190] [main-comp-ctx-251] washing dish 78
[02:28:46.0190] [main-comp-ctx-251] Dish washed 78.
[02:28:46.0190] [main-comp-ctx-251] O:Dish(78,Clean)
[02:28:46.0200] [main-comp-ctx-251] E: 79
[02:28:46.0200] [main-comp-ctx-251] E: 80
[02:28:46.0200] [main-comp-ctx-254] washing dish 79
[02:28:46.0200] [main-comp-ctx-251] E: 81
[02:28:46.1210] [main-comp-ctx-254] Dish washed 79.
[02:28:46.1210] [main-comp-ctx-254] O:Dish(79,Clean)
[02:28:46.1210] [main-comp-ctx-254] washing dish 80
[02:28:46.2220] [main-comp-ctx-254] Dish washed 80.
[02:28:46.2220] [main-comp-ctx-254] O:Dish(80,Clean)
[02:28:46.2230] [main-comp-ctx-254] washing dish 81
[02:28:46.3230] [main-comp-ctx-254] Dish washed 81.
[02:28:46.3230] [main-comp-ctx-254] O:Dish(81,Clean)
[02:28:46.3240] [main-comp-ctx-254] E: 82
[02:28:46.3240] [main-comp-ctx-254] E: 83
[02:28:46.3240] [main-comp-ctx-254] E: 84
[02:28:46.3240] [main-comp-ctx-251] washing dish 82
[02:28:46.4250] [main-comp-ctx-251] Dish washed 82.
[02:28:46.4250] [main-comp-ctx-251] O:Dish(82,Clean)
[02:28:46.4260] [main-comp-ctx-251] washing dish 83
[02:28:46.5260] [main-comp-ctx-251] Dish washed 83.
[02:28:46.5260] [main-comp-ctx-251] O:Dish(83,Clean)
[02:28:46.5270] [main-comp-ctx-251] washing dish 84
[02:28:46.6270] [main-comp-ctx-251] Dish washed 84.
[02:28:46.6270] [main-comp-ctx-251] O:Dish(84,Clean)
[02:28:46.6280] [main-comp-ctx-251] E: 85
[02:28:46.6280] [main-comp-ctx-251] E: 86
[02:28:46.6280] [main-comp-ctx-254] washing dish 85
[02:28:46.6290] [main-comp-ctx-251] E: 87
[02:28:46.7290] [main-comp-ctx-254] Dish washed 85.
[02:28:46.7290] [main-comp-ctx-254] O:Dish(85,Clean)
[02:28:46.7290] [main-comp-ctx-254] washing dish 86
[02:28:46.8300] [main-comp-ctx-254] Dish washed 86.
[02:28:46.8300] [main-comp-ctx-254] O:Dish(86,Clean)
[02:28:46.8300] [main-comp-ctx-254] washing dish 87
[02:28:46.9310] [main-comp-ctx-254] Dish washed 87.
[02:28:46.9310] [main-comp-ctx-254] O:Dish(87,Clean)
[02:28:46.9310] [main-comp-ctx-251] E: 88
[02:28:46.9320] [main-comp-ctx-252] washing dish 88
[02:28:46.9320] [main-comp-ctx-251] E: 89
[02:28:46.9320] [main-comp-ctx-251] E: 90
[02:28:47.0320] [main-comp-ctx-252] Dish washed 88.
[02:28:47.0330] [main-comp-ctx-252] O:Dish(88,Clean)
[02:28:47.0330] [main-comp-ctx-252] washing dish 89
[02:28:47.1340] [main-comp-ctx-252] Dish washed 89.
[02:28:47.1340] [main-comp-ctx-252] O:Dish(89,Clean)
[02:28:47.1340] [main-comp-ctx-252] washing dish 90
[02:28:47.2340] [main-comp-ctx-252] Dish washed 90.
[02:28:47.2350] [main-comp-ctx-252] O:Dish(90,Clean)
[02:28:47.2350] [main-comp-ctx-252] E: 91
[02:28:47.2360] [main-comp-ctx-252] E: 92
[02:28:47.2360] [main-comp-ctx-252] E: 93
[02:28:47.2360] [main-comp-ctx-251] washing dish 91
[02:28:47.3360] [main-comp-ctx-251] Dish washed 91.
[02:28:47.3370] [main-comp-ctx-251] O:Dish(91,Clean)
[02:28:47.3370] [main-comp-ctx-251] washing dish 92
[02:28:47.4370] [main-comp-ctx-251] Dish washed 92.
[02:28:47.4380] [main-comp-ctx-251] O:Dish(92,Clean)
[02:28:47.4380] [main-comp-ctx-251] washing dish 93
[02:28:47.5380] [main-comp-ctx-251] Dish washed 93.
[02:28:47.5380] [main-comp-ctx-251] O:Dish(93,Clean)
[02:28:47.5390] [main-comp-ctx-251] E: 94
[02:28:47.5390] [main-comp-ctx-251] E: 95
[02:28:47.5390] [main-comp-ctx-252] washing dish 94
[02:28:47.5400] [main-comp-ctx-251] E: 96
[02:28:47.6400] [main-comp-ctx-252] Dish washed 94.
[02:28:47.6400] [main-comp-ctx-252] O:Dish(94,Clean)
[02:28:47.6400] [main-comp-ctx-252] washing dish 95
[02:28:47.7410] [main-comp-ctx-252] Dish washed 95.
[02:28:47.7410] [main-comp-ctx-252] O:Dish(95,Clean)
[02:28:47.7410] [main-comp-ctx-252] washing dish 96
[02:28:47.8420] [main-comp-ctx-252] Dish washed 96.
[02:28:47.8420] [main-comp-ctx-252] O:Dish(96,Clean)
[02:28:47.8420] [main-comp-ctx-251] E: 97
[02:28:47.8430] [main-comp-ctx-251] E: 98
[02:28:47.8430] [main-comp-ctx-254] washing dish 97
[02:28:47.8430] [main-comp-ctx-251] E: 99
[02:28:47.9430] [main-comp-ctx-254] Dish washed 97.
[02:28:47.9440] [main-comp-ctx-254] O:Dish(97,Clean)
[02:28:47.9440] [main-comp-ctx-254] washing dish 98
[02:28:48.0450] [main-comp-ctx-254] Dish washed 98.
[02:28:48.0450] [main-comp-ctx-254] O:Dish(98,Clean)
[02:28:48.0450] [main-comp-ctx-254] washing dish 99
[02:28:48.1460] [main-comp-ctx-254] Dish washed 99.
[02:28:48.1460] [main-comp-ctx-254] O:Dish(99,Clean)
[02:28:48.1470] [main-comp-ctx-251] E: 100
[02:28:48.1470] [main-comp-ctx-254] washing dish 100
[02:28:48.2480] [main-comp-ctx-254] Dish washed 100.
[02:28:48.2480] [main-comp-ctx-254] O:Dish(100,Clean)
[02:28:50.2010] [run-main-5] Program Ends.
*/
