package app.articles.vacabulary.editorial.gamefever.editorial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void btn1OnClick(View view) {
        Dictionary dictionary =new Dictionary();
        dictionary.fetchWordMeaning("clean");
    }

    public void signInWithGoogle(View view) {
    }

    public void btn2OnClick(View view) {
    }

    public void btn3OnClick(View view) {
    }

    public void btn4OnClick(View view) {
    }
}
