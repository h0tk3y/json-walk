package com.h0tk3y.jsonwalk

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val jsonFilePath = when {
        args.getOrNull(0) == "--file" -> args.getOrNull(1) ?: error("expected filename after --file")
        args.getOrNull(0) == "--new" -> null
        else -> error("expected args: --file <filename> or --new")
    }

    val rootElement = jsonFilePath?.let {
        MutableElement.fromJsonElement(Json.parseToJsonElement(File(it).readText()))
    } ?: MutableElement.ObjectElement(mutableMapOf())

    val state = State(rootElement)

    loop(state)
}

fun loop(state: State) {
    while (true) {
        val line = readLine()
        val lineWord = line?.substringBefore(" ") ?: return
        val command = when (lineWord) {
            "path" -> Command.Path
            "print" -> Command.Print
            "up" -> Command.Up
            "go" -> Command.Go(ElementPath.parse(line.substringAfter(" ")))
            "findpaths" -> Command.FindPaths(ElementPath.parse(line.substringAfter(" ")))
            "delete" -> Command.Delete(ElementPath.parse(line.substringAfter(" ")))
            "replace", "insert" -> run f@{
                val argsLine = line.substringAfter(" ")
                if (" " !in argsLine) return@f null
                val (pathString, elementString) = argsLine.split(" ", limit = 2)
                val path = ElementPath.parse(pathString)
                val createElement = { MutableElement.fromJsonElement(Json.parseToJsonElement(elementString)) }
                when (lineWord) {
                    "replace" -> Command.Replace(path, createElement)
                    "insert" -> Command.Insert(path, createElement)
                    else -> error("unexpected")
                }
            }
            "copy" -> Command.Copy(ElementPath.parse(line.substringAfter(" ")))
            "paste" -> Command.Paste
            "exit" -> return
            else -> null
        }
        val result = if (command != null) state.applyCommand(command) else CommandResult.Failure
        when (result) {
            CommandResult.Success -> println("OK")
            CommandResult.Failure -> println("fail")
            is CommandResult.ResultValue -> println("OK ${result.value}")
            is CommandResult.ResultValues -> {
                println("OK")
                result.values.forEach { println(it) }
            }
            is CommandResult.InvalidPath -> println("bad-path ${result.atIndex}")
        }
    }
}

class State(
    rootElement: MutableElement
) {
    init {
        require(rootElement is MutableElement.ObjectElement || rootElement is MutableElement.ArrayElement) {
            "the root element must be an object element or an array element; $rootElement is passed"
        }
    }

    fun applyCommand(command: Command): CommandResult {
        return when (command) {
            is Command.Path -> doPath()
            Command.Print -> doPrint()
            Command.Up -> doUp()
            is Command.Go -> doGo(command)
            is Command.FindPaths -> doFindPaths(command)
            is Command.Delete -> doDelete(command)
            is Command.Replace -> doReplace(command)
            is Command.Insert -> doInsert(command)
            is Command.Copy -> doCopy(command)
            is Command.Paste -> doPaste()
        }
    }

    /** Always returns [CommandResult.ResultValue] containing the current path starting from the root element, for
     * example, `foo/bar/[0]/baz/[1]`. */
    private fun doPath(): CommandResult.ResultValue {
        TODO()
    }

    /**
     * Returns [CommandResult.ResultValue] containing the string representation of the current element.
     */
    private fun doPrint(): CommandResult.ResultValue {
        /** Use [Json.encodeToString] */
        TODO()
    }

    /**
     * Go to the parent element, one level up. Returns [CommandResult.Failure] if no parent element exists,
     * [CommandResult.Success] otherwise.
     */
    private fun doUp(): CommandResult {
        TODO()
    }

    /**
     * Changes the current path to an existing object or array element. If the relative path in [Command.Go.path]
     * points to a non-existing element or an element that is not an object or array, [CommandResult.InvalidPath] is
     * returned. Returns [CommandResult.Success] if the path is changed.
     */
    private fun doGo(go: Command.Go): CommandResult {
        TODO()
    }

    /**
     * Returns [CommandResult.ResultValues] with matching paths. A paths is considered matched if ends with the same
     * segments as the requested [Command.FindPaths.path].
     *
     * In the bonus task, the latter may contain `*` segments, which match any single segment.
     *
     * Example: in an object `{ "a": { "b": { "c": 123 } } }`, there are three paths, `a`, `a/b`, and `a/b/c`.
     * Only the second one matches a request for `a/b` (as `a/b/c` doesn't end with `a/b`).
     */
    private fun doFindPaths(command: Command.FindPaths): CommandResult.ResultValues {
        TODO()
    }

    /**
     * Deletes the element found by the relative path in [Command.Delete.path], removing the element from its parent.
     * If the parent is an array element, the successor elements move one index lower.
     *
     * In the bonus task, the path may contain star segments, so more than one element may be removed.
     *
     * If the path points to a non-existing element, [CommandResult.InvalidPath] is returned. [CommandResult.Success]
     * is returned otherwise.
     */
    private fun doDelete(command: Command.Delete): CommandResult {
        TODO()
    }

    /**
     * Replaces the element by the specified [Command.Replace.path] with a new element provided by
     * [Command.Replace.newElement].
     *
     * In the bonus task, the path may contain `*` segments, so more than one element may be replaced.
     */
    private fun doReplace(replace: Command.Replace): CommandResult {
        TODO()
    }


    /**
     * Inserts the element provided by [Command.Insert.newElement] at the designated relative [Command.Insert.path] from
     * the current position.
     *
     * If any intermediate elements in the path do not exist, they are created (they may be object elements as well as
     * array elements if the next segment is an index).
     *
     * If the insertion point position is already taken, [CommandResult.InvalidPath] is returned and no change is made.
     *
     * In an [MutableElement.ArrayElement], as special index segment [ElementPath.appendSegment] specifies that the new
     * element should be added after the last element.
     *
     * If an *intermediate* segment is located in an [MutableElement.ArrayElement] and the index is taken by another
     * element, no change should be made, and the result should be [CommandResult.InvalidPath]. The append segment or
     * the index after the last element are fine to use in intermediate array elements.
     *
     * If the *last* segment is an index in an [MutableElement.ArrayElement], then the new element should be added at
     * the specified index, moving the successor elements one index forward.
     */
    private fun doInsert(insert: Command.Insert): CommandResult {
        TODO()
    }

    /**
     * Remembers the path matching [Command.Copy.path] as well as the element, for future insertion into a different
     * element in [doPaste]. In the bonus task, the requested path may match more than one element, all of which need
     * to be copied to the corresponding relative paths at the destination.
     * Assume that no mutating operation is performed between copy and paste.
     */
    private fun doCopy(command: Command.Copy): CommandResult {
        TODO()
    }

    /**
     * Inserts the element remembered with [doCopy] at the corresponding relative path in the current position.
     */
    private fun doPaste(): CommandResult {
        TODO()
    }
}

