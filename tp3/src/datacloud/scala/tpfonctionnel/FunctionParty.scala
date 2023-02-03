package datacloud.scala.tpfonctionnel

object FunctionParty {
  def curryfie[A,B,C](f:(A,B)=>C): A=>B =>C ={
  	return ( (a:A)=>(b:B) => f(a,b))
  	
  }
  
  def decurryfie[A,B,C](f:A=>B=>C): (A,B) =>C={
  	return (a:A, b:B) => (f(a)(b))
//		return (a:A, b:B) => {var p= f(a); p(b)}
  }
  
  
  def compose[A,B,C](f:B=>C, g:A=>B):A => C={
  	return (a:A)=> f(g(a))
  }
  
  
  def axplusb(a:Int, b:Int): Int=>Int={
  	var cur_add  = curryfie((a:Int, b:Int) => a+b);  	
  	var cur_mult = curryfie((a:Int ,x: Int) =>a*x );
  	
  	var comp = compose(cur_add(b), cur_mult(a))
  	
//  	def f(x:Int):Int ={
//  		return a*x+b;
//  	}

  	return comp
  }
  
}