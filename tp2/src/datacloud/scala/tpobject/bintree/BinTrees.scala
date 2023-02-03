package datacloud.scala.tpobject.bintree


case object BinTrees {
  
  def contains(tree : IntTree, elem: Int) : Boolean = {
    tree match{
      case EmptyIntTree  => false;
      case NodeInt(e,l,r)  => if (e == elem){
        return true;
      }else{
        return contains(l, elem) | contains(r,elem);
      }
    }
    
    return false;
  }
  
  def size(tree : IntTree) : Int = {
    tree match{
      case EmptyIntTree  => return 0;
      case NodeInt(e,l,r)  => return 1 + size(l) + size(r)
        
    }
  }
  

  
  def insert(tree : IntTree, elem : Int) : IntTree = {
//    println ("au debut tree = " +tree)
    tree match{
      case EmptyIntTree  =>  return NodeInt(elem,EmptyIntTree,EmptyIntTree)
      case NodeInt(e,l,r) => 
        if (size(l) == size(r)){
        
          r match{
                
            case EmptyIntTree => tree match{
                                    case NodeInt(e,l,r) => NodeInt(e, l, NodeInt(elem,EmptyIntTree,EmptyIntTree))
                                    case EmptyIntTree => return NodeInt(elem,EmptyIntTree,EmptyIntTree)
                                  }

            case NodeInt(_,_,_)   => NodeInt(e,l, insert(r,elem)); 
          }

        }else{


          l match{
            case EmptyIntTree =>  tree match{
                                    case NodeInt(e,l,r) => NodeInt(e, NodeInt(elem,EmptyIntTree,EmptyIntTree), r)
                                    case EmptyIntTree => return NodeInt(elem,EmptyIntTree,EmptyIntTree)
                                  }
            case NodeInt(e2,l2,r2)   => NodeInt(e, insert(l,elem), r); 
          }
       } 
     }
   }
  
  // Fonction de la partie 2 Type Generique---------------------------------------------------------------------
  
  
  
  def contains[A](tree : Tree[A], elem: A) : Boolean = {
    tree match{
      case EmptyTree  => false;
      case Node(e,l,r)  => if (e == elem){
        return true;
      }else{
        return contains(l, elem) | contains(r,elem);
      }
    }
    
    return false;
  }
  
  def size[A](tree : Tree[A]) : Int = {
    tree match{
      case EmptyTree  => return 0;
      case Node(e,l,r)  => return 1 + size(l) + size(r)
        
    }
  }
  
  def insert[A](tree : Tree[A], elem : A) : Tree[A] = {
//    println ("au debut tree = " +tree)
    tree match{
      case EmptyTree  =>  return Node(elem,EmptyTree,EmptyTree)
      case Node(e,l,r) => 
        if (size(l) == size(r)){
        
          r match{
                
            case EmptyTree => tree match{
                                    case Node(e,l,r) => Node(e, l, Node[A](elem,EmptyTree,EmptyTree))
                                    case EmptyTree => return Node(elem,EmptyTree,EmptyTree)
                                  }

            case Node(_,_,_)   => Node(e,l, insert(r,elem)); 
          }

        }else{


          l match{
            case EmptyTree =>  tree match{
                                    case Node(e,l,r) => Node(e, Node(elem,EmptyTree,EmptyTree), r)
                                    case EmptyTree => return Node(elem,EmptyTree,EmptyTree)
                                  }
            case Node(e2,l2,r2)   => Node(e, insert(l,elem), r); 
          }
       } 
     }
   }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}