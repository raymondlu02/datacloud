package datacloud.hbase
import org.apache.hadoop.hbase.util.Bytes

case class Etudiant(val nom: String, val prenom: String, val age: Int, val notes: Map[String, Int]) {
  // définition de la classe Etudiant
}

object Etudiant {
  def toHbaseObject(e: Etudiant): Map[(String, String), Array[Byte]] = {
    val row = scala.collection.mutable.Map[(String, String), Array[Byte]]()
    row += (("info", "nom") -> e.nom.getBytes())
    row += (("info", "prenom") -> e.prenom.getBytes())
    row += (("info", "age") -> Bytes.toBytes(e.age.toInt))
    for ((matiere, note) <- e.notes) {
      row += (("notes", matiere) -> Bytes.toBytes(note.toInt))
    }
    row.toMap
  }
  
  def HbaseObjectToEtudiant(d: Map[(String, String), Array[Byte]]): Etudiant = {
    val nom = new String(d(("info", "nom")))
    val prenom = new String(d(("info", "prenom")))
//    val age = new String(d(("info", "age")))
  	val age = Bytes.toInt(d(("info","age")))
    val notes = scala.collection.mutable.Map[String, Int]()
    for (((cf, cq), value) <- d) {
      if (cf.equals("notes")) {
        notes += (cq -> Bytes.toInt(value))
      }
    }    
    new Etudiant(nom, prenom, age.toInt, notes.toMap)
  }
}