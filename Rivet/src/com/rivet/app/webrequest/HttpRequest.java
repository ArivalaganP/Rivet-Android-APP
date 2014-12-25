

/*
 * Its class which implement the Http client request in runnable form 
 * 
 */


package com.rivet.app.webrequest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.rivet.app.common.RConstants;

import android.util.Log;

public class HttpRequest implements Runnable {

	URLConnection conn;
	HttpURLConnection httpconn;
	int resposecode = 0;
	String responseType="PROCESSED"; // or RAW // default is PROCESSED
	private String path = null;
	private String tag = ((Object) this).getClass().getSimpleName();
	private HttpMethodType method = null;
	private HttpResponseListener resListener;
	private HttpExceptionListener excepListener;
	private ArrayList<NameValuePair> data;
	static public boolean setCookie=false;
	static public Header mHeaders[] = null;
	private ArrayList<Cookie> cookieList;
	HttpClient client;
	CookieStore cookieStore;
	HttpContext localContext;
	private boolean isJsonRequest;
	private String jsonLogsUserAction;
	
	
	 
	public HttpRequest(String path, 
			HttpMethodType method,
			HttpResponseListener resListener,
			HttpExceptionListener excepListener, 
			ArrayList<NameValuePair> data,
			ArrayList<Cookie> cklist , 
			boolean isJsonRequest , 
			String jsonLogsUserAction) {
		
		this.isJsonRequest = isJsonRequest;
		this.path = path;
		this.method = method;
		this.jsonLogsUserAction = jsonLogsUserAction ;
		this.resListener = resListener;
		this.excepListener = excepListener;
		this.data = data;
		this.cookieList = cklist;
		client = new DefaultHttpClient();
		//Create a local instance of cookie store
		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();
		if(cookieList!=null){
			setCookie(cookieList);
		}
		
	}
	public HttpRequest(String path, HttpMethodType method,
			HttpResponseListener resListener,
			HttpExceptionListener excepListener, ArrayList<NameValuePair> data,ArrayList<Cookie> cklist,String resType , boolean isJsonRequest) {
		this.isJsonRequest = isJsonRequest;
		this.path = path;
		this.method = method;
		this.resListener = resListener;
		this.excepListener = excepListener;
		this.data = data;
		this.cookieList = cklist;
		client = new DefaultHttpClient();
		//Create a local instance of cookie store
		cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();
		if(cookieList!=null){
			setCookie(cookieList);
		}
		this.responseType=resType;
		
	}

	public void setCookie( ArrayList<Cookie> cklist){
		this.cookieList = cklist;
		
		//set cookies for the call.
		for(int idx =0 ; idx < cookieList.size() ; idx++)
		{
			cookieStore.addCookie(cookieList.get(idx));
		}
		setCookie=true;
		 // Bind custom cookie store to the local context
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
	}

	@Override
	public void run() {
		
		if(RConstants.BUILD_DEBUBG){
		Log.i("run method", "calling run method");
		}
		
		try {
			if ((!isJsonRequest) && (method == HttpMethodType.GET)) {
				response(executeHttpGet());
			} else if(isJsonRequest && (method == HttpMethodType.POST)){
				response(executeHttpJsonPost());
			}else {
				response(executeHttpPost());
			}
		} catch (ConnectTimeoutException ex) {
			
			exception("Please retry after sometime...");
			
		} catch (UnknownHostException e) {
			
			exception("Server might be down...");
			
		} catch (IOException e) {
			
			exception("Please check your internet connectivity...");
			
		} catch (Exception e) {
			exception(e.getMessage());
		}

		if(RConstants.BUILD_DEBUBG){
		Log.i(tag, "Http Call Finish");
		}
	}

	private void response(String response) {

		if (resListener != null) {
			resListener.handleResponse(response);
		}
	}

	private void exception(String exception) {
		if (excepListener != null) {
			excepListener.handleException(exception);
		}
	}

	public String executeHttpGet() throws Exception {
		
		if(RConstants.BUILD_DEBUBG){
		Log.i("calling method", "calling execute");
		Log.i("path in method", path);
		}
		
		BufferedReader in = null;
		String page = null;
		try {
			
		    HttpGet request = new HttpGet(path);
		    HttpResponse response = client.execute(request,localContext);
		    
		    if(RConstants.BUILD_DEBUBG){
			Log.i("======", response.toString());
		    }
		    
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";

			while ((line = in.readLine()) != null) {
				
				if(RConstants.BUILD_DEBUBG){
				Log.i("the response is ::", line);
				}
				
				sb.append(line);

			}
			in.close();
			page = sb.toString();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return page;
	}
	
	

	public String executeHttpPost() throws Exception {
		BufferedReader in = null;
		String page = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(path);
											 	 
			request.setEntity(new UrlEncodedFormEntity(data));

			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();
			
			if(RConstants.BUILD_DEBUBG){
			Log.i("Response Code is", "" + responseCode);
			}
			
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			page = sb.toString();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return page;

	}
	
	 
       
	public String executeHttpJsonPost() throws Exception {
		BufferedReader in = null;
		String page = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(path);
		
			
			request.setHeader(RConstants.ACCEPT, "application/json");                 
			request.setHeader(RConstants.CONTENT_TYPE, "application/json"); 
	
		request.setEntity(new StringEntity(this.jsonLogsUserAction));
		

			HttpResponse response = client.execute(request);
			int responseCode = response.getStatusLine().getStatusCode();

			if(RConstants.BUILD_DEBUBG){
			Log.i(RConstants.RESPONSE_CODE, "" + responseCode);
			}
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			page = sb.toString();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return page;

	}

	public URLConnection getConn() {
		return conn;
	}

	public void setConn(URLConnection conn) {
		this.conn = conn;
	}

	public HttpURLConnection getHttpconn() {
		return httpconn;
	}

	public void setHttpconn(HttpURLConnection httpconn) {
		this.httpconn = httpconn;
	}

	public int getResposecode() {
		return resposecode;
	}

	public void setResposecode(int resposecode) {
		this.resposecode = resposecode;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public HttpMethodType getMethod() {
		return method;
	}

	public void setMethod(HttpMethodType method) {
		this.method = method;
	}

	public HttpResponseListener getResListener() {
		return resListener;
	}

	public void setResListener(HttpResponseListener resListener) {
		this.resListener = resListener;
	}

	public HttpExceptionListener getExcepListener() {
		return excepListener;
	}

	public void setExcepListener(HttpExceptionListener excepListener) {
		this.excepListener = excepListener;
	}

	public ArrayList<NameValuePair> getData() {
		return data;
	}

	public void setData(ArrayList<NameValuePair> data) {
		this.data = data;
	}
	
	 public static String convertToUTF8(String s) {
	        String out = null;
	        try {
	            out = new String(s.getBytes("UTF-8"));
	        } catch (java.io.UnsupportedEncodingException e) {
	            return null;
	        }
	        return out;
	    }

}
