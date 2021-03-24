package com.github.msx80.kitteh.producers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.impl.ws.Base64;

/**
 * This producer adds HTML basic authentication to an existing producer.
 * That is, content generated by the producer will be avaiable only after entering
 * valid username and password.<br/>
 * Usage:<br/>
 * <code>DocumentProducer a = new AuthenticationProducer(d, "myuser", "mypass");</code>
 */
public class AuthenticationProducer implements DocumentProducer
{
    private Pattern p = Pattern.compile("([^:]*):(.*)");
    
    private DocumentProducer producer;
    private Authenticator validator;
    private String realm;
    
    public AuthenticationProducer(DocumentProducer producer, String user, String pass)
    {
        this(producer, user, pass, "Secure Area");

    }
    
    
    
    public AuthenticationProducer(DocumentProducer producer, Authenticator validator, String realm) {
		super();
		this.producer = producer;
		this.validator = validator;
		this.realm = realm;
	}



	public AuthenticationProducer(DocumentProducer producer, final String user, final String pass, String realm)
    {
		this(producer, new Authenticator() {
			
			@Override
			public boolean validate(Request request, String givenUser, String givenPass) {
				return (pass.equals(givenPass) && user.equals(givenUser));
			}
		},  realm );
    }

    public void produceDocument(Request request, Response response) throws Exception, Redirection
    {
        String auth = (String)request.getHeaders().get("authorization");
        if ( (auth != null) && auth.toLowerCase().startsWith("basic "))
        {
            auth = auth.substring(6);
            auth = new String( Base64.decode(auth));
            Matcher m = p.matcher(auth);
            if (m.matches())
            {
                String givenUser = m.group(1);
                String givenPass = m.group(2);
                if (validator.validate(request, givenUser, givenPass))
                {
                    producer.produceDocument(request, response);
                    return;
                }
            }
        }
        
        sendAuth(response);
    }

    private void sendAuth(Response response)
    {
        String html = "<HTML><HEAD><TITLE>Error</TITLE><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=ISO-8859-1\"></HEAD><BODY><H1>401 Unauthorised.</H1></BODY></HTML>";
        response.setContent(html);
        response.getHeaders().put("WWW-Authenticate", "Basic realm=\""+realm+"\"");
        response.setHtmlReturnCode(401);
    }

    
    
    public DocumentProducer getProducer()
    {
        return producer;
    }

    public void setProducer(DocumentProducer producer)
    {
        this.producer = producer;
    }

    
    
}