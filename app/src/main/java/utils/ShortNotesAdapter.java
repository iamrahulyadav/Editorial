package utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import app.articles.vacabulary.editorial.gamefever.editorial.EditorialGeneralInfo;
import app.articles.vacabulary.editorial.gamefever.editorial.EditorialGeneralInfoAdapter;
import app.articles.vacabulary.editorial.gamefever.editorial.R;

/**
 * Created by bunny on 16/09/17.
 */

public class ShortNotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    String theme = "Day";
    Context context;
    private List<Object> shortNotesArrayList;

    private static final int EDITORIAL_VIEW_TYPE = 1;
    private static final int AD_VIEW_TYPE = 2;

    private static boolean checkShowAds;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading, date, source, tag, subheading;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.editorial_list_layout_heading);
            date = (TextView) view.findViewById(R.id.editorial_list_layout_date);
            source = (TextView) view.findViewById(R.id.editorial_list_layout_source);
            tag = (TextView) view.findViewById(R.id.editorial_list_layout_tag);
            subheading = (TextView) view.findViewById(R.id.editorial_list_layout_subheading);


        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    public ShortNotesAdapter(List<Object> EditorialGeneralInfoList, Context context) {
        this.shortNotesArrayList = EditorialGeneralInfoList;

        this.context = context;
        checkShowAds = AdsSubscriptionManager.checkShowAds(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {

            case AD_VIEW_TYPE:
                View nativeExpressLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_express_ad_container, parent, false);
                return new ShortNotesAdapter.NativeExpressAdViewHolder(nativeExpressLayoutView);

            case EDITORIAL_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.editorial_list_layout, parent, false);

                setThemeforItem(itemView);

                return new ShortNotesAdapter.MyViewHolder(itemView);
        }
    }


    private void setThemeforItem(View itemView) {


        if (theme.contentEquals("Night")) {
            CardView cv = (CardView) itemView.findViewById(R.id.editorial_list_layout_background_card);

            cv.setCardBackgroundColor(Color.BLACK);

        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //switch statment for making native ads in every 8th card
        int viewType = getItemViewType(position);
        switch (viewType) {

            case AD_VIEW_TYPE:
                ShortNotesAdapter.NativeExpressAdViewHolder nativeExpressAdViewHolder = (ShortNotesAdapter.NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView = (NativeExpressAdView) shortNotesArrayList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressAdViewHolder.itemView;
                adCardView.removeAllViews();

                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                if (checkShowAds) {
                    adCardView.addView(adView);
                }
                break;

            case EDITORIAL_VIEW_TYPE:
            default:

                ShortNotesAdapter.MyViewHolder myViewHolder = (ShortNotesAdapter.MyViewHolder) holder;
                ShortNotesManager shortNotesManager = (ShortNotesManager) shortNotesArrayList.get(position);
                myViewHolder.heading.setText(shortNotesManager.getShortNoteHeading());

                myViewHolder.date.setText(EditorialGeneralInfo.resolveDate(shortNotesManager.getShortNoteEditTimeInMillis()));
                myViewHolder.source.setText(shortNotesManager.getNoteArticleSource());
                myViewHolder.tag.setText("");

                for (String notes :shortNotesManager.getShortNotePointList().values()){
                    myViewHolder.subheading.setText(notes);
                    break;
                }


        }

    }

    @Override
    public int getItemCount() {
        return shortNotesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //logic to implement ad in every 8th card
        return (position % 8 == 0) ? AD_VIEW_TYPE : EDITORIAL_VIEW_TYPE;
    }

}
