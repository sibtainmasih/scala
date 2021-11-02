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
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 5))

Thread.sleep(12000)
println(s"$log Program Ends.")

/* Output
[02:38:33.5560] [run-main-6] Program starts.
[02:38:33.5990] [run-main-6] E: 1
[02:38:33.6050] [run-main-6] E: 2
[02:38:33.6050] [run-main-6] E: 3
[02:38:33.6310] [main-comp-ctx-288] E: 4
[02:38:33.6310] [main-comp-ctx-290] washing dish 2
[02:38:33.6310] [main-comp-ctx-289] washing dish 1
[02:38:33.6310] [main-comp-ctx-288] E: 5
[02:38:33.6320] [main-comp-ctx-288] E: 6
[02:38:33.7350] [main-comp-ctx-292] Dish washed 2.
[02:38:33.7360] [main-comp-ctx-292] O:Dish(2,Clean)
[02:38:33.7370] [main-comp-ctx-292] Dish washed 1.
[02:38:33.7370] [main-comp-ctx-292] O:Dish(1,Clean)
[02:38:33.7400] [main-comp-ctx-289] washing dish 3
[02:38:33.7400] [main-comp-ctx-292] washing dish 4
[02:38:33.8400] [main-comp-ctx-290] Dish washed 3.
[02:38:33.8410] [main-comp-ctx-290] O:Dish(3,Clean)
[02:38:33.8410] [main-comp-ctx-288] washing dish 5
[02:38:33.8410] [main-comp-ctx-289] washing dish 6
[02:38:33.8410] [main-comp-ctx-290] Dish washed 4.
[02:38:33.8410] [main-comp-ctx-291] E: 7
[02:38:33.8420] [main-comp-ctx-290] O:Dish(4,Clean)
[02:38:33.8420] [main-comp-ctx-291] E: 8
[02:38:33.8430] [main-comp-ctx-291] E: 9
[02:38:33.8430] [main-comp-ctx-291] E: 10
[02:38:33.9410] [main-comp-ctx-288] washing dish 7
[02:38:33.9420] [main-comp-ctx-290] washing dish 8
[02:38:33.9420] [main-comp-ctx-292] Dish washed 5.
[02:38:33.9430] [main-comp-ctx-292] O:Dish(5,Clean)
[02:38:33.9430] [main-comp-ctx-292] Dish washed 6.
[02:38:33.9430] [main-comp-ctx-292] O:Dish(6,Clean)
[02:38:34.0420] [main-comp-ctx-292] Dish washed 7.
[02:38:34.0420] [main-comp-ctx-292] O:Dish(7,Clean)
[02:38:34.0420] [main-comp-ctx-289] washing dish 9
[02:38:34.0430] [main-comp-ctx-288] E: 11
[02:38:34.0420] [main-comp-ctx-292] Dish washed 8.
[02:38:34.0430] [main-comp-ctx-290] washing dish 10
[02:38:34.0430] [main-comp-ctx-288] E: 12
[02:38:34.0440] [main-comp-ctx-292] O:Dish(8,Clean)
[02:38:34.0440] [main-comp-ctx-288] E: 13
[02:38:34.1430] [main-comp-ctx-288] Dish washed 9.
[02:38:34.1430] [main-comp-ctx-288] O:Dish(9,Clean)
[02:38:34.1440] [main-comp-ctx-288] washing dish 11
[02:38:34.1450] [main-comp-ctx-290] Dish washed 10.
[02:38:34.1450] [main-comp-ctx-290] O:Dish(10,Clean)
[02:38:34.1460] [main-comp-ctx-290] washing dish 12
[02:38:34.1460] [main-comp-ctx-291] E: 14
[02:38:34.1470] [main-comp-ctx-291] E: 15
[02:38:34.1470] [main-comp-ctx-291] E: 16
[02:38:34.2440] [main-comp-ctx-289] Dish washed 11.
[02:38:34.2450] [main-comp-ctx-289] O:Dish(11,Clean)
[02:38:34.2450] [main-comp-ctx-288] washing dish 13
[02:38:34.2470] [main-comp-ctx-289] Dish washed 12.
[02:38:34.2470] [main-comp-ctx-291] washing dish 14
[02:38:34.2490] [main-comp-ctx-289] O:Dish(12,Clean)
[02:38:34.3460] [main-comp-ctx-289] Dish washed 13.
[02:38:34.3460] [main-comp-ctx-288] E: 17
[02:38:34.3460] [main-comp-ctx-289] O:Dish(13,Clean)
[02:38:34.3470] [main-comp-ctx-288] E: 18
[02:38:34.3460] [main-comp-ctx-292] washing dish 15
[02:38:34.3470] [main-comp-ctx-288] E: 19
[02:38:34.3490] [main-comp-ctx-288] washing dish 16
[02:38:34.3500] [main-comp-ctx-290] Dish washed 14.
[02:38:34.3500] [main-comp-ctx-290] O:Dish(14,Clean)
[02:38:34.4480] [main-comp-ctx-290] Dish washed 15.
[02:38:34.4480] [main-comp-ctx-290] O:Dish(15,Clean)
[02:38:34.4480] [main-comp-ctx-291] washing dish 17
[02:38:34.4500] [main-comp-ctx-292] Dish washed 16.
[02:38:34.4510] [main-comp-ctx-292] O:Dish(16,Clean)
[02:38:34.4510] [main-comp-ctx-288] washing dish 18
[02:38:34.4510] [main-comp-ctx-292] E: 20
[02:38:34.4520] [main-comp-ctx-292] E: 21
[02:38:34.4520] [main-comp-ctx-292] E: 22
[02:38:34.5490] [main-comp-ctx-292] Dish washed 17.
[02:38:34.5500] [main-comp-ctx-292] O:Dish(17,Clean)
[02:38:34.5500] [main-comp-ctx-289] washing dish 19
[02:38:34.5520] [main-comp-ctx-288] Dish washed 18.
[02:38:34.5520] [main-comp-ctx-288] O:Dish(18,Clean)
[02:38:34.5520] [main-comp-ctx-292] washing dish 20
[02:38:34.6510] [main-comp-ctx-288] Dish washed 19.
[02:38:34.6520] [main-comp-ctx-288] O:Dish(19,Clean)
[02:38:34.6520] [main-comp-ctx-290] washing dish 21
[02:38:34.6520] [main-comp-ctx-288] E: 23
[02:38:34.6520] [main-comp-ctx-288] E: 24
[02:38:34.6520] [main-comp-ctx-288] E: 25
[02:38:34.6530] [main-comp-ctx-288] Dish washed 20.
[02:38:34.6530] [main-comp-ctx-288] O:Dish(20,Clean)
[02:38:34.6540] [main-comp-ctx-292] washing dish 22
[02:38:34.7520] [main-comp-ctx-289] Dish washed 21.
[02:38:34.7530] [main-comp-ctx-289] O:Dish(21,Clean)
[02:38:34.7530] [main-comp-ctx-291] washing dish 23
[02:38:34.7580] [main-comp-ctx-289] Dish washed 22.
[02:38:34.7580] [main-comp-ctx-289] O:Dish(22,Clean)
[02:38:34.7590] [main-comp-ctx-290] washing dish 24
[02:38:34.7590] [main-comp-ctx-288] E: 26
[02:38:34.7600] [main-comp-ctx-288] E: 27
[02:38:34.7600] [main-comp-ctx-288] E: 28
[02:38:34.8540] [main-comp-ctx-288] Dish washed 23.
[02:38:34.8550] [main-comp-ctx-291] washing dish 25
[02:38:34.8550] [main-comp-ctx-288] O:Dish(23,Clean)
[02:38:34.8590] [main-comp-ctx-288] Dish washed 24.
[02:38:34.8600] [main-comp-ctx-288] O:Dish(24,Clean)
[02:38:34.8600] [main-comp-ctx-289] washing dish 26
[02:38:34.9550] [main-comp-ctx-288] Dish washed 25.
[02:38:34.9560] [main-comp-ctx-291] washing dish 27
[02:38:34.9560] [main-comp-ctx-288] O:Dish(25,Clean)
[02:38:34.9560] [main-comp-ctx-292] E: 29
[02:38:34.9570] [main-comp-ctx-292] E: 30
[02:38:34.9570] [main-comp-ctx-292] E: 31
[02:38:34.9610] [main-comp-ctx-292] Dish washed 26.
[02:38:34.9610] [main-comp-ctx-292] O:Dish(26,Clean)
[02:38:34.9610] [main-comp-ctx-288] washing dish 28
[02:38:35.0570] [main-comp-ctx-292] Dish washed 27.
[02:38:35.0570] [main-comp-ctx-290] washing dish 29
[02:38:35.0570] [main-comp-ctx-292] O:Dish(27,Clean)
[02:38:35.0620] [main-comp-ctx-292] Dish washed 28.
[02:38:35.0620] [main-comp-ctx-292] O:Dish(28,Clean)
[02:38:35.0620] [main-comp-ctx-288] washing dish 30
[02:38:35.0630] [main-comp-ctx-291] E: 32
[02:38:35.0640] [main-comp-ctx-291] E: 33
[02:38:35.0650] [main-comp-ctx-291] E: 34
[02:38:35.1580] [main-comp-ctx-291] Dish washed 29.
[02:38:35.1580] [main-comp-ctx-291] O:Dish(29,Clean)
[02:38:35.1580] [main-comp-ctx-290] washing dish 31
[02:38:35.1630] [main-comp-ctx-291] Dish washed 30.
[02:38:35.1640] [main-comp-ctx-291] O:Dish(30,Clean)
[02:38:35.1640] [main-comp-ctx-288] washing dish 32
[02:38:35.2590] [main-comp-ctx-291] Dish washed 31.
[02:38:35.2590] [main-comp-ctx-291] O:Dish(31,Clean)
[02:38:35.2590] [main-comp-ctx-289] washing dish 33
[02:38:35.2600] [main-comp-ctx-290] E: 35
[02:38:35.2600] [main-comp-ctx-290] E: 36
[02:38:35.2600] [main-comp-ctx-290] E: 37
[02:38:35.2650] [main-comp-ctx-290] Dish washed 32.
[02:38:35.2650] [main-comp-ctx-290] O:Dish(32,Clean)
[02:38:35.2650] [main-comp-ctx-288] washing dish 34
[02:38:35.3600] [main-comp-ctx-291] Dish washed 33.
[02:38:35.3600] [main-comp-ctx-291] O:Dish(33,Clean)
[02:38:35.3600] [main-comp-ctx-289] washing dish 35
[02:38:35.3660] [main-comp-ctx-291] Dish washed 34.
[02:38:35.3660] [main-comp-ctx-291] O:Dish(34,Clean)
[02:38:35.3660] [main-comp-ctx-288] washing dish 36
[02:38:35.3660] [main-comp-ctx-292] E: 38
[02:38:35.3660] [main-comp-ctx-292] E: 39
[02:38:35.3670] [main-comp-ctx-292] E: 40
[02:38:35.4610] [main-comp-ctx-290] Dish washed 35.
[02:38:35.4610] [main-comp-ctx-290] O:Dish(35,Clean)
[02:38:35.4620] [main-comp-ctx-291] washing dish 37
[02:38:35.4670] [main-comp-ctx-290] Dish washed 36.
[02:38:35.4670] [main-comp-ctx-290] O:Dish(36,Clean)
[02:38:35.4670] [main-comp-ctx-288] washing dish 38
[02:38:35.5620] [main-comp-ctx-289] Dish washed 37.
[02:38:35.5630] [main-comp-ctx-289] O:Dish(37,Clean)
[02:38:35.5630] [main-comp-ctx-290] washing dish 39
[02:38:35.5630] [main-comp-ctx-291] E: 41
[02:38:35.5640] [main-comp-ctx-291] E: 42
[02:38:35.5650] [main-comp-ctx-291] E: 43
[02:38:35.5680] [main-comp-ctx-291] Dish washed 38.
[02:38:35.5680] [main-comp-ctx-288] washing dish 40
[02:38:35.5680] [main-comp-ctx-291] O:Dish(38,Clean)
[02:38:35.6640] [main-comp-ctx-291] Dish washed 39.
[02:38:35.6640] [main-comp-ctx-291] O:Dish(39,Clean)
[02:38:35.6640] [main-comp-ctx-290] washing dish 41
[02:38:35.6690] [main-comp-ctx-291] Dish washed 40.
[02:38:35.6690] [main-comp-ctx-291] O:Dish(40,Clean)
[02:38:35.6690] [main-comp-ctx-288] washing dish 42
[02:38:35.6690] [main-comp-ctx-289] E: 44
[02:38:35.6690] [main-comp-ctx-289] E: 45
[02:38:35.6700] [main-comp-ctx-289] E: 46
[02:38:35.7650] [main-comp-ctx-292] Dish washed 41.
[02:38:35.7650] [main-comp-ctx-292] O:Dish(41,Clean)
[02:38:35.7650] [main-comp-ctx-290] washing dish 43
[02:38:35.7690] [main-comp-ctx-289] Dish washed 42.
[02:38:35.7700] [main-comp-ctx-289] O:Dish(42,Clean)
[02:38:35.7700] [main-comp-ctx-292] washing dish 44
[02:38:35.8660] [main-comp-ctx-289] Dish washed 43.
[02:38:35.8660] [main-comp-ctx-289] O:Dish(43,Clean)
[02:38:35.8660] [main-comp-ctx-290] washing dish 45
[02:38:35.8670] [main-comp-ctx-288] E: 47
[02:38:35.8670] [main-comp-ctx-288] E: 48
[02:38:35.8670] [main-comp-ctx-288] E: 49
[02:38:35.8710] [main-comp-ctx-288] Dish washed 44.
[02:38:35.8710] [main-comp-ctx-288] O:Dish(44,Clean)
[02:38:35.8710] [main-comp-ctx-289] washing dish 46
[02:38:35.9670] [main-comp-ctx-292] Dish washed 45.
[02:38:35.9670] [main-comp-ctx-292] O:Dish(45,Clean)
[02:38:35.9670] [main-comp-ctx-288] washing dish 47
[02:38:35.9720] [main-comp-ctx-292] Dish washed 46.
[02:38:35.9720] [main-comp-ctx-292] O:Dish(46,Clean)
[02:38:35.9720] [main-comp-ctx-289] washing dish 48
[02:38:35.9720] [main-comp-ctx-292] E: 50
[02:38:35.9720] [main-comp-ctx-292] E: 51
[02:38:35.9730] [main-comp-ctx-292] E: 52
[02:38:36.0680] [main-comp-ctx-292] Dish washed 47.
[02:38:36.0680] [main-comp-ctx-292] O:Dish(47,Clean)
[02:38:36.0680] [main-comp-ctx-288] washing dish 49
[02:38:36.0730] [main-comp-ctx-292] Dish washed 48.
[02:38:36.0730] [main-comp-ctx-292] O:Dish(48,Clean)
[02:38:36.0730] [main-comp-ctx-290] washing dish 50
[02:38:36.1690] [main-comp-ctx-292] Dish washed 49.
[02:38:36.1690] [main-comp-ctx-292] O:Dish(49,Clean)
[02:38:36.1690] [main-comp-ctx-288] washing dish 51
[02:38:36.1700] [main-comp-ctx-292] E: 53
[02:38:36.1700] [main-comp-ctx-292] E: 54
[02:38:36.1700] [main-comp-ctx-292] E: 55
[02:38:36.1740] [main-comp-ctx-292] Dish washed 50.
[02:38:36.1740] [main-comp-ctx-289] washing dish 52
[02:38:36.1740] [main-comp-ctx-292] O:Dish(50,Clean)
[02:38:36.2700] [main-comp-ctx-292] Dish washed 51.
[02:38:36.2700] [main-comp-ctx-292] O:Dish(51,Clean)
[02:38:36.2700] [main-comp-ctx-288] washing dish 53
[02:38:36.2750] [main-comp-ctx-292] Dish washed 52.
[02:38:36.2750] [main-comp-ctx-291] washing dish 54
[02:38:36.2750] [main-comp-ctx-292] O:Dish(52,Clean)
[02:38:36.2750] [main-comp-ctx-290] E: 56
[02:38:36.2760] [main-comp-ctx-290] E: 57
[02:38:36.2760] [main-comp-ctx-290] E: 58
[02:38:36.3710] [main-comp-ctx-290] Dish washed 53.
[02:38:36.3710] [main-comp-ctx-290] O:Dish(53,Clean)
[02:38:36.3710] [main-comp-ctx-288] washing dish 55
[02:38:36.3760] [main-comp-ctx-290] Dish washed 54.
[02:38:36.3760] [main-comp-ctx-290] O:Dish(54,Clean)
[02:38:36.3760] [main-comp-ctx-292] washing dish 56
[02:38:36.4730] [main-comp-ctx-290] Dish washed 55.
[02:38:36.4730] [main-comp-ctx-290] O:Dish(55,Clean)
[02:38:36.4730] [main-comp-ctx-289] washing dish 57
[02:38:36.4740] [main-comp-ctx-290] E: 59
[02:38:36.4740] [main-comp-ctx-290] E: 60
[02:38:36.4740] [main-comp-ctx-290] E: 61
[02:38:36.4770] [main-comp-ctx-290] Dish washed 56.
[02:38:36.4770] [main-comp-ctx-290] O:Dish(56,Clean)
[02:38:36.4770] [main-comp-ctx-292] washing dish 58
[02:38:36.5740] [main-comp-ctx-290] Dish washed 57.
[02:38:36.5750] [main-comp-ctx-290] O:Dish(57,Clean)
[02:38:36.5750] [main-comp-ctx-291] washing dish 59
[02:38:36.5780] [main-comp-ctx-290] Dish washed 58.
[02:38:36.5780] [main-comp-ctx-288] washing dish 60
[02:38:36.5780] [main-comp-ctx-290] O:Dish(58,Clean)
[02:38:36.5790] [main-comp-ctx-292] E: 62
[02:38:36.5790] [main-comp-ctx-292] E: 63
[02:38:36.5790] [main-comp-ctx-292] E: 64
[02:38:36.6760] [main-comp-ctx-292] Dish washed 59.
[02:38:36.6760] [main-comp-ctx-292] O:Dish(59,Clean)
[02:38:36.6760] [main-comp-ctx-291] washing dish 61
[02:38:36.6790] [main-comp-ctx-292] Dish washed 60.
[02:38:36.6790] [main-comp-ctx-292] O:Dish(60,Clean)
[02:38:36.6790] [main-comp-ctx-288] washing dish 62
[02:38:36.7770] [main-comp-ctx-292] Dish washed 61.
[02:38:36.7770] [main-comp-ctx-291] washing dish 63
[02:38:36.7780] [main-comp-ctx-289] E: 65
[02:38:36.7780] [main-comp-ctx-289] E: 66
[02:38:36.7770] [main-comp-ctx-292] O:Dish(61,Clean)
[02:38:36.7780] [main-comp-ctx-289] E: 67
[02:38:36.7800] [main-comp-ctx-289] Dish washed 62.
[02:38:36.7800] [main-comp-ctx-289] O:Dish(62,Clean)
[02:38:36.7800] [main-comp-ctx-290] washing dish 64
[02:38:36.8780] [main-comp-ctx-289] Dish washed 63.
[02:38:36.8780] [main-comp-ctx-292] washing dish 65
[02:38:36.8790] [main-comp-ctx-289] O:Dish(63,Clean)
[02:38:36.8810] [main-comp-ctx-289] Dish washed 64.
[02:38:36.8810] [main-comp-ctx-289] O:Dish(64,Clean)
[02:38:36.8810] [main-comp-ctx-291] washing dish 66
[02:38:36.8810] [main-comp-ctx-290] E: 68
[02:38:36.8820] [main-comp-ctx-290] E: 69
[02:38:36.8820] [main-comp-ctx-290] E: 70
[02:38:36.9790] [main-comp-ctx-290] Dish washed 65.
[02:38:36.9800] [main-comp-ctx-290] O:Dish(65,Clean)
[02:38:36.9800] [main-comp-ctx-289] washing dish 67
[02:38:36.9820] [main-comp-ctx-290] Dish washed 66.
[02:38:36.9820] [main-comp-ctx-291] washing dish 68
[02:38:36.9820] [main-comp-ctx-290] O:Dish(66,Clean)
[02:38:37.0810] [main-comp-ctx-292] Dish washed 67.
[02:38:37.0810] [main-comp-ctx-292] O:Dish(67,Clean)
[02:38:37.0810] [main-comp-ctx-289] washing dish 69
[02:38:37.0810] [main-comp-ctx-288] E: 71
[02:38:37.0810] [main-comp-ctx-288] E: 72
[02:38:37.0820] [main-comp-ctx-288] E: 73
[02:38:37.0830] [main-comp-ctx-288] Dish washed 68.
[02:38:37.0830] [main-comp-ctx-288] O:Dish(68,Clean)
[02:38:37.0830] [main-comp-ctx-292] washing dish 70
[02:38:37.1820] [main-comp-ctx-291] Dish washed 69.
[02:38:37.1820] [main-comp-ctx-288] washing dish 71
[02:38:37.1820] [main-comp-ctx-291] O:Dish(69,Clean)
[02:38:37.1840] [main-comp-ctx-291] Dish washed 70.
[02:38:37.1840] [main-comp-ctx-291] O:Dish(70,Clean)
[02:38:37.1840] [main-comp-ctx-292] washing dish 72
[02:38:37.1850] [main-comp-ctx-290] E: 74
[02:38:37.1850] [main-comp-ctx-290] E: 75
[02:38:37.1850] [main-comp-ctx-290] E: 76
[02:38:37.2830] [main-comp-ctx-290] Dish washed 71.
[02:38:37.2830] [main-comp-ctx-290] O:Dish(71,Clean)
[02:38:37.2830] [main-comp-ctx-288] washing dish 73
[02:38:37.2860] [main-comp-ctx-292] Dish washed 72.
[02:38:37.2870] [main-comp-ctx-292] O:Dish(72,Clean)
[02:38:37.2870] [main-comp-ctx-289] washing dish 74
[02:38:37.3840] [main-comp-ctx-292] Dish washed 73.
[02:38:37.3840] [main-comp-ctx-288] washing dish 75
[02:38:37.3840] [main-comp-ctx-292] O:Dish(73,Clean)
[02:38:37.3840] [main-comp-ctx-291] E: 77
[02:38:37.3840] [main-comp-ctx-291] E: 78
[02:38:37.3850] [main-comp-ctx-291] E: 79
[02:38:37.3870] [main-comp-ctx-291] Dish washed 74.
[02:38:37.3870] [main-comp-ctx-291] O:Dish(74,Clean)
[02:38:37.3880] [main-comp-ctx-292] washing dish 76
[02:38:37.4850] [main-comp-ctx-289] Dish washed 75.
[02:38:37.4850] [main-comp-ctx-288] washing dish 77
[02:38:37.4850] [main-comp-ctx-289] O:Dish(75,Clean)
[02:38:37.4880] [main-comp-ctx-289] Dish washed 76.
[02:38:37.4880] [main-comp-ctx-292] E: 80
[02:38:37.4880] [main-comp-ctx-291] washing dish 78
[02:38:37.4880] [main-comp-ctx-289] O:Dish(76,Clean)
[02:38:37.4880] [main-comp-ctx-292] E: 81
[02:38:37.4890] [main-comp-ctx-292] E: 82
[02:38:37.5860] [main-comp-ctx-292] Dish washed 77.
[02:38:37.5860] [main-comp-ctx-288] washing dish 79
[02:38:37.5860] [main-comp-ctx-292] O:Dish(77,Clean)
[02:38:37.5890] [main-comp-ctx-292] Dish washed 78.
[02:38:37.5890] [main-comp-ctx-292] O:Dish(78,Clean)
[02:38:37.5910] [main-comp-ctx-289] washing dish 80
[02:38:37.6870] [main-comp-ctx-290] Dish washed 79.
[02:38:37.6870] [main-comp-ctx-291] E: 83
[02:38:37.6870] [main-comp-ctx-290] O:Dish(79,Clean)
[02:38:37.6870] [main-comp-ctx-291] E: 84
[02:38:37.6870] [main-comp-ctx-291] E: 85
[02:38:37.6870] [main-comp-ctx-292] washing dish 81
[02:38:37.6910] [main-comp-ctx-291] Dish washed 80.
[02:38:37.6920] [main-comp-ctx-291] O:Dish(80,Clean)
[02:38:37.6920] [main-comp-ctx-288] washing dish 82
[02:38:37.7890] [main-comp-ctx-291] Dish washed 81.
[02:38:37.7890] [main-comp-ctx-291] O:Dish(81,Clean)
[02:38:37.7900] [main-comp-ctx-290] washing dish 83
[02:38:37.7930] [main-comp-ctx-292] Dish washed 82.
[02:38:37.7930] [main-comp-ctx-291] washing dish 84
[02:38:37.7930] [main-comp-ctx-292] O:Dish(82,Clean)
[02:38:37.7930] [main-comp-ctx-288] E: 86
[02:38:37.7930] [main-comp-ctx-288] E: 87
[02:38:37.7940] [main-comp-ctx-288] E: 88
[02:38:37.8910] [main-comp-ctx-288] Dish washed 83.
[02:38:37.8910] [main-comp-ctx-288] O:Dish(83,Clean)
[02:38:37.8910] [main-comp-ctx-290] washing dish 85
[02:38:37.8940] [main-comp-ctx-288] Dish washed 84.
[02:38:37.8940] [main-comp-ctx-288] O:Dish(84,Clean)
[02:38:37.8940] [main-comp-ctx-289] washing dish 86
[02:38:37.9920] [main-comp-ctx-288] Dish washed 85.
[02:38:37.9920] [main-comp-ctx-288] O:Dish(85,Clean)
[02:38:37.9920] [main-comp-ctx-290] washing dish 87
[02:38:37.9920] [main-comp-ctx-291] E: 89
[02:38:37.9930] [main-comp-ctx-291] E: 90
[02:38:37.9930] [main-comp-ctx-291] E: 91
[02:38:37.9950] [main-comp-ctx-292] Dish washed 86.
[02:38:37.9950] [main-comp-ctx-289] washing dish 88
[02:38:37.9950] [main-comp-ctx-292] O:Dish(86,Clean)
[02:38:38.0930] [main-comp-ctx-292] Dish washed 87.
[02:38:38.0930] [main-comp-ctx-288] washing dish 89
[02:38:38.0930] [main-comp-ctx-292] O:Dish(87,Clean)
[02:38:38.0960] [main-comp-ctx-290] Dish washed 88.
[02:38:38.0960] [main-comp-ctx-290] O:Dish(88,Clean)
[02:38:38.0960] [main-comp-ctx-289] washing dish 90
[02:38:38.0960] [main-comp-ctx-292] E: 92
[02:38:38.0970] [main-comp-ctx-292] E: 93
[02:38:38.0970] [main-comp-ctx-292] E: 94
[02:38:38.1940] [main-comp-ctx-292] Dish washed 89.
[02:38:38.1940] [main-comp-ctx-292] O:Dish(89,Clean)
[02:38:38.1940] [main-comp-ctx-288] washing dish 91
[02:38:38.1970] [main-comp-ctx-290] Dish washed 90.
[02:38:38.1970] [main-comp-ctx-289] washing dish 92
[02:38:38.1970] [main-comp-ctx-290] O:Dish(90,Clean)
[02:38:38.2950] [main-comp-ctx-290] Dish washed 91.
[02:38:38.2950] [main-comp-ctx-288] washing dish 93
[02:38:38.2950] [main-comp-ctx-290] O:Dish(91,Clean)
[02:38:38.2950] [main-comp-ctx-291] E: 95
[02:38:38.2960] [main-comp-ctx-291] E: 96
[02:38:38.2960] [main-comp-ctx-291] E: 97
[02:38:38.2980] [main-comp-ctx-289] Dish washed 92.
[02:38:38.2980] [main-comp-ctx-289] O:Dish(92,Clean)
[02:38:38.2980] [main-comp-ctx-292] washing dish 94
[02:38:38.3960] [main-comp-ctx-291] Dish washed 93.
[02:38:38.3960] [main-comp-ctx-291] O:Dish(93,Clean)
[02:38:38.3960] [main-comp-ctx-290] washing dish 95
[02:38:38.4000] [main-comp-ctx-289] Dish washed 94.
[02:38:38.4000] [main-comp-ctx-289] O:Dish(94,Clean)
[02:38:38.4000] [main-comp-ctx-288] washing dish 96
[02:38:38.4000] [main-comp-ctx-292] E: 98
[02:38:38.4010] [main-comp-ctx-292] E: 99
[02:38:38.4010] [main-comp-ctx-292] E: 100
[02:38:38.4970] [main-comp-ctx-292] Dish washed 95.
[02:38:38.4970] [main-comp-ctx-292] O:Dish(95,Clean)
[02:38:38.4970] [main-comp-ctx-289] washing dish 97
[02:38:38.5010] [main-comp-ctx-292] Dish washed 96.
[02:38:38.5010] [main-comp-ctx-292] O:Dish(96,Clean)
[02:38:38.5010] [main-comp-ctx-291] washing dish 98
[02:38:38.5980] [main-comp-ctx-292] Dish washed 97.
[02:38:38.5980] [main-comp-ctx-289] washing dish 99
[02:38:38.5980] [main-comp-ctx-292] O:Dish(97,Clean)
[02:38:38.6020] [main-comp-ctx-292] Dish washed 98.
[02:38:38.6020] [main-comp-ctx-288] washing dish 100
[02:38:38.6020] [main-comp-ctx-292] O:Dish(98,Clean)
[02:38:38.6990] [main-comp-ctx-290] Dish washed 99.
[02:38:38.6990] [main-comp-ctx-290] O:Dish(99,Clean)
[02:38:38.7030] [main-comp-ctx-288] Dish washed 100.
[02:38:38.7030] [main-comp-ctx-288] O:Dish(100,Clean)
[02:38:45.7310] [run-main-6] Program Ends.
*/
