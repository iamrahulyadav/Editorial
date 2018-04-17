package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;

import java.util.ArrayList;
import java.util.List;

import utils.AdsSubscriptionManager;
import utils.ClickListener;
import utils.CurrentAffairs;
import utils.CurrentAffairsAdapter;
import utils.DatabaseHandlerRead;


public class EditorialListFragment extends Fragment {


    private int sortSourceIndex;
    private int sortCategoryIndex;
    private long sortDateMillis;

    public ArrayList<Object> editorialGeneralInfoArrayList = new ArrayList<>();

    EditorialGeneralInfoAdapter editorialGeneralInfoAdapter;


    RecyclerView recyclerView;

    private boolean isLoading = false;
    int pageNumber = 2;
    private SwipeRefreshLayout swipeRefreshLayout;


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

        showRefreshing(true);

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                //onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);


                for (int i = editorialGeneralInfoArrayList.size() - 1; i >= 0; i--) {
                    EditorialListFragment.this.editorialGeneralInfoArrayList.add(editorialGeneralInfoArrayList.get(i));
                }

                addReadStatus();
                addNativeExpressAds();

                editorialGeneralInfoAdapter = new EditorialGeneralInfoAdapter(EditorialListFragment.this.editorialGeneralInfoArrayList, "", getContext());

                setAdapterListener();

                if (recyclerView != null) {
                    recyclerView.setAdapter(editorialGeneralInfoAdapter);
                }

                showRefreshing(false);

                EditorialListWithNavActivity.editorialListArrayList = EditorialListFragment.this.editorialGeneralInfoArrayList;

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

