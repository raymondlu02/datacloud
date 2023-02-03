package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue._

class CatalogueSoldeWithAnoFunction extends CatalogueWithNonMutable with CatalogueSolde {
	
	def solde(percent:Int)={
		var map = hash.mapValues((a:Double) => a*((100.0 -percent) /100.0))
		hash = map
		
	}
  
}