package co.leakmania.checker;

import java.io.File;
import java.util.ArrayList;

import co.leakmania.checker.utils.Checker;
import co.leakmania.checker.utils.FileEditor;
import co.leakmania.checker.utils.Logger;

public class Main {
	
	public static char splitChar = ':';
	private static ArrayList<String> proxies = new ArrayList<>();
	private static ArrayList<String> accounts = new ArrayList<>();
	private static int threads;
	private static int maxRetries;
	private static int timeout;
	
	public static void main(String[] args) {
		for (String account : new FileEditor(new File(Logger.prompt("Accounts"))).readAllLines().split("\n")) {
			accounts.add(account);
		}
		for (String proxy : new FileEditor(new File(Logger.prompt("Proxies"))).readAllLines().split("\n")) {
			proxies.add(proxy);
		}
		threads = Integer.parseInt(Logger.prompt("Threads"));
		maxRetries = Integer.parseInt(Logger.prompt("Max Retries"));
		timeout = Integer.parseInt(Logger.prompt("Timeout"));
		for (int i = 0; i != threads; i++) {
			ArrayList<String> threadAccounts = new ArrayList<>();
			for (int j = 0; j != (accounts.size() / threads); j++) {
				threadAccounts.add(accounts.get(i));
			}
			new Checker(maxRetries, timeout , threadAccounts, proxies);
		}
	}
	
}
