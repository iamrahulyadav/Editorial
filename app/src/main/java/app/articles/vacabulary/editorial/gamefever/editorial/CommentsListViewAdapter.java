package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamef on 05-03-2017.
 */

public class CommentsListViewAdapter extends ArrayAdapter<Comment>{
    private Activity context;
    ArrayList<Comment> commentList ;



    public CommentsListViewAdapter(Activity context ,ArrayList<Comment> commentList) {
        super(context, R.layout.comment_listview_item_layout);
        this.commentList =commentList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.comment_listview_item_layout, null, true);
        TextView textViewEmail = (TextView) listViewItem.findViewById(R.id.commentItem_eMail_textView);
        TextView textViewCommentText = (TextView) listViewItem.findViewById(R.id.commentItem_commentText_textView);


        textViewEmail.setText(commentList.get(position).geteMailID());
        textViewCommentText.setText(commentList.get(position).getCommentText());


        return  listViewItem;
    }

}
