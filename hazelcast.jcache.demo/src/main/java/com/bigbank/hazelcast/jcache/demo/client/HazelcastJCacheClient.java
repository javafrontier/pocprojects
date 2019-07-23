package com.bigbank.hazelcast.jcache.demo.client;

import java.net.URI;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import com.bigbank.hazelcast.jcache.demo.client.util.CacheUtil;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleEvent.LifecycleState;
import com.hazelcast.core.LifecycleListener;

/**
 * Hazelcast JCache client using which we will create Cache and perform operations.
 * 
 * @author javafrontier
 *
 */
public class HazelcastJCacheClient
{
	private static HazelcastInstance instance = null;
	private static CacheManager cacheManager = null;
	private static CachingProvider cachingProvider = null;
	private static Logger logger = Logger.getLogger(HazelcastJCacheClient.class.getName());
	private static String cacheName = "testCache";
	private static void createHazelcastClient()
	{
		// create client in order to connect to Hazelcast Cluster
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getGroupConfig().setName("secretGroup");
		clientConfig.addListenerConfig(new ListenerConfig(new LifecycleListener()
		{
			@Override
			public void stateChanged(final LifecycleEvent event)
			{
				if (event.getState() == LifecycleState.STARTED) 
				{
					logger.info("Hazelcast JCache Client instance started");					
				}
			}
		}));
		ClientNetworkConfig networkConfig = clientConfig.getNetworkConfig();
		List<String> addressList = new ArrayList<String>();
		addressList.add("127.0.0.1:5701");
		networkConfig.setAddresses(addressList);
		
		// configure retry
		networkConfig.setConnectionAttemptLimit(3);
		networkConfig.setConnectionAttemptPeriod(10000); // retry every 10 second
		networkConfig.setConnectionTimeout(10000); // 10 second
		
		// if you want to retry the operations in cache of failure then set following option
		networkConfig.setRedoOperation(true);
		instance = HazelcastClient.newHazelcastClient(clientConfig);
	}

	private HazelcastJCacheClient()
	{
		// prepare configuration for Hazelcast client
		createHazelcastClient();
		
		// get instance of caching provider
		cachingProvider = Caching.getCachingProvider();
		
		// specify URI for Caching manager
		URI managerName = null;
		try
		{
			managerName = new URI("my-cache-manager");
		}
		catch (URISyntaxException e)
		{
			logger.logp(Level.SEVERE, HazelcastJCacheClient.class.getName(), "HazelcastJCacheClient()", "incorrect URI for cache manager", e);
		}

		// specify hazelcast instance name 
		Properties properties = new Properties();
		properties.setProperty("hazelcast.instance.name", instance.getName());
		
		cacheManager = cachingProvider.getCacheManager(managerName,null,properties);		
	}
	
	/**
	 * Specifying only basic options keeping other configurations default e.g. default cache size of 10000 and eviction policy LRU
	 * 
	 * @return cache config object
	 */
	private CacheConfig<Object, Object> createCacheConfig()
	{
		// create a cache where both key and value will be stored as object object
		CacheConfig<Object, Object> configObj = (CacheConfig<Object, Object>) new CacheConfig<Object, Object>().setTypes(Object.class, Object.class);
		// enable statistics
		configObj.setStatisticsEnabled(true);
		// storing BINARY format
		configObj.setInMemoryFormat(InMemoryFormat.BINARY);
		return configObj;
		
	}
	
	public Cache<Object, Object> getOrCreateCacheByName(String cacheName)
	{
		if(CacheUtil.isEmpty(cacheName))
			throw new CacheException("Invalid Cache Name");
		Cache<Object,Object> cache = null;
		if(cacheManager != null && !cacheManager.isClosed())
		{
			cache = cacheManager.getCache(cacheName,Object.class,Object.class);
			if(cache == null)
			{
				// create cache
				cache = cacheManager.createCache(cacheName, createCacheConfig());
			}			
		}
		else
		{
			throw new IllegalStateException("Cache Manager is not available");
		}
		return cache;
	}
	
	public Object get(String cacheName,Object key)
	{
		Cache<Object,Object> cache = getOrCreateCacheByName(cacheName);
		return cache.get(key);
	}
	
	public void put(String cacheName,Object key, Object value)
	{
		Cache<Object, Object> cache = getOrCreateCacheByName(cacheName);
		cache.put(key, value);
	}
	
	public Boolean remove(String cacheName, Object key)
	{
		Cache<Object,Object> cache = getOrCreateCacheByName(cacheName);
		return cache.remove(key);
	}	
	
	public static void main(String[] args)
	{
		HazelcastJCacheClient client = new HazelcastJCacheClient();
		logger.info("Cache Entry before put "+ client.get(cacheName, "hello"));
		client.put("testCache", "hello", "world");
		logger.info("Cache Entry after put "+ client.get(cacheName, "hello"));
		client.remove("testCache", "hello");
		logger.info("Cache Entry after remove "+ client.get(cacheName, "hello"));
	}
}
