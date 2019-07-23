/**
 * 
 */
package com.bigbank.hazelcast.jcache.integration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

/**
 * Class for loading cache from external source when read-through is enabled on the cache
 * 
 * @author javafrontier
 *
 */
public class FileCacheLoader implements CacheLoader<Object, Object>, Serializable
{
	private static final long serialVersionUID = -3609129213159513992L;
	private static Logger logger = Logger.getLogger(FileCacheLoader.class.getName());
	String cacheFileName;

	public FileCacheLoader(String cacheName)
	{
		this.cacheFileName = "D:\\"+cacheName;
	}
	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheLoader#load(java.lang.Object)
	 */
	@Override
	public Object load(Object key) throws CacheLoaderException
	{
		HashMap<Object,Object> cache = readCollectionFromCacheFile();
		return cache.get(key);
	}

	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheLoader#loadAll(java.lang.Iterable)
	 */
	@Override
	public Map<Object, Object> loadAll(Iterable<? extends Object> keys) throws CacheLoaderException
	{
		throw new CacheLoaderException("Un-supported Operation");
	}

	private HashMap<Object,Object> readCollectionFromCacheFile()
	{
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		HashMap<Object,Object> cache = null;
		try
		{
			fileInputStream = new FileInputStream(cacheFileName);
			objectInputStream = new ObjectInputStream(fileInputStream);
			cache = (HashMap<Object, Object>) objectInputStream.readObject();
		}
		catch (IOException e)
		{
			logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error accessing cache file", e);
		}
		catch (ClassNotFoundException e)
		{
			logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error casting object out of cache file", e);
		}
		finally 
		{
			try
			{
				fileInputStream.close();
				objectInputStream.close();
			}
			catch (IOException e)
			{
				logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Fail to close streams", e);
			}			
		}
		return cache;
	}
}
