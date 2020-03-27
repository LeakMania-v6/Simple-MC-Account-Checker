package co.leakmania.checker.utils;

import java.util.ArrayList;

public class Checker {
	
	public Checker(int retries, int timeout, ArrayList<String> accounts, ArrayList<String> proxies) {
		new Thread(() -> {
			String threadName = Thread.currentThread().getName();
			MojangAPI mapi = new MojangAPI(proxies, timeout);
			for (String s : accounts) {
				String credentials[] = s.split(":");
				for (int i = 0; i != retries; i++) {
					switch (i) {
						case 0:
							Logger.println("[" + threadName + "] Checking: " + s);
							break;
						default:
							Logger.println("[" + threadName + "] Rechecking (" + (i + 1) + "): " + s);
							break;
					}
					switch (mapi.setUsername(credentials[0]).setPassword(credentials[1]).auth()) {
						case WORKING:
							Logger.println("[" + threadName + "] " + s + " [WORKING]");
							break;
						case DEAD:
							Logger.println("[" + threadName + "] " + s + " [DEAD]");
							break;
					}
				}
			}
		}).start();
	}
	
}
