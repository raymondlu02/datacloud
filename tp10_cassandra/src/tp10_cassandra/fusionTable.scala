package tp10_cassandra

import org.apache.spark._
import com.datastax.spark.connector._
import org.apache.spark.sql._

//case class Mecanicien (idmecano:Int, nom:String, prenom:String , status:String)
case class Vehicule (idvehicule:Int, marque:String, modele:String, kilometrage:Int, mecano:Int)
case class outData (idvehicule:Int, marque:String, modele:String, kilometrage:Int,mecano:Int, nom:String, prenom:String, status:String)

object fusionTable extends App {
	org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF)
	val conf = new SparkConf().setAppName("Spark on Cassandra").setMaster("local[*]").set("spark.cassandra.connection.host" ,"localhost")
	val sc = new SparkContext(conf)
	
	
	val rddm1 = sc.cassandraTable[Mecanicien]("garage" , "mecanicien")
	val rddv1 = sc.cassandraTable[Vehicule]("garage" , "vehicule")

	try{
			sc.cassandraTable ("garage" , "reparation").take(1)
	}catch{
		case e: Exception =>{
			val rddm2 = rddm1.map(cr => (cr.idmecano,cr))
			val rddv2 = rddv1.map(cr => (cr.mecano,cr))

			val rddv3 = rddv2.join(rddm2)
			val rddv4 = rddv3.map(kv => (kv._2._1, kv._2._2)) // v4 = (vehucule,mecano)
			val rddv5 = rddv4.map(kv => 
				outData(kv._1.idvehicule, kv._1.marque,kv._1.modele, kv._1.kilometrage, kv._1.mecano,
						kv._2.nom, kv._2.prenom, kv._2.status
				)
			)
			rddv5.foreach(println)
			rddv5.saveAsCassandraTable("garage", "reparation", 
					SomeColumns("idvehicule","marque","modele","kilometrage","mecano","nom","prenom","status")) 
		}
	}
	sc.stop ()
}

//CREATE KEYSPACE test WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };
//	CREATE TABLE test.kv ( key text PRIMARY KEY , value int );
//	INSERT INTO test.kv ( key , value ) VALUES ('key1', 1);
//	
//	SELECT * FROM test.kv WHERE key='key1';




//CREATE TABLE Vehicule (idvehicule int PRIMARY KEY, marque text, modele text, kilometrage int, mecano int); // q1
//CREATE TABLE Mecanicien (idmecano int PRIMARY KEY, nom text, prenom text, status text);	// q2

// DROP TABLE Mecanicien	// c'est pas une question


// SELECT * from vehicule ; // q4
// SELECT marque from vehicule ; // q5

// SELECT nom from mecanicien WHERE idmecano=3 ; // q6
// SELECT marque, modele from vehicule WHERE mecano=3 ALLOW FILTERING ; // q7
/*InvalidRequest: Error from server: code=2200 [Invalid query] 
* message="Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. 
* If you want to execute this query despite the performance unpredictability, use ALLOW FILTERING"
* */


// CREATE INDEX userIndex ON vehicule (mecano); // q8
// SELECT marque,modele from vehicule WHERE mecano=3 ; // q8 suite

// SELECT * from vehicule WHERE idvehicule<5 ALLOW FILTERING; // q9
// SELECT * from vehicule WHERE token(idvehicule)<5;	// q9

