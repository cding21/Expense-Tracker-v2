package service

interface WorkerService {
    suspend fun provisionWorker(id: String?)

    suspend fun shutdownWorker(id: String, isKilled: Boolean)

    suspend fun assignTasks(tasks: Set<String>)
}