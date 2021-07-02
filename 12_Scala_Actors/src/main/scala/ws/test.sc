import scala.collection.parallel.CollectionConverters.ImmutableIterableIsParallelizable
class Math{
  def prodAndSquares(l:List[Int]): (Int, List[Int]) = {
    val prod = l.par.product
    val squares = l.par.map{i => i*i}.toList
    (prod, squares)
  }
}
val m: Math = new Math

m.prodAndSquares((1 to 10).toList)
m.prodAndSquares((1 to 10).toList)
m.prodAndSquares((1 to 10).toList)
m.prodAndSquares((1 to 10).toList)
