package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.NativeExpressAdView;

import java.util.List;

import utils.AdsSubscriptionManager;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String theme = "Day";
    Context context;
    private List<Object> editorialGeneralInfoList;

    private static final int EDITORIAL_VIEW_TYPE = 1;
    private static final int AD_VIEW_TYPE = 2;

    private static boolean checkShowAds;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading, date, source, tag, subheading, likeText;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.editorial_list_layout_heading);
            date = (TextView) view.findViewById(R.id.editorial_list_layout_date);
            source = (TextView) view.findViewById(R.id.editorial_list_layout_source);
            tag = (TextView) view.findViewById(R.id.editorial_list_layout_tag);
            subheading = (TextView) view.findViewById(R.id.editorial_list_layout_subheading);
            likeText = (TextView) view.findViewById(R.id.editorial_list_layout_like);



           /* if (theme.contentEquals("Night")) {
                CardView cv = (CardView) view.findViewById(R.id.editorial_list_layout_background_card);
                cv.setCardBackgroundColor(ContextCompat.getColor(context ,R.color.card_background_night));

                heading.setTextColor(ContextCompat.getColor(context ,R.color.main_white));
                date.setTextColor(ContextCompat.getColor(context ,R.color.off_white));
                source.setTextColor(ContextCompat.getColor(context ,R.color.off_white));
                subheading.setTextColor(ContextCompat.getColor(context ,R.color.off_white));
            }*/


        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);
        }
    }

    public EditorialGeneralInfoAdapter(List<Object> EditorialGeneralInfoList, String themeActivity, Context context) {
        this.editorialGeneralInfoList = EditorialGeneralInfoList;
        this.theme = themeActivity;
        this.context = context;
        checkShowAds = AdsSubscriptionManager.checkShowAds(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {

            case AD_VIEW_TYPE:
                View nativeExpressLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_express_ad_container, parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);

            case EDITORIAL_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.editorial_list_layout, parent, false);

                setThemeforItem(itemView);

                return new MyViewHolder(itemView);
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
                NativeExpressAdViewHolder nativeExpressAdViewHolder = (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView = (NativeExpressAdView) editorialGeneralInfoList.get(position);
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

                MyViewHolder myViewHolder = (MyViewHolder) holder;
                EditorialGeneralInfo EditorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoList.get(position);
                myViewHolder.heading.setText(EditorialGeneralInfo.getEditorialHeading());

                myViewHolder.date.setText(EditorialGeneralInfo.resolveDate(EditorialGeneralInfo.getTimeInMillis()));
                myViewHolder.source.setText(EditorialGeneralInfo.getEditorialSource());
                myViewHolder.tag.setText(EditorialGeneralInfo.getEditorialTag());
                myViewHolder.subheading.setText(EditorialGeneralInfo.getEditorialSubHeading());
                myViewHolder.likeText.setText(EditorialGeneralInfo.getEditorialLike()+" ");
        }

    }

    @Override
    public int getItemCount() {
        return editorialGeneralInfoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //logic to implement ad in every 8th card
        return (position % 8 == 0) ? AD_VIEW_TYPE : EDITORIAL_VIEW_TYPE;
    }
}
