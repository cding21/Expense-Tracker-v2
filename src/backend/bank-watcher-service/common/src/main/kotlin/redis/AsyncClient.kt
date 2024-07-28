package redis

import io.lettuce.core.SetArgs

interface AsyncClient {
    suspend fun getKey(key: String): String

    suspend fun setKey(key: String, value: String): String

    suspend fun setKey(key: String, value: String, args: SetArgs): String

    suspend fun deleteKey(key: String): Long

    suspend fun addToSet(key: String, value: String): Long

    suspend fun addToSet(key: String, value: Set<String>): Long

    suspend fun getAllSet(key: String): Set<String>

    suspend fun removeFromSet(key: String, value: String): Long

    suspend fun removeFromSet(key: String, value: Set<String>): Long

    suspend fun isInSet(key: String, value: String): Boolean
}