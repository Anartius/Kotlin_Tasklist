package tasklist

fun main(){
    val tasks = mutableListOf<String>()
    tasks.addAll(getTasks())
    if (tasks.isEmpty()) {
        println("No tasks have been input")
    } else  printTasks(tasks)
}


fun getTasks(): MutableList<String> {
    val tasks = mutableListOf<String>()
    val gaps = "\\s*".toRegex()

    println("Input the tasks (enter a blank line to end):")
    while(true) {
        val input = readLine()!!
            .replaceFirst(gaps, "").reversed()
            .replaceFirst(gaps, "").reversed()
        tasks.add(input)
        if (tasks.last().isEmpty()) {
            tasks.removeLast()
            break
        }
    }
    return tasks
}


fun printTasks(tasks: MutableList<String>) {
    for (i in tasks.indices) {
        println("${i + 1}" + (if(i < 9) "  " else " ") + tasks[i])
    }
}