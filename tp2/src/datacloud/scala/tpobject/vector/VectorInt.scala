package datacloud.scala.tpobject.vector

import VectorInt._

class VectorInt(tab: Array[Int]) {
  
   def length() : Int = {
  	 return tab.length;
   }
   
   def get(i : Int ): Int = {
  	 if ( i< length()){
  		 return tab(i)
  	 }
  	 return -1
   }
   
   def tostring() : String = {
  	 var res = "";
  	 for (i <- 0 until length()){
  		 res = res + tab(i) + " ";
  	 }
  	 	
  	 return res;
   }
   
   
   override def equals(a:Any) : Boolean = {
  	 a match {
  		 case vect : VectorInt => 
  			 if (length() != vect.length()){
  				 return false;
  			 }
  			 for (i <- 0 until (vect.length())){
		  		 if (vect.get(i) != tab(i)){
		  			 return false
		  		 }
		  	 }
  			 return true
  	 }
   }
   
   def +(other : VectorInt) : VectorInt = {
  	 // on suppose que les vecteurs ont la meme taille  	 
  	 var tableau : Array[Int] = Array.ofDim(length());
  	 for (i <- 0 until length() ){
  		 tableau(i) = get(i) + other.get(i);
  	 }
  	 
  	 var res = new VectorInt(tableau);
  	 return res

   }
   
   def *(v: Int) : VectorInt ={
  	 var tableau : Array[Int] = Array.ofDim(length());
  	 for (i <- 0 until length()){
  		 tableau(i) = get(i) * v;
  	 }
  	 
  	 var res = new VectorInt(tableau);
  	 return res
   }
   
 
   def prodD(other : VectorInt) : Array[VectorInt] = {
  	 
  	 var arrayVect :Array[VectorInt] = Array.ofDim(length()); // la valeur de retour 
  	 
  	 var tableau : Array[Int] = Array.ofDim(other.length());
  	 for (i <- 0 until length() ){
  		 	 var res = other.*(get(i));
	  	 	 arrayVect(i) = res
  	 }
  	 return arrayVect
   }

}

object VectorInt {
	
	implicit def apply(tableau : Array[Int]) : VectorInt = new VectorInt(tableau);	
}


