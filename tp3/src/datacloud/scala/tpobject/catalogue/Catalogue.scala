package datacloud.scala.tpobject.catalogue;

trait Catalogue {
   def getPrice(nom : String) : Double;
   
   def removeProduct(nom : String) : Unit;
    // peut etre iterator voir poly p56
   def selectProducts(prix_min : Double , prix_max : Double) : Iterable[String];
   
   def storeProduct(nom : String , prix : Double) : Unit;
   
}