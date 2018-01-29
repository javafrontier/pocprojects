package com.bigbank.session;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Validate session for incoming requests, redirect to login page if it is a new session
 * 
 * @author javafrontier
 *
 */
public class BigBankSessionValidator implements Filter {
	private final static Logger LOGGER = Logger.getLogger(BigBankSessionValidator.class.getName());
    /**
     * Default constructor. 
     */
    public BigBankSessionValidator() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession session = httpRequest.getSession();
		// creating session for the first time
		if(session.isNew())
		{
			// send to login jsp
			httpResponse.sendRedirect(httpResponse.encodeRedirectURL(httpRequest.getContextPath()+"/login.jsp"));
			return;
		}
		else
		{
			String loggedInUser = String.valueOf(((HttpServletRequest)request).getSession().getAttribute("loggedInUser"));
			LOGGER.info("Logged In User:::"+loggedInUser);
			if(loggedInUser.isEmpty() || loggedInUser.equalsIgnoreCase("null"))
			{
				// redirect to error page
			}
			chain.doFilter(request, response);
		}		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
