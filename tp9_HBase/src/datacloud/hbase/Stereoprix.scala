package datacloud.hbase
import org.apache.hadoop.hbase.util.Bytes
import datacloud.hbase.tests.HbaseTest._
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import collection.JavaConverters._

case class Stereoprix(){
  // définition de la classe Stereoprix
}

object Stereoprix {
  def nbVenteParCategorie:Map[String,Int] ={
  	var namespace = "stereoprix"
  	var hbaseclient = new HbaseClient(connection)
  	
//  	try{
//  		hbaseclient.createTable(TableName.valueOf("Vente"))
//  	}catch{
//  		case e :Exception => println("existe deja")
//  	}
  	
  	// nom de la categorie, nb vente total
  	var map = scala.collection.mutable.Map[String,Int]()  	
  	val table_vente = connection.getTable(TableName.valueOf(namespace,"vente"))
		val table_produit = connection.getTable(TableName.valueOf(namespace,"produit"))
		val table_categorie = connection.getTable(TableName.valueOf(namespace,"categorie"))

  	
		

		
		// ----------------------------------------------------- table vente
    var scanner = table_vente.getScanner(new Scan).asScala
//  	println("-------"+scanner.isEmpty)
  	scanner.foreach( res =>{ 
  		res.getFamilyMap("defaultcf".getBytes).asScala.foreach(c =>{
  			// recupere le produit de la table vente
  			var produit = ""
  			if(Bytes.toString(c._1).equals("produit")){
  				produit = Bytes.toString(c._2)
//  				println("vente ::::::::::: "+produit)
  			
	  			// -------------------------------------------table produit
					var get = new Get(Bytes.toBytes(produit))
					var resultat = table_produit.get(get)
					
					resultat.getFamilyMap("defaultcf".getBytes).asScala.foreach(co =>{
						if(Bytes.toString(co._1).equals("categorie")){
							var categorie = Bytes.toString(co._2)
//							println("produit :::::::::::"+categorie)

							// -----------------------------------------table categoerie
							var get = new Get(Bytes.toBytes(categorie))
							var resultat2 = table_categorie.get(get)
							
							resultat2.getFamilyMap("defaultcf".getBytes).asScala.foreach(col =>{
								if(Bytes.toString(col._1).equals("designation")){
									var designation = Bytes.toString(col._2)
//									println("designation :::::::::::"+designation)
									
									if (map.contains(designation)){
										map(designation)+=1
									}else{
										map += (designation -> 1)
									}
								}
							})
						}
					})
					//-------
  			}
  		})
  		
  	})
  	
  	return map.toMap
  	
  }
  
  
  // -------------------Exo3
  // -------------------------------------------DENORMALISE
  def denormalise():Unit ={
  	
  	var namespace = "stereoprix"

  	var tn : TableName = TableName.valueOf("vente")
  	val admin = connection.getAdmin  	
  	
  	// ajouter une column (designation) dans la column family defaultcf de la table vente
//  	try{
//  		// descriptor de la column Family
//  		val columnFamilyDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("defaultcf")).build()
////  		val columnDescriptor = new ColumnDescriptorBiulder.newBuilder
//  		val tableDescriptor = new HTableDescriptor(TableName.valueOf("vente"));
//  		admin.addColumnFamily(tn, columnFamilyDescriptor)
//  		
//  		// ajout de la colonne que a rajouter
////  		columnFamilyDescriptor.addColumn(Bytes.toBytes("designation"))
//  	}catch{
//  		case e :Exception => {}
//  	}
  
  	// suppose que la colonne a bine ete ajouter 
  	
  	val table_vente = connection.getTable(TableName.valueOf(namespace,"vente"))
  	val table_produit = connection.getTable(TableName.valueOf(namespace,"produit"))
  	val table_categorie = connection.getTable(TableName.valueOf(namespace,"categorie"))

		
		// ----------------------------------------------------- table vente
    var scanner = table_vente.getScanner(new Scan).asScala
//  	println("-------"+scanner)
  	scanner.foreach( res =>{ 
  		res.getFamilyMap("defaultcf".getBytes).asScala.foreach(c =>{
  			// recupere le produit de la table vente
  			var produit = ""
  			if(Bytes.toString(c._1).equals("produit")){
  				produit = Bytes.toString(c._2)
//  				println("vente ::::::::::: "+produit)
  			
	  			// -------------------------------------------table produit
					var get = new Get(Bytes.toBytes(produit))
					var resultat = table_produit.get(get)
					
					resultat.getFamilyMap("defaultcf".getBytes).asScala.foreach(co =>{
						if(Bytes.toString(co._1).equals("categorie")){
							var categorie = Bytes.toString(co._2)
//							println("produit :::::::::::"+categorie)

							// -----------------------------------------table categoerie
							var get = new Get(Bytes.toBytes(categorie))
							var resultat2 = table_categorie.get(get)
							
							resultat2.getFamilyMap("defaultcf".getBytes).asScala.foreach(col =>{
								if(Bytes.toString(col._1).equals("designation")){
									var designation = Bytes.toString(col._2)
//									println("designation :::::::::::"+designation)
										
										// le chnagement par rapport a avant  
									  var put = new Put(res.getRow)
										put.addColumn("defaultcf".getBytes, "designation".getBytes, col._2)
										table_vente.put(put)
									
								}
							})
						}
					})
  			}
  		})
  	})// fin scanner
  	table_vente.close()
  	table_produit.close()
  	table_categorie.close()

  }// fin fonction 
  
  
  // ---------------------------------------------------------------------------------NB VENTE DENORMALISE
  def nbVenteParCategorieDenormalise:Map[String,Int]={
  	var namespace = "stereoprix"
  	var hbaseclient = new HbaseClient(connection)
  	
  	// nom de la categorie, nb vente total
  	var map = scala.collection.mutable.Map[String,Int]()  	
  	val table_vente = connection.getTable(TableName.valueOf(namespace,"vente"))

		// ----------------------------------------------------- table vente
    var scanner = table_vente.getScanner(new Scan).asScala
//  	println("-------"+scanner.isEmpty)
  	scanner.foreach( res =>{ 
  		res.getFamilyMap("defaultcf".getBytes).asScala.foreach(c =>{
  			// recupere le produit de la table vente
  			var designation = ""
  			if(Bytes.toString(c._1).equals("designation")){
  				designation = Bytes.toString(c._2)
//  				println("vente ::::::::::: "+produit)
  				if (map.contains(designation)){
						map(designation)+=1
					}else{
						map += (designation -> 1)
					}
	
					//-------
  			}// fin if
  		})// fin scanner
  		
  	})// fin fonction
  	
  	table_vente.close()

  	return map.toMap
  	
  }
  
