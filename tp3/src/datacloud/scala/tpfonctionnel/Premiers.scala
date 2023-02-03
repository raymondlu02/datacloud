package datacloud.scala.tpfonctionnel

import scala.collection.immutable.List;

	
object Premiers{
	
	def premiers(n :Int): List[Int] = {
		
		var list = List.range(2,n);
		
		// until = non compris 			to = compris
		for (i <- 2 to n){
			list = list.filter((a :Int )=> (a==i || (a%i!=0)))
		}
		
		return list
		
	}
	
	
	def premiersWithRec(n:Int): List[Int]= {
		var list = List.range(2,n);
		
		def fonct(list :List[Int]): List[Int] ={
			if ((list(0)*list(0))> list(list.size-1)){
				return list
			}else{
				var elem = list(0)
				return elem ::fonct(list.filter((a:Int) => (a%elem!=0)))
			}			
		}
		
		fonct(list);
		
	}
	
}