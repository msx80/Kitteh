package com.github.msx80.kitteh.impl;

import java.util.*;

public class SessionContainer
{
	private static final long SESSION_DURATION = 1000*60*30;
	private static final long SESSION_CHECK = 1000*60;
	private static Map<String, SessionEntry> sessions = new HashMap<String, SessionEntry>();
	private static long lastCheck = 0; 
	
	private static void purgeOldSessions()
	{
		long millis = System.currentTimeMillis();
		// check every minute
		if ((lastCheck+SESSION_CHECK)<millis)
		{
			Iterator<String> i = sessions.keySet().iterator();
			 
			while (i.hasNext())
			{
				String k = i.next();
				SessionEntry s = sessions.get(k);
				if ((s.entry + SESSION_DURATION) < millis)
				{
					i.remove();
				}
			}
		}
	}
	public static boolean hasSession(String sessionId)
	{
		synchronized(sessions)
		{
			purgeOldSessions();
			return sessions.containsKey(sessionId);
		}
	}
	
	public static Map<String, Object> getSession(String sessionId)
	{
		synchronized(sessions)
		{
			purgeOldSessions();
			SessionEntry s = sessions.get(sessionId);
			if (s==null)
			{
				s = new SessionEntry();
				s.session = new HashMap<String, Object>();
				sessions.put(sessionId, s);
			}
			s.entry = System.currentTimeMillis();
			
			return s.session;
		}
	}
	
}
