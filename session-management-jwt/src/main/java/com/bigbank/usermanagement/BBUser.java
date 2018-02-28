package com.bigbank.usermanagement;

public class BBUser
{
	String username = "";
	String role = "";
	String licenses = "";
	String actions = "";	
	
	public String getUsername()
	{
		return username;
	}
	public void setUsername(String username)
	{
		this.username = username;
	}
	public String getRole()
	{
		return role;
	}
	public void setRole(String role)
	{
		this.role = role;
	}
	public String getLicenses()
	{
		return licenses;
	}
	public void setLicenses(String licenses)
	{
		this.licenses = licenses;
	}
	public String getActions()
	{
		return actions;
	}
	public void setActions(String actions)
	{
		this.actions = actions;
	}
	@Override
	public String toString()
	{
		return "BBUser [username=" + username + ", role=" + role + ", licenses=" + licenses + ", actions=" + actions + "]";
	}
	
	
}
