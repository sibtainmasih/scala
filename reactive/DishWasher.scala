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
  )(Scheduler.computation(name = "main-comp-ctx", parallelism = 5))

Thread.sleep(15000)
println(s"$log Program Ends.")


/* Output
[04:44:21.2760] [run-main-1] Program starts.
[04:44:24.0980] [run-main-1] E: 1
[04:44:24.1410] [run-main-1] E: 2
[04:44:24.1410] [run-main-1] E: 3
[04:44:24.1410] [run-main-1] E: 4
[04:44:24.1420] [run-main-1] E: 5
[04:44:24.1420] [run-main-1] E: 6
[04:44:24.1420] [run-main-1] E: 7
[04:44:24.1430] [run-main-1] E: 8
[04:44:24.1430] [run-main-1] E: 9
[04:44:24.1430] [run-main-1] E: 10
[04:44:24.1430] [run-main-1] E: 11
[04:44:24.1430] [run-main-1] E: 12
[04:44:24.1440] [run-main-1] E: 13
[04:44:24.1440] [run-main-1] E: 14
[04:44:24.1440] [run-main-1] E: 15
[04:44:24.1440] [run-main-1] E: 16
[04:44:24.1440] [run-main-1] E: 17
[04:44:24.1450] [run-main-1] E: 18
[04:44:24.1450] [run-main-1] E: 19
[04:44:24.1490] [run-main-1] E: 20
[04:44:24.1490] [run-main-1] E: 21
[04:44:24.1490] [run-main-1] E: 22
[04:44:24.1490] [run-main-1] E: 23
[04:44:24.1490] [run-main-1] E: 24
[04:44:24.1500] [run-main-1] E: 25
[04:44:24.1500] [run-main-1] E: 26
[04:44:24.1500] [run-main-1] E: 27
[04:44:24.1500] [run-main-1] E: 28
[04:44:24.1500] [run-main-1] E: 29
[04:44:24.1510] [run-main-1] E: 30
[04:44:24.1510] [run-main-1] E: 31
[04:44:24.1510] [run-main-1] E: 32
[04:44:24.1510] [run-main-1] E: 33
[04:44:24.1510] [run-main-1] E: 34
[04:44:24.1520] [run-main-1] E: 35
[04:44:24.1520] [run-main-1] E: 36
[04:44:24.1520] [run-main-1] E: 37
[04:44:24.1520] [run-main-1] E: 38
[04:44:24.1520] [run-main-1] E: 39
[04:44:24.1520] [run-main-1] E: 40
[04:44:24.1530] [run-main-1] E: 41
[04:44:24.1530] [run-main-1] E: 42
[04:44:24.1530] [run-main-1] E: 43
[04:44:24.1530] [run-main-1] E: 44
[04:44:24.1530] [run-main-1] E: 45
[04:44:24.1540] [run-main-1] E: 46
[04:44:24.1540] [run-main-1] E: 47
[04:44:24.1540] [run-main-1] E: 48
[04:44:24.1540] [run-main-1] E: 49
[04:44:24.1540] [run-main-1] E: 50
[04:44:24.1540] [run-main-1] E: 51
[04:44:24.1550] [run-main-1] E: 52
[04:44:24.1550] [run-main-1] E: 53
[04:44:24.1550] [run-main-1] E: 54
[04:44:24.1550] [run-main-1] E: 55
[04:44:24.1550] [run-main-1] E: 56
[04:44:24.1550] [run-main-1] E: 57
[04:44:24.1560] [run-main-1] E: 58
[04:44:24.1560] [run-main-1] E: 59
[04:44:24.1560] [run-main-1] E: 60
[04:44:24.1560] [run-main-1] E: 61
[04:44:24.1560] [run-main-1] E: 62
[04:44:24.1570] [run-main-1] E: 63
[04:44:24.1570] [run-main-1] E: 64
[04:44:24.1570] [run-main-1] E: 65
[04:44:24.1980] [main-comp-ctx-195] washing dish 4
[04:44:24.1980] [main-comp-ctx-192] washing dish 1
[04:44:24.1980] [main-comp-ctx-194] washing dish 3
[04:44:24.1980] [main-comp-ctx-193] washing dish 2
[04:44:24.2150] [main-comp-ctx-191] washing dish 5
[04:44:24.4100] [main-comp-ctx-192] washing dish 6
[04:44:24.4100] [main-comp-ctx-193] washing dish 7
[04:44:24.4100] [main-comp-ctx-195] Dish washed 3.
[04:44:24.4110] [main-comp-ctx-194] washing dish 8
[04:44:24.4110] [main-comp-ctx-195] O:Dish(3,Clean)
[04:44:24.4140] [main-comp-ctx-195] Dish washed 1.
[04:44:24.4140] [main-comp-ctx-195] O:Dish(1,Clean)
[04:44:24.4150] [main-comp-ctx-195] Dish washed 2.
[04:44:24.4150] [main-comp-ctx-195] O:Dish(2,Clean)
[04:44:24.4160] [main-comp-ctx-195] Dish washed 4.
[04:44:24.4170] [main-comp-ctx-195] O:Dish(4,Clean)
[04:44:24.4170] [main-comp-ctx-195] Dish washed 5.
[04:44:24.4170] [main-comp-ctx-191] washing dish 10
[04:44:24.4180] [main-comp-ctx-195] O:Dish(5,Clean)
[04:44:24.4190] [main-comp-ctx-195] washing dish 9
[04:44:24.6110] [main-comp-ctx-192] Dish washed 6.
[04:44:24.6120] [main-comp-ctx-193] washing dish 11
[04:44:24.6120] [main-comp-ctx-192] O:Dish(6,Clean)
[04:44:24.6130] [main-comp-ctx-192] Dish washed 7.
[04:44:24.6140] [main-comp-ctx-194] washing dish 13
[04:44:24.6140] [main-comp-ctx-192] O:Dish(7,Clean)
[04:44:24.6150] [main-comp-ctx-192] Dish washed 8.
[04:44:24.6160] [main-comp-ctx-192] O:Dish(8,Clean)
[04:44:24.6160] [main-comp-ctx-192] washing dish 12
[04:44:24.6190] [main-comp-ctx-191] Dish washed 10.
[04:44:24.6190] [main-comp-ctx-191] O:Dish(10,Clean)
[04:44:24.6200] [main-comp-ctx-195] washing dish 14
[04:44:24.6200] [main-comp-ctx-191] Dish washed 9.
[04:44:24.6220] [main-comp-ctx-191] O:Dish(9,Clean)
[04:44:24.6230] [main-comp-ctx-191] washing dish 15
[04:44:24.8130] [main-comp-ctx-193] Dish washed 11.
[04:44:24.8130] [main-comp-ctx-193] O:Dish(11,Clean)
[04:44:24.8140] [main-comp-ctx-194] Dish washed 13.
[04:44:24.8150] [main-comp-ctx-193] washing dish 16
[04:44:24.8150] [main-comp-ctx-194] O:Dish(13,Clean)
[04:44:24.8180] [main-comp-ctx-194] washing dish 17
[04:44:24.8180] [main-comp-ctx-192] Dish washed 12.
[04:44:24.8200] [main-comp-ctx-192] O:Dish(12,Clean)
[04:44:24.8220] [main-comp-ctx-192] Dish washed 14.
[04:44:24.8210] [main-comp-ctx-195] washing dish 18
[04:44:24.8220] [main-comp-ctx-192] O:Dish(14,Clean)
[04:44:24.8230] [main-comp-ctx-192] washing dish 19
[04:44:24.8240] [main-comp-ctx-191] Dish washed 15.
[04:44:24.8240] [main-comp-ctx-191] O:Dish(15,Clean)
[04:44:24.8260] [main-comp-ctx-191] washing dish 20
[04:44:25.0160] [main-comp-ctx-193] Dish washed 16.
[04:44:25.0170] [main-comp-ctx-193] O:Dish(16,Clean)
[04:44:25.0200] [main-comp-ctx-193] washing dish 21
[04:44:25.0220] [main-comp-ctx-194] Dish washed 17.
[04:44:25.0220] [main-comp-ctx-194] O:Dish(17,Clean)
[04:44:25.0230] [main-comp-ctx-194] washing dish 22
[04:44:25.0240] [main-comp-ctx-195] Dish washed 18.
[04:44:25.0240] [main-comp-ctx-195] O:Dish(18,Clean)
[04:44:25.0240] [main-comp-ctx-195] Dish washed 19.
[04:44:25.0240] [main-comp-ctx-192] washing dish 24
[04:44:25.0250] [main-comp-ctx-195] O:Dish(19,Clean)
[04:44:25.0270] [main-comp-ctx-195] washing dish 23
[04:44:25.0270] [main-comp-ctx-191] Dish washed 20.
[04:44:25.0280] [main-comp-ctx-191] O:Dish(20,Clean)
[04:44:25.0280] [main-comp-ctx-191] washing dish 25
[04:44:25.2210] [main-comp-ctx-193] Dish washed 21.
[04:44:25.2210] [main-comp-ctx-193] O:Dish(21,Clean)
[04:44:25.2220] [main-comp-ctx-193] washing dish 26
[04:44:25.2240] [main-comp-ctx-194] Dish washed 22.
[04:44:25.2250] [main-comp-ctx-194] O:Dish(22,Clean)
[04:44:25.2250] [main-comp-ctx-194] Dish washed 24.
[04:44:25.2260] [main-comp-ctx-194] O:Dish(24,Clean)
[04:44:25.2280] [main-comp-ctx-195] Dish washed 23.
[04:44:25.2280] [main-comp-ctx-195] O:Dish(23,Clean)
[04:44:25.2290] [main-comp-ctx-195] Dish washed 25.
[04:44:25.2290] [main-comp-ctx-194] washing dish 27
[04:44:25.2280] [main-comp-ctx-192] washing dish 28
[04:44:25.2290] [main-comp-ctx-191] washing dish 29
[04:44:25.2290] [main-comp-ctx-195] O:Dish(25,Clean)
[04:44:25.2310] [main-comp-ctx-195] washing dish 30
[04:44:25.4230] [main-comp-ctx-193] Dish washed 26.
[04:44:25.4240] [main-comp-ctx-193] O:Dish(26,Clean)
[04:44:25.4250] [main-comp-ctx-193] washing dish 31
[04:44:25.4310] [main-comp-ctx-194] Dish washed 27.
[04:44:25.4320] [main-comp-ctx-191] washing dish 32
[04:44:25.4310] [main-comp-ctx-194] O:Dish(27,Clean)
[04:44:25.4320] [main-comp-ctx-195] washing dish 33
[04:44:25.4320] [main-comp-ctx-194] Dish washed 28.
[04:44:25.4330] [main-comp-ctx-194] O:Dish(28,Clean)
[04:44:25.4330] [main-comp-ctx-194] Dish washed 29.
[04:44:25.4330] [main-comp-ctx-194] O:Dish(29,Clean)
[04:44:25.4330] [main-comp-ctx-192] washing dish 34
[04:44:25.4340] [main-comp-ctx-194] Dish washed 30.
[04:44:25.4350] [main-comp-ctx-194] O:Dish(30,Clean)
[04:44:25.4350] [main-comp-ctx-194] washing dish 35
[04:44:25.6260] [main-comp-ctx-193] Dish washed 31.
[04:44:25.6260] [main-comp-ctx-193] O:Dish(31,Clean)
[04:44:25.6280] [main-comp-ctx-193] washing dish 36
[04:44:25.6320] [main-comp-ctx-191] Dish washed 32.
[04:44:25.6330] [main-comp-ctx-191] O:Dish(32,Clean)
[04:44:25.6330] [main-comp-ctx-195] washing dish 37
[04:44:25.6330] [main-comp-ctx-191] Dish washed 33.
[04:44:25.6340] [main-comp-ctx-191] O:Dish(33,Clean)
[04:44:25.6340] [main-comp-ctx-191] washing dish 38
[04:44:25.6360] [main-comp-ctx-192] Dish washed 34.
[04:44:25.6360] [main-comp-ctx-192] O:Dish(34,Clean)
[04:44:25.6360] [main-comp-ctx-192] Dish washed 35.
[04:44:25.6360] [main-comp-ctx-192] O:Dish(35,Clean)
[04:44:25.6360] [main-comp-ctx-194] washing dish 39
[04:44:25.6370] [main-comp-ctx-192] washing dish 40
[04:44:25.8280] [main-comp-ctx-193] Dish washed 36.
[04:44:25.8280] [main-comp-ctx-193] O:Dish(36,Clean)
[04:44:25.8290] [main-comp-ctx-193] washing dish 41
[04:44:25.8340] [main-comp-ctx-195] Dish washed 37.
[04:44:25.8340] [main-comp-ctx-195] O:Dish(37,Clean)
[04:44:25.8350] [main-comp-ctx-191] Dish washed 38.
[04:44:25.8360] [main-comp-ctx-195] washing dish 42
[04:44:25.8360] [main-comp-ctx-191] O:Dish(38,Clean)
[04:44:25.8360] [main-comp-ctx-191] washing dish 43
[04:44:25.8370] [main-comp-ctx-194] Dish washed 39.
[04:44:25.8370] [main-comp-ctx-194] O:Dish(39,Clean)
[04:44:25.8380] [main-comp-ctx-194] Dish washed 40.
[04:44:25.8380] [main-comp-ctx-194] O:Dish(40,Clean)
[04:44:25.8380] [main-comp-ctx-192] washing dish 44
[04:44:25.8390] [main-comp-ctx-194] washing dish 45
[04:44:26.0300] [main-comp-ctx-193] Dish washed 41.
[04:44:26.0310] [main-comp-ctx-193] O:Dish(41,Clean)
[04:44:26.0320] [main-comp-ctx-193] washing dish 46
[04:44:26.0360] [main-comp-ctx-195] Dish washed 42.
[04:44:26.0370] [main-comp-ctx-195] O:Dish(42,Clean)
[04:44:26.0370] [main-comp-ctx-195] washing dish 47
[04:44:26.0380] [main-comp-ctx-191] Dish washed 43.
[04:44:26.0380] [main-comp-ctx-191] O:Dish(43,Clean)
[04:44:26.0390] [main-comp-ctx-191] washing dish 48
[04:44:26.0400] [main-comp-ctx-192] Dish washed 44.
[04:44:26.0400] [main-comp-ctx-192] O:Dish(44,Clean)
[04:44:26.0400] [main-comp-ctx-194] washing dish 49
[04:44:26.0410] [main-comp-ctx-192] Dish washed 45.
[04:44:26.0410] [main-comp-ctx-192] O:Dish(45,Clean)
[04:44:26.0420] [main-comp-ctx-192] washing dish 50
[04:44:26.2320] [main-comp-ctx-193] Dish washed 46.
[04:44:26.2330] [main-comp-ctx-193] O:Dish(46,Clean)
[04:44:26.2330] [main-comp-ctx-193] washing dish 51
[04:44:26.2380] [main-comp-ctx-195] Dish washed 47.
[04:44:26.2380] [main-comp-ctx-195] O:Dish(47,Clean)
[04:44:26.2400] [main-comp-ctx-195] washing dish 52
[04:44:26.2400] [main-comp-ctx-191] Dish washed 48.
[04:44:26.2410] [main-comp-ctx-191] O:Dish(48,Clean)
[04:44:26.2410] [main-comp-ctx-191] Dish washed 49.
[04:44:26.2410] [main-comp-ctx-191] O:Dish(49,Clean)
[04:44:26.2420] [main-comp-ctx-194] washing dish 53
[04:44:26.2420] [main-comp-ctx-191] washing dish 54
[04:44:26.2430] [main-comp-ctx-192] Dish washed 50.
[04:44:26.2430] [main-comp-ctx-192] O:Dish(50,Clean)
[04:44:26.2430] [main-comp-ctx-192] washing dish 55
[04:44:26.4340] [main-comp-ctx-193] Dish washed 51.
[04:44:26.4340] [main-comp-ctx-193] O:Dish(51,Clean)
[04:44:26.4350] [main-comp-ctx-193] washing dish 56
[04:44:26.4400] [main-comp-ctx-195] Dish washed 52.
[04:44:26.4410] [main-comp-ctx-195] O:Dish(52,Clean)
[04:44:26.4410] [main-comp-ctx-195] washing dish 57
[04:44:26.4420] [main-comp-ctx-194] Dish washed 53.
[04:44:26.4430] [main-comp-ctx-191] washing dish 58
[04:44:26.4430] [main-comp-ctx-194] O:Dish(53,Clean)
[04:44:26.4430] [main-comp-ctx-194] Dish washed 54.
[04:44:26.4430] [main-comp-ctx-194] O:Dish(54,Clean)
[04:44:26.4440] [main-comp-ctx-194] Dish washed 55.
[04:44:26.4440] [main-comp-ctx-194] O:Dish(55,Clean)
[04:44:26.4450] [main-comp-ctx-194] washing dish 59
[04:44:26.4450] [main-comp-ctx-192] washing dish 60
[04:44:26.6360] [main-comp-ctx-193] Dish washed 56.
[04:44:26.6360] [main-comp-ctx-193] O:Dish(56,Clean)
[04:44:26.6370] [main-comp-ctx-193] washing dish 61
[04:44:26.6420] [main-comp-ctx-195] Dish washed 57.
[04:44:26.6430] [main-comp-ctx-195] O:Dish(57,Clean)
[04:44:26.6440] [main-comp-ctx-191] Dish washed 58.
[04:44:26.6430] [main-comp-ctx-195] washing dish 62
[04:44:26.6440] [main-comp-ctx-191] O:Dish(58,Clean)
[04:44:26.6450] [main-comp-ctx-191] washing dish 63
[04:44:26.6460] [main-comp-ctx-194] Dish washed 59.
[04:44:26.6460] [main-comp-ctx-194] O:Dish(59,Clean)
[04:44:26.6470] [main-comp-ctx-194] Dish washed 60.
[04:44:26.6470] [main-comp-ctx-194] O:Dish(60,Clean)
[04:44:26.6470] [main-comp-ctx-194] washing dish 65
[04:44:26.6470] [main-comp-ctx-192] washing dish 64
[04:44:26.8380] [main-comp-ctx-193] Dish washed 61.
[04:44:26.8380] [main-comp-ctx-193] O:Dish(61,Clean)
[04:44:26.8400] [main-comp-ctx-193] E: 66
[04:44:26.8410] [main-comp-ctx-193] E: 67
[04:44:26.8410] [main-comp-ctx-193] E: 68
[04:44:26.8420] [main-comp-ctx-193] E: 69
[04:44:26.8420] [main-comp-ctx-193] E: 70
[04:44:26.8430] [main-comp-ctx-193] E: 71
[04:44:26.8430] [main-comp-ctx-193] E: 72
[04:44:26.8430] [main-comp-ctx-193] E: 73
[04:44:26.8440] [main-comp-ctx-193] E: 74
[04:44:26.8440] [main-comp-ctx-193] E: 75
[04:44:26.8440] [main-comp-ctx-193] E: 76
[04:44:26.8440] [main-comp-ctx-195] Dish washed 62.
[04:44:26.8450] [main-comp-ctx-193] E: 77
[04:44:26.8450] [main-comp-ctx-195] O:Dish(62,Clean)
[04:44:26.8460] [main-comp-ctx-195] Dish washed 63.
[04:44:26.8460] [main-comp-ctx-191] washing dish 66
[04:44:26.8460] [main-comp-ctx-195] O:Dish(63,Clean)
[04:44:26.8460] [main-comp-ctx-193] E: 78
[04:44:26.8460] [main-comp-ctx-193] E: 79
[04:44:26.8460] [main-comp-ctx-193] E: 80
[04:44:26.8470] [main-comp-ctx-195] washing dish 67
[04:44:26.8470] [main-comp-ctx-193] E: 81
[04:44:26.8470] [main-comp-ctx-193] E: 82
[04:44:26.8470] [main-comp-ctx-193] E: 83
[04:44:26.8480] [main-comp-ctx-193] E: 84
[04:44:26.8480] [main-comp-ctx-193] E: 85
[04:44:26.8480] [main-comp-ctx-194] Dish washed 65.
[04:44:26.8480] [main-comp-ctx-192] washing dish 68
[04:44:26.8480] [main-comp-ctx-193] E: 86
[04:44:26.8480] [main-comp-ctx-194] O:Dish(65,Clean)
[04:44:26.8480] [main-comp-ctx-193] E: 87
[04:44:26.8480] [main-comp-ctx-194] Dish washed 64.
[04:44:26.8480] [main-comp-ctx-193] E: 88
[04:44:26.8490] [main-comp-ctx-193] E: 89
[04:44:26.8490] [main-comp-ctx-194] O:Dish(64,Clean)
[04:44:26.8490] [main-comp-ctx-193] E: 90
[04:44:26.8490] [main-comp-ctx-193] E: 91
[04:44:26.8490] [main-comp-ctx-193] E: 92
[04:44:26.8490] [main-comp-ctx-193] E: 93
[04:44:26.8490] [main-comp-ctx-193] E: 94
[04:44:26.8500] [main-comp-ctx-193] E: 95
[04:44:26.8500] [main-comp-ctx-193] E: 96
[04:44:26.8500] [main-comp-ctx-194] washing dish 69
[04:44:26.8500] [main-comp-ctx-193] E: 97
[04:44:26.8500] [main-comp-ctx-193] E: 98
[04:44:26.8500] [main-comp-ctx-193] E: 99
[04:44:26.8500] [main-comp-ctx-193] E: 100
[04:44:26.8560] [main-comp-ctx-193] washing dish 70
[04:44:27.0460] [main-comp-ctx-191] Dish washed 66.
[04:44:27.0470] [main-comp-ctx-191] O:Dish(66,Clean)
[04:44:27.0470] [main-comp-ctx-191] Dish washed 67.
[04:44:27.0470] [main-comp-ctx-191] O:Dish(67,Clean)
[04:44:27.0480] [main-comp-ctx-195] washing dish 71
[04:44:27.0480] [main-comp-ctx-191] washing dish 72
[04:44:27.0490] [main-comp-ctx-192] Dish washed 68.
[04:44:27.0490] [main-comp-ctx-192] O:Dish(68,Clean)
[04:44:27.0500] [main-comp-ctx-192] washing dish 73
[04:44:27.0510] [main-comp-ctx-194] Dish washed 69.
[04:44:27.0510] [main-comp-ctx-194] O:Dish(69,Clean)
[04:44:27.0520] [main-comp-ctx-194] washing dish 74
[04:44:27.0570] [main-comp-ctx-193] Dish washed 70.
[04:44:27.0570] [main-comp-ctx-193] O:Dish(70,Clean)
[04:44:27.0570] [main-comp-ctx-193] washing dish 75
[04:44:27.2480] [main-comp-ctx-195] Dish washed 71.
[04:44:27.2490] [main-comp-ctx-195] O:Dish(71,Clean)
[04:44:27.2490] [main-comp-ctx-195] Dish washed 72.
[04:44:27.2490] [main-comp-ctx-191] washing dish 76
[04:44:27.2490] [main-comp-ctx-195] O:Dish(72,Clean)
[04:44:27.2500] [main-comp-ctx-195] washing dish 77
[04:44:27.2510] [main-comp-ctx-192] Dish washed 73.
[04:44:27.2510] [main-comp-ctx-192] O:Dish(73,Clean)
[04:44:27.2520] [main-comp-ctx-192] washing dish 78
[04:44:27.2530] [main-comp-ctx-194] Dish washed 74.
[04:44:27.2530] [main-comp-ctx-194] O:Dish(74,Clean)
[04:44:27.2550] [main-comp-ctx-194] washing dish 79
[04:44:27.2580] [main-comp-ctx-193] Dish washed 75.
[04:44:27.2580] [main-comp-ctx-193] O:Dish(75,Clean)
[04:44:27.2580] [main-comp-ctx-193] washing dish 80
[04:44:27.4500] [main-comp-ctx-191] Dish washed 76.
[04:44:27.4500] [main-comp-ctx-191] O:Dish(76,Clean)
[04:44:27.4510] [main-comp-ctx-191] washing dish 81
[04:44:27.4520] [main-comp-ctx-195] Dish washed 77.
[04:44:27.4520] [main-comp-ctx-195] O:Dish(77,Clean)
[04:44:27.4520] [main-comp-ctx-195] Dish washed 78.
[04:44:27.4530] [main-comp-ctx-192] washing dish 82
[04:44:27.4530] [main-comp-ctx-195] O:Dish(78,Clean)
[04:44:27.4540] [main-comp-ctx-195] washing dish 83
[04:44:27.4560] [main-comp-ctx-194] Dish washed 79.
[04:44:27.4560] [main-comp-ctx-194] O:Dish(79,Clean)
[04:44:27.4570] [main-comp-ctx-194] washing dish 84
[04:44:27.4590] [main-comp-ctx-193] Dish washed 80.
[04:44:27.4590] [main-comp-ctx-193] O:Dish(80,Clean)
[04:44:27.4590] [main-comp-ctx-193] washing dish 85
[04:44:27.6520] [main-comp-ctx-191] Dish washed 81.
[04:44:27.6520] [main-comp-ctx-191] O:Dish(81,Clean)
[04:44:27.6530] [main-comp-ctx-191] washing dish 86
[04:44:27.6540] [main-comp-ctx-192] Dish washed 82.
[04:44:27.6550] [main-comp-ctx-192] O:Dish(82,Clean)
[04:44:27.6560] [main-comp-ctx-192] Dish washed 83.
[04:44:27.6560] [main-comp-ctx-195] washing dish 87
[04:44:27.6560] [main-comp-ctx-192] O:Dish(83,Clean)
[04:44:27.6580] [main-comp-ctx-192] washing dish 88
[04:44:27.6580] [main-comp-ctx-194] Dish washed 84.
[04:44:27.6580] [main-comp-ctx-194] O:Dish(84,Clean)
[04:44:27.6590] [main-comp-ctx-194] washing dish 89
[04:44:27.6600] [main-comp-ctx-193] Dish washed 85.
[04:44:27.6600] [main-comp-ctx-193] O:Dish(85,Clean)
[04:44:27.6610] [main-comp-ctx-193] washing dish 90
[04:44:27.8540] [main-comp-ctx-191] Dish washed 86.
[04:44:27.8540] [main-comp-ctx-191] O:Dish(86,Clean)
[04:44:27.8550] [main-comp-ctx-191] washing dish 91
[04:44:27.8570] [main-comp-ctx-195] Dish washed 87.
[04:44:27.8570] [main-comp-ctx-195] O:Dish(87,Clean)
[04:44:27.8580] [main-comp-ctx-195] washing dish 92
[04:44:27.8580] [main-comp-ctx-192] Dish washed 88.
[04:44:27.8590] [main-comp-ctx-192] O:Dish(88,Clean)
[04:44:27.8590] [main-comp-ctx-194] Dish washed 89.
[04:44:27.8590] [main-comp-ctx-192] washing dish 93
[04:44:27.8600] [main-comp-ctx-194] O:Dish(89,Clean)
[04:44:27.8600] [main-comp-ctx-194] washing dish 94
[04:44:27.8610] [main-comp-ctx-193] Dish washed 90.
[04:44:27.8620] [main-comp-ctx-193] O:Dish(90,Clean)
[04:44:27.8650] [main-comp-ctx-193] washing dish 95
[04:44:28.0560] [main-comp-ctx-191] Dish washed 91.
[04:44:28.0560] [main-comp-ctx-191] O:Dish(91,Clean)
[04:44:28.0570] [main-comp-ctx-191] washing dish 96
[04:44:28.0590] [main-comp-ctx-195] Dish washed 92.
[04:44:28.0590] [main-comp-ctx-195] O:Dish(92,Clean)
[04:44:28.0600] [main-comp-ctx-192] Dish washed 93.
[04:44:28.0600] [main-comp-ctx-195] washing dish 97
[04:44:28.0600] [main-comp-ctx-192] O:Dish(93,Clean)
[04:44:28.0610] [main-comp-ctx-192] Dish washed 94.
[04:44:28.0610] [main-comp-ctx-192] O:Dish(94,Clean)
[04:44:28.0620] [main-comp-ctx-192] washing dish 98
[04:44:28.0660] [main-comp-ctx-193] Dish washed 95.
[04:44:28.0660] [main-comp-ctx-193] O:Dish(95,Clean)
[04:44:28.0660] [main-comp-ctx-193] washing dish 100
[04:44:28.0680] [main-comp-ctx-194] washing dish 99
[04:44:28.2580] [main-comp-ctx-191] Dish washed 96.
[04:44:28.2580] [main-comp-ctx-191] O:Dish(96,Clean)
[04:44:28.2610] [main-comp-ctx-191] Dish washed 97.
[04:44:28.2610] [main-comp-ctx-191] O:Dish(97,Clean)
[04:44:28.2620] [main-comp-ctx-191] Dish washed 98.
[04:44:28.2620] [main-comp-ctx-191] O:Dish(98,Clean)
[04:44:28.2660] [main-comp-ctx-191] Dish washed 100.
[04:44:28.2670] [main-comp-ctx-191] O:Dish(100,Clean)
[04:44:28.2690] [main-comp-ctx-191] Dish washed 99.
[04:44:28.2700] [main-comp-ctx-191] O:Dish(99,Clean)
[04:44:39.3140] [run-main-1] Program Ends.
*/
