package com.bigbank.sp.endpoint;

import static org.junit.Assert.fail;


import java.util.Base64;

import org.junit.Ignore;
import org.junit.Test;

import com.bigbank.sp.BigbankSP;
import com.bigbank.sp.Endpoints;

public class BigbankSPTest {

	@Ignore
	@Test
	public void testGenerateAuthNRequest() {
		fail("Not yet implemented");
	}
	@Ignore
	@Test
	public void testConsumeSamlResponse() {
		String samlResponse = "";
		if(!BigbankSP.consumeSamlResponse(samlResponse, Endpoints.IDP_ENTITY_ID))
		{
			fail("SAMLResponse not valid");
		}		
	}
	@Ignore
	@Test
	public void testbase64decode()
	{
		String samlResponse = "";
		Base64.Decoder decoder = Base64.getDecoder();  
		String dStr = new String(decoder.decode(samlResponse));  
        System.out.println("Decoded string: "+dStr); 		
	}
}
