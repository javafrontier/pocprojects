/**
 * 
 */
package com.bigbank.hazelcast.jcache.integration;

import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;

import org.bson.Document;

import com.bigbank.mongoDB.MongoDBManager;

/**
 * Writer used to support write-through cache , this is a simple demo writer which maintain cache enteries in a 
 * collection and write that collection to a NOSQL database MongoDB 
 * 
 * @author javafrontier
 *
 */
public class MongoDBCacheWriter implements CacheWriter<Object, Object>, Serializable
{
	private static final long serialVersionUID = 9192741012438627143L;
	private static Logger logger = Logger.getLogger(MongoDBCacheWriter.class.getName());
	private String cacheName ;
	public MongoDBCacheWriter(String cacheName)
	{
		this.cacheName = cacheName;
	}
	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheWriter#write(javax.cache.Cache.Entry)
	 */
	@Override
	public void write(Entry<? extends Object, ? extends Object> entry) throws CacheWriterException
	{
		// get the collection to write to
		Document cacheRecord = new Document();
		cacheRecord.put("cache_name", cacheName);
		cacheRecord.put("cache_key", entry.getKey());
		cacheRecord.put("cache_value", entry.getValue());
		cacheRecord.put("last_modified", System.currentTimeMillis());
		MongoDBManager.getInstance().table().insertOne(cacheRecord);
		logger.info("Record added:"+cacheRecord);
	}

	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheWriter#writeAll(java.util.Collection)
	 */
	@Override
	public void writeAll(Collection<Entry<? extends Object, ? extends Object>> entries) throws CacheWriterException
	{
		throw new CacheWriterException("Un-supported Operation");
		
	}

	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheWriter#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object key) throws CacheWriterException
	{
		Document cacheRecord = new Document();
		cacheRecord.put("cache_name", cacheName);
		cacheRecord.put("cache_key", key);
		MongoDBManager.getInstance().table().deleteOne(cacheRecord);
		logger.info("Record deleted:"+cacheRecord);
		
	}

	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheWriter#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection<?> keys) throws CacheWriterException
	{
		throw new CacheWriterException("Un-supported Operation");		
	}

}
