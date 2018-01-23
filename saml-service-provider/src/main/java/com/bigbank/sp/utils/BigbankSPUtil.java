package com.bigbank.sp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.validation.ValidationException;

/**
 * Class containing service provider utility methods like loading private key, public certificate etc.
 * 
 * @author Sandeep Singh
 *
 */
public class BigbankSPUtil
{
	private final static Logger LOGGER = Logger.getLogger(BigbankSPUtil.class.getName());	
	
	public static PrivateKey loadPrivateKey(String jksFileLocation,String jkspassword,String alias,String aliasPassword)
	{
		PrivateKey privateKey = null;
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		
		try(InputStream jksInputStream = classloader.getResourceAsStream(jksFileLocation))
		{
			KeyStore keyStore = KeyStore.getInstance("JKS");			
			keyStore.load(jksInputStream, jkspassword.toCharArray());
			privateKey = (PrivateKey) keyStore.getKey(alias, aliasPassword.toCharArray());
			LOGGER.info("Private Key:::"+privateKey.getAlgorithm());
			
		}
		catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e)
		{
			LOGGER.severe("Fail to load private key"+e.getMessage());
			e.printStackTrace();
		}
		return privateKey;
	}
	
	public static X509Certificate getCertificate(String pemFileLocation)
	{
		CertificateFactory factory;
		X509Certificate certificate = null;
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try(InputStream pemInputStream = classloader.getResourceAsStream(pemFileLocation))
		{
			factory = CertificateFactory.getInstance("X.509");
			certificate = (X509Certificate) factory.generateCertificate(pemInputStream);
		}
		catch (CertificateException | IOException e)
		{
			LOGGER.severe("Fail to get public certificate"+e.getMessage());
			e.printStackTrace();
		}

		LOGGER.info("certificate:::"+certificate.getSigAlgName());
		return certificate;
	}
	
	public static Signature buildSignature(PrivateKey privateKey,X509Certificate certificate)
	{
		Signature signature = new SignatureBuilder().buildObject();

		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		credential.setPrivateKey(privateKey);
		signature.setSigningCredential(credential);
		
		// set signature algorithm		
		signature.setSignatureAlgorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setCanonicalizationAlgorithm(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		
		LOGGER.info("signature created");
		return signature;
	}
	
	public static boolean validateSignature(Signature signature,String idpCertLocation)
	{
		boolean isValid = false;
		// load IDP signing cert
		CertificateFactory factory;
		X509Certificate certificate = null;
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try(InputStream idpCertInputStream = classloader.getResourceAsStream(idpCertLocation))
		{
			factory = CertificateFactory.getInstance("X.509");
			certificate = (X509Certificate) factory.generateCertificate(idpCertInputStream);
		}
		catch (CertificateException | IOException e)
		{
			LOGGER.severe("Fail to get public certificate"+e.getMessage());
			e.printStackTrace();
		}
		X509EncodedKeySpec encKeySpec = new X509EncodedKeySpec(certificate.getPublicKey().getEncoded());
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			LOGGER.info("keyFactory: " + keyFactory);

			// generate public key to validate signatures
			PublicKey publicKey = keyFactory.generatePublic(encKeySpec);
			LOGGER.info("publicKey: " + publicKey);
			// create credentials
			BasicX509Credential credential = new BasicX509Credential();

			// add public key value
			credential.setPublicKey(publicKey);
			
			SignatureValidator signatureValidator = new SignatureValidator(credential);
			
			signatureValidator.validate(signature);
			isValid = true;
			
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | ValidationException e) {
			e.printStackTrace();
		} 
		
		return isValid;		
	}
	
	
	
	public static String base64Encode(String orignal,String encoding)
	{
		String base64Encoded = "";
		try
		{
			base64Encoded = new String(Base64.encodeBase64(orignal.getBytes(encoding)), encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return base64Encoded;
	}
	
	public static String urlDecode(String originalMsg, String encoding)
	{
		String urlDecoded = null;
		try {
			urlDecoded = URLDecoder.decode(originalMsg, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return urlDecoded;
	}
	
	public static String base64Decode(final String originalMsg, boolean isCompressed, String encoding)
	{
		String retString = originalMsg;
		Base64 base64Decoder = new Base64();
		byte[] xmlBytes;
		try {
			xmlBytes = originalMsg.getBytes(encoding);
			// check if bytes are base64 encoded
			boolean isArrayByteBase64 = Base64.isArrayByteBase64(xmlBytes);
			if (isArrayByteBase64)
			{
				byte[] decodeByteArray = base64Decoder.decode(xmlBytes);
				if(!isCompressed)
				{
					retString = new String(decodeByteArray, encoding);
				}
				else
				{
					try
					{
						Inflater inflater = new Inflater(true);
						inflater.setInput(decodeByteArray);
						byte[] xmlMessageBytes = new byte[8000];
						int resultLength = inflater.inflate(xmlMessageBytes);
						LOGGER.info("resultLength: " + resultLength);
						if (!inflater.finished())
						{
							LOGGER.info("Not enough space " + "inflater couldnt finish");
							throw new RuntimeException("Not enough space " + "inflater couldnt finish");
						}
						inflater.end();
						retString = new String(xmlMessageBytes, 0, resultLength, encoding);
					}
					catch (DataFormatException e)
					{
						ByteArrayInputStream bais = new ByteArrayInputStream(decodeByteArray);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						InflaterInputStream iis = new InflaterInputStream(bais);
						byte[] buf = new byte[1024];
						int count = iis.read(buf);
						while (count != -1)
						{
							baos.write(buf, 0, count);
							count = iis.read(buf);
						}
						iis.close();
						retString = new String(baos.toByteArray(), Charset.defaultCharset());
					}
				}				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retString;
	}

	public static String base64Java8Decoder(final String originalMsg)
	{
		java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();  
		String decodedString = new String(decoder.decode(originalMsg)); 
		return decodedString;
	}
	public static void main(String[] args)
	{
		loadPrivateKey("bigBank.jks", "secret", "bigBank", "secret");
	}
}
