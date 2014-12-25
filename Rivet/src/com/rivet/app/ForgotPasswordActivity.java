package com.rivet.app;

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
import android.widget.Toast;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.rivet.app.common.RConstants;


public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener,
                                  MySpinServerSDK.ConnectionStateListener {

    Button backBttn;
    ImageView backIv;
    EditText emailEt;
	private TextView title;
	private TextView bt_submit;
	private Typeface metaProTypeFace;
	private TextView sentEmailTV;
	private TextView checkTV;
	private Button okeyBT;
	
	

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
        setContentView(R.layout.activity_forgot_password);

        emailEt=(EditText)findViewById(R.id.et_email);
        backBttn=(Button)findViewById(R.id.bt_back);
        title = (TextView) findViewById(R.id.title);
        okeyBT = (Button) findViewById(R.id.bt_ok);
        okeyBT.setOnClickListener(this);
        sentEmailTV = (TextView) findViewById(R.id.sentEmailTV);
        checkTV = (TextView) findViewById(R.id.checkTV);
        bt_submit = (TextView) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
        backBttn.setOnClickListener(this);
        backIv=(ImageView)findViewById(R.id.iv_back);
        backIv.setOnClickListener(this);
        
        
     	// registration with bosch
    		try {
    			MySpinServerSDK.sharedInstance().registerApplication(getApplication());
    		} catch (MySpinException e) {
    			Log.i(TAG, "register with Bosch Failed");
    		}
    		
        emailEt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					emailEt.setHint("");
				}else if(!hasFocus && emailEt.getText().toString().isEmpty()){
					emailEt.setHint("Enter Email");
				}
			}
		});
        setTypeface();
    }
    
  public void makeTextViewsVisiable(){
	  
	  checkTV.setVisibility(View.VISIBLE);
	  sentEmailTV.setVisibility(View.VISIBLE);
	  okeyBT.setVisibility(View.VISIBLE);
	  bt_submit.setVisibility(View.GONE);
	  emailEt.setVisibility(View.GONE);
	  
    }

    private void setTypeface() {
    	metaProTypeFace = Typeface.createFromAsset(getAssets(), "MetaPro_Book.otf");
    	bt_submit.setTypeface(metaProTypeFace);
    	title.setTypeface(metaProTypeFace);
    	emailEt.setTypeface(metaProTypeFace);
    	backBttn.setTypeface(metaProTypeFace);
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_submit:
            	
            	
            	String mailId = emailEt.getText().toString();
            	if(mailId.isEmpty()){
            		Toast.makeText(ForgotPasswordActivity.this, RConstants.ENTER_EMAIL_ID , Toast.LENGTH_LONG).show();
            	}else{
            	LoginModel loginModel = new LoginModel(ForgotPasswordActivity.this);
            	loginModel.resetPassword(mailId);
            	}
            	
            	break;
            case R.id.bt_ok:
            	finish();
            	break;
        }
    }

	@Override
	public void onConnectionStateChanged(boolean arg0) {
		// TODO Auto-generated method stub
		RConstants.BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES = true ;
		this.finish();
	}
}
