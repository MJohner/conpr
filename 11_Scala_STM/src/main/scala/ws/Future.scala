package ws

import java.util
import java.util.Optional
import java.util.concurrent.Executors

class Future[A] {
  @volatile
  private var isFinished = false
  private var result: Optional[A] = new Optional[A]()
  private val callbackList = new util.LinkedList[A=>Unit]
  private val lock = new Object

  private def setResult(result: A):Unit = {
    this.result = Optional.of(result)
    isFinished = true
    lock.synchronized(
      while(!callbackList.isEmpty){
      callbackList.remove()(result)
    })
  }

  def onSuccess(callback: A => Unit) : Unit = {
    if(!isFinished){
      lock.synchronized(
        callbackList.add(callback)
      )
    }else{
      callback(result.get())
    }
  }

  def map[B](f: A => B): Future[B] = {
    val fu = new Future[B]
    this.onSuccess(a=>{
      fu.setResult(f(a))
    })
    fu
  }
}

object Future{
  private val es = Executors.newFixedThreadPool(8)

  def async[A](body: => A): Future[A] = {
    val f = new Future[A]
    es.execute(() =>
      f.setResult(body)
    )
    f
  }
}
