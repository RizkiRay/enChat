package net.enjoystudio.enchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;

import net.enjoystudio.enchat.account.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sp = getSharedPreferences(C.SESSION, MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("enChat");
//        FirebaseInstanceId.getInstance().getToken();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!sp.getString(C.USER_ID, "0").equals("0")){
                    startActivity(new Intent(SplashScreen.this,BaseAppActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
            }
        },3000);
    }
}
