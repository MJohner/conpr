package ws

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


object GetMaxInt {

  case class biggestInt(elements: Array[Int], start: Int, end: Int){}

  class MaxActor()extends Actor{
    override def receive: Receive = {
      case biggestInt(elements, start, end) =>
        var max = 0
        println(start + " - " + end)
        if(end-start >= 32){
          val leftActor = context.actorOf(Props[MaxActor])
          val rightActor = context.actorOf(Props[MaxActor])
          implicit val t = new Timeout(20 second)
          val leftAnswer = leftActor ? biggestInt(elements, start, end/2)
          val rightAnswer = rightActor ? biggestInt(elements, ((start + end)/2)+1, end)

          val lResult = Await.result(leftAnswer.mapTo[Int], 20 seconds)
          val rResult = Await.result(rightAnswer.mapTo[Int], 20 seconds)

          max = if(lResult > rResult){lResult} else rResult

        }else{
          for(x <- start to end){
            if(elements.apply(x) > max){
              max = elements.apply(x)
            }
          }
        }
        sender ! max
    }
  }

  def main(args: Array[String]): Unit = {
    val as = ActorSystem("as")
    val firstActor = as.actorOf(Props[MaxActor])
    val start = 0
    val end = 1000
    val elements = (start to end).toArray
    implicit val t = new Timeout(20 second)
    val result = firstActor ? biggestInt(elements, start, end)
    println(Await.result(result.mapTo[Int], 20 seconds))



  }

}
