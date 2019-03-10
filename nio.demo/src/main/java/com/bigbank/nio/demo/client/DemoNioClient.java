/**
 * 
 */
package com.bigbank.nio.demo.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author javafrontier
 *
 */
public class DemoNioClient
{
	private static Logger mLogger = Logger.getLogger(DemoNioClient.class.getName());
	public static void main(String[] args)
	{
		Runnable clientTask = new Runnable()
		{			
			public void run()
			{
				InetSocketAddress hostAddress = new InetSocketAddress("127.0.0.1",9999);
				try
				{
					SocketChannel socketChannel = SocketChannel.open(hostAddress);					
					mLogger.info(Thread.currentThread().getName() + "Client Started");
					String messageFromClient = Thread.currentThread().getName() + " Hello Server";					
		            byte [] messageFromClientInBytes = messageFromClient.getBytes();
		            ByteBuffer buffer = ByteBuffer.wrap(messageFromClientInBytes);
		            socketChannel.write(buffer);
		            buffer.clear();
		            TimeUnit.SECONDS.sleep(30);			        
			        socketChannel.close(); 					
				}
				catch (IOException e)
				{
					mLogger.logp(Level.SEVERE, DemoNioClient.class.getName(), "startClient", "Fail to open channel", e);
				}
				catch (InterruptedException e)
				{
					mLogger.logp(Level.SEVERE, DemoNioClient.class.getName(), "startClient", "Sleep interuppted", e);
				}				
			}
		};
		
		Thread clientThreadA = new Thread(clientTask,"Client ThreadA");
		clientThreadA.start();
		Thread clientThreadB = new Thread(clientTask,"Client ThreadB");
		clientThreadB.start();
	}
}
