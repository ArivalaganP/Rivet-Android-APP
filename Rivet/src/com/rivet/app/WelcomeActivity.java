package com.rivet.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    Button nextBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        nextBttn=(Button)findViewById(R.id.bt_next);
        nextBttn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_next:
                Intent loginOptions=new Intent(WelcomeActivity.this, LoginOptionsActivity.class);
                startActivity(loginOptions);
           //     finish();
                break;
        }
    }
}
