package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue._

class CatalogueSoldeWithFor extends CatalogueWithNonMutable with CatalogueSolde{
	
	def solde(rabais: Int) :Unit ={
		for ((nom,prix) <- hash){
			var prixRabais = prix - prix*(rabais.asInstanceOf[Double]/100)
			storeProduct(nom,prixRabais)
		}
	}
}