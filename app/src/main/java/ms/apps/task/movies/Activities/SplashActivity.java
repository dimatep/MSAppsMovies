package ms.apps.task.movies.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import ms.apps.task.movies.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top action bar


        new Handler().postDelayed(new Runnable() {

            // Showing splash activity for 3 seconds with the logo of the app
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent intent = new Intent(SplashActivity.this, MovieListActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);

    }
}
