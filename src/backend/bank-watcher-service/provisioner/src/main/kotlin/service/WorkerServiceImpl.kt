package service

import io.ktor.client.*
import io.ktor.server.plugins.*
import redis.AsyncClient
import store.WorkerStore
import utils.redisLock
import utils.redisUnlock

class WorkerServiceImpl(private val store: WorkerStore,
                        private val httpClient: HttpClient,
                        private val redisClient: AsyncClient
): WorkerService {
    override suspend fun provisionWorker(id: String?) {
        redisLock(redisClient, "ProvisionNewInstanceLock")
        val workerId = id ?: TODO("Automatic worker provisioning not implemented yet")
        // TODO: Implement worker healthcheck on startup
        store.addWorker(id)
        redisUnlock(redisClient, "ProvisionNewInstanceLock")
    }

    override suspend fun shutdownWorker(id: String, isKilled: Boolean) {
        if (!isKilled) {
            TODO("Automatic worker shutdown not implemented yet")
        }

        store.removeWorker(id)
    }

    override suspend fun assignTasks(tasks: Set<String>) {
        val workers = store.getAllWorkers()
        var isAssigned: Boolean

        for (taskId in tasks) {
            isAssigned = false
            for (workerId in workers) {
                // TODO: Assign task to worker via worker API (verify worker can handle task)
                isAssigned = true
                break
            }
            if (!isAssigned) {
                // Check if new workers has been added
                val newWorkers = store.getAllWorkers() subtract workers
                for (workerId in newWorkers) {
                    // TODO: Assign task to worker via worker API (verify worker can handle task)
                    isAssigned = true
                    break
                }
            }
            if (!isAssigned) {
                throw NotFoundException("Unable to assign task to worker")
            }
        }
    }
}