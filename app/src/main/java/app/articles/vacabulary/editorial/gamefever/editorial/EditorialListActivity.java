package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditorialListActivity extends AppCompatActivity {

    private List<EditorialGeneralInfo> editorialListArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editorial_list);
        fetchEditorialGeneralList();

        recyclerView =(RecyclerView)findViewById(R.id.editoriallist_recyclerview);

        mAdapter = new EditorialGeneralInfoAdapter(editorialListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addItemDecoration(new DividerItemDecorator(this, LinearLayoutManager.VERTICAL));


        recyclerView.setAdapter(mAdapter);



        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(this, recyclerView ,new RecyclerTouchListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        Toast.makeText(EditorialListActivity.this, "Item clicked "+position+editorialListArrayList.get(position).getEditorialHeading(), Toast.LENGTH_SHORT).show();

                        onRecyclerViewItemClick(position);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );





    }

    private void onRecyclerViewItemClick(int position) {
        EditorialGeneralInfo editorialgenralInfo = editorialListArrayList.get(position);
        Intent i = new Intent(this , EditorialFeedActivity.class);
        i.putExtra("editorialID",editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate",editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading",editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource",editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading",editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag",editorialgenralInfo.getEditorialTag());

        startActivity(i);


    }

    public void fetchEditorialGeneralList(){
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(20 ,"" ,this , true);

    }



    private void prepareMovieData() {

        EditorialGeneralInfo editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("This is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);


        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("economics time");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("this is heading text for demo purpose and testing layout");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);
        editorial =new EditorialGeneralInfo();
        editorial.setEditorialHeading("Heading some");
        editorial.setEditorialDate("20-01-17");
        editorial.setEditorialSource("hindu");

        editorialListArrayList.add(editorial);


        mAdapter.notifyDataSetChanged();
    }

    public void loadMoreClick(View view) {
    }

    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist) {

        int insertPosition = editorialListArrayList.size();

        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist){
            editorialListArrayList.add(insertPosition ,editorialGeneralInfo);
        }
        mAdapter.notifyDataSetChanged();
    }
}