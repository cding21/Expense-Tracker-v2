package store

interface WorkerStore {
    suspend fun getAllWorkers(): Set<String>

    suspend fun isWorkerActive(id: String): Boolean

    suspend fun getTasksByWorkerId(workerId: String): Set<String>

    suspend fun addWorker(id: String)

    suspend fun removeWorker(id: String)
}