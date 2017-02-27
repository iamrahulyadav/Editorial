package app.articles.vacabulary.editorial.gamefever.editorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.editorialAbout_activity_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle("About");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
