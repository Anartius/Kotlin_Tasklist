package tasklist
import kotlinx.datetime.*
import java.lang.IllegalArgumentException

data class Task( var priority: String,
                 var toDo: MutableList<String>,
                 var dateTime: LocalDateTime)

fun main(){
    val tasks = mutableListOf<Task>()
    tasks.clear()

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> {
                val task = getTask()
                if (task.toDo.isNotEmpty()) tasks.add(task)
            }
            "print" -> printTasks(tasks)
            "edit" -> editTask(tasks)
            "delete" -> deleteTask(tasks)
            "end" -> {
                println("Tasklist exiting!")
                return
            }
            else -> println("The input action is invalid")
        }
    }
}


fun getTask(): Task {

    val priority = getPriority()
    val date = getDate()
    val dateTime = getDateTime(date)
    val taskToDo = getTaskToDo()

    return Task(priority, taskToDo, dateTime)
}


fun printTasks(tasks: MutableList<Task>): Boolean {
    return if (tasks.isEmpty()) {
        println("No tasks have been input")
        false
    } else {
        for (i in tasks.indices) {
            val inTime = accordingToDeadline(tasks[i].dateTime)
            println("${i + 1}".padEnd(3) +
                    tasks[i].dateTime.toString().replace('T', ' ') +
                    " ${tasks[i].priority} $inTime\n" +
                    tasks[i].toDo.joinToString("\n   ", "   ", "\n"))
        }
        true
    }
}


fun editTask(tasks: MutableList<Task>) {
    val taskNumber = getTaskNumber(tasks)
    if (taskNumber != -1) {
        val task = tasks[taskNumber]
        while (true) {
            println("Input a field to edit (priority, date, time, task):")
            when (readln()) {
                "priority" -> task.priority = getPriority()
                "date" -> {
                    val time = task.dateTime.toString().substringAfter('T')
                    task.dateTime = ("${getDate()}T" + time).toLocalDateTime()
                }
                "time" -> task.dateTime = getDateTime(task.dateTime.date)
                "task" -> task.toDo = getTaskToDo()
                else -> {
                    println("Invalid field")
                    continue
                }
            }
            println("The task is changed")
            break
        }
    }
}


fun deleteTask(tasks: MutableList<Task>) {
    val taskNumber = getTaskNumber(tasks)
    if (taskNumber != -1) {
        tasks.removeAt(taskNumber)
        println("The task is deleted")
    }
}


fun getPriority(): String {
    var priority: String

    while (true) {
        println("Input the task priority (C, H, N, L):")
        priority = readln().uppercase()
        if ("[CHNL]".toRegex().matches(priority)) break else continue
    }
    return priority
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


fun getTaskToDo(): MutableList<String> {
    val taskToDo = mutableListOf<String>()
    taskToDo.clear()

    println("Input a new task (enter a blank line to end):")
    while(true) {
        val input = readln().trim()
        if (input.isEmpty()) {
            if (taskToDo.isEmpty()) println("The task is blank")
            break
        }
        taskToDo.add(input)
    }
    return taskToDo
}


fun getTaskNumber(tasks: MutableList<Task>): Int{
    if (printTasks(tasks)) {
        while (true) {
            println("Input the task number (1-${tasks.size}):")
            try {
                val input = readln().toInt()
                if (input in 1..tasks.size) {
                    return input - 1
                } else throw NumberFormatException()
            } catch (e: NumberFormatException) {
                println("Invalid task number")
                continue
            }
        }
    } else return -1
}


fun accordingToDeadline(dateTime: LocalDateTime): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    val taskDate = dateTime.date
    val daysToDeadline = currentDate.daysUntil(taskDate)

    return if(daysToDeadline == 0) {
        "T"
    } else if (daysToDeadline > 0) {
        "I"
    } else "O"
}