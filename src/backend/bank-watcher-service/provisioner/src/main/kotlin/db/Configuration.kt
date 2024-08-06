package db

import io.ktor.server.application.*
import io.lettuce.core.RedisClient
import io.lettuce.core.api.reactive.RedisReactiveCommands

fun Application.configureRedis(): RedisReactiveCommands<String, String> {
    val redisClient = RedisClient.create("redis://${System.getenv("REDIS_PASSWORD")}@localhost:6379/0")
    val commands = redisClient.connect().reactive()
    return commands
}