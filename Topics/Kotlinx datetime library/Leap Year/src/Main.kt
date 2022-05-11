import kotlinx.datetime.*

fun isLeapYear(year: String): Boolean {
    return try {
        "$year-02-29T00:00:00Z".toInstant()
        true
    } catch (e: Exception) {
        false
    }
}

fun main() {
    val year = readLine()!!
    println(isLeapYear(year))
}