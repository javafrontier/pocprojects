package com.bigbank.crypto;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.KmsClients;
import com.google.crypto.tink.aead.AeadConfig;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.integration.awskms.AwsKmsClient;
import com.google.crypto.tink.proto.KeyTemplate;


/**
 * Manager class responsbile for initializing Tink and expose methods for encryption & decryption
 *  
 * @author javafrontier
 *
 */
public class EnvelopeEncryptionManager
{
	private static Logger mLogger = Logger.getLogger("com.bigbank.crypto");
	private static volatile EnvelopeEncryptionManager instance;
	private Aead envelopEncryptor;
	private Aead kekEncryptor;
	private static AwsKmsClient awsKmsClient = null;
	private KeysetHandle keysetHandle;
	
	public static final KeyTemplate dekTemplate = AeadKeyTemplates.AES128_GCM;
	public static final String keysetFile = "encrypted_keyset.json";
	
	// prevent initialization from outside
	private EnvelopeEncryptionManager()
	{		
		try
		{
			AeadConfig.register();
			awsKmsClient = new AwsKmsClient();
			awsKmsClient.withDefaultCredentials();
			KmsClients.add(awsKmsClient);
			
			// initialize KEK encryptor
			kekEncryptor = awsKmsClient.getAead(getKekUri());
			
			// load if key material is already present else load it
			File file = new File(keysetFile);
			if(file != null && file.exists())
			{
				// load encrypted keyset
				keysetHandle = KeysetHandle.read(JsonKeysetReader.withFile(file), kekEncryptor);
			}
			else
			{
				// create key material & persist
				keysetHandle = KeysetHandle.generateNew(getEnvelopeKeyTemplate(getKekUri()));
				keysetHandle.write(JsonKeysetWriter.withFile(file), kekEncryptor);
			}
			envelopEncryptor = AeadFactory.getPrimitive(keysetHandle);
		}
		catch (GeneralSecurityException | IOException e)
		{
			mLogger.logp(Level.SEVERE, EnvelopeEncryptionManager.class.getName(), "EnvelopeEncryptionManager()", "Fail to initialize encryption platform", e);
		}
	}
	
	public static EnvelopeEncryptionManager getInstance()
	{
		// using double check idiom for initialization 
		if(instance == null)
		{
			synchronized (EnvelopeEncryptionManager.class)
			{
				if(instance == null)
				{
					instance = new EnvelopeEncryptionManager();
				}
			}
		}
		return instance;
	}
	
	public String getKekUri()
	{
		// TODO : Hard coded, fetch it from database 
		return "aws-kms://arn:aws:kms:us-east-2:<AWSAccountId>:key/6eeed6e5-c84d-4f81-8f35-2070a31dd5d0";
	}
	
	public static KeyTemplate getEnvelopeKeyTemplate(String cmkArn)
	{
		return AeadKeyTemplates.createKmsEnvelopeAeadKeyTemplate(cmkArn,dekTemplate);
	}
	
	public byte[] performEnvelopeEncryption(byte[] plaintext, byte[] associatedData) throws GeneralSecurityException
	{
		return envelopEncryptor.encrypt(plaintext, associatedData);
	}
	
	public byte[] performEnvelopeDecryption(byte[] ciphertext, byte[] associatedData) throws GeneralSecurityException
	{
		return envelopEncryptor.decrypt(ciphertext, associatedData);
	}
	

	public static void main(String[] args) throws GeneralSecurityException
	{
		EnvelopeEncryptionManager instance = EnvelopeEncryptionManager.getInstance();
		if(instance != null)
		{
			mLogger.log(Level.FINEST,"Encryption Platform initialized");
			byte[] cipherBytes = instance.performEnvelopeEncryption("Hello World!".getBytes(), "AA".getBytes());
			mLogger.log(Level.INFO, "Encrypted Base64 result:"+Base64.getEncoder().encodeToString(cipherBytes));
			byte[] plaintextBytes = instance.performEnvelopeDecryption(cipherBytes, "AA".getBytes());
			mLogger.log(Level.INFO, "Decryption result:"+new String(plaintextBytes));
		}
	}
}
