package com.rivet.app.webrequest;

/* this is the Exception Listener interface
 * need to be implemented while dealing with
 * the http thing.
 */


public interface HttpExceptionListener {

	/*
	 * this method handles the exception, based on
	 * what to do with the exception occurred
	 */
	public void handleException(String exception);

	/*
	 * this method handles the response as 
	 * what actually to do with the response
	 * when an exception occurs.
	 */
	void handleResponse(String response);
}
