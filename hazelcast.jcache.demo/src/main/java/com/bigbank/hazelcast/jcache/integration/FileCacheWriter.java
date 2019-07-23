/**
 * 
 */
package com.bigbank.hazelcast.jcache.integration;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.cache.Cache.Entry;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;

/**
 * Writer used to support write-through cache , this is a simple demo writer which maintain cache enteries in a 
 * collection and write that collection on file system 
 * 
 * @author javafrontier
 *
 */
public class FileCacheWriter implements CacheWriter<Object, Object>, Serializable
{
	private static final long serialVersionUID = -6309803515554350233L;
	private static Logger logger = Logger.getLogger(FileCacheWriter.class.getName());
	String cacheFileName;	
	/**
	 * 
	 */
	public FileCacheWriter(String cacheName)
	{
		this.cacheFileName = "D:\\"+cacheName;
	}
	/* (non-Javadoc)
	 * @see javax.cache.integration.CacheWriter#write(javax.cache.Cache.Entry)
	 */
	@Override
	public void write(Entry<? extends Object, ? extends Object> entry) throws CacheWriterException
	{
		// check if file exist
		File cacheFile = new File(cacheFileName);
		if(cacheFile.exists())
		{
			// read existing collection add entry into that and write it again
			HashMap<Object,Object> cache = readCollectionFromCacheFile();			
			cache.put(entry.getKey(), entry.getValue());
			// write this back to file
			writeCollectionToCacheFile(cache);
		}
		else
		{
			HashMap<Object,Object> cache = new HashMap<>();
			cache.put(entry.getKey(),entry.getValue());
			writeCollectionToCacheFile(cache);
		}		
	}
	
	private void writeCollectionToCacheFile(HashMap<Object,Object> cache)
	{
		logger.info("Persisting collection");
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try
		{
			// write this back to file
			fileOutputStream = new FileOutputStream(cacheFileName);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(cache);
			objectOutputStream.flush();
			
		}
		catch (IOException e)
		{
			logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error accessing cache file", e);
		}
		finally 
		{
			try
			{
				objectOutputStream.close();
				fileOutputStream.close();
			}
			catch (IOException e)
			{
				logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error closing streams", e);
			}	
			
		}
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
		// check if file exist
		File cacheFile = new File(cacheFileName);
		if(cacheFile.exists())
		{
			logger.info("Removing cache entry for:"+key);
			// read existing collection add entry into that and write it again
			try(FileInputStream fileInputStream = new FileInputStream(cacheFile);
				FileOutputStream fileOutputStream = new FileOutputStream(cacheFile))
			{
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				HashMap<Object,Object> cache = (HashMap<Object, Object>) objectInputStream.readObject();
				fileInputStream.close();
				cache.remove(key);
				// write this back to file
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(cache);
				objectOutputStream.flush();
				objectOutputStream.close();				
			}
			catch(EOFException e)
			{
				logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Cache is empty, nothing to delete");
			}
			catch (IOException e)
			{
				logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error accessing cache file", e);
			}
			catch (ClassNotFoundException e)
			{
				logger.logp(Level.SEVERE, FileCacheWriter.class.getName(), "write()", "Error casting object out of cache file", e);
			}
		}
		
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
