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

import java.util.List;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfoAdapter extends RecyclerView.Adapter<EditorialGeneralInfoAdapter.MyViewHolder> {

    String theme = "Day";
Context context ;
    private List<EditorialGeneralInfo> EditorialGeneralInfoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading, date, source, tag, subheading;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.editorial_list_layout_heading);
            date = (TextView) view.findViewById(R.id.editorial_list_layout_date);
            source = (TextView) view.findViewById(R.id.editorial_list_layout_source);
            tag = (TextView) view.findViewById(R.id.editorial_list_layout_tag);
            subheading = (TextView) view.findViewById(R.id.editorial_list_layout_subheading);




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


    public EditorialGeneralInfoAdapter(List<EditorialGeneralInfo> EditorialGeneralInfoList, String themeActivity ,Context context) {
        this.EditorialGeneralInfoList = EditorialGeneralInfoList;
        this.theme = themeActivity;
        this.context =context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editorial_list_layout, parent, false);

        setThemeforItem(itemView);

        return new MyViewHolder(itemView);
    }

    private void setThemeforItem(View itemView) {


        if (theme.contentEquals("Night")) {
            CardView cv = (CardView) itemView.findViewById(R.id.editorial_list_layout_background_card);

            cv.setCardBackgroundColor(Color.BLACK);

        }
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EditorialGeneralInfo EditorialGeneralInfo = EditorialGeneralInfoList.get(position);
        holder.heading.setText(EditorialGeneralInfo.getEditorialHeading());

        holder.date.setText(EditorialGeneralInfo.resolveDate(EditorialGeneralInfo.getTimeInMillis()));
        holder.source.setText(EditorialGeneralInfo.getEditorialSource());
        holder.tag.setText(EditorialGeneralInfo.getEditorialTag());
        holder.subheading.setText(EditorialGeneralInfo.getEditorialSubHeading());


    }

    @Override
    public int getItemCount() {
        return EditorialGeneralInfoList.size();
    }


}
