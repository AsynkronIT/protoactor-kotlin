package actor.proto

val EmptyMessageHeader = MessageHeader()

class MessageHeader {
    private val map: MutableMap<String, String> = mutableMapOf()

    fun getOrDefault(key: String, default: String): String = map.getOrDefault(key, default)

    fun set(key: String, value: String) {
        map[key] = value
    }
}

