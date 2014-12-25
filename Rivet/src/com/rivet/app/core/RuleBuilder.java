package com.rivet.app.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.rivet.app.abstracts.BuilderBase;
import com.rivet.app.common.RConstants;
import com.rivet.app.observer.RuleBuilderObserver;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

/**
 * Created by Brian on 16/6/14.
 */
public class RuleBuilder extends BuilderBase {

	private List<PropertyChangeListener> rBlistener = new ArrayList<PropertyChangeListener>();

	RuleParser parser = null;
	String TAG = "RuleBuilder";
	String playListUrl = null ;
	String rulesString;

	private Context context;

	public RuleBuilder(RuleParser parser, Context context) {

		this.parser = parser;
		this.context = context;

	}

	public RuleBuilder(Context ctx) {
		//this.parser = new RuleParser(ctx);
		this.context = ctx;

	}

	public void start() throws VerifyError {

		if (RConstants.BUILD_DEBUBG) {
			Log.i(TAG, "***START RULE API ***");
		}

		String url = RConstants.BaseUrl + "playlistrulesurl";
		RulesListResponseHandler rulesResponseHandler = new RulesListResponseHandler();

		Thread getRulesThread = new Thread(new HttpRequest(url,HttpMethodType.GET, rulesResponseHandler, rulesResponseHandler,
				null, null, false, null));
		getRulesThread.start();

		try {
			getRulesThread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		if (RConstants.BUILD_DEBUBG) {
			Log.i(TAG, "***END RULE API ***");
		}
		final String ruleUrl = this.determineRuleURLString(playListUrl);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
			
				try {

					final HttpClient client = new DefaultHttpClient();
					final HttpContext localContext = new BasicHttpContext();

					final HttpGet request = new HttpGet(ruleUrl);
					final HttpResponse response;
					response = client.execute(request, localContext);
					rulesString = EntityUtils.toString(response.getEntity());

				} catch (ClientProtocolException e) {
				
					e.printStackTrace();
				} catch (IOException e) {
				
					e.printStackTrace();
				}
			}
		});

		thread.start();

		try {
			thread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		if (RConstants.BUILD_DEBUBG) {
			Log.i(TAG, "***RULE API STRING START***");
		}
		if (parser == null) {

			if (RConstants.BUILD_DEBUBG) {

				Log.i(TAG, "***RULE PARSER START***");
			}
			parser = new RuleParser(rulesString,this.context);

		} else {

			parser.setRuleString(rulesString);
		}
		Thread ruleParserThread = new Thread( parser);
		ruleParserThread.start();
		
		try {
			ruleParserThread.join();
			
		} catch (InterruptedException e) {
		
		
			e.printStackTrace();
		
		}

		notifyListeners(RConstants.FINISH_RULE_BUILDING);

	}

	public String determineRuleURLString(String ruleString) {

		Set<String> weekendDays = new HashSet<String>(3);
		weekendDays.add("Sat");
		weekendDays.add("Sun");

		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE|HH|a");
		Date date = new Date();
		dateFormat.format(date);

		String currentTimeString = dateFormat.format(date);
		String seperator = "\\|";
		String[] currentTimeParams = TextUtils.split(currentTimeString,
				seperator);
		int hour = 0;
		if (2 < currentTimeParams.length) {
			hour = Integer.parseInt(currentTimeParams[1]);
		}

		boolean isWeekendDay = false;

		if (currentTimeParams.length > 2) {

			if (weekendDays.contains(currentTimeParams[0])
					|| (currentTimeParams[0].equalsIgnoreCase("Fri") && hour >= 18)
					|| (currentTimeParams[0].equalsIgnoreCase("Mon") && hour < 6)) {
				isWeekendDay = true;
			}
		}
		if (!isWeekendDay) {
			if (hour >= 6 && hour < 18) {
				ruleString = ruleString.concat("weekday_business.rul");
			} else {
				ruleString = ruleString.concat("weekday_evening.rul");
			}
		} else {
			ruleString = ruleString.concat("weekend.rul");// weekend.rul
		}

		return ruleString;

	}



	public class RulesListResponseHandler implements HttpResponseListener,
			HttpExceptionListener {

		@Override
		public void handleResponse(String response) {
			
			try {
				JSONObject downloadUrlObject = new JSONObject(response);
				playListUrl = downloadUrlObject.getString("url");
				
				if(playListUrl == null){
					playListUrl = "http://prod-playlistrules.s3.amazonaws.com/" ;
				}
				
			} catch (JSONException e) {
				
				if(playListUrl == null){
					playListUrl = "http://prod-playlistrules.s3.amazonaws.com/" ;
				}
				e.printStackTrace();
			}
		}

		@Override
		public void handleException(String exception) {
			
			if(RConstants.BUILD_DEBUBG){
			Log.i("RuleBuilder", exception.toString());
			}
			
			if(playListUrl == null){
				playListUrl = "http://prod-playlistrules.s3.amazonaws.com/" ;
			}
			
			// Notify the contentmanger that we got exception server might be
			// down
			notifyListeners(RConstants.HHR_ERROR_SERVER_IS_DOWN_RULE_BUILDER);
		}

	}

	public void addChangeListener(RuleBuilderObserver ruleBuilderObserver) {
		
		rBlistener.add(ruleBuilderObserver);

	}

	public void notifyListeners(String ruleBuildComplete) {
		for (PropertyChangeListener name : rBlistener) {
			name.propertyChange(new PropertyChangeEvent(this,
					ruleBuildComplete, null, null));
		}
	}

}