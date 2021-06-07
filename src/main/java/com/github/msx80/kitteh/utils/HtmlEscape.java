package com.github.msx80.kitteh.utils;


public class HtmlEscape
{
	private static final char[] CHARS = new char[]{'&', '<', '>', '"', '\''} ;
	private static final String[] ESC = new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#39;"};
    

    private HtmlEscape ()
    {
    	
    }

    public static String escape(String s)
    {
       StringBuffer str = new StringBuffer (s);
       for (int i = 0; i < CHARS.length; i++)
       {
    	   int index = str.indexOf(""+CHARS[i], 0);
    	   while (index>=0)
    	   {
    		   str.replace(index, index+1, ESC[i]);
    		   index+=ESC[i].length();
    		   index = str.indexOf(""+CHARS[i], index);
    	   }
       }
       return str.toString();
    }
}
