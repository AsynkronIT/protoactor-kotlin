package actor.proto.remote

import actor.proto.*
import actor.proto.mailbox.newMpscUnboundedArrayMailbox
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.netty.NettyServerBuilder
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}
object Remote {
    private lateinit var _server: Server
    private val Kinds: HashMap<String, Props> = HashMap()
    lateinit var endpointManagerPid: PID
    lateinit var activatorPid: PID
    fun getKnownKinds(): Set<String> = Kinds.keys
    fun registerKnownKind(kind: String, props: Props) {
        Kinds.put(kind, props)
    }

    fun getKnownKind(kind: String): Props {
        return Kinds.getOrElse(kind) {
            throw Exception("No Props found for kind '$kind'")
        }
    }

    fun start(hostname: String, port: Int, config: RemoteConfig = RemoteConfig()) {
        ProcessRegistry.registerHostResolver { pid -> RemoteProcess(pid) }
        val serverBuilder = NettyServerBuilder.forPort(port).addService(EndpointReader())
        config.keepAliveTime?.let { serverBuilder.permitKeepAliveTime(config.keepAliveTime/2, TimeUnit.MILLISECONDS) }
        config.keepAliveWithoutCalls?.let {serverBuilder.permitKeepAliveWithoutCalls(config.keepAliveWithoutCalls)}
        _server = serverBuilder.build().start()
        val boundPort: Int = _server.port
        val boundAddress: String = "$hostname:$boundPort"
        val address: String = "${config.advertisedHostname ?: hostname}:${config.advertisedPort ?: boundPort}"
        ProcessRegistry.address = address
        spawnEndpointManager(config)
        spawnActivator()
        logger.info("Starting Proto.Actor server on $boundAddress")
    }

    private fun spawnActivator() {
        val props = fromProducer { Activator() }
        activatorPid = spawnNamed(props, "activator")
    }

    private fun spawnEndpointManager(config: RemoteConfig) {
        val props = fromProducer { EndpointManager(config) }
                .withMailbox { newMpscUnboundedArrayMailbox(200) }
        endpointManagerPid = spawnNamed(props, "endpointmanager")
        EventStream.subscribe({
            if (it is EndpointTerminatedEvent) {
                send(endpointManagerPid, it)
            }
        })
    }

    fun activatorForAddress(address: String): PID = PID(address, "activator")

    suspend fun spawn(address: String, kind: String, timeout: Duration): PID = spawnNamed(address, "", kind, timeout)

    suspend fun spawnNamed(address: String, name: String, kind: String, timeout: Duration): PID {
        val activator: PID = activatorForAddress(address)
        val req = ActorPidRequest(kind, name)
        val res = requestAwait<RemoteProtos.ActorPidResponse>(activator, req, timeout)
        return res.pid
    }

    fun sendMessage(pid: PID, msg: Any, serializerId: Int) {
        val (message, sender) = when (msg) {
            is MessageEnvelope -> Pair(msg.message, msg.sender)
            else -> Pair(msg, null)
        }
        val env: RemoteDeliver = RemoteDeliver(message, pid, sender, serializerId)
        send(endpointManagerPid, env)
    }
}

