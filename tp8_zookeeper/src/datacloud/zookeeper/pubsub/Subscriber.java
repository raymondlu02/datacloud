package datacloud.zookeeper.pubsub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;

import datacloud.zookeeper.ZkClient;

public class Subscriber extends ZkClient{
	
	private List<Pair<String, List<String>>> list ;
	private List<String> l1;
	public static Object mutex = new Object();

	public Subscriber(String name, String servers) throws IOException, KeeperException, InterruptedException {
		super(name, servers);
		list = new ArrayList<Pair<String,List<String>>>();
		
		if (this.zk().exists("/tmp", true) == null) {	
			this.zk().create("/tmp",intToBytes(0), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);	
		}
	}

	
	public void subscribe(String topic) {
		try {
			this.zk().exists("/"+topic,this);
//			System.out.println("sub a souscrit");
			for(Pair<String, List<String>> p : list) {
				// si il est deja dans la liste des souscrits
				if (("/"+topic).equals(p.getL())){
					return ;
				}
			}
			// sinon on l'ajoute 
			list.add(new Pair<String, List<String>>("/"+topic, new ArrayList<String>()));
			
		} catch (KeeperException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> received(String topic){
		
		// recherche le bon element de la liste
		for(Pair<String, List<String>> p : list) {
//			System.out.println("p ="+p);
			if (("/"+topic).equals(p.getL())){ // c'est le bon elem
				return p.getR();
			}
			
		} 
		return new ArrayList<String>();
	}
	
	 
	@Override
	public void process(WatchedEvent arg0) {
//		try {
//			System.out.println(arg0.getPath()+"		"+arg0.getType()+"		"+convertByteArrayToInt(this.zk().getData("/tmp",true, null))+"		"+arg0.getPath().equals("/tmp") );
//		} catch (KeeperException | InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
		if (!(arg0.getPath().equals("/tmp")) && (arg0.getType() == EventType.NodeDataChanged || arg0.getType() == EventType.NodeCreated)) {
			for(Pair<String, List<String>> p : list) {
//				System.out.println("p ="+p);
				if (arg0.getPath().equals(p.getL())){ // c'est le bon elem
					try {
						// ajoute dans la liste les data recu + abonnement 
//						System.out.println("Bytes en string"+new String(this.zk().getData("/"+topic, true, null), "UTF-8"));
						p.getR().add(new String(this.zk().getData(arg0.getPath(), true, null), "UTF-8"));
						

						if (this.zk().exists("/tmp", true) != null) {	
							this.zk().setData("/tmp", intToBytes(0),-1);
						}
						
					} catch (KeeperException | InterruptedException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} 
			
			
		}
		
	}
	
	
	public static class Pair<L,R> {
	    private L l;
	    private R r;
	    public Pair(L l, R r){
	        this.l = l;
	        this.r = r;
	    }
	    public L getL(){ return l; }
	    public R getR(){ return r; }
	    public void setL(L l){ this.l = l; }
	    public void setR(R r){ this.r = r; }
	}
	
	private int convertByteArrayToInt(byte[] data) {
	    if (data == null || data.length != 4) return 0x0;
	    // ----------
	    return (int)( // NOTE: type cast not necessary for int
	            (0xff & data[0]) << 24  |
	            (0xff & data[1]) << 16  |
	            (0xff & data[2]) << 8   |
	            (0xff & data[3]) << 0
	            );
	}
	
	private static byte[] intToBytes(final int data) {
	    return new byte[] {
	        (byte)((data >> 24) & 0xff),
	        (byte)((data >> 16) & 0xff),
	        (byte)((data >> 8) & 0xff),
	        (byte)((data >> 0) & 0xff),
	    };
	}

}
