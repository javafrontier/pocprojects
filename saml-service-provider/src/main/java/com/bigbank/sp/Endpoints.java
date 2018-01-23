package com.bigbank.sp;

public class Endpoints 
{
	public static final String IDP_URL = "https://idpserverfqdn/idp/profile/SAML2/POST/SSO";
	public static final String IDP_ENTITY_ID = "https://idpserverfqdn/idp/shibboleth";
	public static final String SP_ENTITY_ID = "http://spserverfqdn/BIGBANKSP";
	public static final String ACS_URL = "http://spserverfqdn/BIGBANKSP/SAMLServlet";
	public static final String PROTECTED_RESOURCE_URL = "http://spserverfqdn/BIGBANKSP/protected/resource.jsp";	
}
