package examples.pages;

import java.util.*;

import com.github.msx80.kitteh.*;

public class Welcome implements DocumentProducer
{

	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		StringBuffer html = new StringBuffer();
        html.append("<html><body>");
        html.append("<strong>Hello, this is a dynamic page :)</strong><br>");
        html.append("Time is: ");
        html.append(new Date());
        html.append("<br><br><ul>");
        html.append("<li>see a <a href=\"file.html\">static page</a>");
        html.append("<li>see another <a href=\"another.html\">dynamic page</a>");
        html.append("<li>see some <a href=\"pages/Hello\">automatic dispatch</a> and some <a href=\"pages/TestPage\">more</a>");
        html.append("<li>do some <a href=\"pages/Sum\">math</a>!");
        html.append("<li>see a slooooow <a href=\"slow.html\">page</a>!");
        html.append("<li>or access our <a href=\"secret/anything.html\">private area</a> (myuser/mypass)");
        html.append("<li>and don't ever <a href=\"breakme\">click here!</a>");
        html.append("</body></html>");
        response.setContent(html.toString());
	}

}
