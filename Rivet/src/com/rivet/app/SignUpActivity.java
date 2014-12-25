package com.rivet.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.rivet.app.common.RConstants;


public class SignUpActivity extends BaseActivity implements View.OnClickListener, MySpinServerSDK.ConnectionStateListener {

    Context context=this;
    LoginModel loginModel;

    EditText passwordEt, emailEt;
    Button signUpBttn, backBttn;
    ImageView backIv;
	private Typeface metaProTypeFace;
	private TextView tv_terms;
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		// When this activity gets started register for mySPIN connection events
		// in order to
		// adapt views for the according connection state.
		try {
			MySpinServerSDK.sharedInstance().registerConnectionStateListener(this);
		} catch (MySpinException e) {
			e.printStackTrace();
		}

	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);
       
        metaProTypeFace = Typeface.createFromAsset(getAssets(), "MetaPro_Book.otf");
        emailEt=(EditText)findViewById(R.id.et_email);
        tv_terms = (TextView) findViewById(R.id.tv_terms);
        passwordEt=(EditText)findViewById(R.id.et_userPas);
        emailEt.setTypeface(metaProTypeFace);
        passwordEt.setTypeface(metaProTypeFace);
        signUpBttn=(Button)findViewById(R.id.bt_sign_up);
        signUpBttn.setOnClickListener(this);
        backBttn=(Button)findViewById(R.id.bt_back);
        backBttn.setOnClickListener(this);
        backIv=(ImageView)findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);
        tv_terms.setOnClickListener(this);
        
        
        
    	// registration with bosch
		try {
			MySpinServerSDK.sharedInstance().registerApplication(getApplication());
		} catch (MySpinException e) {
			Log.i(TAG, "register with Bosch Failed");
		}
        
      
        passwordEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					passwordEt.setHint("");
				}else if(!hasFocus && passwordEt.getText().toString().isEmpty()){
					passwordEt.setHint("Password");
				}
			}
		});
        
       emailEt.setOnFocusChangeListener(new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(hasFocus){
				emailEt.setHint("");
			}else if(!hasFocus && emailEt.getText().toString().isEmpty()){
				emailEt.setHint("Email Address");
			}
		}
	});
       
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_back:
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_sign_up:
            	
            String	userEmail = emailEt.getText().toString() ;
            String userPassword = passwordEt.getText().toString();
            
            if(userEmail.isEmpty() || userPassword.isEmpty() ){
            	
            	showToast("No Email Address or password was provided");
            }else{
            	backBttn.setClickable(false);
                loginModel=new LoginModel(context);
                loginModel.register(userEmail, passwordEt.getText().toString());
            }
                break;
            case R.id.tv_terms :
            	
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RConstants.TERMS_OF_USE_URL));
				startActivity(browserIntent);
            	
            	break ;
        }
    }
    
	public void enableBackButtonToPress() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				backBttn.setClickable(true);
			}
		});

	}

	@Override
	public void onConnectionStateChanged(boolean arg0) {
		// TODO Auto-generated method stub
		RConstants.BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES = true ;
		this.finish();
	}

}
