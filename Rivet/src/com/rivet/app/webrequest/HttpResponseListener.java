/*
 * this is the Response Listener interface
 * need to be implemented while dealing with
 * the http thing. 
 */

package com.rivet.app.webrequest;

public interface HttpResponseListener {
	
	/*
	 * handles the response
	 */
	public void handleResponse(String response);
	/*
	 * handles the exception	
	 */
	void handleException(String exception);
	
	
	
}
