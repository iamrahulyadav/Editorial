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

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import utils.AdsSubscriptionManager;
import utils.CurrentAffairs;
import utils.CurrentAffairsAdapter;
import utils.DatabaseHandlerRead;
import utils.JsonParser;
import utils.VolleyManager;

import static android.content.ContentValues.TAG;


public class CurrentAffairListFragment extends Fragment {


    ArrayList<Object> currentAffairsArrayList = new ArrayList<>();
    private int category;

    RecyclerView recyclerView;
    CurrentAffairsAdapter currentAffairsAdapter;

    private boolean isLoading = false;
    int pageNumber = 2;


    public long sortDateMillis = -1;

    SwipeRefreshLayout swipeRefreshLayout;

    public CurrentAffairListFragment() {
    }

    public static CurrentAffairListFragment newInstance(int category) {
        CurrentAffairListFragment fragment = new CurrentAffairListFragment();
        Bundle args = new Bundle();
        args.putInt("category", category);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getInt("category");

        }

        fetchCurrentAffairs();

    }

    public void fetchCurrentAffairs() {

        if (category<1){
            return;
        }

        String url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category;
        ;

        if (sortDateMillis < 1) {
            url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category;
        } else if (sortDateMillis >= 1) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String afterDateString = dateFormat.format(sortDateMillis) + "T00:00:00";
            String beforeDateString = dateFormat.format((sortDateMillis + 86400000l)) + "T00:00:00";


            url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category + "&after=" + afterDateString + "&before=" + beforeDateString;


        }

        loadCache(url);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);

                        ArrayList<CurrentAffairs> arrayList;

                        arrayList = new JsonParser().parseCurrentAffairsList(response);

                        currentAffairsArrayList.clear();

                        for (CurrentAffairs currentAffairs : arrayList) {
                            currentAffairsArrayList.add(currentAffairs);
                        }

                        addNativeExpressAds(true);
                        addReadStatus();

                        currentAffairsAdapter = new CurrentAffairsAdapter(currentAffairsArrayList, getContext());

                        setAdapterListener();

                        if (recyclerView != null) {
                            recyclerView.setAdapter(currentAffairsAdapter);
                        }

                        showRefreshing(false);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: " + error);

                    }
                });


        jsonArrayRequest.setShouldCache(true);

        VolleyManager.getInstance().addToRequestQueue(jsonArrayRequest, "Group request");

    }

    private void loadCache(String url) {

        Cache cache = VolleyManager.getInstance().getRequestQueue().getCache();

        Cache.Entry entry = cache.get(url);
        if (entry != null) {
            //Cache data available.
            try {

                String response = new String(entry.data, "UTF-8");

                ArrayList<CurrentAffairs> arrayList;

                arrayList = new JsonParser().parseCurrentAffairsList(response);

                currentAffairsArrayList.clear();

                for (CurrentAffairs currentAffairs : arrayList) {
                    currentAffairsArrayList.add(currentAffairs);
                }

                addNativeExpressAds(false);
                addReadStatus();

                currentAffairsAdapter = new CurrentAffairsAdapter(currentAffairsArrayList, getContext());

                setAdapterListener();

                if (recyclerView != null) {
                    recyclerView.setAdapter(currentAffairsAdapter);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // Cache data not exist.
        }

    }


    public void fetchCurrentAffairs(boolean date) {

        if (!(category > 1)) {
            return;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String afterDateString = dateFormat.format(sortDateMillis) + "T00:00:00";
        String beforeDateString = dateFormat.format((sortDateMillis + 86400000l)) + "T00:00:00";


        String url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category + "&after=" + afterDateString + "&before=" + beforeDateString;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);

                        ArrayList<CurrentAffairs> arrayList;

                        arrayList = new JsonParser().parseCurrentAffairsList(response);

                        currentAffairsArrayList.clear();

                        for (CurrentAffairs currentAffairs : arrayList) {
                            currentAffairsArrayList.add(currentAffairs);
                        }

                        addNativeExpressAds(true);
                        addReadStatus();

                        currentAffairsAdapter = new CurrentAffairsAdapter(currentAffairsArrayList, getContext());

                        setAdapterListener();

                        if (recyclerView != null) {
                            recyclerView.setAdapter(currentAffairsAdapter);
                        }

                        showRefreshing(false);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: " + error);

                    }
                });


        jsonArrayRequest.setShouldCache(true);

        VolleyManager.getInstance().addToRequestQueue(jsonArrayRequest, "Group request");

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

    private void setAdapterListener() {
        currentAffairsAdapter.setClickListener(new CurrentAffairsAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position < 0) {

                    return;
                }

                if (position % AdsSubscriptionManager.ADSPOSITION_COUNT == 0) {
                    return;
                }

                CurrentAffairs currentAffairs = (CurrentAffairs) currentAffairsArrayList.get(position);

                Intent intent = new Intent(getContext(), CurrentAffairsFeedActivity.class);
                intent.putExtra("news", currentAffairs);
                startActivity(intent);

                EditorialListWithNavActivity.showInterstitialAd(getContext());

                /*Read Status*/

                try {
                    currentAffairs.setReadStatus(true);
                    currentAffairsAdapter.notifyDataSetChanged();

                    new DatabaseHandlerRead(getContext()).addReadNews(currentAffairs);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void addReadStatus() {
        try {
            DatabaseHandlerRead databaseHandlerRead = new DatabaseHandlerRead(getContext());
            CurrentAffairs currentAffairs;
            for (Object object : currentAffairsArrayList) {
                if (object.getClass() == CurrentAffairs.class) {
                    currentAffairs = (CurrentAffairs) object;
                    currentAffairs.setReadStatus(databaseHandlerRead.getNewsReadStatus(String.valueOf(currentAffairs.getId())));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_current_affair_list, container, false);

        recyclerView = view.findViewById(R.id.currentAffairFragment_recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(currentAffairsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {

                    if (!isLoading) {

                        if (sortDateMillis > 1) {

                        } else {
                            downloadMoreArticleList();
                            Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });


        swipeRefreshLayout = view.findViewById(R.id.swiperefreshlayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchCurrentAffairs();
                showRefreshing(true);
            }
        });

        if (currentAffairsArrayList.isEmpty()) {
            showRefreshing(true);
        }


        return view;
    }

    private void downloadMoreArticleList() {

        if (sortDateMillis > 1) {

            return;
        }

        isLoading = true;
        showRefreshing(true);

        String url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category + "&page=" + pageNumber;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);

                        ArrayList<CurrentAffairs> currentAffairsList = new JsonParser().parseCurrentAffairsList(response);

                        currentAffairsArrayList.addAll(currentAffairsList);

                        addNativeExpressAds(true);
                        addReadStatus();

                        if (currentAffairsAdapter != null) {
                            currentAffairsAdapter.notifyDataSetChanged();
                        }

                        pageNumber++;

                        isLoading = false;
                        showRefreshing(false);


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d(TAG, "onErrorResponse: " + error);

                    }
                });


        jsonArrayRequest.setShouldCache(true);

        VolleyManager.getInstance().addToRequestQueue(jsonArrayRequest, "Group request");

    }

    private void addNativeExpressAds(boolean loadAd) {

if (getContext()==null){
    return;
}


        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(getContext());


        for (int i = 0; i < (currentAffairsArrayList.size()); i += AdsSubscriptionManager.ADSPOSITION_COUNT) {
            if (currentAffairsArrayList.get(i) != null) {
                if (currentAffairsArrayList.get(i).getClass() != NativeAd.class) {


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
                            currentAffairsAdapter.notifyDataSetChanged();
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
                    if (checkShowAds && loadAd) {
                        nativeAd.loadAd();
                    }

                    currentAffairsArrayList.add(i, nativeAd);

                }
            }
        }


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
        void onFragmentInteraction(Uri uri);
    }

}
