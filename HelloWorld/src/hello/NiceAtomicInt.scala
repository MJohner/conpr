package hello

import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

class NiceAtomicInt(v: Int) {
  var amount = new AtomicInteger(v)

  def modify(f: (Int) => Int): Unit ={
    while(true){
      val currentValue = amount.get();
      if(amount.compareAndSet(currentValue, f(currentValue))) return
    }


  }
}

class NiceAtomicRef[A](ref: A){
  val atomicRef = new AtomicReference(ref)

  def modify(f: A => A): Unit = {
    while(true) {
      val currentRef = atomicRef.get()
      if(atomicRef.compareAndSet(currentRef, f(currentRef))) return
    }
  }



}