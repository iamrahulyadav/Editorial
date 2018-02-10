package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;

import java.text.SimpleDateFormat;
import java.util.Date;

import utils.NightModeManager;
import utils.SettingManager;
import utils.Vocabulary;
import utils.VolleyManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyVocabularyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyVocabularyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyVocabularyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    Vocabulary vocabulary;

    private OnFragmentInteractionListener mListener;

    public DailyVocabularyFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static DailyVocabularyFragment newInstance(Vocabulary vocabulary) {
        DailyVocabularyFragment fragment = new DailyVocabularyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, vocabulary);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vocabulary = (Vocabulary) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_vocabulary, container, false);

        TextView dateTextView, wordTextView, meaningTextView, synonymsTextView, antonymsTextView, formsTextView, relatedWordTextView, exampleTextView;
        NetworkImageView wordImageView;

        wordTextView = (TextView) view.findViewById(R.id.vocabularyFragment_word_textView);
        wordImageView = (NetworkImageView) view.findViewById(R.id.vocabularyFragment_word_networkImageView);
        meaningTextView = (TextView) view.findViewById(R.id.vocabularyFragment_meaning_textView);
        synonymsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_synonyms_textView);
        antonymsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_antonyms_textView);
        //formsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_forms_textView);
        relatedWordTextView = (TextView) view.findViewById(R.id.vocabularyFragment_relatedWord_textView);
        exampleTextView = (TextView) view.findViewById(R.id.vocabularyFragment_example_textView);
        dateTextView = (TextView) view.findViewById(R.id.vocabularyFragment_date_textView);

        wordTextView.setText(vocabulary.getmWord() + " (" + vocabulary.getmPartOfSpeech() + ")");

        meaningTextView.setText(vocabulary.getmWordMeaning() + " ( " + vocabulary.getmHindiMeaning() + " )");
        synonymsTextView.setText(vocabulary.getmSynonyms());
        antonymsTextView.setText(vocabulary.getmAntonyms());
        // formsTextView.setText(vocabulary.getmForms());
        relatedWordTextView.setText(vocabulary.getmRelated());
        exampleTextView.setText(vocabulary.getmExample());

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
            String myDate = dateFormat.format(new Date(vocabulary.getTimeInMillis()));
            dateTextView.setText(myDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (vocabulary.getmImageURL() != null && !vocabulary.getmImageURL().isEmpty()) {
                ImageLoader imageLoader = VolleyManager.getInstance().getImageLoader();


                wordImageView.setImageUrl(vocabulary.getmImageURL(), imageLoader);


            } else {
                wordImageView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeNAtiveAds(view);

        return view;
    }

    private void initializeNAtiveAds(final View view) {
        final CardView cardView = view.findViewById(R.id.vocabularyFragment_adContainer_cardView);

        if (vocabulary.getContentType() == 1) {

            if (vocabulary.getNativeAd() == null) {
                cardView.setVisibility(View.GONE);
                return;
            } else {
                if (vocabulary.getNativeAd().isAdLoaded()) {
                    cardView.setVisibility(View.VISIBLE);
                    NativeAdViewAttributes viewAttributes;
                    if (NightModeManager.getNightMode(getContext())) {

                        viewAttributes = new NativeAdViewAttributes()
                                .setBackgroundColor(Color.parseColor("#28292e"))
                                .setTitleTextColor(Color.WHITE)
                                .setButtonTextColor(Color.WHITE)
                                .setDescriptionTextColor(Color.WHITE)
                                .setButtonColor(Color.parseColor("#F44336"));

                    } else {
                    /*viewAttributes = new NativeAdViewAttributes()
                            .setBackgroundColor(Color.LTGRAY)

                            .setButtonTextColor(Color.WHITE)
                            .setButtonColor(Color.parseColor("#F44336"));*/

                        viewAttributes = new NativeAdViewAttributes()
                                .setButtonTextColor(Color.WHITE)
                                .setButtonColor(Color.parseColor("#F44336"));

                    }

                    View adView = NativeAdView.render(getContext(), vocabulary.getNativeAd(), NativeAdView.Type.HEIGHT_120, viewAttributes);


                    cardView.removeAllViews();
                    cardView.addView(adView);

                } else {
                    cardView.setVisibility(View.GONE);

                    vocabulary.getNativeAd().setAdListener(new AdListener() {
                        @Override
                        public void onError(Ad ad, AdError adError) {

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {

                            if (view != null) {

                                try {

                                    cardView.setVisibility(View.VISIBLE);


                                    cardView.removeAllViews();

                                    View adView = NativeAdView.render(getContext(), vocabulary.getNativeAd(), NativeAdView.Type.HEIGHT_120);
                                    // Add the Native Ad View to your ad container
                                    cardView.addView(adView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    });


                }
            }

        } else {
            cardView.setVisibility(View.GONE);
            return;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
