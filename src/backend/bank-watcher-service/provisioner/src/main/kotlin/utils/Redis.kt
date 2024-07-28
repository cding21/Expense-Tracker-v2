package utils

import redis.AsyncClient
import io.lettuce.core.SetArgs

suspend fun redisLock(redisClient: AsyncClient, lockName: String) {
    while (redisClient.setKey(lockName, "locked", SetArgs().nx().ex(30L)) != "OK") {/*NO-OP*/}
}

suspend fun redisUnlock(redisClient: AsyncClient, lockName: String) {
    redisClient.deleteKey(lockName)
}