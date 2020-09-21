import com.h0tk3y.jsonwalk.main
import org.junit.Assume
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File

@RunWith(Parameterized::class)
class TestJsonWalk {
    companion object {
        private val testDataPath = "src/test/resources/testData"

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data() = File(testDataPath).listFiles().orEmpty().filter { it.extension == "log" }.map {
            arrayOf(it.nameWithoutExtension)
        }.sortedBy { it[0] }
    }

    @Parameterized.Parameter
    lateinit var testId: String

    @Test
    fun test() {
        val testLog = testId + ".log"
        val logLines = File("$testDataPath/$testLog").readLines()

        if (testId.startsWith(starTaskPrefix) && !isStarTaskEnabled()) {
            println("The bonus task is disabled. To enable it, add a file '$starTaskMarkerFile' to the project root.")
            Assume.assumeTrue(false)
        }

        val input =
            logLines.filter { it.startsWith(">") }.map { it.removePrefix(">") }.joinToString(System.lineSeparator())
        val output =
            logLines.filter { !it.startsWith(">") }.joinToString(System.lineSeparator())

        val jsonFile = File("$testDataPath/${testId.substringBefore("-")}.json").takeIf { it.isFile }

        val args = if (jsonFile != null) arrayOf("--file", jsonFile.absolutePath) else arrayOf("--new")
        TestUtils.testStdInOut(input, output) { main(args) }
    }

    private val starTaskPrefix = "star"
    private val starTaskMarkerFile = "star.txt"

    /** Enabled when a file named [starTaskMarkerFile] */
    private fun isStarTaskEnabled() = File(starTaskMarkerFile).isFile
}