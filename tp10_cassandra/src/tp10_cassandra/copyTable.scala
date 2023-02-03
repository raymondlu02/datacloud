package tp10_cassandra

import org.apache.spark._
import com.datastax.spark.connector._
import org.apache.spark.sql._

case class Mecanicien (idmecano:Int, nom:String, prenom:String , status:String)

object copyTable extends App {
	org.apache.log4j.Logger.getLogger("org.apache.spark").setLevel(org.apache.log4j.Level.OFF)
	val conf = new SparkConf().setAppName("Spark on Cassandra").setMaster("local[*]").set("spark.cassandra.connection.host" ,"localhost")
	val sc = new SparkContext(conf)

	
	try {
		sc.cassandraTable ("garage" , "mecanicien_cpy").take(1).isEmpty
	}catch{
		case e:Exception =>{
			//	val rdd = sc.cassandraTable ("garage" , "mecanicien")	// soit ca (1)
			//	val rdd2 =  rdd.map(cr => (cr.getInt("idmecano"), cr.getString("nom"), cr.getString("prenom"), cr.getString("status"))) // avec ca (2)
			val rdd = sc.cassandraTable[Mecanicien]("garage" , "mecanicien")	// soit direct ca avec le case class
			rdd.saveAsCassandraTable("garage", "mecanicien_cpy")
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