    public void fetchEditorialGeneralList(int sortSourceIndex, int sortCategoryIndex, long sortDateMillis) {
        EditorialListFragment.this.sortSourceIndex = sortSourceIndex;
        EditorialListFragment.this.sortCategoryIndex = sortCategoryIndex;
        EditorialListFragment.this.sortDateMillis = sortDateMillis;


        showRefreshing(true);

        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {
                //onFetchEditorialGeneralInfo(editorialGeneralInfoArrayList, true);

                EditorialListFragment.this.editorialGeneralInfoArrayList.clear();

                for (int i = editorialGeneralInfoArrayList.size() - 1; i >= 0; i--) {
                    EditorialListFragment.this.editorialGeneralInfoArrayList.add(editorialGeneralInfoArrayList.get(i));
                }

                addReadStatus();
                addNativeExpressAds();

                editorialGeneralInfoAdapter = new EditorialGeneralInfoAdapter(EditorialListFragment.this.editorialGeneralInfoArrayList, "", getContext());

                setAdapterListener();

                if (recyclerView != null) {
                    recyclerView.setAdapter(editorialGeneralInfoAdapter);
                }

                showRefreshing(false);

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


    private void setAdapterListener() {

        editorialGeneralInfoAdapter.setOnclickListener(new ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position < 0) {

                    return;
                }

                if (position % 8 == 0) {
                    return;
                }


                EditorialGeneralInfo editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoArrayList.get(position);
                Intent i;
                if (editorialGeneralInfo.getEditorialSourceIndex() == 0) {
                    i = new Intent(getContext(), EditorialFeedWebViewActivity.class);
                } else {
                    i = new Intent(getContext(), EditorialFeedActivity.class);
                }


                i.putExtra("editorial", editorialGeneralInfo);


                startActivity(i);

                try {
                    editorialGeneralInfo.setReadStatus(true);
                    editorialGeneralInfoAdapter.notifyDataSetChanged();

                    new DatabaseHandlerRead(getContext()).addReadNews(editorialGeneralInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                EditorialListWithNavActivity.showInterstitialAd(getContext());


            }
        });

    }


    private void addNativeExpressAds() {

        if (getContext()==null){
            return;
        }

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(getContext());


        for (int i = 0; i < (editorialGeneralInfoArrayList.size()); i += 8) {
            if (editorialGeneralInfoArrayList.get(i) != null) {
                if (editorialGeneralInfoArrayList.get(i).getClass() != NativeAd.class) {


                    NativeAd nativeAd = new NativeAd(getContext(), "113079036048193_119892505366846");
                    nativeAd.setAdListener(new com.facebook.ads.AdListener() {

                        @Override
                        public void onError(Ad ad, AdError error) {
                            // Ad error callback
                            try {
                                Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                                        .putCustomAttribute("Placement", "List native").putCustomAttribute("errorType", error.getErrorMessage()).putCustomAttribute("Source", "Facebook"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            // Ad loaded callback
                            editorialGeneralInfoAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            // Ad clicked callback
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            // Ad impression logged callback
                        }
                    });

                    // Request an ad
                    if (checkShowAds) {
                        nativeAd.loadAd();
                    }

                    editorialGeneralInfoArrayList.add(i, nativeAd);

                }
            }
        }


    }

    private void addReadStatus() {
        try {
            DatabaseHandlerRead databaseHandlerRead = new DatabaseHandlerRead(getContext());
            EditorialGeneralInfo editorialGeneralInfo;
            for (Object editorialGeneralInfoObject : editorialGeneralInfoArrayList) {
                if (editorialGeneralInfoObject.getClass() == EditorialGeneralInfo.class) {
                    editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoObject;
                    editorialGeneralInfo.setReadStatus(databaseHandlerRead.getNewsReadStatus(editorialGeneralInfo.getEditorialID()));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_editorial_list, container, false);

        recyclerView = view.findViewById(R.id.editorialFragment_recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(editorialGeneralInfoAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {

                    if (!isLoading) {
                        downloadMoreArticleList();
                        //Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchEditorialGeneralList(sortSourceIndex, sortCategoryIndex, -1);

            }
        });

        if (editorialGeneralInfoArrayList.isEmpty()) {
            showRefreshing(true);
        }


        return view;
    }

    public void showRefreshing(boolean value) {

        try {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void downloadMoreArticleList() {

        isLoading = true;
        showRefreshing(true);

        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        //dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, editorialListArrayList.get(editorialListArrayList.size() - 1).getEditorialID(), this, false);

        DBHelperFirebase.OnEditorialListListener onEditorialListListener = new DBHelperFirebase.OnEditorialListListener() {
            @Override
            public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

            }

            @Override
            public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful) {

                editorialGeneralInfoArrayList.remove(editorialGeneralInfoArrayList.size() - 1);

                for (int i = editorialGeneralInfoArrayList.size() - 1; i >= 0; i--) {
                    EditorialListFragment.this.editorialGeneralInfoArrayList.add(editorialGeneralInfoArrayList.get(i));
                }

                addReadStatus();
                addNativeExpressAds();

                if (editorialGeneralInfoAdapter != null) {
                    editorialGeneralInfoAdapter.notifyDataSetChanged();
                }

                isLoading = false;
                showRefreshing(false);
            }
        };

        if (editorialGeneralInfoArrayList.size() > 0) {
            if (editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 1).getClass() == EditorialGeneralInfo.class) {
                if (sortSourceIndex > -1) {
                    dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 1)).getEditorialID(), sortSourceIndex, onEditorialListListener);

                } else if (sortCategoryIndex > -1) {
                    dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 1)).getEditorialID(), sortCategoryIndex, onEditorialListListener);

                } else if (sortDateMillis > -1l) {
                    Toast.makeText(getContext(), "No more Editorial available", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 1)).getEditorialID(), onEditorialListListener);

                }
            } else {
                if (sortSourceIndex > -1) {
                    dbHelperFirebase.fetchSourceSortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 2)).getEditorialID(), sortSourceIndex, onEditorialListListener);

                } else if (sortCategoryIndex > -1) {
                    dbHelperFirebase.fetchCategorySortEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 2)).getEditorialID(), sortCategoryIndex, onEditorialListListener);

                } else if (sortDateMillis > -1l) {
                    Toast.makeText(getContext(), "No more Editorial available", Toast.LENGTH_SHORT).show();

                } else {
                    dbHelperFirebase.fetchEditorialList(EditorialListWithNavActivity.listLimit, ((EditorialGeneralInfo) editorialGeneralInfoArrayList.get(editorialGeneralInfoArrayList.size() - 2)).getEditorialID(), onEditorialListListener);

                }
            }
        }

        //progressBar.setVisibility(View.VISIBLE);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
