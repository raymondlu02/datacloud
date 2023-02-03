package datacloud.zookeeper.pubsub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import datacloud.zookeeper.ZkClient;
import java.util.Arrays;






public class Publisher extends ZkClient{

	public static Object mutex = new Object();
	public static int sync = 0;
	
	
	public Publisher(String name, String servers) throws IOException, KeeperException, InterruptedException {
		super(name, servers);
		if (this.zk().exists("/tmp", true) == null) {	
			this.zk().create("/tmp",intToBytes(0), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);	 // 0 = tt a ete lu
		}

	}
	
	public void publish(String topic, String message) {
		synchronized(mutex) {
			try {
				if(sync == 1) {
					mutex.wait(100);
				}
	//			System.out.println("string"+message+ "-------------- et "+"1".getBytes("UTF-8"));
				if (this.zk().exists("/"+topic,true)==null) {
					sync =1;
//					System.out.println("string en bytes[]"+message.getBytes("UTF-8"));
					if (this.zk().exists("/tmp", true) != null) {	
//						System.out.println("modif tmp");

						this.zk().setData("/tmp", intToBytes(1),-1);
					}
					this.zk().create("/"+topic,message.getBytes(StandardCharsets.UTF_8), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
					
//					System.out.println("attend ici la");
					mutex.wait(100);		
					sync =0;
				}else {
					sync =1;
	//				System.out.println("string en bytes[]"+message.getBytes("UTF-8").toString());
					if (this.zk().exists("/tmp", true) != null) {	
						this.zk().setData("/tmp", intToBytes(1),-1);
					}
					this.zk().setData("/"+topic, message.getBytes(StandardCharsets.UTF_8),-1);
					
//					System.out.println("attend ici la et la");
					mutex.wait(100);					
					sync = 0;
				}
				
				
			} catch (KeeperException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void process(WatchedEvent arg0) {

		if(arg0.getPath().equals("/tmp") && (arg0.getType() == EventType.NodeDataChanged || arg0.getType() == EventType.NodeCreated)) {
			synchronized(mutex) {

				try {
//					if (this.zk().exists("/tmp",true)==null) {
						if(	convertByteArrayToInt(this.zk().getData("/tmp",true, null))==0 && sync ==1) {
							mutex.notifyAll();
						}
//					}
				} catch (KeeperException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

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
