Sample code:

```
val allocator = QueueAllocatorImpl(1, 1)
runBlocking {
    val anz = ANZClientImpl(allocator, "username", "password")
    val accountId = anz.getAccounts()[0].id
    println(anz.getTransactions(accountId, 1000))
    anz.getRealTimeTransactions(accountId).collect {
        println(it)
    }
}
allocator.close()
```