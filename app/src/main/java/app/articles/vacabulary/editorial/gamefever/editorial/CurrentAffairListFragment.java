package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;

import org.json.JSONArray;

import java.util.ArrayList;

import utils.AdsSubscriptionManager;
import utils.CurrentAffairs;
import utils.CurrentAffairsAdapter;
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

        String url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);

                        ArrayList<CurrentAffairs> arrayList;

                        arrayList = new JsonParser().parseCurrentAffairsList(response);

                        for (CurrentAffairs currentAffairs : arrayList) {
                            currentAffairsArrayList.add(currentAffairs);
                        }

                        addNativeExpressAds();

                        currentAffairsAdapter = new CurrentAffairsAdapter(currentAffairsArrayList, getContext());

                        setAdapterListener();

                        if (recyclerView != null) {
                            recyclerView.setAdapter(currentAffairsAdapter);
                        }

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

    private void setAdapterListener() {
        currentAffairsAdapter.setClickListener(new CurrentAffairsAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position % AdsSubscriptionManager.ADSPOSITION_COUNT == 0) {
                    return;
                }

                Intent intent = new Intent(getContext(), CurrentAffairsFeedActivity.class);
                intent.putExtra("news", (CurrentAffairs)currentAffairsArrayList.get(position));
                startActivity(intent);
            }
        });
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
                        downloadMoreArticleList();
                        Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        return view;
    }

    private void downloadMoreArticleList() {

        isLoading=true;

        String url = "http://aspirantworld.in/wp-json/wp/v2/posts?categories=" + category + "&page=" + pageNumber;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, "onResponse: " + response);

                        ArrayList<CurrentAffairs> currentAffairsList = new JsonParser().parseCurrentAffairsList(response);

                        currentAffairsArrayList.addAll(currentAffairsList);

                        addNativeExpressAds();

                        currentAffairsAdapter.notifyDataSetChanged();

                        pageNumber++;

                        isLoading = false;


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

    private void addNativeExpressAds() {

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
                    if (checkShowAds) {
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
