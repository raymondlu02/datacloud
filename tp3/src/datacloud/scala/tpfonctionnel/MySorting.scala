package datacloud.scala.tpfonctionnel

import scala.math.Ordering;

object MySorting {
  
	def isSorted[A](tab :Array[A], f:(A,A)=> Boolean): Boolean={
		for (i <- 0 to tab.length-2){
			if ( f(tab(i),tab(i+1)) == false){
				return false;
			}
		}
		return true;
		
	}
	
	
	def ascending[T](a:T, b:T)(implicit order :Ordering[T]) : Boolean={
		
		if(order.compare(a, b) > 0){
			return false
		}else{
			return true
		}
	}
	
	
	def descending[T](a:T, b:T)(implicit order :Ordering[T]): Boolean={
		
	
		if(order.compare(b, a) > 0){
			return false
		}else{
			return true
		}
		
	}
}



