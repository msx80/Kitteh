package examples.pages;

import java.util.*;

import com.github.msx80.kitteh.*;
import com.github.msx80.kitteh.utils.*;

public class Another implements DocumentProducer
{

	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
        StringBuffer html = new StringBuffer();
        html.append("<html><body>Don't trust the .html extension :) this is really a dynamic page! <br><br><strong>System properties are:</strong><br><br>");
        mapToTable(html, System.getProperties());
        html.append("<br><br>");
        html.append("<strong>Your headers:</strong><br><br>");
        mapToTable(html, request.getHeaders());
        html.append("<br><br>");
        html.append("<strong>Your parameters:</strong><br><br>");
        mapToTable(html, request.getParameters());
        html.append("</body></html>");
        response.setContent(html.toString());
	}
	private void mapToTable(StringBuffer b, Map<?, ?> map)
    {
        b.append("<table border=\"1\">");
        for (Object k : map.keySet() )
        {
            String v = (String) map.get(k);
            b.append("<tr><td>");
            b.append(HtmlEscape.escape(k.toString()));
            b.append("</td><td>");
            b.append(HtmlEscape.escape(v));
            b.append("</td><tr>");
        }
        b.append("</table>");
        
    }
}
