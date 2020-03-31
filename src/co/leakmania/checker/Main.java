package co.leakmania.checker;

import java.io.File;
import java.util.ArrayList;

import co.leakmania.checker.utils.Checker;
import co.leakmania.checker.utils.FileEditor;
import co.leakmania.checker.utils.Logger;

public class Main {
	
	public static String splitChar = ":";
	public static boolean guiMode;
	private static ArrayList<String> proxies = new ArrayList<>();
	private static ArrayList<String> accounts = new ArrayList<>();
	private static int threads;
	private static int maxRetries;
	private static int timeout;
	
	public static void main(String[] args) {
		//if (args.length == 1 && args[0].equalsIgnoreCase("--nogui")) {
			for (String account : new FileEditor(new File(Logger.prompt("Accounts"))).readAllLines().split("\n")) {
				accounts.add(account);
			}
			for (String proxy : new FileEditor(new File(Logger.prompt("Proxies"))).readAllLines().split("\n")) {
				proxies.add(proxy);
			}
			threads = Integer.parseInt(Logger.prompt("Threads"));
			maxRetries = Integer.parseInt(Logger.prompt("Max Retries"));
			timeout = Integer.parseInt(Logger.prompt("Timeout"));
			int i = accounts.size();
			for (int j = 0; j != threads; j++) {
				ArrayList<String> threadAccounts = new ArrayList<>();
				for (int k = 0; k != (accounts.size() / threads); k++) {
					threadAccounts.add(accounts.get((i - 1)));
					i--;
				}
				new Checker(maxRetries, timeout, threadAccounts, proxies);
			}
			//Work in progress...
		/*} else {
			new MainForm().setVisible(true);
			return;
		}*/
	}
	
}
