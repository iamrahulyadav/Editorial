package utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.articles.vacabulary.editorial.gamefever.editorial.R;

/**
 * Created by bunny on 06/04/18.
 */

public class CurrentAffairsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<CurrentAffairs> currentAffairsArrayList;


    ClickListener clickListener;


    public class ClientViewHolder extends RecyclerView.ViewHolder {
        public TextView headingTextView, dateTextView, sourceTextView, subHeadingTextView;

        public ClientViewHolder(final View view) {
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


    public CurrentAffairsAdapter(ArrayList<CurrentAffairs> currentAffairsArrayList, Context context) {
        this.currentAffairsArrayList = currentAffairsArrayList;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_affairs_adapter_row_layout, parent, false);


        return new ClientViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        ClientViewHolder viewHolder = (ClientViewHolder) holder;

        CurrentAffairs currentAffairs = currentAffairsArrayList.get(position);

        viewHolder.headingTextView.setText(currentAffairs.getTitle());
        viewHolder.dateTextView.setText(currentAffairs.getDate());
        viewHolder.sourceTextView.setText(currentAffairs.getCategory());
        viewHolder.subHeadingTextView.setText(currentAffairs.getSubHeading());



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
