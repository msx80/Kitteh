package com.github.msx80.kitteh;

import java.io.*;
import java.util.*;

public class Mime
{
	public static final Hashtable<String, String> MIME_TYPES = new Hashtable<String, String>();

    static {
        MIME_TYPES.put(".gif", "image/gif");
        MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".jpeg", "image/jpeg");
        MIME_TYPES.put(".png", "image/png");
        MIME_TYPES.put(".html", "text/html");
        MIME_TYPES.put(".htm", "text/html");
        MIME_TYPES.put(".css", "text/css");
        MIME_TYPES.put(".txt", "text/plain");
		MIME_TYPES.put(".pdf", "application/pdf");
		MIME_TYPES.put(".js", "application/javascript");
    }
    
    private static String getExtension(java.io.File file) {
        String extension = "";
        String filename = file.getName();
        int dotPos = filename.lastIndexOf(".");
        if (dotPos >= 0) {
            extension = filename.substring(dotPos);
        }
        return extension.toLowerCase();
    }
	
	public static String getMIMEType(File file)
	{
		String t = (String) MIME_TYPES.get(getExtension(file));
		if (t==null) t = "application/octet";
		return t;
	}
	
	public static void add(String extension, String mimeType)
	{
		if(!extension.startsWith(".")) extension = "."+extension;
		MIME_TYPES.put(extension, mimeType);
	}
}
