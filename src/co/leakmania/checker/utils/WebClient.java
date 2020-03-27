package co.leakmania.checker.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import co.leakmania.checker.enums.Protocol;

public class WebClient {
	
	private String url;
	private String userAgent;
	private ArrayList<String> headers = new ArrayList<>();
	private Protocol protocol = Protocol.HTTP;
	private HttpURLConnection con;
	private String proxy;
	private int timeout;
	
	public WebClient() {
		this.timeout = 400;
	}
	
	public WebClient(Protocol protocol, int timeout) {
		this.timeout = timeout;
		try {
			if (protocol == Protocol.HTTPS) {
				try {
					TrustManager[] trustAllCerts = new TrustManager[] {
						new X509TrustManager() {
							public X509Certificate[] getAcceptedIssuers() {
								return null;
							}
							public void checkClientTrusted(X509Certificate[] certs, String authType) {}
							public void checkServerTrusted(X509Certificate[] certs, String authType) {}
						}
					};
					final SSLContext sc = SSLContext.getInstance("SSL");
					sc.init(null, trustAllCerts, new SecureRandom());
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebClient addHeaders(ArrayList<String> headers) {
		for (String header : headers) addHeader(header);
		return this;
	}
	
	public WebClient addHeader(String header) {
		this.headers.add(header); return this;
	}
	
	public ArrayList<String> getHeaders() {
		return this.headers;
	}

	public String get() throws Exception {
		con = (HttpURLConnection)new URL(this.url).openConnection();
		con.setConnectTimeout(this.timeout);
		if (headers.size() == 0) {
			con.addRequestProperty("User-Agent", userAgent);
		} else {
			for (String header : headers) {
				con.addRequestProperty(header.split(":")[0], header.split(":")[1]);
			}
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String lines = ""; String line;
		while ((line = br.readLine()) != null) lines += line;
		br.close();
		return lines;
	}
	
	public String post(String content, String contentType) throws Exception {
		con = (HttpURLConnection)new URL(this.url).openConnection();
		con.setConnectTimeout(this.timeout);
		con.setRequestMethod("POST");
		if (headers.size() == 0) {
			con.addRequestProperty("User-Agent", userAgent);
			con.setRequestProperty("Content-Type", contentType);
			con.setRequestProperty("Content-Length", Integer.toString(content.getBytes().length));
			con.setRequestProperty("Content-Language", "en-US");
		} else {
			for (String header : headers) {
				con.addRequestProperty(header.split(":")[0], header.split(":")[1]);
			}
		}
		con.setUseCaches(false); con.setDoInput(true); con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(content); wr.flush(); wr.close();
		BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String lines = ""; String line;
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
	
	public WebClient setURL(String url) {
		this.url = url;
		return this;
	}
	
	public String getURL() {
		return this.url;
	}
	
	public WebClient setuserAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}
	
	public String getDefaultUserAgent() {
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
