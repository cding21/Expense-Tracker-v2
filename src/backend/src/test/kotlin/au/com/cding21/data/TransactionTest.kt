package au.com.cding21.data

import org.junit.Assert.assertEquals
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import org.bson.Document


class TransactionTest {
    @Test
    fun testToDocument() {
        val transaction = Transaction(
            "",
            "testUser",
            LocalDate.parse("01/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            -100.0,
            "AUD", // Default currency code to "AUD
            "description",
            "category",
            "fromAccount",
            "",
            "toAccount",
            ""
        )
        val expectedDocument = Document(
            mapOf(
                "userId" to "testUser",
                "date" to "01/01/2022",
                "amount" to -100.0,
                "currencyCode" to "AUD",
                "description" to "description",
                "category" to "category",
                "fromAccount" to "fromAccount",
                "fromNote" to "",
                "toAccount" to "toAccount",
                "toNote" to ""
            )
        )

        val actualDocument = transaction.toDocument()

        assertEquals(expectedDocument, actualDocument)
    }

    @Test
    fun testFromDocument() {
        val document = Document(
            mapOf(
                "userId" to "testUser",
                "date" to "01/01/2022",
                "amount" to -100.0,
                "currencyCode" to "AUD",
                "description" to "description",
                "category" to "category",
                "fromAccount" to "fromAccount",
                "fromNote" to "",
                "toAccount" to "toAccount",
                "toNote" to ""
            )
        )
        val expectedTransaction = Transaction(
            "",
            "testUser",
            LocalDate.parse("01/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            -100.0,
            "AUD", // Default currency code to "AUD
            "description",
            "category",
            "fromAccount",
            "",
            "toAccount",
            ""
        )

        val actualTransaction = Transaction.fromDocument(document)

        assertEquals(expectedTransaction, actualTransaction)
    }

    @Test
    fun testFromCsvLine() {
        val csvLine = "01/01/2022,\"-100.0\",description,category,fromAccount,,toAccount,"
        val userId = "testUser"
        val expectedTransaction = Transaction(
            "",
            userId,
            LocalDate.parse("01/01/2022", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            -100.0,
            "AUD", // Default currency code to "AUD
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