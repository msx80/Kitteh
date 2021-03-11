package examples.pages.dispatch;

import com.github.msx80.kitteh.*;

public class Sum implements DocumentProducer
{

	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		try
		{
			int a = Integer.parseInt( request.getParameter("a") );
			int b = Integer.parseInt( request.getParameter("b") );
			int sum = a+b;
			response.setContent("<html><body>The sum is: "+sum+"</body></html>");
		}
		catch (NumberFormatException e)
		{
			String form = "<form><input name=\"a\"><input name=\"b\"><input type=\"submit\"></form>";
			response.setContent("<html><body>Please enter two number to sum:<br/>"+form+"</body></html>");
		}
	}

}
