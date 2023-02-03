package datacloud.spark.core.matrix

import org.apache.spark.rdd.RDD;
import datacloud.scala.tpobject.vector.VectorInt
import org.apache.spark._




object MatrixIntAsRDD{
		implicit def apply(rddvect : RDD[VectorInt]) : MatrixIntAsRDD = new MatrixIntAsRDD(rddvect);	
		
		def makeFromFile(url :String , nbpartition: Int, spark: SparkContext):MatrixIntAsRDD={
			var rdd1 = spark.textFile(url, nbpartition)
			var rdd2 = rdd1.map(text => text.split(" ").map(x=> x.toInt))
			var rdd3 = rdd2.map(tab => VectorInt(tab))
			
			var rdd4 = rdd3.zipWithIndex()
			var rdd5 = rdd4.sortBy(_._2,true)
			var rdd6 = rdd5.map(kv => kv._1)
			
			return MatrixIntAsRDD(rdd6)
		}
}


class MatrixIntAsRDD(rdd :RDD[VectorInt]) {
	
	val lines :RDD[VectorInt] = rdd
	
	override def toString={
		val sb = new StringBuilder()
		lines.collect().foreach(line=>sb.append(line.toString()+"\n"))
		sb.toString()
	}
	
	
	def nbLines():Int={
		var rdd1 = lines.map(vector => 1)
		var rdd2 = rdd1.reduce(_+_)
		return rdd2
	}
  
	def nbColumns():Int={
		var rdd1 = lines.map(vector => vector.length()).first()
		return rdd1
	}
	
	
	def get(i:Int, j:Int):Int={
		var rdd1 = lines.zipWithIndex()
		
		var rdd2 = rdd1.filter(_._2 == i)
		var rdd3 = rdd2.map(tab => tab._1.elements(j)).first()
		return rdd3
	}
	// ---------------Q7
	
	
	override def equals(a:Any):Boolean={
		a match {
	 				
		 		case rdd : MatrixIntAsRDD => 
		 			var rdd1 = lines.zipWithIndex().map(kv => (kv._2,kv._1))
		 			var rddA = rdd.lines.zipWithIndex().map(kv => (kv._2,kv._1))
		 			
		 			var rdd2 = rdd1.join(rddA) // (int,(vectorint,vectorint))
		 			var rdd3 = rdd2.map(kvv => kvv._2._1.equals(kvv._2._2))
					
		 			var rdd4 = rdd3.filter(x => x==false).isEmpty()
		 			return rdd4
		 			
		}
		return false

	}
	
	
	def +(other:MatrixIntAsRDD):MatrixIntAsRDD={
			var rdd1 = lines.zipWithIndex().map(kv => (kv._2,kv._1))
//			println("\n\n--------------------\n"+MatrixIntAsRDD(lines).toString)

			var rddA = other.lines.zipWithIndex().map(kv => (kv._2,kv._1))
			var rdd2 = rdd1.join(rddA) // (int,(vectorint,vectorint))
			var rdd3 = rdd2.map(kvv => (kvv._1, kvv._2._1.+(kvv._2._2)))
			var rdd4 = rdd3.sortByKey()
			var rdd5 = rdd4.map(kv => kv._2)
			
			return MatrixIntAsRDD(rdd5)
	}
	
	
	def transpose():MatrixIntAsRDD={

			
			var tailleLigne = nbLines() 
//			var rdd2 = rdd1.flatMap({case (row,rowIndex) => Seq(row)});
			var rdd1 = lines.flatMap({row=> row.toString.split(" ")}); //  une val par ligne en string
			var rdd2 = rdd1.zipWithIndex()		// ajoute a chaque ligne son index
			
			//recup qqchose du type avec nbligne=3  et nbcolonne=2
//						=> (0,elem1) \n (1,elem2) \n (2,elem3) \n (0,elem4) \n (1,elem5) \n (2,elem6) \n 
			var rdd3 = rdd2.map(kv => ( kv._2.toInt%tailleLigne, kv._1.toInt)) // (int, int)
			var rdd4 = rdd3.groupByKey() // on a : (0,(elem1,elem4)) \n (1,(elem2,elem5) \n ...
			var rdd5 = rdd4.sortByKey()
			
			// les conversions de type pour avoir le bon type de retour
			var rdd6 = rdd5.map(kv => (kv._2).toArray)
			var rdd7 = rdd6.map(kv =>VectorInt(kv))
			


			return MatrixIntAsRDD(rdd7)
	}
	
	

	
		def *(rdd: MatrixIntAsRDD):MatrixIntAsRDD={
			val tailleLigne = nbLines();
			
			
			var mat = transpose()	
			
			var rdd1 = mat.lines.zipWithIndex().map(kv => (kv._2,kv._1))
			var rddA = rdd.lines.zipWithIndex().map(kv => (kv._2,kv._1))
			
			
			// etape 1 coupler la colonne i de la matrice A avec la ligne i de la matrice B
			
			var rdd2 = rdd1.join(rddA)			// 1er vecteur = colonne , 2eme = ligne
			
			
			// etape 2 prduit dyadique
			var rdd3 = rdd2.map(kvv => (kvv._2._1).prodD(kvv._2._2))
			
//			etape 3  
			
//			var rdd4 = rdd3.flatMap({row=> row.toString.split(" ");}); //  une val par ligne en string
//			var rddCast = rdd4.map(kv => kv.asInstanceOf[VectorInt])
//
//			var rdd5 = rddCast.zipWithIndex()		// ajoute a chaque ligne son index
//			
			
			var rdd4 = rdd3.flatMap({row => row}); //  une val par ligne en string
//			var rddCast = rdd4.map(kv => kv.asInstanceOf[VectorInt])

			var rdd5 = rdd4.zipWithIndex()		// ajoute a chaque ligne son index
			
			//recup qqchose du type avec nbligne=3  et nbcolonne=2
//						=> (0,elem1) \n (1,elem2) \n (2,elem3) \n (0,elem4) \n (1,elem5) \n (2,elem6) \n 
			var rdd7 = rdd5.map(kv => ( kv._2.toInt%tailleLigne, kv._1.asInstanceOf[VectorInt])) // (int, int)
			var rdd8 = rdd7.groupByKey() // on a : (0,(elem1,elem4)) \n (1,(elem2,elem5) \n ...
			var rdd9 = rdd8.sortByKey()
			
			var rdd10 = rdd9.map(kv => kv._2.toArray)
			var rdd11 = rdd10.map(kv => (kv).reduce(_+_))

			
			rdd11.foreach(println)
//			println("\n\n\n"+rdd2.toString)
			
			
			
			return MatrixIntAsRDD(rdd11);
			
		}
		
		
		
		
		
}

