package datacloud.zookeeper.barrier;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;

import datacloud.zookeeper.ZkClient;




public class BoundedBarrier implements Watcher{

	private ZkClient zkclient;
	private String absPath;
	private int n;
	public static Object mutex = new Object();
	public static int crash = 0;
	
	public BoundedBarrier(ZkClient zkclient, String absPath, int n) throws KeeperException, InterruptedException {
		this.zkclient = zkclient;
		this.absPath = absPath;
		this.n = n;
		// si existe pas, creer
		if (this.zkclient.zk().exists(absPath, true) == null) {	
			this.zkclient.zk().create(absPath,intToBytes(n), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);	
		}
	}
	
	
	public synchronized void sync() {
		synchronized (mutex){
			try {
				// tant que existe, attend
				if (this.zkclient.zk().exists(absPath, this) != null && sizeBarrier() > 0) { // exist
						int nb=sizeBarrier();
						if (crash != 1) {
							nb = nb-1 ;
							byte[] b = intToBytes(nb);
							this.zkclient.zk().setData(absPath,b,-1);
						}else {
							crash =0;
						}
//						System.out.println(nb+" et id = "+zkclient.id());
						mutex.wait();					
				}					
			

			} catch (KeeperException | InterruptedException e1) {
				e1.printStackTrace();		
				crash = 1;
			}
		}
	}
	
	@Override
	public void process(WatchedEvent arg0) {
		try {
			synchronized(mutex) {
				if (arg0.getType() == EventType.NodeDataChanged) {
					if (sizeBarrier() == 0) {	
//						System.out.println(sizeBarrier()+ " --------id"+zkclient.id());
						this.zkclient.zk().delete(absPath, -1);
						mutex.notifyAll();
					}
				}
				
				if (arg0.getType() == EventType.NodeDeleted) {
					mutex.notifyAll();
				}

			}
		} catch (KeeperException | InterruptedException e) {
			
		}
	}
	
	
	public int sizeBarrier() throws KeeperException, InterruptedException {
		byte[] b =  this.zkclient.zk().getData(absPath,true, null);
		return convertByteArrayToInt(b);
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
