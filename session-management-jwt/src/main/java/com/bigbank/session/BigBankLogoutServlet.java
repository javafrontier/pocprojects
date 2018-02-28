package com.bigbank.session;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bigbank.jwt.BBTokenManager;

/**
 * Servlet for processing Logout
 * 
 * @author javafrontier
 */
public class BigBankLogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BigBankLogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{		
		// remove cookie
		Cookie cookie = new Cookie(BBTokenManager.BBSESSIONCOOKIENAME, "");
		cookie.setPath("/BIGBANK");
		cookie.setMaxAge(0);
		cookie.setValue(null);
		response.addCookie(cookie);
		// redirect to login page
		response.sendRedirect(request.getContextPath()+"/login.jsp");
	}

}
