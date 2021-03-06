package tasklist
import kotlinx.datetime.*
import java.lang.IllegalArgumentException
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

class Task(var dateTime: MutableList<String>,
           var toDo: MutableList<String>,
           private var priority: String = "",
           var priorityColor: String = "") {

    fun setThePriority(priority: String) {
        this.priority = priority

        priorityColor = when (priority) {
            "C" -> "\u001B[101m \u001B[0m"
            "H" -> "\u001B[103m \u001B[0m"
            "N" -> "\u001B[102m \u001B[0m"
            else -> "\u001B[104m \u001B[0m"
        }
    }

    fun getDateTime(): LocalDateTime {
        return (dateTime[0] + "T" + dateTime[1]).toLocalDateTime()
    }
}


fun main(){
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
    val taskAdapter = moshi.adapter<List<Task>>(type)
    val file = File("tasklist.json")
    val tasks = mutableListOf<Task>()
    tasks.clear()

    if (file.exists()) {
        try {
            taskAdapter.fromJson(file.readText())?.let { tasks.addAll(it) }
        } catch (e:Exception) { }
    }

    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        when (readln().lowercase()) {
            "add" -> {
                val task = getTask()
                if (task.toDo.isNotEmpty()) tasks.add(task)
            }
            "print" -> {
                printTasks(tasks)
            }
            "edit" -> {
                if (printTasks(tasks)){
                    val taskNumber = getTaskNumber(tasks)
                    tasks[taskNumber] = editTask(tasks, taskNumber)
                }
            }
            "delete" -> deleteTask(tasks)
            "end" -> {
                if (saveTasksToFile(taskAdapter, tasks, file)) {
                    println("Tasklist exiting!")
                    return
                } else continue
            }
            else -> println("The input action is invalid")
        }
    }
}


fun saveTasksToFile(taskAdapter: JsonAdapter<List<Task>>,
                    tasks: MutableList<Task>, file: File):Boolean {

    try {
        file.writeText(taskAdapter.indent("  ").toJson(tasks))
        return true
    } catch (e: Exception) {
        println("Something went wrong! Tasks hasn't been saved! Anyway end? (y/n):")
        while (true) {
            return when (readln().lowercase()) {
                "y" -> true
                "n" -> false
                else -> {
                    println("Wrong answer! Input \"y\" or \"n\"")
                    continue
                }
            }
        }
    }
}

fun getTask(): Task {
    val priority = getPriority()
    val date = getDate()
    val time = getTime()
    val dateTime = mutableListOf(date, time)
    val taskToDo = getTaskToDo()

    val task = Task(dateTime, taskToDo)
    task.setThePriority(priority)

    return task
}


fun printTasks(tasks: MutableList<Task>): Boolean {

    var due: String
    var dueColor: String

    return if (tasks.isEmpty()) {
        println("No tasks have been input")
        false
    } else {

        val line = "+----+".padEnd(18, '-') + "+".padEnd(8, '-') +
                "+---+---+".padEnd(53, '-') + "+"

        var rowData = listOf("N", "   Date   ", "Time ", "P", "D",
            "Task".padStart(23, ' '))

        println(line)
        printRow(rowData)
        println(line)

        for (i in tasks.indices) {
            due = getDue(tasks[i].getDateTime())
            dueColor = when (due) {
                "I" -> "\u001B[102m \u001B[0m"
                "T" -> "\u001B[103m \u001B[0m"
                else -> "\u001B[101m \u001B[0m"
            }
            rowData = listOf(
                "${i + 1}",
                tasks[i].dateTime[0],
                tasks[i].dateTime[1],
                tasks[i].priorityColor,
                dueColor,
                tasks[i].toDo.map { it.chunked(44) }.flatten().joinToString("\n")
            )
            printRow(rowData)
            println(line)
        }
        true
    }
}


fun printRow(row: List<String>) {
    var rowData: MutableList<String>

    val toDoLines = (row[5].split("\n")).map { it.padEnd(44) }.toList()

    for (i in toDoLines.indices) {
        rowData = if (i == 0) {
            val firstString = row.subList(0, 5).toMutableList()
            firstString.add(toDoLines[0])
            firstString
        } else {
            mutableListOf(" ", " ", " ", " ", " ", toDoLines[i])
        }
        val string = "| ${rowData[0]} ".padEnd(5, ' ') +
                "| ${rowData[1]} ".padEnd(13, ' ') +
                "| ${rowData[2]} ".padEnd(8, ' ') +
                "| ${rowData[3]} ".padEnd(4, ' ') +
                "| ${rowData[4]} |".padEnd(4, ' ') +
                rowData[5].padEnd(44, ' ') +"|"

        println(string)
    }
}


fun editTask(tasks: MutableList<Task>, taskNumber: Int): Task {
    val task = tasks[taskNumber]
    while (true) {
        println("Input a field to edit (priority, date, time, task):")
        when (readln()) {
            "priority" -> task.setThePriority(getPriority())
            "date" -> task.dateTime[0] = getDate()
            "time" -> task.dateTime[1] = getTime()
            "task" -> task.toDo = getTaskToDo()
            else -> { println("Invalid field"); continue }
        }
        println("The task is changed")
        break
    }
    return task
}


fun deleteTask(tasks: MutableList<Task>) {
    if (printTasks(tasks)) {
        val taskNumber = getTaskNumber(tasks)
        tasks.removeAt(taskNumber)
        println("The task is deleted")
    }
}


fun getPriority(): String {
    var priority: String

    while (true) {
        println("Input the task priority (C, H, N, L):")
        priority = readln().uppercase()
        if ("[CHNL]".contains(priority)) break else continue
    }
    return priority
}


fun getDate(): String {
    var date: String

    while (true) {
        println("Input the date (yyyy-mm-dd):")
        try {
            val input = mutableListOf<String>()
            readln().split("-").toList()
                .forEach { input.add(if (it.length < 2) "0$it" else it) }

            date = input.joinToString("-")
            date.toLocalDate()
            break

        } catch (e: IllegalArgumentException) {
            println("The input date is invalid")
            continue
        }
    }
    return date
}


fun getTime(): String {
    var time: String

    while (true) {
        println("Input the time (hh:mm):")
        try {
            val input = mutableListOf<String>()
            readln().split(":").toList()
                .forEach { input.add(if (it.length < 2) "0$it" else it) }

            time = input.joinToString(":")
            "0001-01-01T$time".toLocalDateTime()
            break
        } catch (e: IllegalArgumentException) {
            println("The input time is invalid")
            continue
        }
    }
    return time
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


fun getTaskNumber(tasks: MutableList<Task>): Int {

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
}


fun getDue(dateTime: LocalDateTime): String {
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
    val taskDate = dateTime.date
    val daysToDeadline = currentDate.daysUntil(taskDate)

    return if(daysToDeadline == 0) {
        "T"
    } else if (daysToDeadline > 0) {
        "I"
    } else "O"
}