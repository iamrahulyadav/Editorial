package utils;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.articles.vacabulary.editorial.gamefever.editorial.Comment;
import app.articles.vacabulary.editorial.gamefever.editorial.R;

/**
 * Created by bunny on 07/12/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    Context context;
    private ArrayList<Comment> commentArrayList;

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        public TextView commentTextView, emailTextView, dateTextView ;


        public CommentViewHolder(final View view) {
            super(view);

            commentTextView = (TextView) view.findViewById(R.id.commentItem_commentText_textView);
            emailTextView = (TextView) view.findViewById(R.id.commentItem_eMail_textView);
            dateTextView = (TextView) view.findViewById(R.id.commentItem_date_textView);

        }
    }

    public CommentAdapter(ArrayList<Comment> commentArrayList, Context context) {
        this.commentArrayList = commentArrayList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_listview_item_layout, parent, false);


                return new CommentViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                Comment comment = commentArrayList.get(position);

                commentViewHolder.commentTextView.setText(comment.getCommentText());
                commentViewHolder.dateTextView.setText(comment.getCommentDate());
                commentViewHolder.emailTextView.setText(comment.geteMailID());

    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }


}
