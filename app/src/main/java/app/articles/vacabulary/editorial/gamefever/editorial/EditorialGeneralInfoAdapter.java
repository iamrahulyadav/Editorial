package app.articles.vacabulary.editorial.gamefever.editorial;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gamef on 23-02-2017.
 */

public class EditorialGeneralInfoAdapter extends RecyclerView.Adapter<EditorialGeneralInfoAdapter.MyViewHolder>{


    private List<EditorialGeneralInfo> EditorialGeneralInfoList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading, date, source;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.editorial_list_layout_heading);
            date = (TextView) view.findViewById(R.id.editorial_list_layout_date);
            source = (TextView) view.findViewById(R.id.editorial_list_layout_source);
        }
    }


    public EditorialGeneralInfoAdapter(List<EditorialGeneralInfo> EditorialGeneralInfoList) {
        this.EditorialGeneralInfoList = EditorialGeneralInfoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editorial_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EditorialGeneralInfo EditorialGeneralInfo = EditorialGeneralInfoList.get(position);
        holder.heading.setText(EditorialGeneralInfo.getEditorialHeading());
        holder.date.setText(EditorialGeneralInfo.getEditorialDate());
        holder.source.setText(EditorialGeneralInfo.getEditorialSource());
    }

    @Override
    public int getItemCount() {
        return EditorialGeneralInfoList.size();
    }


}
