package datacloud.hbase

import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import java.io.File
import collection.JavaConverters._
import datacloud.hbase.tests.HbaseTest._
//import datacloud.hbase._
import scala.io.Source


object LastfmFilling {
  def fromFile(data: File, dest: TableName, col_fam: String): Unit = {
  	val client = new HbaseClient(connection)
  	val admin = connection.getAdmin
  	
//    val conf = HBaseConfiguration.create()
//    val connection: Connection = ConnectionFactory.createConnection(conf)
  	
  	val table: Table = connection.getTable(dest)
	
  	for (line <- Source.fromFile(data).getLines){
  		var line2 =line.split(" ") // user0 track5 16 12 9  
  		
  		var rowkey = line2(0).concat(line2(1))
//    		println(rowkey)
  		
  		// la synchro est degueu, personne peut acceder a la table tant que yen a 1 qui y accede 
  		synchronized{
	  		val get = new Get(Bytes.toBytes(rowkey))
		    // Get the data from the table
		    var result = table.get(get)
		    
		    var local : Long = line2(2).toLong
		    var radio : Long = line2(3).toLong
				var skip  : Long = line2(4).toLong
	
		    
		    
	  		if (!result.isEmpty) {
	  			
	//    			var map = scala.collection.mutable.Map[(String, String),Array[Byte]]()
	//    			result.getFamilyMap("compteurs".getBytes).asScala.foreach(x=> println(Bytes.toString(x._1)+"      "+Bytes.toString(x._2)))
	    		result.getFamilyMap("compteurs".getBytes).asScala.foreach(c =>{
	      			var col = Bytes.toString(c._1)
				  		var valeur = c._2
	//		  				println("ccc = "+topic+"     "+Bytes.toInt(col)+" "+Bytes.toString(col))
	//		  				map +=(("compteurs",col)->valeur)	
		  				
	//		  				println (line2(2)+"   +++  "+Bytes.toString(valeur))
	
		  				if (col.equals("locallistening")){
		  					local = local + Bytes.toLong(valeur)
		  				}
	      			if (col.equals("radiolistening")){
		  					radio = radio + Bytes.toLong(valeur)
		  				}
	      			if (col.equals("skip")){
		  					skip = skip + Bytes.toLong(valeur)
		  				}
	      			
	      			
	  			})
	  			
	  		}
	  		// dans tout les cas j'ajoute la nouvelle valeur
	  		
	  		
	  		val put = new Put(Bytes.toBytes(rowkey))
				put.addColumn(Bytes.toBytes(col_fam), Bytes.toBytes("userid"), Bytes.toBytes(line2(0)))
				put.addColumn(Bytes.toBytes(col_fam), Bytes.toBytes("trackid"), Bytes.toBytes(line2(1)))
				put.addColumn(Bytes.toBytes(col_fam), Bytes.toBytes("locallistening"), Bytes.toBytes(local))
				put.addColumn(Bytes.toBytes(col_fam), Bytes.toBytes("radiolistening"), Bytes.toBytes(radio))
				put.addColumn(Bytes.toBytes(col_fam), Bytes.toBytes("skip"), Bytes.toBytes(skip))
	
				table.put(put)
			}
  			
  			
    	

    }
  }
}