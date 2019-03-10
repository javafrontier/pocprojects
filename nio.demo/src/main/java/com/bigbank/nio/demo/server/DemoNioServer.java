/**
 * 
 */
package com.bigbank.nio.demo.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A demo server to which multiple clients can connect and send messages.
 * 
 * @author javafrontier
 *
 */
public class DemoNioServer
{
	private static Logger logger = Logger.getLogger(DemoNioServer.class.getName());
	static Selector socketChannelselector;
	
	public static void startServer(String address, int port)
	{
		try
		{			
			// create server socket
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			// configure non-blocking mode for the channel
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(address, port));
			
			// open selector
			socketChannelselector = Selector.open();
			// register this serverChannel for accept connection events
			serverChannel.register(socketChannelselector, SelectionKey.OP_ACCEPT);
			
			logger.info("Server Started @ "+address+":"+port);
			Thread eventLoop = new Thread(new EventLoopThread());
			eventLoop.start();
			logger.info("Continue with other tasks");
		}
		catch (IOException e)
		{
			logger.logp(Level.SEVERE, DemoNioServer.class.getName(), "startServer()", "Fail to initialize channel selector", e);
		}		
	}
	
	public static void main(String[] args)
	{
		startServer("127.0.0.1",9999);
	}
}
