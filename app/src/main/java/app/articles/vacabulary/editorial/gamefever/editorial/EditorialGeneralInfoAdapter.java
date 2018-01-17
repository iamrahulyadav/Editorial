package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import java.util.List;

import utils.AdsSubscriptionManager;
import utils.ClickListener;
import utils.NightModeManager;

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
    private ClickListener clicklistener;

    private boolean nightMode = false;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView heading, date, source, tag, subheading, likeText;
        View mustReadImageView;
        ImageView  readMaskImageView;
        CardView backGroundCard;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.editorial_list_layout_heading);
            date = (TextView) view.findViewById(R.id.editorial_list_layout_date);
            source = (TextView) view.findViewById(R.id.editorial_list_layout_source);
            tag = (TextView) view.findViewById(R.id.editorial_list_layout_tag);
            subheading = (TextView) view.findViewById(R.id.editorial_list_layout_subheading);
            likeText = (TextView) view.findViewById(R.id.editorial_list_layout_like);
            mustReadImageView =view.findViewById(R.id.editorial_list_layout_mustRead_imageView);

            backGroundCard=(CardView)view.findViewById(R.id.editorial_list_layout_background_card);

            readMaskImageView= (ImageView)view.findViewById(R.id.editorial_list_layout_isReadMask_imageView);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            if (clicklistener != null) {
                clicklistener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        CardView cardView;

        TextView recommendedTextView;

        public NativeExpressAdViewHolder(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.nativeExpress_container_linearLayout);

            cardView = (CardView) itemView.findViewById(R.id.nativeExpress_background_cardView);

            recommendedTextView =(TextView)itemView.findViewById(R.id.nativeExpress_recommended_textView);

        }
    }

    public EditorialGeneralInfoAdapter(List<Object> EditorialGeneralInfoList, String themeActivity, Context context) {
        this.editorialGeneralInfoList = EditorialGeneralInfoList;
        this.theme = themeActivity;
        this.context = context;
        checkShowAds = AdsSubscriptionManager.checkShowAds(context);
        nightMode=NightModeManager.getNightMode(context);
    }

    public void setOnclickListener(ClickListener clicklistener) {
        this.clicklistener = clicklistener;
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
                NativeExpressAdViewHolder adView = (NativeExpressAdViewHolder) holder;


                NativeAd nativeAd = (NativeAd) editorialGeneralInfoList.get(position);
                if (nativeAd.isAdLoaded()) {
                    adView.cardView.setVisibility(View.VISIBLE);
                    adView.recommendedTextView.setVisibility(View.VISIBLE);
                    View view;
                    if (nightMode){

                        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                                .setBackgroundColor(Color.parseColor("#28292e"))
                                .setTitleTextColor(Color.WHITE)
                                .setButtonTextColor(Color.WHITE)
                                .setDescriptionTextColor(Color.WHITE)
                                .setButtonColor(Color.parseColor("#F44336"));

                        view = NativeAdView.render(context, nativeAd, NativeAdView.Type.HEIGHT_120 ,viewAttributes);
                    }else {
                        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                                .setButtonTextColor(Color.WHITE)
                                .setButtonColor(Color.parseColor("#F44336"));

                        view = NativeAdView.render(context, nativeAd, NativeAdView.Type.HEIGHT_120, viewAttributes);
                    }


                    adView.linearLayout.removeAllViews();
                    adView.linearLayout.addView(view);

                } else {

                    adView.cardView.setVisibility(View.GONE);
                    adView.recommendedTextView.setVisibility(View.GONE);


                }

                break;

            case EDITORIAL_VIEW_TYPE:
            default:

                MyViewHolder myViewHolder = (MyViewHolder) holder;


                EditorialGeneralInfo editorialGeneralInfo = (EditorialGeneralInfo) editorialGeneralInfoList.get(position);

                myViewHolder.heading.setText(editorialGeneralInfo.getEditorialHeading());
                myViewHolder.date.setText(EditorialGeneralInfo.resolveDate(editorialGeneralInfo.getTimeInMillis()));

                myViewHolder.tag.setText(editorialGeneralInfo.getEditorialTag());
                myViewHolder.subheading.setText(editorialGeneralInfo.getEditorialSubHeading());
                myViewHolder.likeText.setText(editorialGeneralInfo.getEditorialLike() + " ");

                if (editorialGeneralInfo.isMustRead()){
                    myViewHolder.mustReadImageView.setVisibility(View.VISIBLE);
                    myViewHolder.source.setText(editorialGeneralInfo.getEditorialSource() +" [IMP]");
                }else{
                    myViewHolder.mustReadImageView.setVisibility(View.GONE);
                    myViewHolder.source.setText(editorialGeneralInfo.getEditorialSource());
                }

                if (editorialGeneralInfo.isReadStatus()){
                    myViewHolder.backGroundCard.setCardElevation(0);
                    myViewHolder.readMaskImageView.setVisibility(View.VISIBLE);
                }else{
                    myViewHolder.backGroundCard.setCardElevation(16);
                    myViewHolder.readMaskImageView.setVisibility(View.GONE);
                }


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
