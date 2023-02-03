package datacloud.spark.streaming.twit

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._

import org.apache.log4j.Logger
import org.apache.log4j.Level


object TopTwitAtTime extends App{
	Logger.getLogger("org").setLevel(Level.OFF)
	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]");
	val sc = new SparkContext(conf);
	sc.setLogLevel("ERROR")
	val ssc = new StreamingContext(sc, Seconds(1));
	var regex = """^#.*""".r;
	val lines = ssc.socketTextStream("localhost", 4242); // peut etre que tout ca c'est dans la socket qui est passé en param
	val stringSplit = lines.flatMap(_.split(" ")) // ou split("#")
	val stringfilter = stringSplit.filter(x => (regex.pattern.matcher(x).matches));
	val stringCouple = stringfilter.map(x=>(x,1));
	val count = stringCouple.reduceByKey(_+_);
//	 pour la dependance q2 utiliser reduceByKeyAndWindow()
	val trier = count.transform(rdd=>{
		rdd.sortBy(_._2,false)
	})
	trier.print();
	ssc.start();
	ssc.awaitTermination();
}