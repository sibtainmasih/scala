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
  .mapParallelUnordered(5)(d => d.wash)
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
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 3))

Thread.sleep(12000)
println(s"$log Program Ends.")

/* Output
[03:32:06.6710] [run-main-ad] Program starts.
[03:32:06.6860] [run-main-ad] E: 1
[03:32:06.6940] [run-main-ad] E: 2
[03:32:06.6950] [run-main-ad] E: 3
[03:32:06.7060] [main-comp-ctx-6055] washing dish 2
[03:32:06.7060] [main-comp-ctx-6054] washing dish 3
[03:32:06.7070] [main-comp-ctx-6053] washing dish 1
[03:32:06.9120] [main-comp-ctx-6053] E: 4
[03:32:06.9130] [main-comp-ctx-6053] E: 5
[03:32:06.9130] [main-comp-ctx-6054] washing dish 4
[03:32:06.9130] [main-comp-ctx-6053] E: 6
[03:32:06.9140] [main-comp-ctx-6055] Dish washed 2.
[03:32:06.9150] [main-comp-ctx-6053] E: 7
[03:32:06.9150] [main-comp-ctx-6055] O:Dish(2,Clean)
[03:32:06.9170] [main-comp-ctx-6055] Dish washed 3.
[03:32:06.9180] [main-comp-ctx-6055] O:Dish(3,Clean)
[03:32:06.9180] [main-comp-ctx-6055] Dish washed 1.
[03:32:06.9180] [main-comp-ctx-6055] O:Dish(1,Clean)
[03:32:06.9180] [main-comp-ctx-6053] washing dish 5
[03:32:06.9180] [main-comp-ctx-6055] washing dish 6
[03:32:07.1140] [main-comp-ctx-6054] Dish washed 4.
[03:32:07.1140] [main-comp-ctx-6054] O:Dish(4,Clean)
[03:32:07.1140] [main-comp-ctx-6054] washing dish 7
[03:32:07.1220] [main-comp-ctx-6055] Dish washed 6.
[03:32:07.1220] [main-comp-ctx-6055] O:Dish(6,Clean)
[03:32:07.1230] [main-comp-ctx-6055] Dish washed 5.
[03:32:07.1230] [main-comp-ctx-6055] O:Dish(5,Clean)
[03:32:07.1230] [main-comp-ctx-6053] E: 8
[03:32:07.1240] [main-comp-ctx-6053] E: 9
[03:32:07.1240] [main-comp-ctx-6055] washing dish 8
[03:32:07.1260] [main-comp-ctx-6053] E: 10
[03:32:07.1260] [main-comp-ctx-6053] E: 11
[03:32:07.1270] [main-comp-ctx-6053] washing dish 9
[03:32:07.3150] [main-comp-ctx-6054] Dish washed 7.
[03:32:07.3150] [main-comp-ctx-6054] O:Dish(7,Clean)
[03:32:07.3160] [main-comp-ctx-6054] washing dish 10
[03:32:07.3260] [main-comp-ctx-6055] Dish washed 8.
[03:32:07.3270] [main-comp-ctx-6055] O:Dish(8,Clean)
[03:32:07.3270] [main-comp-ctx-6055] washing dish 11
[03:32:07.3280] [main-comp-ctx-6053] Dish washed 9.
[03:32:07.3280] [main-comp-ctx-6053] O:Dish(9,Clean)
[03:32:07.3290] [main-comp-ctx-6053] E: 12
[03:32:07.3290] [main-comp-ctx-6053] E: 13
[03:32:07.3290] [main-comp-ctx-6053] E: 14
[03:32:07.3300] [main-comp-ctx-6053] washing dish 12
[03:32:07.5160] [main-comp-ctx-6054] Dish washed 10.
[03:32:07.5170] [main-comp-ctx-6054] O:Dish(10,Clean)
[03:32:07.5170] [main-comp-ctx-6054] washing dish 13
[03:32:07.5280] [main-comp-ctx-6055] Dish washed 11.
[03:32:07.5280] [main-comp-ctx-6055] O:Dish(11,Clean)
[03:32:07.5280] [main-comp-ctx-6055] washing dish 14
[03:32:07.5310] [main-comp-ctx-6053] Dish washed 12.
[03:32:07.5310] [main-comp-ctx-6053] O:Dish(12,Clean)
[03:32:07.5310] [main-comp-ctx-6053] E: 15
[03:32:07.5320] [main-comp-ctx-6053] E: 16
[03:32:07.5320] [main-comp-ctx-6053] E: 17
[03:32:07.5330] [main-comp-ctx-6053] washing dish 15
[03:32:07.7180] [main-comp-ctx-6054] Dish washed 13.
[03:32:07.7180] [main-comp-ctx-6054] O:Dish(13,Clean)
[03:32:07.7190] [main-comp-ctx-6054] washing dish 16
[03:32:07.7290] [main-comp-ctx-6055] Dish washed 14.
[03:32:07.7290] [main-comp-ctx-6055] O:Dish(14,Clean)
[03:32:07.7290] [main-comp-ctx-6055] washing dish 17
[03:32:07.7330] [main-comp-ctx-6053] Dish washed 15.
[03:32:07.7340] [main-comp-ctx-6053] O:Dish(15,Clean)
[03:32:07.7340] [main-comp-ctx-6053] E: 18
[03:32:07.7340] [main-comp-ctx-6053] E: 19
[03:32:07.7350] [main-comp-ctx-6053] E: 20
[03:32:07.7360] [main-comp-ctx-6053] washing dish 18
[03:32:07.9190] [main-comp-ctx-6054] Dish washed 16.
[03:32:07.9200] [main-comp-ctx-6054] O:Dish(16,Clean)
[03:32:07.9210] [main-comp-ctx-6054] washing dish 19
[03:32:07.9300] [main-comp-ctx-6055] Dish washed 17.
[03:32:07.9300] [main-comp-ctx-6055] O:Dish(17,Clean)
[03:32:07.9310] [main-comp-ctx-6055] washing dish 20
[03:32:07.9370] [main-comp-ctx-6053] Dish washed 18.
[03:32:07.9370] [main-comp-ctx-6053] O:Dish(18,Clean)
[03:32:07.9370] [main-comp-ctx-6053] E: 21
[03:32:07.9380] [main-comp-ctx-6053] E: 22
[03:32:07.9380] [main-comp-ctx-6053] E: 23
[03:32:07.9390] [main-comp-ctx-6053] washing dish 21
[03:32:08.1210] [main-comp-ctx-6054] Dish washed 19.
[03:32:08.1210] [main-comp-ctx-6054] O:Dish(19,Clean)
[03:32:08.1220] [main-comp-ctx-6054] washing dish 22
[03:32:08.1320] [main-comp-ctx-6055] Dish washed 20.
[03:32:08.1320] [main-comp-ctx-6055] O:Dish(20,Clean)
[03:32:08.1320] [main-comp-ctx-6055] washing dish 23
[03:32:08.1390] [main-comp-ctx-6053] Dish washed 21.
[03:32:08.1390] [main-comp-ctx-6053] O:Dish(21,Clean)
[03:32:08.1400] [main-comp-ctx-6053] E: 24
[03:32:08.1400] [main-comp-ctx-6053] E: 25
[03:32:08.1410] [main-comp-ctx-6053] E: 26
[03:32:08.1420] [main-comp-ctx-6053] washing dish 24
[03:32:08.3220] [main-comp-ctx-6054] Dish washed 22.
[03:32:08.3230] [main-comp-ctx-6054] O:Dish(22,Clean)
[03:32:08.3230] [main-comp-ctx-6054] washing dish 25
[03:32:08.3330] [main-comp-ctx-6055] Dish washed 23.
[03:32:08.3330] [main-comp-ctx-6055] O:Dish(23,Clean)
[03:32:08.3330] [main-comp-ctx-6055] washing dish 26
[03:32:08.3420] [main-comp-ctx-6053] Dish washed 24.
[03:32:08.3420] [main-comp-ctx-6053] O:Dish(24,Clean)
[03:32:08.3430] [main-comp-ctx-6053] E: 27
[03:32:08.3430] [main-comp-ctx-6053] E: 28
[03:32:08.3440] [main-comp-ctx-6053] E: 29
[03:32:08.3460] [main-comp-ctx-6053] washing dish 27
[03:32:08.5240] [main-comp-ctx-6054] Dish washed 25.
[03:32:08.5240] [main-comp-ctx-6054] O:Dish(25,Clean)
[03:32:08.5250] [main-comp-ctx-6054] washing dish 28
[03:32:08.5340] [main-comp-ctx-6055] Dish washed 26.
[03:32:08.5340] [main-comp-ctx-6055] O:Dish(26,Clean)
[03:32:08.5340] [main-comp-ctx-6055] washing dish 29
[03:32:08.5460] [main-comp-ctx-6053] Dish washed 27.
[03:32:08.5470] [main-comp-ctx-6053] O:Dish(27,Clean)
[03:32:08.5470] [main-comp-ctx-6053] E: 30
[03:32:08.5480] [main-comp-ctx-6053] E: 31
[03:32:08.5480] [main-comp-ctx-6053] E: 32
[03:32:08.5490] [main-comp-ctx-6053] washing dish 30
[03:32:08.7260] [main-comp-ctx-6054] Dish washed 28.
[03:32:08.7260] [main-comp-ctx-6054] O:Dish(28,Clean)
[03:32:08.7270] [main-comp-ctx-6054] washing dish 31
[03:32:08.7350] [main-comp-ctx-6055] Dish washed 29.
[03:32:08.7350] [main-comp-ctx-6055] O:Dish(29,Clean)
[03:32:08.7360] [main-comp-ctx-6055] washing dish 32
[03:32:08.7500] [main-comp-ctx-6053] Dish washed 30.
[03:32:08.7510] [main-comp-ctx-6053] O:Dish(30,Clean)
[03:32:08.7520] [main-comp-ctx-6053] E: 33
[03:32:08.7520] [main-comp-ctx-6053] E: 34
[03:32:08.7530] [main-comp-ctx-6053] E: 35
[03:32:08.7540] [main-comp-ctx-6053] washing dish 33
[03:32:08.9280] [main-comp-ctx-6054] Dish washed 31.
[03:32:08.9280] [main-comp-ctx-6054] O:Dish(31,Clean)
[03:32:08.9290] [main-comp-ctx-6054] washing dish 34
[03:32:08.9360] [main-comp-ctx-6055] Dish washed 32.
[03:32:08.9360] [main-comp-ctx-6055] O:Dish(32,Clean)
[03:32:08.9370] [main-comp-ctx-6055] washing dish 35
[03:32:08.9550] [main-comp-ctx-6053] Dish washed 33.
[03:32:08.9560] [main-comp-ctx-6053] O:Dish(33,Clean)
[03:32:08.9560] [main-comp-ctx-6053] E: 36
[03:32:08.9570] [main-comp-ctx-6053] E: 37
[03:32:08.9580] [main-comp-ctx-6053] E: 38
[03:32:08.9590] [main-comp-ctx-6053] washing dish 36
[03:32:09.1290] [main-comp-ctx-6054] Dish washed 34.
[03:32:09.1300] [main-comp-ctx-6054] O:Dish(34,Clean)
[03:32:09.1310] [main-comp-ctx-6054] washing dish 37
[03:32:09.1370] [main-comp-ctx-6055] Dish washed 35.
[03:32:09.1380] [main-comp-ctx-6055] O:Dish(35,Clean)
[03:32:09.1380] [main-comp-ctx-6055] washing dish 38
[03:32:09.1600] [main-comp-ctx-6053] Dish washed 36.
[03:32:09.1600] [main-comp-ctx-6053] O:Dish(36,Clean)
[03:32:09.1610] [main-comp-ctx-6053] E: 39
[03:32:09.1620] [main-comp-ctx-6053] E: 40
[03:32:09.1630] [main-comp-ctx-6053] E: 41
[03:32:09.1640] [main-comp-ctx-6053] washing dish 39
[03:32:09.3310] [main-comp-ctx-6054] Dish washed 37.
[03:32:09.3320] [main-comp-ctx-6054] O:Dish(37,Clean)
[03:32:09.3320] [main-comp-ctx-6054] washing dish 40
[03:32:09.3390] [main-comp-ctx-6055] Dish washed 38.
[03:32:09.3390] [main-comp-ctx-6055] O:Dish(38,Clean)
[03:32:09.3400] [main-comp-ctx-6055] washing dish 41
[03:32:09.3650] [main-comp-ctx-6053] Dish washed 39.
[03:32:09.3660] [main-comp-ctx-6053] O:Dish(39,Clean)
[03:32:09.3660] [main-comp-ctx-6053] E: 42
[03:32:09.3670] [main-comp-ctx-6053] E: 43
[03:32:09.3680] [main-comp-ctx-6053] E: 44
[03:32:09.3690] [main-comp-ctx-6053] washing dish 42
[03:32:09.5330] [main-comp-ctx-6054] Dish washed 40.
[03:32:09.5330] [main-comp-ctx-6054] O:Dish(40,Clean)
[03:32:09.5330] [main-comp-ctx-6054] washing dish 43
[03:32:09.5400] [main-comp-ctx-6055] Dish washed 41.
[03:32:09.5400] [main-comp-ctx-6055] O:Dish(41,Clean)
[03:32:09.5410] [main-comp-ctx-6055] washing dish 44
[03:32:09.5700] [main-comp-ctx-6053] Dish washed 42.
[03:32:09.5700] [main-comp-ctx-6053] O:Dish(42,Clean)
[03:32:09.5700] [main-comp-ctx-6053] E: 45
[03:32:09.5710] [main-comp-ctx-6053] E: 46
[03:32:09.5710] [main-comp-ctx-6053] E: 47
[03:32:09.5720] [main-comp-ctx-6053] washing dish 45
[03:32:09.7340] [main-comp-ctx-6054] Dish washed 43.
[03:32:09.7340] [main-comp-ctx-6054] O:Dish(43,Clean)
[03:32:09.7340] [main-comp-ctx-6054] washing dish 46
[03:32:09.7410] [main-comp-ctx-6055] Dish washed 44.
[03:32:09.7420] [main-comp-ctx-6055] O:Dish(44,Clean)
[03:32:09.7420] [main-comp-ctx-6055] washing dish 47
[03:32:09.7730] [main-comp-ctx-6053] Dish washed 45.
[03:32:09.7730] [main-comp-ctx-6053] O:Dish(45,Clean)
[03:32:09.7730] [main-comp-ctx-6053] E: 48
[03:32:09.7740] [main-comp-ctx-6053] E: 49
[03:32:09.7740] [main-comp-ctx-6053] E: 50
[03:32:09.7760] [main-comp-ctx-6053] washing dish 48
[03:32:09.9350] [main-comp-ctx-6054] Dish washed 46.
[03:32:09.9350] [main-comp-ctx-6054] O:Dish(46,Clean)
[03:32:09.9360] [main-comp-ctx-6054] washing dish 49
[03:32:09.9430] [main-comp-ctx-6055] Dish washed 47.
[03:32:09.9430] [main-comp-ctx-6055] O:Dish(47,Clean)
[03:32:09.9440] [main-comp-ctx-6055] washing dish 50
[03:32:09.9760] [main-comp-ctx-6053] Dish washed 48.
[03:32:09.9770] [main-comp-ctx-6053] O:Dish(48,Clean)
[03:32:09.9780] [main-comp-ctx-6053] E: 51
[03:32:09.9780] [main-comp-ctx-6053] E: 52
[03:32:09.9790] [main-comp-ctx-6053] E: 53
[03:32:09.9800] [main-comp-ctx-6053] washing dish 51
[03:32:10.1370] [main-comp-ctx-6054] Dish washed 49.
[03:32:10.1370] [main-comp-ctx-6054] O:Dish(49,Clean)
[03:32:10.1380] [main-comp-ctx-6054] washing dish 52
[03:32:10.1440] [main-comp-ctx-6055] Dish washed 50.
[03:32:10.1440] [main-comp-ctx-6055] O:Dish(50,Clean)
[03:32:10.1450] [main-comp-ctx-6055] washing dish 53
[03:32:10.1810] [main-comp-ctx-6053] Dish washed 51.
[03:32:10.1810] [main-comp-ctx-6053] O:Dish(51,Clean)
[03:32:10.1820] [main-comp-ctx-6053] E: 54
[03:32:10.1830] [main-comp-ctx-6053] E: 55
[03:32:10.1830] [main-comp-ctx-6053] E: 56
[03:32:10.1840] [main-comp-ctx-6053] washing dish 54
[03:32:10.3390] [main-comp-ctx-6054] Dish washed 52.
[03:32:10.3390] [main-comp-ctx-6054] O:Dish(52,Clean)
[03:32:10.3390] [main-comp-ctx-6054] washing dish 55
[03:32:10.3460] [main-comp-ctx-6055] Dish washed 53.
[03:32:10.3460] [main-comp-ctx-6055] O:Dish(53,Clean)
[03:32:10.3460] [main-comp-ctx-6055] washing dish 56
[03:32:10.3850] [main-comp-ctx-6053] Dish washed 54.
[03:32:10.3850] [main-comp-ctx-6053] O:Dish(54,Clean)
[03:32:10.3860] [main-comp-ctx-6053] E: 57
[03:32:10.3860] [main-comp-ctx-6053] E: 58
[03:32:10.3880] [main-comp-ctx-6053] E: 59
[03:32:10.3900] [main-comp-ctx-6053] washing dish 57
[03:32:10.5400] [main-comp-ctx-6054] Dish washed 55.
[03:32:10.5400] [main-comp-ctx-6054] O:Dish(55,Clean)
[03:32:10.5410] [main-comp-ctx-6054] washing dish 58
[03:32:10.5470] [main-comp-ctx-6055] Dish washed 56.
[03:32:10.5470] [main-comp-ctx-6055] O:Dish(56,Clean)
[03:32:10.5480] [main-comp-ctx-6055] washing dish 59
[03:32:10.5900] [main-comp-ctx-6053] Dish washed 57.
[03:32:10.5910] [main-comp-ctx-6053] O:Dish(57,Clean)
[03:32:10.5910] [main-comp-ctx-6053] E: 60
[03:32:10.5920] [main-comp-ctx-6053] E: 61
[03:32:10.5920] [main-comp-ctx-6053] E: 62
[03:32:10.5940] [main-comp-ctx-6053] washing dish 60
[03:32:10.7410] [main-comp-ctx-6054] Dish washed 58.
[03:32:10.7420] [main-comp-ctx-6054] O:Dish(58,Clean)
[03:32:10.7420] [main-comp-ctx-6054] washing dish 61
[03:32:10.7480] [main-comp-ctx-6055] Dish washed 59.
[03:32:10.7480] [main-comp-ctx-6055] O:Dish(59,Clean)
[03:32:10.7490] [main-comp-ctx-6055] washing dish 62
[03:32:10.7940] [main-comp-ctx-6053] Dish washed 60.
[03:32:10.7940] [main-comp-ctx-6053] O:Dish(60,Clean)
[03:32:10.7950] [main-comp-ctx-6053] E: 63
[03:32:10.7960] [main-comp-ctx-6053] E: 64
[03:32:10.7960] [main-comp-ctx-6053] E: 65
[03:32:10.7970] [main-comp-ctx-6053] washing dish 63
[03:32:10.9430] [main-comp-ctx-6054] Dish washed 61.
[03:32:10.9430] [main-comp-ctx-6054] O:Dish(61,Clean)
[03:32:10.9430] [main-comp-ctx-6054] washing dish 64
[03:32:10.9490] [main-comp-ctx-6055] Dish washed 62.
[03:32:10.9500] [main-comp-ctx-6055] O:Dish(62,Clean)
[03:32:10.9500] [main-comp-ctx-6055] washing dish 65
[03:32:10.9980] [main-comp-ctx-6053] Dish washed 63.
[03:32:10.9980] [main-comp-ctx-6053] O:Dish(63,Clean)
[03:32:10.9990] [main-comp-ctx-6053] E: 66
[03:32:10.9990] [main-comp-ctx-6053] E: 67
[03:32:11.0010] [main-comp-ctx-6053] E: 68
[03:32:11.0020] [main-comp-ctx-6053] washing dish 66
[03:32:11.1440] [main-comp-ctx-6054] Dish washed 64.
[03:32:11.1440] [main-comp-ctx-6054] O:Dish(64,Clean)
[03:32:11.1440] [main-comp-ctx-6054] washing dish 67
[03:32:11.1510] [main-comp-ctx-6055] Dish washed 65.
[03:32:11.1510] [main-comp-ctx-6055] O:Dish(65,Clean)
[03:32:11.1520] [main-comp-ctx-6055] washing dish 68
[03:32:11.2020] [main-comp-ctx-6053] Dish washed 66.
[03:32:11.2030] [main-comp-ctx-6053] O:Dish(66,Clean)
[03:32:11.2030] [main-comp-ctx-6053] E: 69
[03:32:11.2040] [main-comp-ctx-6053] E: 70
[03:32:11.2050] [main-comp-ctx-6053] E: 71
[03:32:11.2060] [main-comp-ctx-6053] washing dish 69
[03:32:11.3450] [main-comp-ctx-6054] Dish washed 67.
[03:32:11.3450] [main-comp-ctx-6054] O:Dish(67,Clean)
[03:32:11.3460] [main-comp-ctx-6054] washing dish 70
[03:32:11.3520] [main-comp-ctx-6055] Dish washed 68.
[03:32:11.3530] [main-comp-ctx-6055] O:Dish(68,Clean)
[03:32:11.3530] [main-comp-ctx-6055] washing dish 71
[03:32:11.4230] [main-comp-ctx-6053] Dish washed 69.
[03:32:11.4230] [main-comp-ctx-6053] O:Dish(69,Clean)
[03:32:11.4230] [main-comp-ctx-6053] E: 72
[03:32:11.4240] [main-comp-ctx-6053] E: 73
[03:32:11.4240] [main-comp-ctx-6053] E: 74
[03:32:11.4240] [main-comp-ctx-6053] washing dish 72
[03:32:11.5460] [main-comp-ctx-6054] Dish washed 70.
[03:32:11.5470] [main-comp-ctx-6054] O:Dish(70,Clean)
[03:32:11.5470] [main-comp-ctx-6054] washing dish 73
[03:32:11.5540] [main-comp-ctx-6055] Dish washed 71.
[03:32:11.5540] [main-comp-ctx-6055] O:Dish(71,Clean)
[03:32:11.5550] [main-comp-ctx-6055] washing dish 74
[03:32:11.6260] [main-comp-ctx-6053] Dish washed 72.
[03:32:11.6260] [main-comp-ctx-6053] O:Dish(72,Clean)
[03:32:11.6270] [main-comp-ctx-6053] E: 75
[03:32:11.6280] [main-comp-ctx-6053] E: 76
[03:32:11.6280] [main-comp-ctx-6053] E: 77
[03:32:11.6300] [main-comp-ctx-6053] washing dish 75
[03:32:11.7480] [main-comp-ctx-6054] Dish washed 73.
[03:32:11.7480] [main-comp-ctx-6054] O:Dish(73,Clean)
[03:32:11.7480] [main-comp-ctx-6054] washing dish 76
[03:32:11.7550] [main-comp-ctx-6055] Dish washed 74.
[03:32:11.7560] [main-comp-ctx-6055] O:Dish(74,Clean)
[03:32:11.7560] [main-comp-ctx-6055] washing dish 77
[03:32:11.8300] [main-comp-ctx-6053] Dish washed 75.
[03:32:11.8300] [main-comp-ctx-6053] O:Dish(75,Clean)
[03:32:11.8310] [main-comp-ctx-6053] E: 78
[03:32:11.8320] [main-comp-ctx-6053] E: 79
[03:32:11.8320] [main-comp-ctx-6053] E: 80
[03:32:11.8340] [main-comp-ctx-6053] washing dish 78
[03:32:11.9490] [main-comp-ctx-6054] Dish washed 76.
[03:32:11.9490] [main-comp-ctx-6054] O:Dish(76,Clean)
[03:32:11.9490] [main-comp-ctx-6054] washing dish 79
[03:32:11.9570] [main-comp-ctx-6055] Dish washed 77.
[03:32:11.9570] [main-comp-ctx-6055] O:Dish(77,Clean)
[03:32:11.9570] [main-comp-ctx-6055] washing dish 80
[03:32:12.0340] [main-comp-ctx-6053] Dish washed 78.
[03:32:12.0350] [main-comp-ctx-6053] O:Dish(78,Clean)
[03:32:12.0350] [main-comp-ctx-6053] E: 81
[03:32:12.0370] [main-comp-ctx-6053] E: 82
[03:32:12.0370] [main-comp-ctx-6053] E: 83
[03:32:12.0380] [main-comp-ctx-6053] washing dish 81
[03:32:12.1500] [main-comp-ctx-6054] Dish washed 79.
[03:32:12.1500] [main-comp-ctx-6054] O:Dish(79,Clean)
[03:32:12.1510] [main-comp-ctx-6054] washing dish 82
[03:32:12.1580] [main-comp-ctx-6055] Dish washed 80.
[03:32:12.1590] [main-comp-ctx-6055] O:Dish(80,Clean)
[03:32:12.1590] [main-comp-ctx-6055] washing dish 83
[03:32:12.2380] [main-comp-ctx-6053] Dish washed 81.
[03:32:12.2390] [main-comp-ctx-6053] O:Dish(81,Clean)
[03:32:12.2390] [main-comp-ctx-6053] E: 84
[03:32:12.2400] [main-comp-ctx-6053] E: 85
[03:32:12.2400] [main-comp-ctx-6053] E: 86
[03:32:12.2410] [main-comp-ctx-6053] washing dish 84
[03:32:12.3510] [main-comp-ctx-6054] Dish washed 82.
[03:32:12.3520] [main-comp-ctx-6054] O:Dish(82,Clean)
[03:32:12.3520] [main-comp-ctx-6054] washing dish 85
[03:32:12.3600] [main-comp-ctx-6055] Dish washed 83.
[03:32:12.3600] [main-comp-ctx-6055] O:Dish(83,Clean)
[03:32:12.3610] [main-comp-ctx-6055] washing dish 86
[03:32:12.4420] [main-comp-ctx-6053] Dish washed 84.
[03:32:12.4420] [main-comp-ctx-6053] O:Dish(84,Clean)
[03:32:12.4420] [main-comp-ctx-6053] E: 87
[03:32:12.4430] [main-comp-ctx-6053] E: 88
[03:32:12.4440] [main-comp-ctx-6053] E: 89
[03:32:12.4440] [main-comp-ctx-6053] washing dish 87
[03:32:12.5530] [main-comp-ctx-6054] Dish washed 85.
[03:32:12.5530] [main-comp-ctx-6054] O:Dish(85,Clean)
[03:32:12.5530] [main-comp-ctx-6054] washing dish 88
[03:32:12.5610] [main-comp-ctx-6055] Dish washed 86.
[03:32:12.5620] [main-comp-ctx-6055] O:Dish(86,Clean)
[03:32:12.5620] [main-comp-ctx-6055] washing dish 89
[03:32:12.6450] [main-comp-ctx-6053] Dish washed 87.
[03:32:12.6450] [main-comp-ctx-6053] O:Dish(87,Clean)
[03:32:12.6460] [main-comp-ctx-6053] E: 90
[03:32:12.6460] [main-comp-ctx-6053] E: 91
[03:32:12.6470] [main-comp-ctx-6053] E: 92
[03:32:12.6480] [main-comp-ctx-6053] washing dish 90
[03:32:12.7540] [main-comp-ctx-6054] Dish washed 88.
[03:32:12.7540] [main-comp-ctx-6054] O:Dish(88,Clean)
[03:32:12.7540] [main-comp-ctx-6054] washing dish 91
[03:32:12.7630] [main-comp-ctx-6055] Dish washed 89.
[03:32:12.7630] [main-comp-ctx-6055] O:Dish(89,Clean)
[03:32:12.7630] [main-comp-ctx-6055] washing dish 92
[03:32:12.8490] [main-comp-ctx-6053] Dish washed 90.
[03:32:12.8490] [main-comp-ctx-6053] O:Dish(90,Clean)
[03:32:12.8500] [main-comp-ctx-6053] E: 93
[03:32:12.8510] [main-comp-ctx-6053] E: 94
[03:32:12.8510] [main-comp-ctx-6053] E: 95
[03:32:12.8520] [main-comp-ctx-6053] washing dish 93
[03:32:12.9550] [main-comp-ctx-6054] Dish washed 91.
[03:32:12.9550] [main-comp-ctx-6054] O:Dish(91,Clean)
[03:32:12.9560] [main-comp-ctx-6054] washing dish 94
[03:32:12.9640] [main-comp-ctx-6055] Dish washed 92.
[03:32:12.9640] [main-comp-ctx-6055] O:Dish(92,Clean)
[03:32:12.9650] [main-comp-ctx-6055] washing dish 95
[03:32:13.0530] [main-comp-ctx-6053] Dish washed 93.
[03:32:13.0530] [main-comp-ctx-6053] O:Dish(93,Clean)
[03:32:13.0540] [main-comp-ctx-6053] E: 96
[03:32:13.0540] [main-comp-ctx-6053] E: 97
[03:32:13.0550] [main-comp-ctx-6053] E: 98
[03:32:13.0550] [main-comp-ctx-6053] washing dish 96
[03:32:13.1560] [main-comp-ctx-6054] Dish washed 94.
[03:32:13.1570] [main-comp-ctx-6054] O:Dish(94,Clean)
[03:32:13.1570] [main-comp-ctx-6054] washing dish 97
[03:32:13.1660] [main-comp-ctx-6055] Dish washed 95.
[03:32:13.1660] [main-comp-ctx-6055] O:Dish(95,Clean)
[03:32:13.1670] [main-comp-ctx-6055] washing dish 98
[03:32:13.2560] [main-comp-ctx-6053] Dish washed 96.
[03:32:13.2560] [main-comp-ctx-6053] O:Dish(96,Clean)
[03:32:13.2570] [main-comp-ctx-6053] E: 99
[03:32:13.2570] [main-comp-ctx-6053] E: 100
[03:32:13.2580] [main-comp-ctx-6053] washing dish 99
[03:32:13.3580] [main-comp-ctx-6054] Dish washed 97.
[03:32:13.3580] [main-comp-ctx-6054] O:Dish(97,Clean)
[03:32:13.3580] [main-comp-ctx-6054] washing dish 100
[03:32:13.3680] [main-comp-ctx-6055] Dish washed 98.
[03:32:13.3680] [main-comp-ctx-6055] O:Dish(98,Clean)
[03:32:13.4590] [main-comp-ctx-6055] Dish washed 99.
[03:32:13.4590] [main-comp-ctx-6055] O:Dish(99,Clean)
[03:32:13.5590] [main-comp-ctx-6053] Dish washed 100.
[03:32:13.5600] [main-comp-ctx-6053] O:Dish(100,Clean)
[03:32:18.8090] [run-main-ad] Program Ends.
*/
