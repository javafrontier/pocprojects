package com.bigbank.mongoDB;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Initialize MongoDB , create database and provide reference to table
 * 
 * @author javafrontier
 *
 */
public class MongoDBManager
{
	private static MongoClient mongoClient = null;
	private MongoDatabase cacheDatabase = null;
	private MongoCollection<Document> cacheTable = null;
	private static MongoDBManager instance = null;
	private MongoDBManager()
	{
		// create database client
    	mongoClient = MongoClients.create("mongodb://localhost:27017");
    	// get or create database
    	cacheDatabase = mongoClient.getDatabase("cacheDB");
    	// get or create collection(table)
    	cacheTable = cacheDatabase.getCollection("cacheCollection");
	}
	
	public static MongoDBManager getInstance()
	{
		if(instance == null)
		{
			synchronized (MongoDBManager.class)
			{
				if(instance == null)
				{
					instance = new MongoDBManager();
				}
			}
		}
		return instance;
	}
	
	public MongoCollection<Document> table()
	{
		return cacheTable;
	}
	
	public void shutdownClient()
	{
		if(mongoClient != null)
		{
			mongoClient.close();
		}
	}
}
