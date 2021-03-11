package examples.pages;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Redirection;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;

public class Slow implements DocumentProducer {

	public void produceDocument(Request request, Response response)	throws Exception, Redirection {
		
		PipedInputStream  writeIn = new PipedInputStream();
		PipedOutputStream decodeOut = new PipedOutputStream(writeIn);
		
		SlowProducer p = new SlowProducer(decodeOut);
		p.start();
		response.setContentType("text/html");
		response.setContent(writeIn);
	}

	
	class SlowProducer extends Thread
	{
		private OutputStream os;

		public SlowProducer(OutputStream os) {
			super();
			this.os = os;
		}

		@Override
		public void run() {
			try {
				os.write("<html><body><p>Initializing...</p>\n".getBytes());
				//send about 1k filler to activate partial rendering of html in browser
				for (int j = 0; j < 10; j++) os.write("<!--                                                                                                    -->".getBytes());
				
				os.flush();
				Thread.sleep(1000);
				for (int i = 1; i <= 10; i++) 
				{
					os.write(("<p>Performing operation "+i+"/10...</p>\n").getBytes());
					os.flush();
					Thread.sleep(1000);
				}
				os.write("<p>Finished!</p>\n".getBytes());
				os.write("</body></html>\n".getBytes());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			} 
				
		}
		
		
	}
	
}
