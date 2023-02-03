package datacloud.scala.tpfonctionnel

import scala.collection.immutable.List;

object Counters {
  
	def nbLetters(listString : List[String]):Int={
		
//		var list = listString.flatMap(_.toLowerCase);
//		list = list.filter((a:Char)=>(!a.equals(' ')));
//		return list.size
		
		

    var words = listString.flatMap(s => s.split(" "))
//    println(words)
    var res = words.map(w => w.length)
    return res.reduce((a,b) => a+b)
				
	}
}