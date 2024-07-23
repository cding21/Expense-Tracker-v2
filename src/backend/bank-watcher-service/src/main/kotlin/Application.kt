import au.com.cding21.third_party.banks.INGClientImpl
import au.com.cding21.third_party.banks.allocators.QueueAllocatorImpl
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val allocator = QueueAllocatorImpl(1, 1)
        val bankClient = INGClientImpl(allocator, "abc", listOf(0, 0, 0, 0))
        bankClient.getAccounts()
    }
}