package datacloud.zookeeper.barrier;

import static datacloud.zookeeper.util.ConfConst.EMPTY_CONTENT;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;

import datacloud.zookeeper.ZkClient;

public class SimpleBarrier implements Watcher{
	
	private ZkClient zkclient;
	private String absPath;
	
	public SimpleBarrier(ZkClient zkclient, String absPath) throws KeeperException, InterruptedException {
		this.zkclient = zkclient;
		this.absPath = absPath;
//		if (this.zkclient.zid() == null) { // exist
		// si existe pas, creer
		if (this.zkclient.zk().exists(absPath, false) == null) {	
			this.zkclient.zk().create(absPath, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);			
		}
	}
	
	public void sync() {
		try {
			// tant que existe, attend
			if (this.zkclient.zk().exists(absPath, this) != null ) { // exist
				synchronized (this){
					wait();
					this.zkclient.zk().create(absPath, EMPTY_CONTENT, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
				}					
				

			}// peut etre un else sleep(5)
		} catch (KeeperException | InterruptedException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void process(WatchedEvent arg0) {
		synchronized(this) {
			if (arg0.getPath().equals(absPath)) {				
				notifyAll();
			}
		}

	}
}
