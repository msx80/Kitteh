package com.github.msx80.kitteh.producers;

import java.io.*;

import com.github.msx80.kitteh.*;

/**
 * This is a producer that respond to request by sending the relative file.
 * Files are taken from a directory supplied on the constructor, mapping the url
 * to the reltive path.
 * Useful to send static pages, images and such.
 * If the file is not found, the fallback producer is called if defined, else a 404 error is sent.
 */
public class FileProducer implements DocumentProducer
{
	
	private File webContentDir;
	private DocumentProducer fallback;
	private String defaultFile = null;
	
	public FileProducer(String webContentDir, DocumentProducer fallback) throws Exception
	{
		this.webContentDir = new File(webContentDir).getCanonicalFile();
		this.fallback = fallback;
	}
	public FileProducer(String webContentDir) throws Exception
	{
		this(webContentDir,  DocumentProducer.ERR_404_PRODUCER);
	}
	public void produceDocument(Request request, Response response) throws Exception, Redirection
	{
		String doc = request.getDocumentName();
		if (doc.equals("") && (defaultFile != null))
		{
			doc = defaultFile; 
		}
		File filex = new File(webContentDir+File.separator+doc);
		// expand eventual shortcuts or such
		File file = filex.getCanonicalFile();
		
		if (!file.toString().startsWith(webContentDir.toString()))
		{
			throw new Exception("Access denied");
		}
		if (!file.exists())
		{
			
			fallback.produceDocument(request, response);
			
		}
		else
		{
			sendFile(response, file);
		}
	}
	private void sendFile(Response response, File file) throws FileNotFoundException, IOException
	{
		response.setCacheable(true)
				.setContent( new FileInputStream(file), (int)file.length() )
				.setContentType( Mime.getMIMEType(file) );
	}
	public String getDefaultFile() {
		return defaultFile;
	}
	public void setDefaultFile(String defaultFile) {
		this.defaultFile = defaultFile;
	}

}
