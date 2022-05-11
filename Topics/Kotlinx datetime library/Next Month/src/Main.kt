import kotlinx.datetime.*

fun nextMonth(date: String): String {
    val previousData = date.toInstant()
    val period = "P1M".toDatePeriod()
    return previousData.plus(period, TimeZone.UTC).toString()
}

fun main() {
    val date = readLine()!!
    println(nextMonth(date))
}