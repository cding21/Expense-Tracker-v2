package au.com.cding21.data

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TransactionTest {
    // Write a Junit test for the Transaction class
    // Test the toDocument method

    // Test the fromDocument method

    // Test the fromCsvLine method
    @Test
    fun testFromCsvLine() {
        val csvLine = "01/01/2022,\"-100.0\",description,category,fromAccount,,toAccount,"
        val userId = "testUser"
        val expectedTransaction = Transaction(
            userId,
            LocalDate.parse("01/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            -100.0,
            "description",
            "category",
            "fromAccount",
            "",
            "toAccount",
            ""
        )

        val actualTransaction = Transaction.fromCsvLine(csvLine, userId)

        assertEquals(expectedTransaction, actualTransaction)
    }
}