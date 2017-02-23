package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void btn1OnClick(View view) {
        fetchEditorialFullInfo(0);

    }

    public void insertfunc(){
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        EditorialFullInfo editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is first article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");


        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);

        editorialFullInfo = new EditorialFullInfo();
        editorialFullInfo.getEditorialExtraInfo().setEditorialText("this is second article in db");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialHeading("this is heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSource("hindu");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialDate("20-03-1997");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialSubHeading("this is sub heading Text to Firebase databse");
        editorialFullInfo.getEditorialGeneralInfo().setEditorialTag("first");

        dbHelperFirebase.insertEditorial(editorialFullInfo);


    }

    public void fetchEditorialFullInfo(int i){
        DBHelperFirebase dbhelperFirebase = new DBHelperFirebase();
        dbhelperFirebase.getEditorialFullInfoByID(editorialGeneralInfoArraylistmain.get(i) ,this);



    }



    public void signInWithGoogle(View view) {
        fetchmore();
    }

    public void btn2OnClick(View view) {

        DBHelperFirebase dbHelper = new DBHelperFirebase();
        dbHelper.fetchEditorialList(20, "", this, true);

    }

    public void btn3OnClick(View view) {
        Intent i = new Intent(this, EditorialFeedActivity.class);
        startActivity(i);
    }

    public void btn4OnClick(View view) {
        Intent i = new Intent(this, EditorialListActivity.class);
        startActivity(i);
    }

    ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylistmain = new ArrayList<EditorialGeneralInfo>();


    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist) {

        TextView tv = (TextView) findViewById(R.id.test_activity_textview);
        String statusText = "";
        Toast.makeText(this, "done Fetching", Toast.LENGTH_SHORT).show();
        int indexToadd = editorialGeneralInfoArraylistmain.size();

        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist) {

            //editorialGeneralInfoArraylistmain.add(editorialGeneralInfo);
            editorialGeneralInfoArraylistmain.add(indexToadd ,editorialGeneralInfo);



        }
        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylistmain){
            statusText = statusText + editorialGeneralInfo.getEditorialHeading() + " - " + editorialGeneralInfo.getEditorialSource() + "\n";

        }
        tv.setText(statusText);



    }

    public void fetchmore(){
        DBHelperFirebase dbHelper = new DBHelperFirebase();
        Toast.makeText(this, "key lenght "+editorialGeneralInfoArraylistmain.size(), Toast.LENGTH_SHORT).show();
        dbHelper.fetchEditorialList(20, editorialGeneralInfoArraylistmain.get(editorialGeneralInfoArraylistmain.size()-1).getEditorialID(), this, false);

    }


    public void onGetEditorialFullInfo(EditorialFullInfo editorialFullInfo) {

        TextView tv = (TextView) findViewById(R.id.test_activity_textview);
        String statusText = "";
        statusText= editorialFullInfo.getEditorialExtraInfo().getEditorialText() +" - "+editorialFullInfo.getEditorialExtraInfo().getEditorialId() +" - "+editorialFullInfo.getEditorialGeneralInfo().getEditorialID();
        tv.setText(statusText);
    }
}
