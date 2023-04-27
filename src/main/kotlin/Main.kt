import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.system.exitProcess


fun serverRequest() {
    /**
     * Creates a socket, buffered reader and writer
     * and uses them to call functions of sending and receiving requests.
     */

    val socket = Socket("npm.mipt.ru", 9048)

    val inputStream = socket.getInputStream()
    val outputStream = socket.getOutputStream()

    sendAGreeting(outputStream)

    val sum = returnRESBytesSum(inputStream)
    sendASum(outputStream, sum)

    // no answer?
    while (inputStream.available() > 0) println("Answer: ${getAnswer(inputStream)}")

    socket.close()
}

fun getAnswer(inputStream: InputStream): String = inputStream.bufferedReader().readText()

fun sendASum(outputStream: OutputStream, sum: Int) {
    /**
     * Takes the sum and the buffered writer as input
     * and sends a request to the server.
     */
    outputStream.write("SUM${sum}\n".toByteArray())
    outputStream.flush()
    println("Sum posted: $sum.")
}

fun sendAGreeting(outputStream: OutputStream) {
    /** Sends HELLO to the server. */
    outputStream.write("HELLO\n".toByteArray())
    outputStream.flush()
}

fun returnRESBytesSum(inputStream: InputStream): Int {
    /**
     * Looks for a phrase in the RES phrase, reads the required number
     * of bytes and then reads the bytes themselves if message was received,
     * call function which calculates its sum and returns it.
     */
    val receivedBytes = inputStream.readBytes()

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

    return calculateUByteSum(receivedBytes.toList().subList(sizeByteIndex + 1, sizeByteIndex + 1 + size))
}

fun calculateUByteSum(byteArray: List<Byte>): Int {
    /**
     * Calculates the bytes sum from the byteArray,
     * considering them to be unsigned.
     */
    var sum = 0
    byteArray.forEach { sum += it.toUByte().toInt() }
    return sum
}

fun main() {
    serverRequest()
}


