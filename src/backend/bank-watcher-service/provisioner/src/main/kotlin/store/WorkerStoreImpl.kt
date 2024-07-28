package store

import redis.AsyncClient

class WorkerStoreImpl(private val client: AsyncClient): WorkerStore {
    private val WORKER_TABLE_ID = "workers"

    override suspend fun getAllWorkers(): Set<String> = client.getAllSet(WORKER_TABLE_ID)

    override suspend fun isWorkerActive(id: String): Boolean = client.isInSet(WORKER_TABLE_ID, id)

    override suspend fun getTasksByWorkerId(workerId: String): Set<String> = client.getAllSet(workerId)

    override suspend fun addWorker(id: String) {
        client.addToSet(WORKER_TABLE_ID, id)
    }

    override suspend fun removeWorker(id: String) {
        client.removeFromSet(WORKER_TABLE_ID, id)
    }
}