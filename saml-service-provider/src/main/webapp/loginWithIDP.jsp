<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.bigbank.sp.*" %>
    <%
	    String authNRequest = null;
		String relayState = request.getParameter("relayState");
		String idpUrl = Endpoints.IDP_URL;
		String spEntityId = Endpoints.SP_ENTITY_ID;
		String acsUrl = Endpoints.ACS_URL;
		authNRequest = BigbankSP.generateAuthNRequest(spEntityId, acsUrl, idpUrl);
		
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Login via Shibboleth IDP</title>
	</head>

	<body>
		<form id="authNRequestForm" method="post" action="<%=idpUrl%>">
			<input type="hidden" name="SAMLRequest" value="<%=authNRequest%>">
			<input type="hidden" name="RelayState" value="<%=relayState%>" />
		</form>
		<script type="text/javascript">
			function postAuthNRequest()
			{
				document.getElementById("authNRequestForm").submit();
			}		
			postAuthNRequest();
		</script>
	</body>

</html>