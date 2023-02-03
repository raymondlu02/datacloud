package datacloud.scala.tpfonctionnel.catalogue

import datacloud.scala.tpobject.catalogue._

class CatalogueSoldeWithNamedFunction extends CatalogueWithNonMutable with CatalogueSolde{
	
	def diminution(a:Double, percent:Int):Double = a*((100.0 -percent) /100.0)
//	def mapValues[C](f: (B) => C): Map[A, C]
	
  def solde(rabais :Int): Unit = {
  	
  		var map = hash.mapValues(diminution(_,rabais))
  		hash = map
  	
  }
}