package id.co.sweetmusroom.panicbuttonapps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    Boolean session = false;
    String id, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }

        TextView iView = findViewById(R.id.logoSplash);
        Animation animasi = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        iView.startAnimation(animasi);

        Thread timer = new Thread() {

            @Override
            public void run() {

                try {
                    sleep(5000);

                    sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
                    session = sharedpreferences.getBoolean(session_status, false);
                    id = sharedpreferences.getString(TAG_ID, null);
                    username = sharedpreferences.getString(TAG_USERNAME, null);

                    if (session) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(TAG_ID, id);
                        intent.putExtra(TAG_USERNAME, username);
                        finish();
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        };
        timer.start();
    }
}

