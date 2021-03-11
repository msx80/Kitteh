package com.github.msx80.kitteh.producers;

import java.util.*;
import java.util.regex.*;

import com.github.msx80.kitteh.*;

/**
 * This is a producer that dispatch requests to different producers, based on some rules.
 * Rules are defined by regular expressions that will be matched against the document name.
 * When a match is found, the corresponding producer will be called.<br/>
 * A rule can define a producer by suppling either a string (a fully qualified name) or a DocumentProducer.<br/>
 * <li>If a string is supplied, the relative class will be instantiated ad each request and then discarded.
 * Regular expression submatches can be used in the string to group multiple rules, as in:
 * <code>rules.put("pages/(.*)", "examples.pages.$1");</code>
 * <li>If a DocumentProducer is supplied, it will be used directly. Example:
 * <code>rules.put("index.html", new Welcome());</code>
 * <li>If no rules apply to a given request, the fallback producer is called if defined, otherwise 
 * an exception is thrown (resulting in a 500 error).
 *
 */
public class DispatcherProducer implements DocumentProducer
{
	private Map<Pattern, Object> prules = new HashMap<Pattern, Object>();
	private DocumentProducer fallback;
	
	public DispatcherProducer(Map<String, Object> rules) throws Exception
	{
		this(rules, null);
	}
	public DispatcherProducer(Map<String, Object> rules, DocumentProducer fallback) throws Exception
	{
		this.fallback = fallback;

		// init rule table
        for (String regEx : rules.keySet())
        {
			Object clazz = (Object) rules.get(regEx);

            Pattern p = Pattern.compile(regEx);

			prules.put(p, clazz);
		}
	}
	
	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		String docName = request.getDocumentName();
		
        for (Pattern pattern : prules.keySet())
		{
            Matcher m = pattern.matcher(docName);
			if(m.matches())
			{
				DocumentProducer d;
				Object clazz = prules.get(pattern);
				if (clazz instanceof String)
				{
					String className = (String) clazz;
					for (int i = 1; i <= m.groupCount(); i++)
					{
						className = className.replaceAll("\\$"+i, m.group(i));
					}
					Class<?> c;
					try
					{
						c = Class.forName(className);
					}
					catch (NoClassDefFoundError e) 
					{
						throw new Exception("Class loading error", e);
					}
					d = (DocumentProducer)c.newInstance();
				}
				else if (clazz instanceof DocumentProducer)
				{
					d = (DocumentProducer)clazz;
				}	
				else
				{
					throw new ClassCastException("Producers on the Dispatcher rules must be either String or DocumentProducer");
				}
			
				d.produceDocument(request, response);
				return;
			}
		}
		
		if (fallback != null)
		{
			fallback.produceDocument(request, response);
		}
		else
		{
			throw new Exception("Request cannot be dispatched: "+request.getDocumentName());
		}
	
	}
}
