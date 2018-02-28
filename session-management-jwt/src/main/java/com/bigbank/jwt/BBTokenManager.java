package com.bigbank.jwt;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Utility class which has methods to work with json web token and session tracking cookie.
 * 
 * @author javafrontier
 *
 */
public class BBTokenManager
{
	private final static Logger LOGGER = Logger.getLogger(BBTokenManager.class.getName());
	public final static String BBSESSIONCOOKIENAME = "BB-SESSION-ID";
	private static byte[] key = null;
	
	static
	{
		try
		{
			key = "secret".getBytes("UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * This method in real enterprise app will call a down stream operation to get roles,licenses and actions assigned to user.
	 * We can keep additional data related to user which needs to be kept in session
	 * 
	 * @return Map of user claims
	 */
	private static Map<String,Object> getUserClaims(String username)
	{
		Map<String, Object> userClaims = new HashMap<>();
		userClaims.put("username", username);
		userClaims.put("role", "manager");
		userClaims.put("licenses", "licensesId1,licensesId2,licensesId3");
		userClaims.put("actions", "authorizePayment,viewReport,createUser");		
		return userClaims;		
	}
	
	/**
	 * Create JWT for the successfully authenticated user which have details like what will have information about his role,licenses and actions etc. 
	 * 
	 * @return JWT
	 * @throws Exception 
	 */
	public static String createSignedJWT(String username) throws Exception
	{
		String jwtString = null;
		try
		{
			// TODO : read it from database 
			jwtString = Jwts.builder().setClaims(getUserClaims(username)).signWith(SignatureAlgorithm.HS512, key).setIssuedAt(new Date()).compact();
			LOGGER.info(jwtString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception("Fail to create JWT");
		}
		return jwtString;
	}
	
	public static boolean validateJWT(String jwtString)
	{
		if(hasValue(jwtString))
		{		
			Claims body = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtString).getBody();
			if(body != null)
			{
				String username = body.get("username", String.class);
				String role = body.get("role", String.class);
				String licenses = body.get("licenses", String.class);
				String actions = body.get("actions", String.class);
				Date iat = body.getIssuedAt();
				LOGGER.info("username :::"+username);
				LOGGER.info("role :::"+role);
				LOGGER.info("licenses :::"+licenses);
				LOGGER.info("actions :::"+actions);
				LOGGER.info("Isued at :::"+iat);
				// TODO : validate against user store
				if(username.equals("admin"))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	
	public static String getCookieFromRequest(HttpServletRequest request,String cookieName)
	{
		String cookieValue = "";
		if (request != null)
		{
			Cookie[] cookies = request.getCookies();
			if (cookies != null)
			{
				for (int i = 0; i < cookies.length; i++)
				{
					Cookie cookie = cookies[i];
					if (cookie != null && cookie.getName().equals(cookieName))
					{
						cookieValue = cookie.getValue();
					}
				}
			}
		}
		return cookieValue;
	}
	
	private static boolean hasValue(String str)
	{
		if(str != null && str.length() > 0)
			return true;
		else
			return false;
	}
}
