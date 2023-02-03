package tp10_cassandra

import org.apache.spark._
import com.datastax.spark.connector._
import org.apache.spark.sql._
import com.datastax.spark.connector.cql.CassandraConnector


//case class Mecanicien (idmecano:Int, nom:String, prenom:String , status:String)
//case class Vehicule (idvehicule:Int, marque:String, modele:String, kilometrage:Int, mecano:Int)
case class outData2 (idmecano:Int, nom:String, prenom:String, status:String, vehicules:Iterable[Int])


object addColumn extends App {
	org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF)
	val conf = new SparkConf().setAppName("Spark on Cassandra").setMaster("local[*]").set("spark.cassandra.connection.host" ,"localhost")
	val sc = new SparkContext(conf)
	
	
	val rddm1 = sc.cassandraTable[Mecanicien]("garage" , "mecanicien")
	val rddv1 = sc.cassandraTable[Vehicule]("garage" , "vehicule")

	// virer le try plus tard
	try{
			sc.cassandraTable ("garage" , "reparations").take(1)
	}catch{
		case e: Exception =>{
			
			
			
			CassandraConnector(conf).withSessionDo { session =>
			   session.execute("ALTER TABLE garage.mecanicien ADD vehicules list<int>;")
			}
			val rddm1a = sc.cassandraTable[Mecanicien]("garage" , "mecanicien")

			val rddm2 = rddm1a.map(cr => (cr.idmecano,cr))
			val rddv2 = rddv1.map(cr => (cr.mecano,cr.idvehicule))
			val rddv3 = rddv2.groupByKey()	// fais rien dans le test du prof
			val rddm3 = rddm2.join(rddv3)
			val rddm4 = rddm3.map(kv=> outData2(kv._2._1.idmecano, kv._2._1.nom, kv._2._1.prenom, kv._2._1.status, kv._2._2))
			

//			val rddm3 = rddm1.map(cr => (cr.idmecano, cr.nom, cr.prenom, cr.status,  List("apples", "oranges", "pears")))
			rddm4.saveToCassandra("garage", "mecanicien",SomeColumns("idmecano","nom","prenom", "status","vehicules"))

		}
	}
	sc.stop ()
}
