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
        for (taskId in tasks) {
            val workers = store.getAllWorkers()
            val workersByTaskSize = workers.map { Pair(it, store.getTasksByWorkerId(it).size) }.sortedBy { it.second }
            val firstWorker = workersByTaskSize.firstOrNull {
                TODO("Implement task allocation API call to worker")
            }
            if (firstWorker == null) {
                continue
            }
            // Check set diff to see if new workers has been added
            val diff = store.getAllWorkers() subtract workers;
            if (diff.isNotEmpty()) {
                val finalWorker = diff.map { Pair(it, store.getTasksByWorkerId(it).size) }.sortedBy { it.second }.firstOrNull {
                    TODO("Implement task allocation API call to worker")
                }
                if (finalWorker != null) {
                    continue
                }
            }
            throw NotFoundException("Unable to allocate task to available worker")
        }
    }
}