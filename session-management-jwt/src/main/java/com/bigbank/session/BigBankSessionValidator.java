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

import com.bigbank.jwt.BBTokenManager;

/**
 * Validate session for incoming requests, redirect to login page if a valid JWT token is not present in cookie
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
		
		// check if request is caring a valid jwt 
		String jwt = BBTokenManager.getCookieFromRequest(httpRequest, BBTokenManager.BBSESSIONCOOKIENAME);
		
		if(BBTokenManager.validateJWT(jwt))
		{
			LOGGER.info("User is logged in");
			chain.doFilter(request, response);
		}
		else
		{
			// send to login jsp
			httpResponse.sendRedirect(httpResponse.encodeRedirectURL(httpRequest.getContextPath()+"/login.jsp"));
			return;
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