  //--------------------------------------------------------------------addVente
  
  def addVente(c:Connection,idvente:String,idclient:String,idmag:String,idprod:String,date:String):Unit={
  	var namespace = "stereoprix"
  	val table_vente = c.getTable(TableName.valueOf(namespace,"vente"))
  	
  	var put = new Put(idvente.getBytes)
		put.addColumn("defaultcf".getBytes, "idvente".getBytes, idvente.getBytes)
		put.addColumn("defaultcf".getBytes, "client".getBytes, idclient.getBytes)
		put.addColumn("defaultcf".getBytes, "produit".getBytes, idprod.getBytes)
		put.addColumn("defaultcf".getBytes, "magasin".getBytes, idmag.getBytes)
		put.addColumn("defaultcf".getBytes, "date".getBytes, date.getBytes)
//		put.addColumn("defaultcf".getBytes, "designation".getBytes, col._2)
		
		
		// ------------------------------------- ajouter la colonne denormaliser
  	val table_produit = connection.getTable(TableName.valueOf(namespace,"produit"))
  	val table_categorie = connection.getTable(TableName.valueOf(namespace,"categorie"))
  			
	  // -------------------------------------------table produit
		var get = new Get(Bytes.toBytes(idprod))
		var resultat = table_produit.get(get)
		
		resultat.getFamilyMap("defaultcf".getBytes).asScala.foreach(co =>{
			if(Bytes.toString(co._1).equals("categorie")){
				var categorie = Bytes.toString(co._2)
//							println("produit :::::::::::"+categorie)

				// -----------------------------------------table categoerie
				var get = new Get(Bytes.toBytes(categorie))
				var resultat2 = table_categorie.get(get)
				
				resultat2.getFamilyMap("defaultcf".getBytes).asScala.foreach(col =>{
					if(Bytes.toString(col._1).equals("designation")){
						var designation = Bytes.toString(col._2)
//									println("designation :::::::::::"+designation)
							
							// le chnagement par rapport a avant  
						 
							put.addColumn("defaultcf".getBytes, "designation".getBytes, col._2)						
					}
				})
			}
		})
  	table_produit.close()
  	table_categorie.close()
		

		table_vente.put(put)
		table_vente.close()

  }
  
  
  
  // ------------FIN 
  
  
  
  
}