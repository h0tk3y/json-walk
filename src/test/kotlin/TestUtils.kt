import org.junit.Assert
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

object TestUtils {
    private val newline = System.getProperty("line.separator")

    fun testStdInOut(stdin: String, expected: String, fn: () -> Unit) {
        val oldStdOut = System.out
        val oldStdIn = System.`in`
        try {
            val inputStream = stdin.byteInputStream()
            val outputStream = ByteArrayOutputStream()
            inputStream.use {
                outputStream.use {
                    PrintStream(outputStream, true, "UTF-8").use {
                        System.setIn(inputStream)
                        System.setOut(it)
                        fn()
                    }
                }
            }
            val actual = outputStream.toByteArray().inputStream()
                .bufferedReader()
                .readLines()
                .map { it.trim() }
                .dropLastWhile { it.isBlank() }
                .joinToString(newline)
            Assert.assertEquals(expected, actual)
        } finally {
            System.setOut(oldStdOut)
            System.setIn(oldStdIn)
        }
    }
}