package datacloud.spark.streaming.pi

import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
import org.apache.log4j.Logger
import org.apache.log4j.Level


object PiAtTime extends App{
	Logger.getLogger("org.apache.spark").setLevel(Level.OFF)
	val conf = new SparkConf().setAppName("Mon appli spark").setMaster("local[*]");
	val sc = new SparkContext(conf);
	sc.setLogLevel("ERROR")
	val ssc = new StreamingContext(sc, Seconds(1));
//	ssc.checkpoint("./checkpoint")
	val lines = ssc.socketTextStream("localhost", 4242);
	val lines2 = lines.map(_.split(" "))
	val stringSplit = lines2.map(x => (x(0).toDouble, x(1).toDouble)) 
	val inCercle =  stringSplit.map(x => if (x._1*x._1 + x._2*x._2 < 1) 1 else 0).reduce(_+_)
	val total = stringSplit.count();
	val rdd5 = total.map(x => (1,x))
	val rdd6 = inCercle.map(x => (1,x))
	val rdd7 = rdd6.join(rdd5) //in,total
	val rdd8 = rdd7.map(x => 4*((x._2._1.toDouble)/(x._2._2.toDouble)))
	
	rdd8.print();	
	ssc.start();
	ssc.awaitTermination();
}

