package co.leakmania.checker.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.leakmania.checker.enums.Protocol;

public class WebClient {
	
	private String URL;
	private String UserAgent;
	private Protocol protocol = Protocol.HTTP;
	private HttpURLConnection con;
	private String proxy;
	private int timeout;
	
	public WebClient() {
		new WebClient(Protocol.HTTP, 400);
	}
	
	public WebClient(Protocol protocol, int timeout) {
		this.timeout = timeout;
		this.protocol = protocol;
		if (protocol == Protocol.HTTPS) {
			try {
				TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}
						public void checkClientTrusted(X509Certificate[] certs, String authType) {}
						public void checkServerTrusted(X509Certificate[] certs, String authType) {}
					}
				};
				final SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String get() throws Exception {
		con = (HttpURLConnection)new URL(this.URL).openConnection();
		con.setConnectTimeout(this.timeout);
		con.addRequestProperty("User-Agent", UserAgent);
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String lines = "";
		String line;
		while ((line = br.readLine()) != null) lines += line;
		br.close();
		return lines;
	}
	
	public String post(String content, String contentType) throws Exception {
		con = (HttpURLConnection)new URL(this.URL).openConnection();
		con.setConnectTimeout(this.timeout);
		con.setRequestMethod("POST");
		con.addRequestProperty("User-Agent", UserAgent);
		con.setRequestProperty("Content-Type", contentType);
		con.setRequestProperty("Content-Length", Integer.toString(content.getBytes().length));
		con.setRequestProperty("Content-Language", "en-US");
		con.setUseCaches (false);
		con.setDoInput(true);
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(content);
		wr.flush();
		wr.close();
		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String lines = "";
		String line;
		while ((line = rd.readLine()) != null) lines += line;
		rd.close();
		return lines;
	}
	
	public WebClient setProtocol(Protocol protocol) {
		this.protocol = protocol;
		return this;
	}
	
	public Protocol getProtocol() {
		return this.protocol;
	}
	
	public WebClient setURL(String URL) {
		this.URL = URL;
		return this;
	}
	
	public String getURL() {
		return this.URL;
	}
	
	public WebClient setUserAgent(String UserAgent) {
		this.UserAgent = UserAgent;
		return this;
	}
	
	public String defaultUserAgent() {
		return "MC-Checker/1.0";
	}
	
	public WebClient setProxy(String proxy) {
		this.proxy = proxy;
		if (this.proxy != null) {
			System.setProperty("java.net.useSystemProxies", "true");
			System.setProperty("http.proxyHost", proxy.split(":")[0]);
			System.setProperty("http.proxyPort", proxy.split(":")[1]);
			System.setProperty("https.proxyHost", proxy.split(":")[0]);
			System.setProperty("https.proxyPort", proxy.split(":")[1]);
		}
		return this;
	}
	
	public String getProxy() {
		return this.proxy;
	}

}
