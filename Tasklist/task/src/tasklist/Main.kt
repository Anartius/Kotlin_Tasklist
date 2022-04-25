package tasklist
import kotlinx.datetime.*
import java.lang.IllegalArgumentException

data class Task( val priority: String,
                 val toDo: MutableList<String>,
                 val dateTime: LocalDateTime)

fun main(){
    val tasks = mutableListOf<Task>()
    tasks.clear()

    while (true) {
        println("Input an action (add, print, end):")
        when (readln().lowercase()) {
            "end" -> {
                println("Tasklist exiting!")
                return
            }
            "add" -> {
                val task = getTask()
                if (task.toDo.isNotEmpty()) tasks.add(task)
            }
            "print" -> printTasks(tasks)

            else -> println("The input action is invalid")
        }
    }
}


fun getTask(): Task {

    val task = mutableListOf<String>()
    task.clear()
    var priority: String

    while (true) {
        println("Input the task priority (C, H, N, L):")
        priority = readln().uppercase()
        if ("[CHNL]".toRegex().matches(priority)) break else continue
    }

    val date = getDate()
    val dateTime = getDateTime(date)

    println("Input a new task (enter a blank line to end):")
    while(true) {
        val input = readln().trim()
        if (input.isEmpty()) {
            if (task.isEmpty()) println("The task is blank")
            break
        }
        task.add(input)
    }

    return Task(priority, task, dateTime)
}


fun getDate(): LocalDate {
    var date: LocalDate

    while (true) {
        println("Input the date (yyyy-mm-dd):")

        try {

            val input = mutableListOf<String>()
            readln().split("-").toList()
                .forEach { input.add(if (it.length < 2) "0$it" else it) }

            date = input.joinToString("-").toLocalDate()
            break
        } catch (e: IllegalArgumentException) {
            println("The input date is invalid")
            continue
        }
    }
    return date
}


fun getDateTime(date: LocalDate): LocalDateTime {
    var dateTime: LocalDateTime

    while (true) {
        println("Input the time (hh:mm):")
        try {
            val input = mutableListOf<String>()
            readln().split(":").toList()
                .forEach { input.add(if (it.length < 2) "0$it" else it) }

            dateTime = ("${date}T" + input.joinToString(":")).toLocalDateTime()
            break
        } catch (e: IllegalArgumentException) {
            println("The input time is invalid")
            continue
        }
    }
    return dateTime
}


fun printTasks(tasks: MutableList<Task>) {
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else {
        for (i in tasks.indices) {
            println("${i + 1}".padEnd(3) +
                    tasks[i].dateTime.toString().replace('T', ' ') +
                    " ${tasks[i].priority}\n" +
                    tasks[i].toDo.joinToString("\n   ", "   ", "\n"))
        }
    }
}