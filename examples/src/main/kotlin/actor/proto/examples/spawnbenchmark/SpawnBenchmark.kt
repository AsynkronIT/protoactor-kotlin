package actor.proto.examples.spawnbenchmark

import actor.proto.*


fun main(args: Array<String>) {
    var start: Long = 0
    val managerProps = fromFunc {
        val msg = message
        when (msg) {

            is Begin -> {
                val root = spawn(SpawnActor.props)
                start = System.currentTimeMillis()
                root.send(Request(10, 0, 1000000, self))
            }
            is Long -> {
                val millis = System.currentTimeMillis() - start
                println("Elapsed " + millis)
                println("Result " + msg)
                println("done")
            }
        }
    }
    val managerPid: PID = spawn(managerProps)
    managerPid.send(Begin)
    readLine()
    managerPid.send(Begin)
    readLine()
    managerPid.send(Begin)
    readLine()
    managerPid.send(Begin)
    readLine()
    managerPid.send(Begin)
    readLine()
}

object Begin
data class Request(var div: Long, var num: Long, var size: Long, val respondTo : PID)

class SpawnActor : Actor {
    companion object Foo{
        private fun produce() = SpawnActor()
        val props: Props = fromProducer(SpawnActor.Foo::produce)
    }

    private var replies: Long = 0
    private lateinit var replyTo: PID
    private var sum: Long = 0

    suspend override fun receive(context: Context) {
        val msg: Any = context.message
        when (msg) {
            is Request -> when {
                msg.size == 1L -> {
                    msg.respondTo.send(msg.num)
                    context.self.stop()
                }
                else -> {
                    replies = msg.div
                    replyTo = msg.respondTo
                    for (i in 0 until msg.div) {
                        val child: PID = spawn(props)
                        val s = msg.size / msg.div
                        child.send(Request(
                                msg.div,
                                msg.num + i * s,
                                s,
                                context.self))
                    }
                }
            }
            is Long -> {
                sum += msg
                replies--
                if (replies == 0L) replyTo.send(sum)
            }
        }
    }
}



