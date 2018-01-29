package com.bigbank.session;

import java.io.IOException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bigbank.usermanagement.BigBankUserManager;

/**
 * Authentication servlet validate entered credentials and set security data in session
 * 
 * @author javafrontier
 *
 */
public class BigBankAuthServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BigBankAuthServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			BigBankUserManager.authenticateUser(request);
			
			// redirect to landing page
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/stock/getStockQuotes.jsp"));
		}
		catch(AuthenticationException e)
		{
			// redirect to error page
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath()+"/error.html"));		
		}
	}

}
