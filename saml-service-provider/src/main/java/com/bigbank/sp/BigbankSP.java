package com.bigbank.sp;

import java.io.StringReader;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.SignableSAMLObject;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.ResponseUnmarshaller;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.bigbank.sp.utils.BigbankSPUtil;

public class BigbankSP
{
	private final static Logger LOGGER = Logger.getLogger(BigbankSP.class.getName());
	private final static String CHARACTERENCODING = "UTF-8";
	private static String jksFile = "bigBank.jks";
	private static String alias = "bigBank";
	private static String jksPassword ="secret";
	private static String aliasPassword ="secret";
	private static String pemFile = "bigBank.pem";
	private static String idpSigningCert = "idp-signing.crt";
	static 
	{
		// initialize openSAML library
		try
		{
			DefaultBootstrap.bootstrap();
		}
		catch (ConfigurationException e)
		{
			LOGGER.severe("Fail to initialize SAML Library");
			e.printStackTrace();
		}
	}
	
	public static String generateAuthNRequest(String serviceProviderEntityId,String acsUrl,String idpUrl)
	{

		String authNRequestSAMLToken = null;
		
		AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
		AuthnRequest authRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest","samlp");
	
		
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "samlp");
		// e.g. http://spserverfqdn/BIGBANK
		issuer.setValue(serviceProviderEntityId);
				
		authRequest.setID("SP_" + UUID.randomUUID().toString());
		authRequest.setForceAuthn(false);
		authRequest.setIsPassive(false);
		authRequest.setIssueInstant(new DateTime());
		authRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
		authRequest.setVersion(SAMLVersion.VERSION_20);
		authRequest.setIssuer(issuer);
		// e.g. "http://spserverfqdn/BIGBANK/SAMLServlet"
		authRequest.setAssertionConsumerServiceURL(acsUrl);
		// e.g. "https://idpserverfqdn/idp/profile/SAML2/POST/SSO"
		authRequest.setDestination(idpUrl);
		Signature signature = null;
		
		try
		{
			PrivateKey privateKey = BigbankSPUtil.loadPrivateKey(jksFile, jksPassword, alias, aliasPassword);
			X509Certificate certificate = BigbankSPUtil.getCertificate(pemFile);
			signature = BigbankSPUtil.buildSignature(privateKey, certificate);
			SignableSAMLObject signableSAMLObject = authRequest;
			signableSAMLObject.setSignature(signature);
			MarshallerFactory factory = org.opensaml.Configuration.getMarshallerFactory();
			Marshaller marshaller = factory.getMarshaller(signableSAMLObject);
			Element plain = marshaller.marshall(signableSAMLObject);
			Signer.signObject(signature);
			
			// Marshal Authentication request object to xml						
			authNRequestSAMLToken = XMLHelper.nodeToString(plain);
			authNRequestSAMLToken = BigbankSPUtil.base64Encode(authNRequestSAMLToken, CHARACTERENCODING);
			LOGGER.info("AuthNRequest creation successful");			
		}
		catch (Exception e)
		{
			LOGGER.severe("Fail to create signature");
			e.printStackTrace();
		}		
		
		return authNRequestSAMLToken;
	}
	
	public static boolean consumeSamlResponse(String samlResponse,String idpEntityId)
	{
		boolean isTokenValid = false;		
		// url decode 
		samlResponse = BigbankSPUtil.urlDecode(samlResponse, CHARACTERENCODING);

		// base64 decode
		samlResponse = BigbankSPUtil.base64Java8Decoder(samlResponse);
		
		// Get root element of xml and unmarshall it to Saml Response object
		BasicParserPool parser = new BasicParserPool();
		parser.setNamespaceAware(true);
		StringReader reader = new StringReader(samlResponse);
		Document document;
		try
		{
			document = parser.parse(reader);
			Element rootElement = document.getDocumentElement();
			ResponseUnmarshaller responseUnmarshaller = new ResponseUnmarshaller();
			Response response = (Response) responseUnmarshaller.unmarshall(rootElement);
			if (null != response.getStatus() && null != response.getStatus().getStatusCode())
			{
				// check if entity id is valid
				String entityIdInSamlToken = response.getIssuer().getDOM().getTextContent();				
				if(!idpEntityId.equalsIgnoreCase(entityIdInSamlToken))
				{
					LOGGER.severe("Entity Id doesn't match");
				}
				if(!BigbankSPUtil.validateSignature(response.getSignature(),idpSigningCert))
				{
					LOGGER.severe("Signature not valid");
				}
				isTokenValid = true;
			}
			else
			{
				LOGGER.severe("couldn't get status .. token is invalid");
			}			
		}
		catch (XMLParserException | UnmarshallingException e)
		{
			e.printStackTrace();
		}		
		return isTokenValid;		
	}
	
	
	public static void main(String[] args)
	{
		generateAuthNRequest("http://spserverfqdn/BIGBANK", "http://spserverfqdn/BIGBANK/SAMLServlet", "https://idpserverfqdn/idp/profile/SAML2/POST/SSO");
	}

}
