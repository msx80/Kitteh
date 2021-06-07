package com.github.msx80.kitteh.utils;

public class Cleaner {
	
	public static String cleanDocName(String docName)
	{
		docName = docName.replace('\\', '/');
        while (docName.startsWith("/"))
        {
            docName = docName.substring(1, docName.length());
        }
        while (docName.endsWith("/"))
        {
            docName = docName.substring(0, docName.length()-1);
        }
        while (docName.contains("//"))
        {
        	docName = docName.replace("//", "/");
        }
		return docName;
	}
}
