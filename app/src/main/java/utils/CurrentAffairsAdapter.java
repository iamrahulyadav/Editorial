package utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdView;
import com.facebook.ads.NativeAdViewAttributes;

import java.util.ArrayList;

import app.articles.vacabulary.editorial.gamefever.editorial.R;

/**
 * Created by bunny on 06/04/18.
 */

public class CurrentAffairsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<Object> currentAffairsArrayList;

    private static final int NEWS_VIEW_TYPE = 1;
    private static final int AD_VIEW_TYPE = 2;

    ClickListener clickListener;


    public class CurrentAffairsViewHolder extends RecyclerView.ViewHolder {
        public TextView headingTextView, dateTextView, sourceTextView, subHeadingTextView;

        public CurrentAffairsViewHolder(final View view) {
            super(view);

            headingTextView = view.findViewById(R.id.currentAffairs_heading_textView);
            dateTextView = view.findViewById(R.id.currentAffairs_date_textView);
            sourceTextView = view.findViewById(R.id.currentAffairs_source_textView);
            subHeadingTextView = view.findViewById(R.id.currentAffairs_subheading_textView);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });

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


    public CurrentAffairsAdapter(ArrayList<Object> currentAffairsArrayList, Context context) {
        this.currentAffairsArrayList = currentAffairsArrayList;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {



        switch (viewType) {

            case AD_VIEW_TYPE:
                View nativeExpressLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.native_express_ad_container, parent, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);

            case NEWS_VIEW_TYPE:
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.current_affairs_adapter_row_layout, parent, false);


                return new CurrentAffairsViewHolder(itemView);
        }


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {



        int viewType = getItemViewType(position);
        switch (viewType) {

            case AD_VIEW_TYPE:


                NativeExpressAdViewHolder adView = (NativeExpressAdViewHolder) holder;
                boolean nightMode = NightModeManager.getNightMode(context);

                NativeAd nativeAd = (NativeAd) currentAffairsArrayList.get(position);
                if (nativeAd.isAdLoaded()) {
                    adView.cardView.setVisibility(View.VISIBLE);
                    adView.recommendedTextView.setVisibility(View.VISIBLE);
                    View view;
                    if (nightMode) {

                        NativeAdViewAttributes viewAttributes = new NativeAdViewAttributes()
                                .setBackgroundColor(Color.parseColor("#28292e"))
                                .setTitleTextColor(Color.WHITE)
                                .setButtonTextColor(Color.WHITE)
                                .setDescriptionTextColor(Color.WHITE)
                                .setButtonColor(Color.parseColor("#F44336"));

                        view = NativeAdView.render(context, nativeAd, NativeAdView.Type.HEIGHT_120, viewAttributes);
                    } else {
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

            case NEWS_VIEW_TYPE:
            default:


                CurrentAffairsViewHolder viewHolder = (CurrentAffairsViewHolder) holder;

                CurrentAffairs currentAffairs = (CurrentAffairs) currentAffairsArrayList.get(position);

                viewHolder.headingTextView.setText(currentAffairs.getTitle());
                viewHolder.dateTextView.setText(currentAffairs.getDate());
                viewHolder.sourceTextView.setText(currentAffairs.getCategory());
                viewHolder.subHeadingTextView.setText(currentAffairs.getSubHeading());




        }




    }

    @Override
    public int getItemViewType(int position) {
        //logic to implement ad in every 8th card

        return (position % AdsSubscriptionManager.ADSPOSITION_COUNT == 0) ? AD_VIEW_TYPE : NEWS_VIEW_TYPE;
        //return NEWS_VIEW_TYPE;

    }


    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return currentAffairsArrayList.size();
    }


    public interface ClickListener {
        public void onItemClick(View view, int position);


    }

}
