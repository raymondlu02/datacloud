package datacloud.synthese

import org.apache.spark._
import com.datastax.spark.connector._
import org.apache.spark.sql._
import com.datastax.spark.connector

import com.datastax.oss.driver.api.core.CqlSession

// immatriculation  modelevehicule  proprietaire

object ImmatUtil {
  val keyspace = "immat"

	def changeAdresse(id: String, adresse: String , sc : SparkContext): Unit ={
		val rdd = sc.cassandraTable(keyspace, "proprietaire")
		
		val session: CqlSession = CqlSession.builder().withKeyspace("immat").build()
		
		try {
		  val tableName = "proprietaire"
		  val updateStmt = s"UPDATE immat.$tableName SET adresse = ? WHERE id = ?"
		  val prepared = session.prepare(updateStmt)
		  val bound = prepared.bind(adresse, id)
		  val result = session.execute(bound)
		} finally {
		  session.close()
		}
		
		
	}
}