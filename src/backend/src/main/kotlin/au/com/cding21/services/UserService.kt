package au.com.cding21.services

import au.com.cding21.data.User

interface UserService {
    suspend fun getUserByUsername(username: String): User?
    suspend fun createUser(user: User): Boolean
}