package proto.actor

class Actor {
    companion object {
        val Done: Task = Task()
        val eventStream: EventStream
            get() = EventStream.Instance

        fun fromProducer(producer: () -> IActor): Props = Props().withProducer(producer)
        fun fromFunc(receive: (IContext) -> Task): Props = fromProducer { -> FunActor(receive) }
        fun spawn(props: Props): PID {
            val name: String = ProcessRegistry.instance.nextId()
            return spawnNamed(props, name)
        }

        fun spawnPrefix(props: Props, prefix: String): PID {
            val name: String = prefix + ProcessRegistry.instance.nextId()
            return spawnNamed(props, name)
        }

        fun spawnNamed(props: Props, name: String): PID {
            return props.spawn(name, null)
        }
    }
}