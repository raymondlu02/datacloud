package datacloud.spark.core.stereoprix
import org.apache.spark._


object Stats  {
  def chiffreAffaire(url :String, annee: Int):Int ={
  	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]")
  	val spark = new SparkContext(conf)
  	val textFile = spark.textFile(url)
  	
//  	var rdd1 = textFile.map(text => text.split(" ")(0).split("_")(2).equals(String.valueOf(annee))).map( x=>x(6).toInt)	
  	var rdd1 = textFile.map(text =>(text.split(" ")(0),text.split(" ")(2)))
  	var rdd2 = rdd1.map(date_prix => (date_prix._1.split("_")(2).equals(annee.toString), date_prix._2.toInt))
  	var rdd3 = rdd2.filter(_._1)
  	var rdd4 = rdd3.map(date_prix => date_prix._2.toInt)
  	
  	var res = rdd4.reduce(_+_)
  	
  	spark.stop()

  	return res;
  }
  
  
  
  
  def chiffreAffaireParCategorie(urlEntree :String, urlSortie: String): Unit={
  	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]")
  	val spark = new SparkContext(conf)
  	val textFile = spark.textFile(urlEntree)
  	
  	// traitement pour recuperer tous les couples (categorie,prix)
  	var rdd1 = textFile.map(text =>(text.split(" ")(4),(text.split(" ")(2)).toInt))
//  	rdd1.foreach(println)
  	var rdd2 = rdd1.reduceByKey(_+_)
  	var rdd3 = rdd2.map(kv =>(kv._1+":"+kv._2))

  	rdd3.saveAsTextFile(urlSortie)

  	spark.stop()
  	
  }
  
  def produitLePlusVenduParCategorie(urlEntree :String, urlSortie: String): Unit={
  	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]")
  	val spark = new SparkContext(conf)
  	val textFile = spark.textFile(urlEntree)
  	
  	def max (ite :Iterable[(String,Int)]): String={
  		var res_max = -1;
  		var produit = "";
  		
  		for (elem <- ite){
  			if (res_max < elem._2){
  				res_max = elem._2;
  				produit = elem._1;
  			}
  		}
  			
  		return produit;
  	}
  	
  	var rdd1 = textFile.map(text =>((text.split(" ")(4),(text.split(" ")(3))),1))
  	var rdd2 = rdd1.reduceByKey(_+_)
  	var rdd3= rdd2.map( kkv => (kkv._1._1,(kkv._1._2, kkv._2)))


  	var rdd4 = rdd3.groupByKey()

  	var rdd5 = rdd4.map(kvv => (kvv._1+":"+max(kvv._2)))

  	rdd5.saveAsTextFile(urlSortie)

  	spark.stop()
  	
  }

}