package datacloud.scala.tpobject.bintree

sealed abstract class Tree[+A]
object EmptyTree extends Tree
case class Node[A](elem : A, left : Tree[A], right : Tree[A]) extends Tree


