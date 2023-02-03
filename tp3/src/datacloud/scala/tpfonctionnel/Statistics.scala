package datacloud.scala.tpfonctionnel

object Statistics {
  
	def average(listDouble : List[(Double,Double)]) : Double = {
		
		var list = listDouble.map(a=> (a._1*a._2,a._2))
		var res: (Double,Double) = list.reduce(((a:(Double,Double),b: (Double,Double)) => ((a._1+b._1),(a._2+b._2))))
		return res._1/res._2;
		
	}
}