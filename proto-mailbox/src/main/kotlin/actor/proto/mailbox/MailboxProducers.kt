package actor.proto.mailbox

import org.jctools.queues.MpscArrayQueue
import org.jctools.queues.MpscGrowableArrayQueue
import org.jctools.queues.MpscLinkedQueue8
import org.jctools.queues.MpscUnboundedArrayQueue
import org.jctools.queues.QueueFactory.newQueue
import org.jctools.queues.atomic.MpscAtomicArrayQueue
import org.jctools.queues.atomic.MpscGrowableAtomicArrayQueue
import org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue
import org.jctools.queues.spec.ConcurrentQueueSpec
import java.util.concurrent.ConcurrentLinkedQueue

private val emptyStats : Array<MailboxStatistics> = arrayOf()
fun newUnboundedMailbox(stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), ConcurrentLinkedQueue<Any>(), stats)

fun newMpscAtomicArrayMailbox(capacity: Int = 1000, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscAtomicArrayQueue(capacity), stats)
fun newMpscArrayMailbox(capacity: Int = 1000, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscArrayQueue(capacity), stats)

fun newMpscUnboundedArrayMailbox(chunkSize: Int = 5, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscUnboundedArrayQueue(chunkSize), stats)
fun newMpscUnboundedAtomicArrayMailbox(chunkSize: Int = 5, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscUnboundedAtomicArrayQueue(chunkSize), stats)


fun newMpscGrowableArrayMailbox(initialCapacity: Int = 5, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscGrowableArrayQueue(initialCapacity, 2 shl 11), stats)
fun newMpscGrowableAtomicArrayMailbox(initialCapacity: Int = 5, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscGrowableAtomicArrayQueue(initialCapacity, 2 shl 11), stats)


fun newMpscLinkedMailbox(stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), MpscLinkedQueue8(), stats)

fun newSpecifiedMailbox(spec:ConcurrentQueueSpec, stats: Array<MailboxStatistics> = emptyStats): Mailbox = DefaultMailbox(ConcurrentLinkedQueue<Any>(), newQueue(spec) , stats)
