package com.bigbank.sp;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Servlet to consume SAMLResponse token coming IDP
 *
 */
public class SAMLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static String samlTokenRequestParameter = "SAMLResponse";
	private final static String relayStateParameter = "RelayState";
    /**
     * Default constructor. 
     */
    public SAMLServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String samlResponse = request.getParameter(samlTokenRequestParameter);
		String relayState = request.getParameter(relayStateParameter);		

		boolean responseValid = false;
		if(null !=samlResponse )
		{
			// consume and validate saml response 
			if(BigbankSP.consumeSamlResponse(samlResponse, Endpoints.IDP_ENTITY_ID))
			{
				// redirect to protected resource
				responseValid = true;
			}	
		}
		if(responseValid)
		{
			// set session attribute
			request.getSession().setAttribute("athenticationFlag", true);
			response.sendRedirect(relayState);
		}
		else
		{
			response.sendRedirect("error.jsp");
		}	
	}

}
