package com.github.msx80.kitteh;

/**
 * This interface represent an object that can respond to an HTTP request.
 * It can be used with a WebServer object to generate responses to clients.
 */
public interface DocumentProducer 
{
	/**
	 * produce a reply for an HTTP request.
	 * @param request 
	 * @param response
	 * @throws Exception
	 * @throws Redirection can be thrown to redirect to another url
	 */
	void produceDocument(Request request, Response response) throws Exception, Redirection;
	
	public static final DocumentProducer ERR_404_PRODUCER = new DocumentProducer() {
		
		@Override
		public void produceDocument(Request request, Response response) throws Exception, Redirection {
			response.setHtmlReturnCode(404);
			response.setContent("Document not found");
			
		}
	};
	
}
