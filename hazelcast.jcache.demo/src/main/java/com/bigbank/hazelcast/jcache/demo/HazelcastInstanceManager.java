package com.bigbank.hazelcast.jcache.demo;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bigbank.mongoDB.MongoDBManager;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

/**
 * Manager class responsible for starting Hazelcast instance using configuration API.
 * 
 * @author javafrontier
 *
 */
public class HazelcastInstanceManager
{
	private static HazelcastInstance hzInstance = null;
	static Logger logger = Logger.getLogger(HazelcastInstanceManager.class.getName());
	private static Config prepareHazelcastConfiguration()
	{
		Config hazelcastClusterConfig = new Config();
		
		// specify cluster group and password
		hazelcastClusterConfig.getGroupConfig().setName("secretGroup");
		
		// create TCP/IP configuration for member discovery
		NetworkConfig hazelcastClusterNetworkConfig = new NetworkConfig();
		hazelcastClusterNetworkConfig.setPort(5701);
		hazelcastClusterNetworkConfig.setPortAutoIncrement(true);
		
		// set cluster member, for POC keeping only localhost
		JoinConfig joinConfig = hazelcastClusterNetworkConfig.getJoin();
		joinConfig.getTcpIpConfig().addMember("127.0.0.1");
		joinConfig.getTcpIpConfig().setEnabled(true);
		
		// disable other discovery options
		joinConfig.getAwsConfig().setEnabled(false);
		joinConfig.getMulticastConfig().setEnabled(false);
		
		// add life cycle listner , if you intend to perform any other initialization on Hazelcast instance startup		
		hazelcastClusterConfig.addListenerConfig(new ListenerConfig((LifecycleListener) event -> {
			// If the member is started and is part of cluster
			if (event.getState() == LifecycleState.STARTED) 
			{
				logger.info("Hazelcast Instance started");
			}
		}));
		
		return hazelcastClusterConfig;
	}
	private static HazelcastInstance createAndStartHazelcastInstance() throws Exception
	{
		Config config = prepareHazelcastConfiguration();
		if(config == null)
			throw new Exception("INVALID configuration");
		return Hazelcast.newHazelcastInstance(config);
		
	}
	public static HazelcastInstance getHazelcastInstance()
	{
		if(hzInstance == null)
		{
			try
			{
				hzInstance = HazelcastInstanceManager.createAndStartHazelcastInstance();
			}
			catch (Exception e)
			{
				logger.logp(Level.SEVERE, HazelcastInstanceManager.class.getName(), "main", "Fail to create Hazelcast Instance", e);
			}
		}
		return hzInstance;
	}
	public static void main(String[] args)
	{		
		HazelcastInstanceManager.getHazelcastInstance();
	}
}
