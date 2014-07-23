package memcache;

import java.net.InetSocketAddress;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;

import org.junit.Test;

import com.b5m.base.common.utils.CollectionTools;

public class MemcacheOp {
	private static String ADDRESS = "10.10.100.14";
	private static int PORT = 11311;
	
	
	@Test
	public void testTestMemcache() throws Exception{
		XMemcachedClientBuilder builder = new XMemcachedClientBuilder(CollectionTools.newList(new InetSocketAddress("172.16.11.208", 11211)));
		MemcachedClient client = builder.build();
		System.out.println(client.get("good_20"));
	}
	
	
}
