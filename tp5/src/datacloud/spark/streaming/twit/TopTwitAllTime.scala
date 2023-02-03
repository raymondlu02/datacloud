package datacloud.spark.streaming.twit

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.log4j.Logger
import org.apache.log4j.Level


object TopTwitAllTime extends App{

	Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]");
	val sc = new SparkContext(conf);
	sc.setLogLevel("ERROR")
	val ssc = new StreamingContext(sc, Seconds(1));
	ssc.checkpoint("./checkpoint")
	var regex = """^#.*""".r;
	val lines = ssc.socketTextStream("localhost", 4242); // peut etre que tout ca c'est dans la socket qui est passé en param
	val stringSplit = lines.flatMap(_.split(" ")) // ou split("#")
	val stringfilter = stringSplit.filter(x => (regex.pattern.matcher(x).matches));
//	val stringCouple = stringfilter.map(x=>(x,1));
//	val count = stringCouple.reduceByKeyAndWindow(_+_,_-_, Seconds(20),Seconds(2));
	val count = stringfilter.countByValueAndWindow(Seconds(20), Seconds(2))
	val trier = count.transform(rdd=>rdd.sortBy(_._2,false))
	trier.print();
	ssc.start();
	ssc.awaitTermination();
}

//object TopTwitSomeTime extends App{
//    
//    Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
//    
//     val conf = new SparkConf().setAppName("TopTwitAtTime").setMaster("local[*]")
//     val sc = new SparkContext(conf)
//    sc.setLogLevel("ERROR")
//     val ssc =  new StreamingContext(sc, Seconds(1))
//    ssc.checkpoint("twit")
//     val regex = "^#.*".r
//     val lines = ssc.socketTextStream("localhost", 4242)
//     val rdd1 = lines.flatMap(_.split(" "))
//     val rdd2 = rdd1.filter(x => (regex.pattern.matcher(x).matches()))
//     val rdd3  = rdd2.countByValueAndWindow(Seconds(20),Seconds(2))
////     val rdd3 = rdd2.map(x => (x,1))
////     val rdd4 = rdd3.reduceByKey(_+_)
//     val rdd4 = rdd3.transform(x => (x.sortBy(_._2,false)))
//     rdd4.print()
//     
//     ssc.start()
//     ssc.awaitTermination()
//}

