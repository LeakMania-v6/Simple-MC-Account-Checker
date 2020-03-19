package co.leakmania.checker;

import java.io.File;

import co.leakmania.checker.utils.Checker;
import co.leakmania.checker.utils.Logger;
import co.leakmania.checker.utils.WebClient;

public class Main {
	
	public String userID;
	
	public void test() {
		userID = new Main().userID();
	}
	
	public String userID() {
		return "test";
	}
	
	public static void main(String[] args) {
		File accounts = new File(Logger.prompt("Accounts Path"));
		File proxies = new File(Logger.prompt("Proxies Path"));
		new Checker(Integer.parseInt(Logger.prompt("Max Retries")), accounts, proxies);
	}

}
