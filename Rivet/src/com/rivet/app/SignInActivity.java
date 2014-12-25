package com.rivet.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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


public class SignInActivity extends BaseActivity implements View.OnClickListener ,  MySpinServerSDK.ConnectionStateListener {

    Context context=this;
    LoginModel loginModel;

    EditText userIdEt, passwordEt;
    Button signInBttn, backBttn;
    ImageView backIv;
	private TextView title;
	private Typeface metaProTypeFace;
	private TextView forgotPassword;
	
	
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
        setContentView(R.layout.activity_sign_in);

        userIdEt=(EditText)findViewById(R.id.et_user_id);
        passwordEt=(EditText)findViewById(R.id.et_password);
        title = (TextView) findViewById(R.id.title);
        signInBttn=(Button)findViewById(R.id.bt_sign_in);
        forgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        signInBttn.setOnClickListener(this);
        backBttn=(Button)findViewById(R.id.bt_back);
        backBttn.setOnClickListener(this);
        backIv=(ImageView)findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        
        
    	// registration with bosch
		try {
			MySpinServerSDK.sharedInstance().registerApplication(getApplication());
		} catch (MySpinException e) {
			Log.i(TAG, "register with Bosch Failed");
		}
    
        userIdEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus ){
					userIdEt.setHint("");
				}else if(!hasFocus && userIdEt.getText().toString().isEmpty() ){
					userIdEt.setHint("Enter Email");
				}
				
				
			}
		});
       
        passwordEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					passwordEt.setHint("");
				}else if(!hasFocus && passwordEt.getText().toString().isEmpty() ){
					passwordEt.setHint("Enter Password");
				}
			}
			
		});
        
        setTypeFace();
    }

    private void setTypeFace() {
    	metaProTypeFace = Typeface.createFromAsset(getAssets(), "MetaPro_Book.otf");
		title.setTypeface(metaProTypeFace);
		backBttn.setTypeface(metaProTypeFace);
		userIdEt.setTypeface(metaProTypeFace);
		passwordEt.setTypeface(metaProTypeFace);
		forgotPassword.setTypeface(metaProTypeFace);
		signInBttn.setTypeface(metaProTypeFace);
		
		
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
            case R.id.bt_sign_in:
            boolean isLoginWithEmail =	prefStore.getBooleanData(RConstants.IS_LOGIN_WITH_GMAIL , false);
            
            if(!isLoginWithEmail){
                loginModel=new LoginModel(context);
                loginModel.login(userIdEt.getText().toString(), passwordEt.getText().toString());
            }else{
             showToast("Already Logged in , please logout first from Home Screen");
            }
                
                break;
            case R.id.tv_forgot_password:
            	
              Intent forgotPasswordIntent = new Intent(SignInActivity.this , ForgotPasswordActivity.class);
              startActivity(forgotPasswordIntent);
            	
            	break ;
        }
    }

	@Override
	public void onConnectionStateChanged(boolean arg0) {
		// TODO Auto-generated method stub
		RConstants.BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES = true ;
		this.finish();
	}
	
	
}
