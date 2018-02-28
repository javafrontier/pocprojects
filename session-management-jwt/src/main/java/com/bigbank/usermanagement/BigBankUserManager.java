package com.bigbank.usermanagement;

import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;

import com.bigbank.jwt.BBTokenManager;

/**
 * Authenticate user against a data store, here it is just checking hard coded username password
 * 
 * @author javafrontier
 *
 */
public class BigBankUserManager
{
	private final static Logger LOGGER = Logger.getLogger(BigBankUserManager.class.getName());
	
	public static boolean authenticateUser(HttpServletRequest request) throws AuthenticationException
	{
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		LOGGER.fine("username:::"+username+" password:::"+password);
		if("admin".equals(username) && "secret".equals(password))
		{
			LOGGER.info("Authentication successful");
			return true;
		}
		else
		{
			throw new AuthenticationException("Incorrect credentials");
		}
	}
}
