package co.leakmania.checker.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import co.leakmania.checker.enums.Lang;

public class FileEditor {
	
	private String newLine = System.getProperty("line.separator");
	private Lang lang;
	private File file;
	
	public FileEditor(File file, Lang lang) {
		this.file = file; this.lang = lang;
	}
	
	public FileEditor(File file) {
		this.file = file;
	}
	
	public String getResourceFileAsString(String path) throws IOException {
		String str = null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(path), "UTF-8"));
		for (int c = br.read(); c != -1; c = br.read()) sb.append((char)c);
			str += sb.toString();
		return str;
	}

	public void addLines(ArrayList<String> results) {
		for (String line : results) addLine(line);
	}

	public synchronized boolean addLine(String line)  {
	    PrintWriter printWriter = null;
	    try {
	        if (!this.file.exists()) this.file.createNewFile();
	        printWriter = new PrintWriter(new FileOutputStream(this.file.getAbsolutePath(), true));
	        printWriter.write(line + newLine);
            printWriter.flush();
            printWriter.close();
	        return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean SetAttributes(String os, String path, int attribute, boolean value) {
		try {
			switch (os) {
			case "windows":
				Path windowsFile = Paths.get(path);
				if (attribute <= 3) {
					String[] Attributes = {
						"dos:archive",
						"dos:hidden",
						"dos:readonly",
						"dos:system"
					};
					Files.setAttribute(windowsFile, Attributes[attribute], value, LinkOption.NOFOLLOW_LINKS);
				} else {
					return false;
				}
				break;
			case "linux":
				File linuxFile = new File(path);
				if (attribute <= 2) {
					switch (attribute) {
					case 0:
						linuxFile.setExecutable(value);
						break;
					case 1:
						linuxFile.setReadable(value);
						break;
					case 2:
						linuxFile.setWritable(value);
						break;
					}
				}
				break;
			}
			return true;
		} catch (Exception e) {
			String privileges = null;
			if (os.contains("windows")) {
				privileges = "admin";
			} else if (os.contains("linux")) {
				privileges = "root";
			}
			System.out.println("This program needs " + privileges + " privileges !");
			e.printStackTrace();
			return false;
		}
	}	
	
	public String readAllLines() {
		try {
			Scanner sc = new Scanner(this.file);
			String lines = "";
			while (sc.hasNextLine()) lines += sc.nextLine() + "\n";
			sc.close();
			return lines;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void createNewFile() {
		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getStartupPath() {
		return System.getProperty("user.dir");
	}
	
	public boolean contains(String word) {
		String lines = new FileEditor(this.file).readAllLines();
		if (lines != null
			&& !lines.equalsIgnoreCase(null)
			&& !lines.equalsIgnoreCase("")
			&& lines.contains(word)) {
			return true;
		}
		return false;
	}
	
	public ArrayList<String> walk(String filter) {
		ArrayList<String> result = new ArrayList<>();
		List<String> files = new ArrayList<>();
		try (Stream<Path> walk = Files.walk(Paths.get(this.file.getAbsolutePath()))) {
			files = walk.map(x -> x.toString()).filter(f -> f.endsWith(filter)).collect(Collectors.toList());
			files.forEach(x -> result.add(x));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public void extractResource(String internalPath) {
		try {
			if (!this.file.exists()) {
			     InputStream is = (getClass().getResourceAsStream(internalPath));
			     Files.copy(is, this.file.getAbsoluteFile().toPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public String getFileSize(String filePath) {
    	char lastChar;
    	if (this.lang != null && this.lang == Lang.fr_FR) {
    		lastChar = 'o';
    	} else {
    		lastChar = 'B';
    	}
    	long size = new File(filePath).length();
        String s = "";
        double kb = size / 1024;
        double mb = kb / 1024;
        double gb = kb / 1024;
        double tb = kb / 1024;
        if(size < 1024) {
        	if (this.lang != null && this.lang == Lang.fr_FR) {
                s = size + " octets";
        	}
            s = size + " Bytes";
        } else if(size >= 1024 && size < (1024 * 1024)) {
            s =  String.format("%.2f", kb) + "K";
        } else if(size >= (1024 * 1024) && size < (1024 * 1024 * 1024)) {
            s = String.format("%.2f", mb) + "M";
        } else if(size >= (1024 * 1024 * 1024) && size < (1024 * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", gb) + "G";
        } else if(size >= (1024 * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", tb) + "T";
        }
        if (!s.endsWith("octets") && !s.endsWith("Bytes")) {
        	s += lastChar;
        }
        return s;
    }
	
    public String getFileSizeAsBytes(String filePath) {
    	String lastString;
    	if (this.lang == Lang.fr_FR) {
    		lastString = "octets";
    	} else {
    		lastString = "Bytes";
    	}
        return Long.toString(new File(filePath).length()) + " " + lastString;
    }
    
    public void setLang(Lang lang) {
    	this.lang = lang;
    }
	
}