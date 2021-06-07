package examples;

import java.awt.EventQueue;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.github.msx80.kitteh.DocumentProducer;
import com.github.msx80.kitteh.Request;
import com.github.msx80.kitteh.Response;
import com.github.msx80.kitteh.WebServerBuilder;
import com.github.msx80.kitteh.producers.DeferredProducer;
import java.awt.GridLayout;

public class SwingAndWeb {

	private static final int PORT = 8080;
	private JFrame frmDemo;
	private JLabel lblInfo;
	private JLabel lblReport;

	
	private void produce(Request request, Response response)
	{
		// It is safe to interact with Swing components here because we are in the 
		// Swing event queue.
		lblReport.setText("Page was called by "+request.getRemoteAddr()+" at "+new Date());
		response.setContent("Field was changed! This thread is: "+Thread.currentThread().getName());
	}
	
	public static void main(String[] args) {
		
		SwingAndWeb app = new SwingAndWeb();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					app.initialize();
					app.frmDemo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// setup a deferred producer which will call our "produce" on swing event thread
		DocumentProducer p = new DeferredProducer(EventQueue::invokeLater, app::produce);
		
		WebServerBuilder
			.produce(p)
			.port(PORT)
			.run();
		
	}



	private void initialize() {
		frmDemo = new JFrame();
		frmDemo.setTitle("Swing and Kitteh demo");
		frmDemo.setBounds(100, 100, 450, 200);
		frmDemo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDemo.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		lblInfo = new JLabel("Connect to http://localhost:"+PORT+" to change the label below!");
		frmDemo.getContentPane().add(lblInfo);
		
		lblReport = new JLabel("(page not called yet)");
		frmDemo.getContentPane().add(lblReport);
	}
	public JLabel getLblInfo() {
		return lblInfo;
	}
	public JLabel getLblReport() {
		return lblReport;
	}
}
