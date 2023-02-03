package datacloud.zookeeper.membership;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import datacloud.zookeeper.ZkClient;

public class ClientMembership extends ZkClient{
	private List<String> member;

	public ClientMembership(String name, String servers) throws IOException, KeeperException, InterruptedException {
		super(name, servers);
		member = this.zk().getChildren("/ids", true);
		
	}

	@Override
	public void process(WatchedEvent arg0) {
//		EventType et = new EventType("NodeCreated");
		if (arg0.getType() == EventType.NodeChildrenChanged) {
			try {
				member = (this.zk().getChildren("/ids", true));
			} catch (KeeperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public List<String> getMembers(){
		return member;
		
	}

}
