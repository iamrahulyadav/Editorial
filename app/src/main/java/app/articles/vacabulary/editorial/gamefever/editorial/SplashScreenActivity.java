package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseLongArray;
import android.widget.TextView;

import utils.SettingManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        startTimer(3000l);

        TextView quoteTextView = findViewById(R.id.splashScreen_quote_textView);

        int random = (int) (Math.random() * 11 + 1);
        String quote = "To acquire knowledge, one must study; but to acquire wisdom, one must observe.";

        switch (random) {
            case 1:
                quote = "It always seems impossible until it’s done.";
                break;
            case 2:
                quote = "Start where you are. Use what you have. Do what you can.";
                break;
            case 3:
                quote = "The secret of success is to do the common things uncommonly well.";
                break;
            case 4:
                quote = "Strive for progress, not perfection.";
                break;
            case 5:
                quote = "Don’t wish it were easier; wish you were better.";
                break;
            case 6:
                quote = "I don’t regret the things I’ve done. I regret the things I didn’t do when I had the chance.";
                break;
            case 7:
                quote = "Push yourself, because no one else is going to do it for you.";
                break;
            case 8:
                quote = "You don’t always get what you wish for; you get what you work for.";
                break;
            case 9:
                quote = "It’s not about how bad you want it. It’s about how hard you’re willing to work for it.";
                break;
            case 10:
                quote = "If it’s important to you, you’ll find a way. If not, you’ll find an excuse..";
                break;
            default:
                quote = "To acquire knowledge, one must study; but to acquire wisdom, one must observe.";
        }

        quoteTextView.setText("\"" + quote + "\"");


    }

    public void startTimer(long timer) {


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreenActivity.this, EditorialListWithNavActivity.class);

                startActivity(i);

                // close this activity
                finish();
            }
        }, timer);


    }


}
