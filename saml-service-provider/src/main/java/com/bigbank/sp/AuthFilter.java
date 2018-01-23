package com.bigbank.sp;

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
 * 
 * Authentication filter which will intercept incoming request and check if session exist
 *
 */
public class AuthFilter implements Filter {

    /**
     * Default constructor. 
     */
    public AuthFilter() {
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// check if session exist 
		HttpSession session = ((HttpServletRequest)request).getSession(false);
		if(null == session)
		{
			// session doesn't exist, redirect to login page
			((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() +"/loginWithIDP.jsp"+"?relayState="+Endpoints.PROTECTED_RESOURCE_URL);
			return;
		}
		else
		{
			// session exist, check of user authentication data
			boolean isUserAuthenticated = session.getAttribute("athenticationFlag") == null ? false : (boolean) session.getAttribute("athenticationFlag");
			if(!isUserAuthenticated)
			{
				((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + "/error.jsp");
				return;
			}
			else
			{
				//authentication information present for user
			}
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
