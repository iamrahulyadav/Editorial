package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import utils.SettingManager;
import utils.Vocabulary;


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

        TextView dateTextView,wordTextView, meaningTextView, synonymsTextView, antonymsTextView, formsTextView, relatedWordTextView, exampleTextView;
        ImageView wordImageView;

        wordTextView = (TextView) view.findViewById(R.id.vocabularyFragment_word_textView);
        wordImageView = (ImageView) view.findViewById(R.id.vocabularyFragment_word_imageView);
        meaningTextView = (TextView) view.findViewById(R.id.vocabularyFragment_meaning_textView);
        synonymsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_synonyms_textView);
        antonymsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_antonyms_textView);
        formsTextView = (TextView) view.findViewById(R.id.vocabularyFragment_forms_textView);
        relatedWordTextView = (TextView) view.findViewById(R.id.vocabularyFragment_relatedWord_textView);
        exampleTextView = (TextView) view.findViewById(R.id.vocabularyFragment_example_textView);
        dateTextView = (TextView) view.findViewById(R.id.vocabularyFragment_date_textView);

        wordTextView.setText(vocabulary.getmWord());

        meaningTextView.setText(vocabulary.getmWordMeaning()+" ( "+vocabulary.getmHindiMeaning()+" )");
        synonymsTextView.setText(vocabulary.getmSynonyms());
        antonymsTextView.setText(vocabulary.getmAntonyms());
        formsTextView.setText(vocabulary.getmForms());
        relatedWordTextView.setText(vocabulary.getmRelated());
        exampleTextView.setText(vocabulary.getmExample());

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMM");
            String myDate = dateFormat.format(new Date(vocabulary.getTimeInMillis()));
            dateTextView.setText(myDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (vocabulary.getmImageURL() != null && !vocabulary.getmImageURL().isEmpty()) {
            Picasso.with(getContext())
                    .load(vocabulary.getmImageURL())
                    .into(wordImageView);

        } else {
            wordImageView.setVisibility(View.GONE);
        }


        return view;
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
