package datacloud.hbase
//
//
import collection.JavaConverters._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.util.Bytes 
//import scala.collection.mutable._
//import scala.collection.immutable._
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder 





class HbaseClient(val connection: Connection) {
	
	
	
  def createTable(tn: TableName, colfams: String*): Unit = {
//  	println(colfams)
    val admin = connection.getAdmin()
    if (!admin.tableExists(tn)) {
      val tdb = TableDescriptorBuilder.newBuilder(tn)
      for (cf <- colfams){
      	tdb.setColumnFamily(
      		ColumnFamilyDescriptorBuilder.newBuilder(cf.getBytes).build()
      	)
      }
      val tableDesc = tdb.build()
      val descriptor = tableDesc.getTableName.getNamespaceAsString
      if (!connection.getAdmin.listNamespaces.contains(descriptor)){
				admin.createNamespace(NamespaceDescriptor.create(descriptor).build())
      }
//     	println("passe dans le if "+admin+"----------"+tableDesc.getTableName.getNamespaceAsString)

      admin.createTable(tableDesc)
    }
  }
  
  
  def deleteTable(tn : TableName) : Unit={
    val admin = connection.getAdmin()
    val tdb = TableDescriptorBuilder.newBuilder(tn)
  	val tableDesc = tdb.build()
  	val descriptor = tableDesc.getTableName.getNamespaceAsString
//  	println("------------------ici "+tableDesc.getTableName.getNamespaceAsString)
		

		if (admin.tableExists(tn)) {	
      admin.disableTable(tn)
      admin.deleteTable(tn)
    }
    if (connection.getAdmin.listNamespaces.contains(descriptor) && admin.listTableNamesByNamespace(descriptor).isEmpty){
    	admin.deleteNamespace(descriptor)
    }
  
  }
  
//  def toRow[E](e: E): Map[(String, String), Array[Byte]] 
//	}
  
  
  // write object
  // e est le truc a ecrire dans la table
  def writeObject[E](tn : TableName, rowkey : Array[Byte], e : E) (implicit funcConvert : E=>Map[(String,String),Array[Byte]])
//  									(implicit funcConvert : E=>Map[(String,String),Array[Byte]] = { e :E=>
//										  val row = scala.collection.mutable.Map[(String, String), Array[Byte]]()
//										  row += (("cf1", "e1") -> Bytes.toBytes(e.toString()))
//										  row.toMap
//  									})
		:Unit ={
		
  	val admin = connection.getAdmin
  	val table = connection.getTable(tn);
  	val rowData = funcConvert(e)
//  	println(rowData)
  	val put = new Put(rowkey)
  	val td = TableDescriptorBuilder.newBuilder(tn).build()

		rowData.foreach { case ((family, qualifier), value) =>
      put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), value)
//      println("j'ai ecris la val = "+family+"---"+qualifier+"---"+value) // columnfamily, column, valeur
        	
      // si la columnfamily existe pas, le cree 
      try{
      	  val cfd = ColumnFamilyDescriptorBuilder.newBuilder(family.getBytes).build()


			  	admin.addColumnFamily(tn, cfd)
      }catch{
      	case e : Exception =>{}
      }

    }  
  	
  	table.put(put)
  	table.close()
  
  }
  
  
  
  def readObject[E](tn : TableName, rowkey : Array[Byte])
  								(implicit funcConvert :Map[(String,String),Array[Byte]]=>E): Option[E] ={

//    	val cell = res.listCells()
//    	val it = res.listCells().iterator() 
//			while (it.hasNext()){
//      	println("-----"+it.next())
//			}
//    }
//  	while (res.advance().!=(null)){
//  		println(res)
//  	}

//  
    // Get the table
    val table = connection.getTable(tn)
    // Create a Get object with the row key
    val get = new Get(rowkey)
    // Get the data from the table
    val result = table.get(get)
//    println(result)
    
    // Convert the result to an object of type E
  	// recupere la liste des columns family
  	var list = new scala.collection.mutable.ListBuffer[String]()
  	result.getMap.asScala.foreach(c => {
			// dans le println, le premier c'est info et note (columnfamily) et 2eme c'est la colonne cad nom prenom age
//				println(Bytes.toString(c._1)+"----"+Bytes.toString(c._2.firstKey()))
//					println("etrange ------------>"+Bytes.toString(c._2.firstEntry().getValue.firstEntry().getKey))
//					println("etrange ------------>"+(c._2.firstEntry().getValue.firstEntry().getKey))
					list+=(Bytes.toString(c._1))
//					var map = ((Bytes.toString(c._1),Bytes.toString(c._2.firstKey())) -> c._2.firstEntry().getValue.firstEntry().getValue)
			}
		)
//		list.foreach(x => println("++++"+x))

		
  	var map = scala.collection.mutable.Map[(String, String),Array[Byte]]()
  	// pour chaque columnFamily 
		for (l:String <- list){
			// pour chaque element de la columnFamily
	    result.getFamilyMap(l.getBytes).asScala.foreach{ c=>
				val topic = Bytes.toString(c._1)
			  val col = c._2
//	  		println("ccc = "+topic+"     "+Bytes.toInt(col)+" "+Bytes.toString(col))
	//    	case (k :Array[Byte], v : java.util.NavigableMap[Array[Byte], java.util.NavigableMap[Long, Array[Byte]]]) =>
	//      (Bytes.toString(k), Bytes.toString(v.firstKey()))
	  		
	  		map +=((l,topic)->col)  	
//	  		println("Map = ((info,"+(topic)+")->"+Bytes.toString(col)+" ou "+Bytes.toInt(col)+" )")
	    	
	    }
	   }

 		val immutableMap = map.toMap

    // Close the table and connection
    table.close()
    return Some(funcConvert(immutableMap))
  }
//}
  
  

}