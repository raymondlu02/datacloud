package datacloud.spark.core.lastfm
import org.apache.spark._
import org.apache.spark.rdd.RDD

object HitParade {
	case class TrackId(id :String)
	case class UserId(id:String)
	
	def loadAndMergeDuplicates(spark:SparkContext, url:String): RDD[((UserId,TrackId),(Int,Int,Int))] = {
		val textfile = spark.textFile(url)
		val rdd1 = textfile.map(line => ((line.split(" ")(0),line.split(" ")(1)),(line.split(" ")(2).toInt,line.split(" ")(3).toInt,line.split(" ")(4).toInt)))
		val rdd2 = rdd1.reduceByKey((a:(Int,Int,Int),b:(Int,Int,Int)) => (a._1+b._1,a._2+b._2,a._3+b._3))
	
		val rdd3 = rdd2.map(tuple => ((UserId(tuple._1._1),TrackId(tuple._1._2)) , ( tuple._2._1, tuple._2._2, tuple._2._3)))
	
	
	
		return rdd3        
}

	
	def hitparade(rdd : RDD[((UserId,TrackId),(Int,Int,Int))]): RDD[TrackId] = {
		val rdd1 = rdd.map(kkvv => ((kkvv._1._2),(kkvv._2._1+kkvv._2._2-kkvv._2._3,1))) //track user score 1
		val rdd2 = rdd1.reduceByKey((a:(Int,Int),b:(Int,Int)) => (a._1+b._1,a._2+b._2))
		val rdd3 = rdd2.sortBy(_._1.toString(),true)
		val rdd4 = rdd3.sortBy(_._2._1, false)
		val rdd5 = rdd4.sortBy(_._2._2,false)
		val rdd6 = rdd5.map(kkvv=>(kkvv._1))
		return rdd6
	}
    
    
}