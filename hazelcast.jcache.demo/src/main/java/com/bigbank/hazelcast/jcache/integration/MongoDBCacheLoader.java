/**
 * 
 */
package com.bigbank.hazelcast.jcache.integration;

import java.io.Serializable;
import java.util.Map;
import java.util.logging.Logger;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

import org.bson.Document;

import com.bigbank.mongoDB.MongoDBManager;
import com.mongodb.client.FindIterable;

/**
 * Load cache data from MongoDB when read-through is enabled and cache entry is not present in in-memory cache
 * 
 * @author javafrontier
 *
 */
public class MongoDBCacheLoader implements CacheLoader<Object, Object>, Serializable
{
	private static final long serialVersionUID = -600022213859675875L;
	private static Logger logger = Logger.getLogger(MongoDBCacheLoader.class.getName());
	String cacheName;

	public MongoDBCacheLoader(String cacheName)
	{
		this.cacheName = cacheName;
	}
	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheLoader#load(java.lang.Object)
	 */
	@Override
	public Object load(Object key) throws CacheLoaderException
	{
		Object value = null;
		Document cacheRecord = new Document();
		cacheRecord.put("cache_name", cacheName);
		cacheRecord.put("cache_key", key);
		FindIterable<Document> results = MongoDBManager.getInstance().table().find(cacheRecord);
		for(Document doc : results)
		{
			value = doc.get("cache_value");
		}
		if(value == null)
			logger.info("Lookup failed for :"+cacheName+":"+key);
		return value;
	}

	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheLoader#loadAll(java.lang.Iterable)
	 */
	@Override
	public Map<Object, Object> loadAll(Iterable<? extends Object> keys) throws CacheLoaderException
	{
		throw new CacheLoaderException("Un-supported Operation");
	}

}
