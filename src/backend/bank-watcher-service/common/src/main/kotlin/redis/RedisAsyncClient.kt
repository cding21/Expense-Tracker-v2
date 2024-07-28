package redis

import io.lettuce.core.SetArgs
import io.lettuce.core.api.reactive.RedisReactiveCommands
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.reactive.asFlow
import java.util.HashSet
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RedisAsyncClient(private val commands: RedisReactiveCommands<String, String>): AsyncClient {
    override suspend fun getKey(key: String) = suspendCoroutine<String> {
        commands.get(key).subscribe(it::resume)
    }

    override suspend fun setKey(key: String, value: String) = suspendCoroutine<String> {
        commands.set(key, value).subscribe(it::resume)
    }

    override suspend fun setKey(key: String, value: String, args: SetArgs): String = suspendCoroutine<String> {
        commands.set(key, value, args).subscribe(it::resume)
    }

    override suspend fun deleteKey(key: String) = suspendCoroutine<Long> {
        commands.del(key).subscribe(it::resume)
    }

    override suspend fun addToSet(key: String, value: String) = suspendCoroutine<Long> {
        commands.sadd(key, value).subscribe(it::resume)
    }

    override suspend fun addToSet(key: String, value: Set<String>) = suspendCoroutine<Long> {
        commands.sadd(key, *value.toTypedArray()).subscribe(it::resume)
    }

    override suspend fun getAllSet(key: String): Set<String> {
        val result: MutableSet<String> = HashSet()
        commands.smembers(key).asFlow().toSet(result)
        return result.toSet()
    }

    override suspend fun removeFromSet(key: String, value: String) = suspendCoroutine<Long> {
        commands.srem(key, value).subscribe(it::resume)
    }

    override suspend fun removeFromSet(key: String, value: Set<String>) = suspendCoroutine<Long> {
        commands.srem(key, *value.toTypedArray()).subscribe(it::resume)
    }

    override suspend fun isInSet(key: String, value: String): Boolean = suspendCoroutine<Boolean> {
        commands.sismember(key, value).subscribe(it::resume)
    }
}