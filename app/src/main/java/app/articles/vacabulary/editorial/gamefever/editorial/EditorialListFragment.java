package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class EditorialListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private int sortSourceIndex;
    private int sortCategoryIndex;
    private long sortDateMillis;

    public EditorialListFragment() {

    }


    public static EditorialListFragment newInstance(int sortSourceIndex, int sortCategoryIndex, long sortDateMillis) {
        EditorialListFragment fragment = new EditorialListFragment();
        Bundle args = new Bundle();
        args.putInt("source", sortSourceIndex);
        args.putInt("category", sortCategoryIndex);
        args.putLong("date", sortDateMillis);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortSourceIndex = getArguments().getInt("source");
            sortCategoryIndex = getArguments().getInt("category");
            sortDateMillis = getArguments().getLong("date");
        }

        fetchEditorialGeneralList();

    }


    public void fetchEditorialGeneralList() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                //onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);
            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }
        };

        //isRefreshing = true;

        if (sortSourceIndex > -1) {
            dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, sortSourceIndex, onEditorialListListener);

        } else if (sortCategoryIndex > -1) {
            dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, sortCategoryIndex, onEditorialListListener);

        } else if (sortDateMillis > -1l) {
            dbHelperFirebase.fetchDateSortEditorialList(sortDateMillis, (sortDateMillis + 86400000), onEditorialListListener);
        } else {
            dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, onEditorialListListener);

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_editorial_list, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
