package actor.proto.tests

import actor.proto.*
import actor.proto.fixture.EmptyReceive
import org.junit.Assert.assertSame
import org.junit.Test

class SpawnTests {
    @Test
    fun `given props with spawner spawn should return pid created by spawner`() {
        val spawnedPid: PID = PID("test", "test")
        val props: Props = fromFunc(EmptyReceive).withSpawner { _, _, _ -> spawnedPid }
        val pid: PID = spawn(props)
        assertSame(spawnedPid, pid)
    }
}

