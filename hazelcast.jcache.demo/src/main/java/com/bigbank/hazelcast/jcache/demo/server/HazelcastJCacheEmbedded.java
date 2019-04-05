package com.bigbank.hazelcast.jcache.demo.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import com.bigbank.hazelcast.jcache.demo.HazelcastInstanceManager;
import com.hazelcast.cache.HazelcastCachingProvider;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.InMemoryFormat;

/**
 * 
 * Demo class which initialize caching provider in server/embedded mode and provide methods to interact with cache
 * 
 * @author javafrontier
 *
 */
public class HazelcastJCacheEmbedded
{
	private static CacheManager cacheManager = null;
	private static CachingProvider cachingProvider = null;	
	private static Logger logger = Logger.getLogger(HazelcastJCacheEmbedded.class.getName());
	private static String cacheName = "testCache";
	
	private HazelcastJCacheEmbedded()
	{
		// get caching provider in embedded mode
		cachingProvider = Caching.getCachingProvider("com.hazelcast.cache.impl.HazelcastServerCachingProvider");
		URI managerName = null;
		try
		{
			managerName = new URI("test-cache-manager");
		}
		catch (URISyntaxException e)
		{
			logger.logp(Level.SEVERE, HazelcastJCacheEmbedded.class.getName(), "HazelcastJCacheEmbedded()", "incorrect URI for cache manager", e);
		}
		// use existing instance 
		String instanceName = HazelcastInstanceManager.getHazelcastInstance().getName();
		Properties properties = new Properties();
		properties.setProperty(HazelcastCachingProvider.HAZELCAST_INSTANCE_NAME, instanceName);
		cacheManager = cachingProvider.getCacheManager(managerName, null, properties);
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
		if(cacheName.isEmpty())
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
	
	public void closeCacheManager()
	{
		logger.info("Closing cache manager");
		cacheManager.close();
		cachingProvider.close();
		
	}
	
	public static void main(String[] args)
	{
		HazelcastJCacheEmbedded jCacheEmbedded = new HazelcastJCacheEmbedded();
		logger.info("Cache Entry before put "+ jCacheEmbedded.get(cacheName, "hello"));
		jCacheEmbedded.put("testCache", "hello", "world");
		logger.info("Cache Entry after put "+ jCacheEmbedded.get(cacheName, "hello"));
		jCacheEmbedded.remove("testCache", "hello");
		logger.info("Cache Entry after remove "+ jCacheEmbedded.get(cacheName, "hello"));
		
		jCacheEmbedded.closeCacheManager();	
		HazelcastInstanceManager.getHazelcastInstance().shutdown();
	}
}
