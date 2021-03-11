package examples.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.msx80.kitteh.WebServer;
import com.github.msx80.kitteh.WebSocket;
import com.github.msx80.kitteh.WebSocketListener;
import com.github.msx80.kitteh.producers.FileProducer;

public class WebSocketTest implements WebSocketListener {

	Set<WebSocket> connections = Collections.synchronizedSet(new HashSet<WebSocket>());
	
	public WebSocketTest() throws Exception
	{
		FileProducer f = new FileProducer("www");
        f.setDefaultFile("websocket.html");
		WebServer w = new WebServer(f,8080);
		w.setWebSocketListener(this);
		w.runAsThread();
		System.out.println("Server started!");
		System.out.println("Press ENTER to quit.");
		
		BufferedReader r = new BufferedReader(new InputStreamReader( System.in ));
		r.readLine();
		w.close();
		System.out.println("Quitting");
	}
	
	

	@Override
	public void connection(WebSocket ws) {
		
		System.out.println("Connection received by session "+ws.getRequest().getSessionId()+" param is: "+ws.getRequest().getParameter("param"));
		connections.add(ws);
	}
	
	public static void main(String[] args) throws Exception
	{
		new WebSocketTest();
	}



	@Override
	public void dataText(WebSocket ws, String data) {
		System.out.println("Data received by session "+ws.getRequest().getSessionId()+": "+data);
		for (WebSocket webSocket : connections) {
			webSocket.sendString(data);
		}
		
	}



	@Override
	public void dataBinary(WebSocket ws, byte[] data) {

		System.out.println("Binary data received by session "+ws.getRequest().getSessionId()+": "+data);
		
	}



	@Override
	public void disconnection(WebSocket ws) {
		connections.remove(ws);
		System.out.println("Disconnection by session "+ws.getRequest().getSessionId());
		
	}
}
