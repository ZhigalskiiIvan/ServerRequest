import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.Socket
import kotlin.system.exitProcess

fun serverRequest() {
    /**
     * Creates a socket, buffered reader and writer
     * and uses them to call functions of sending and receiving requests.
     */

    val socket = Socket("npm.mipt.ru", 9048)

    val reader = socket.getInputStream().bufferedReader()
    val writer = socket.getOutputStream().bufferedWriter()


    sendAGreeting(writer)

    val sum = returnRESBytesSum(reader)
    sendASum(writer, sum)

    print("Answer: ${reader.readText()}")
}

fun sendASum(writer: BufferedWriter, sum: Int) {
    /**
     * Takes the sum and the buffered writer as input
     * and sends a request to the server.
     */
    writer.write("SUM$sum\n")
    writer.flush()
}

fun sendAGreeting(writer: BufferedWriter) {
    /**
     * Sends HELLO to the server.
     */
    writer.write("HELLO\n")
    writer.flush()
}

fun returnRESBytesSum(reader: BufferedReader): Int {
    /**
     * Looks for a phrase in the RES phrase, reads the required number
     * of bytes and then reads the bytes themselves if message was received,
     * call function which calculates its sum and returns it.
     */
    val receivedBytes = reader.readText().toByteArray()

    var sizeByteIndex: Int? = null

    for (byteNum in 2..receivedBytes.lastIndex) {
        if ("${receivedBytes[byteNum - 2].toInt().toChar()}" +
            "${receivedBytes[byteNum - 1].toInt().toChar()}" +
            "${receivedBytes[byteNum].toInt().toChar()}" == "RES"
        ) {
            sizeByteIndex = byteNum + 1
            break
        } else continue
    }
    if (sizeByteIndex == null) {
        println("No message was received including \"RES\".")
        exitProcess(-1)
    }

    val size = receivedBytes[sizeByteIndex].toUByte().toInt()

    val returningSum =
        calculateByteSum(receivedBytes.toList().subList(sizeByteIndex + 1, sizeByteIndex + 1 + size).toByteArray())

    println(returningSum)
    return returningSum
}

fun calculateByteSum(byteArray: ByteArray): Int {
    /**
     * Calculates the bytes sum from the byteArray,
     * considering them to be unsigned.
     */
    var sum = 0
    for (byte in byteArray) sum += byte.toUByte().toInt()
    return sum
}

fun main() {
    serverRequest()
}


