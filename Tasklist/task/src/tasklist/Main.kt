package tasklist

fun main(){
    val tasks = mutableListOf(mutableListOf<String>())
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
                if (task.isNotEmpty()) tasks.add(task)
                continue
            }
            "print" -> {
                printTasks(tasks)
                continue
            }

            else -> {
                println("The input action is invalid")
                continue
            }
        }
    }
}


fun getTask(): MutableList<String> {
    val task = mutableListOf<String>()
    task.clear()

    println("Input a new task (enter a blank line to end):")
    while(true) {
        val input = readln().trim()
        if (input.isEmpty()) {
            if (task.isEmpty()) println("The task is blank")
            return task
        }
        task.add(input)
    }
}


fun printTasks(tasks: MutableList<MutableList<String>>) {
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else {
        for (i in tasks.indices) {
            println("${i + 1}".padEnd(3)
                    + tasks[i].joinToString("\n   ") + "\n")
        }
    }
}