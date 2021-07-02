import java.util.concurrent.{ConcurrentLinkedQueue, Executors}
import scala.concurrent.stm.{Ref, Txn, atomic}

object MiniAct {
  val workers = Executors.newFixedThreadPool(2)

}

abstract class Actor() {
  val msgQ = new ConcurrentLinkedQueue[Any]()

  def !(msg: Any): Unit = {
    msgQ.add(msg)
    MiniAct.workers.execute(new Runnable() {
      def run(): Unit = {
        val nextMsg = msgQ.remove()
        if (receive.isDefinedAt(nextMsg)) {
          receive(nextMsg)
        }
      }
    })
  }

  def receive: PartialFunction[Any, Unit]
}

val exampleActor: Actor = new Actor {
  val i = Ref(0)

  def receive = {
    case msg =>
      atomic{ implicit txn =>
        i.set(i() + 1)
        Txn.afterCommit{_ => println(i.single.get + " " + msg)}
      }
  }
}
for(i<- 1 to 10) {
  exampleActor ! "Hi"
  exampleActor ! "Bye"
}
