package hello

import java.time.LocalDateTime
import java.util.Date

object HelloWorld{
  def main(args: Array[String]): Unit = {
    //async(call)
    val aInt = new NiceAtomicInt(10);
    aInt.modify(b => b + 10);
     println(aInt.amount.get())
  }
  def async(action:  => Unit):Unit = {
    new Thread() {
      override def run(): Unit = {
        println("T1")
        action
      }
    }.start()
  }

  def everySecond(action: Int => Unit) ={
    new Thread(){
      override def run(): Unit ={
        for(i <- 0 to 10) action(i)
      }
    }.start()
  }
  def call(): Unit = println(":-)")

  def clock(i: Int) : Unit = println("Tick("+i+")")

  def time[A](block: => A):A = {
    val start = LocalDateTime.now.getNano
    val res: A = block
    val end = LocalDateTime.now.getNano
    println((end - start)/1_000_000)
    res
  }
}
