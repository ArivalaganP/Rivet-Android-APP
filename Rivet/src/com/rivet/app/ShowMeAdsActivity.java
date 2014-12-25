package com.rivet.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rivet.app.common.RConstants;

public class ShowMeAdsActivity extends BaseActivity {
	
	

	private WebView webView;
	 int pass=0;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_terms_of_use);
		String clickableAdsUrl =	getIntent().getStringExtra(RConstants.CLICKABLE_AD_URL);
		pass  =0;
		webView = (WebView) findViewById(R.id.termsOfUseWV);
		
		startWebView(clickableAdsUrl);
  
	}
	
	 @SuppressLint("SetJavaScriptEnabled")
	private void startWebView(String url) {
	         
	        webView.setWebViewClient(new WebViewClient() {     
	            ProgressDialog progressDialog;
	           
	            
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {             
	                view.loadUrl(url);
	                return true;
	            }
	        
	            //Show loader on url load
	            public void onLoadResource (WebView view, String url) {
	            	if(pass==0){
		                if (progressDialog == null) {
		                	 progressDialog = ProgressDialog.show(ShowMeAdsActivity.this,
		                             getString(R.string.app_name), RConstants.LOADING_PAGE_WAIT, true, true);
		                     progressDialog.show();
		                     pass++;
		                }
	            	}
	            }
	            public void onPageFinished(WebView view, String url) {
	                try{
	                if (progressDialog.isShowing()) {
	                    progressDialog.dismiss();
	                    progressDialog = null;
	                }
	                }catch(Exception exception){
	                    exception.printStackTrace();
	                }
	            }
	             
	        });
	          
	        webView.getSettings().setJavaScriptEnabled(true);
	         
	       
	        webView.loadUrl(url);
	          
	          
	    }
	     
	   
	     
	    @Override
	    // Detect when the back button is pressed
	    public void onBackPressed() {
	        if(webView.canGoBack()) {
	            webView.goBack();
	        } else {
	            // Let the system handle the back button
	            super.onBackPressed();
	        }
	    }

}
