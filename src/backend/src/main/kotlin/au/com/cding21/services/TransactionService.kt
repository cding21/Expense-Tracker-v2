package au.com.cding21.services

import au.com.cding21.data.Transaction
import java.io.BufferedReader

interface TransactionService {
    suspend fun createTransaction(transaction: Transaction): String
    suspend fun getTransactionByUserId(userId: String): List<Transaction>
    suspend fun getTransactionById(id: String): Transaction?
    suspend fun updateTransaction(id: String, transaction: Transaction): Transaction?
    suspend fun deleteTransaction(id: String): Transaction?
    suspend fun uploadTransactions(csv: BufferedReader, userId: String): List<Transaction>
}