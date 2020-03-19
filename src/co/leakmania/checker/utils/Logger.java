package co.leakmania.checker.utils;

import java.util.Scanner;

public class Logger {
	
	@SuppressWarnings("resource")
	public static String prompt(String msg) {
		System.out.print('\r' + msg + ": ");
		return new Scanner(System.in).next();
	}
	
	public static void print(String msg) {
		System.out.print('\r' + msg);
	}
	
	public static void println() {
		println("");
	}
	
	public static void println(String msg) {
		System.out.println(msg);
	}
	
}
