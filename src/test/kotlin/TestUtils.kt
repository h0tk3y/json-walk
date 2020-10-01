import org.junit.Assert
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
            val ignoreBadPathItem = { line: String ->
                if (line.matches("""bad-path \d+""".toRegex())) {
                    "bad-path"
                } else {
                    line
                }
            }
            val actual = outputStream.toByteArray().inputStream()
                .bufferedReader()
                .readLines()
                .map { it.trim() }
                .map(ignoreBadPathItem)
                .dropLastWhile { it.isBlank() }
                .joinToString(newline)
            val correctedExpected = expected.split(System.lineSeparator()).joinToString(separator = System.lineSeparator(), transform = ignoreBadPathItem)
            Assert.assertEquals(correctedExpected, actual)
        } finally {
            System.setOut(oldStdOut)
            System.setIn(oldStdIn)
        }
    }
}
