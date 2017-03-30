package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by harsh on 22-03-2017.
 */

public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            addPreferencesFromResource(R.xml.preferences);


            //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            //String s = sharedPref.getString("font_size_list", "none");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
