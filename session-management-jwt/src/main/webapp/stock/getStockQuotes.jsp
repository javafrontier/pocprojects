<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Stock Quote</title>
</head>
<body onload="showSessionTrackingCookie()">
	<div id="dateId"></div>
	<br/>
	<form id="logoutForm" method="post" action="/BIGBANK/BigBankLogoutServlet">
	    <button type="submit" onclick="postLogoutForm">Logout</button>
	</form>	
	<br/>
	<table border="1" style="width:50%">
	  <tr>
		<th>Symbol</th>
		<th>OPEN</th> 
		<th>HIGH</th>
		<th>LOW</th>
		<th>CURRENT</th>
	  </tr>
	  <tr>
		<th>AMZN</th>
		<th>1395</th> 
		<th>1415</th>
		<th>1380</th>
		<th>1405</th>
	  </tr>
	  <tr>
		<th>GOOGL</th>
		<th>1170</th> 
		<th>1190</th>
		<th>1160</th>
		<th>1180</th>
	  </tr>
	  <tr>
		<th>FB</th>
		<th>195</th> 
		<th>200</th>
		<th>170</th>
		<th>190</th>
	  </tr>
	</table>
	<br/>
	<span>Session Tracked by Cookie : BB-SESSION-ID, Value : </span><div id="cookieValue"></div>
	
	<script>
		function showSessionTrackingCookie() 
		{
		    var name = "BB-SESSION-ID=";
		    var cookieVal = "";
		    var decodedCookie = decodeURIComponent(document.cookie);
		    var ca = decodedCookie.split(';');
		    for(var i = 0; i < ca.length; i++) {
		        var c = ca[i];
		        while (c.charAt(0) == ' ') {
		            c = c.substring(1);
		        }
		        if (c.indexOf(name) == 0) {
		        	cookieVal = c.substring(name.length, c.length);
		        }
		    }
		    var s= document.getElementById("cookieValue");
			s.innerHTML = cookieVal;
			// show date
			var today = new Date();
			var day = today.getDate();
			var month = today.getMonth();
			month++;
			var year = today.getFullYear();
			document.getElementById("dateId").innerHTML = "DATE : "+ day + "-" + month + "-" + year;
		}
		function postLogoutForm()
		{
			document.getElementById("logoutForm").submit();
		}
		
	</script>
</body>
</html>