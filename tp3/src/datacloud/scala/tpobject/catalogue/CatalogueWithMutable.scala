package datacloud.scala.tpobject.catalogue;
import scala.collection.mutable.Map;
import scala.collection.mutable.Set;

class CatalogueWithMutable extends Catalogue{
  // (nom , prix)
   var hash = Map[String, Double]();
    
  
   def getPrice(nom : String) : Double = {
     if (hash.contains(nom)){  
       return hash(nom) ;
     }
     return -1;
   }
   
   def removeProduct(nom : String) : Unit = {
     if (hash.contains(nom)){  
       hash.remove(nom);
     }
   }
   
   
   
   // peut etre iterator vaoir poly p56
   def selectProducts(prix_min : Double , prix_max : Double) : Iterable[String] = {
     var ite : Set[String] = Set();
     hash.foreach{
       case(key :String ,value : Double) => if (value > prix_min && value < prix_max){
         ite = ite + key;
       }
     }
     return ite;
   }
   
   
   
   
   def storeProduct(nom : String , prix : Double) : Unit = {
     if (hash.contains(nom)){
       hash(nom) = prix; 
     }else{
       hash += (nom->prix);
     }
   }
  
}