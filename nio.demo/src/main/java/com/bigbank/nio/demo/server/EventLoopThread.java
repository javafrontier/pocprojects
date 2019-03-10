/**
 * 
 */
package com.bigbank.nio.demo.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Event loop thread which process selector events
 * 
 * @author javafrontier
 *
 */
public class EventLoopThread implements Runnable
{
	private static Logger logger = Logger.getLogger(EventLoopThread.class.getName());

	public void run()
	{
		while(true)
		{			
			try
			{
				// blocking call which wait for events
				DemoNioServer.socketChannelselector.select();
				// work on selected keys
				Iterator keys = DemoNioServer.socketChannelselector.selectedKeys().iterator();
				while(keys.hasNext())
				{
					SelectionKey key = (SelectionKey) keys.next();
					keys.remove();
					if(key.isAcceptable())
					{
						handleAccept(key);
					}
					
					if(key.isReadable())
					{
						handleRead(key);
					}
				}
			}
			catch (IOException e)
			{
				logger.logp(Level.SEVERE, EventLoopThread.class.getName(), "startEventLoop()", "Execption while processing selector events", e);
			}		
		}		
	}
	/**
	 * For incoming client connection , register the channel for read operation
	 * 
	 * @param key
	 */
	private static void handleAccept(SelectionKey key) 
	{
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		try
		{
			SocketChannel clientChannel = serverChannel.accept();
			// configure non-blocking mode and register this channel for read events
			clientChannel.configureBlocking(false);
			clientChannel.register(DemoNioServer.socketChannelselector, SelectionKey.OP_READ);
			logger.info("Connected to client @"+clientChannel.socket().getRemoteSocketAddress() );
		}
		catch (IOException e)
		{
			logger.logp(Level.SEVERE, EventLoopThread.class.getName(), "handleAccept", "Fail to get socket channel for sever channel accept event", e);
		}
	}
	
	/**
	 * Handle read events for selector
	 * 
	 * @param key
	 */
	private static void handleRead(SelectionKey key)
	{
		SocketChannel clientChannel = (SocketChannel) key.channel();
		// creating a buffer to read bytes sent by client
		ByteBuffer buffer = ByteBuffer.allocate(128);
		int numberOfBytesRead = 0;
		try
		{
			numberOfBytesRead = clientChannel.read(buffer);
			if(numberOfBytesRead == -1)
			{
				// connection closed , do cleanup
				logger.info("Connection Closed by:"+clientChannel.socket().getRemoteSocketAddress());
				clientChannel.close();
				key.cancel();
				return;		
			}
			byte[] data = new byte[numberOfBytesRead];
			System.arraycopy(buffer.array(), 0, data, 0, numberOfBytesRead);
			logger.info("Bytes Read :"+numberOfBytesRead+" Got :"+new String(data));
		}
		catch (IOException e)
		{
			logger.logp(Level.SEVERE, EventLoopThread.class.getName(), "handleRead", "Fail to read channel on read event", e);
		}		
	}	

}