class ElementPath(val segments: List<String>) {
    override fun toString() = segments.joinToString(segmentSeparator)

    fun hasIndexAt(index: Int) = indexAtOrNull(index) != null
    fun indexAtOrNull(index: Int) = toIndexOrNull(segments[index])
    fun hasStarSegmentAt(index: Int) = isStarSegment(segments[index])
    fun hasAppendSegmentAt(index: Int) = isAppendSegment(segments[index])

    companion object {
        const val segmentSeparator = "/"
        const val appendSegment = "[_]"
        const val starSegment = "*"

        fun toIndexOrNull(segment: String): Int? =
            segment.removeSurrounding("[", "]").takeIf { it != segment }?.toIntOrNull()

        fun isAppendSegment(segment: String) = segment == appendSegment
        fun isStarSegment(segment: String) = segment == starSegment

        fun parse(string: String) = ElementPath(string.split(segmentSeparator))
    }
}

sealed class CommandResult {
    object Success : CommandResult()
    object Failure : CommandResult()
    class ResultValue(val value: String) : CommandResult()
    class ResultValues(val values: List<String>) : CommandResult()
    class InvalidPath(val atIndex: Int) : CommandResult()
}

sealed class Command {
    object Path : Command()
    object Up : Command()
    object Print : Command()
    class Go(val path: ElementPath) : Command()
    class Replace(val path: ElementPath, val newElement: () -> MutableElement) : Command()
    class Insert(val path: ElementPath, val newElement: () -> MutableElement) : Command()
    class Copy(val path: ElementPath) : Command()
    object Paste : Command()
    class FindPaths(val path: ElementPath) : Command()
    class Delete(val path: ElementPath) : Command()
}

sealed class MutableElement {
    class PrimitiveNumber(var number: Number) : MutableElement()
    class PrimitiveString(var string: String) : MutableElement()
    class PrimitiveBoolean(var value: Boolean) : MutableElement()
    object PrimitiveNull : MutableElement()

    class ArrayElement(val items: MutableList<MutableElement>) : MutableElement()
    class ObjectElement(val items: MutableMap<String, MutableElement>) : MutableElement()

    fun toJsonElement(): JsonElement = when (this) {
        is PrimitiveNumber -> JsonPrimitive(number)
        is PrimitiveString -> JsonPrimitive(string)
        is PrimitiveBoolean -> JsonPrimitive(value)
        PrimitiveNull -> JsonNull
        is ArrayElement -> JsonArray(items.map { it.toJsonElement() })
        is ObjectElement -> JsonObject(items.mapValues { it.value.toJsonElement() })
    }

    companion object {
        fun fromJsonElement(json: JsonElement): MutableElement = when (json) {
            is JsonPrimitive -> when {
                json.isString -> PrimitiveString(json.content)
                json is JsonNull -> PrimitiveNull
                json.content == "true" || json.content == "false" -> PrimitiveBoolean(json.content.toBoolean())
                json.content.toIntOrNull() != null -> PrimitiveNumber(json.content.toInt())
                json.content.toDoubleOrNull() != null -> PrimitiveNumber(json.content.toDouble())
                else -> PrimitiveString(json.content)
            }
            is JsonArray -> ArrayElement(json.mapTo(mutableListOf()) { fromJsonElement(it) })
            is JsonObject -> ObjectElement(json.keys.associateTo(mutableMapOf()) { key ->
                key to fromJsonElement(json.getValue(key))
            })
        }
    }
}