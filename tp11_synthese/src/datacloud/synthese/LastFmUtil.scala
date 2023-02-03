package datacloud.synthese

import java.io.File
import java.nio.file.Files
import scala.io.Source


import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

import org.apache.hadoop.hbase.TableName


object LastFmUtil{
	def fillfromFile(file :File, tn : TableName, cf : String, sc : SparkContext):Unit = {
		
		for(line<- Source.fromFile(file).getLines){
			print(line)
			
		}
	}
  
}