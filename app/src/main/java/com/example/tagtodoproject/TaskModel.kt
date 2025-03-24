 class TaskModel(
    val title: String,
    val tags: String,
    val date: String,
    val category: String
)

object TaskData {
    val trashTasks = mutableListOf<TaskModel>()
    val completedTasks = mutableListOf<TaskModel>()
    val allTasks = mutableListOf<TaskModel>()

    fun getAllTasksGroupedByTag(): List<TaskModel> {
        return allTasks.groupBy { it.tags }.flatMap { it.value }
    }

    fun returnToOrigin(task: TaskModel) {
        // TODO: pindahkan kembali ke kategori School/Work/Exercise berdasarkan task.category
    }
}
