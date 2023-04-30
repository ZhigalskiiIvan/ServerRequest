import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.system.exitProcess

/**
 * Creates a socket, buffered reader and writer
 * and uses them to call functions of sending and receiving requests.
 */
fun serverRequest() {

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

/**
 * Reads the text from inputStream
 * @param inputStream
 */
fun getAnswer(inputStream: InputStream): String = inputStream.bufferedReader().readText()

/**
 * Takes the sum and the buffered writer as input
 * and sends a request to the server.
 */
fun sendASum(outputStream: OutputStream, sum: Int) {
    outputStream.write("SUM${sum}\n".toByteArray())
    outputStream.flush()
    println("Sum posted: $sum.")
}

/** Sends HELLO to the server. */
fun sendAGreeting(outputStream: OutputStream) {
    outputStream.write("HELLO\n".toByteArray())
    outputStream.flush()
}

/**
 * Looks for a phrase in the RES phrase, reads the required number
 * of bytes and then reads the bytes themselves if message was received,
 * call function which calculates its sum and returns it.
 */
fun returnRESBytesSum(inputStream: InputStream): Int {val receivedBytes = inputStream.readBytes()

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

/**
 * Calculates the bytes sum from the byteArray,
 * considering them to be unsigned.
 */
fun calculateUByteSum(byteArray: List<Byte>): Int {
    var sum = 0
    byteArray.forEach { sum += it.toUByte().toInt() }
    return sum
}

fun main() {
    serverRequest()
}


