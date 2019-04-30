package com.example.myapplication.ListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.PlaceDetailActivity;
import com.example.myapplication.models.Comment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentListAdapter extends BaseAdapter {
    private List<Comment> comments;
    private Context mContext;
    private PlaceDetailActivity mActivity;

    public CommentListAdapter(PlaceDetailActivity mActivity, List<Comment> comments) {
        this.mActivity = mActivity;
        this.mContext = mActivity.getApplicationContext();
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.comment_list, null);
        ImageView iv_user_photo = view.findViewById(R.id.iv_user_photo);
        TextView tv_user_name = view.findViewById(R.id.tv_user_name);
        TextView tv_user_message = view.findViewById(R.id.tv_user_message);
        TextView tv_create_date = view.findViewById(R.id.tv_create_date);


        tv_user_name.setText(comments.get(position).getUser().getUsername());
        tv_user_message.setText(comments.get(position).getMessage());
        if (comments.get(position).getUser().getImage()!=null) {
            Picasso
                    .get()
                    .load(comments.get(position).getUser().getImage())
                    .fit()
                    .error(R.drawable.error)
                    .into(iv_user_photo);
        } else {
            iv_user_photo.setImageResource(R.mipmap.ic_user_image);
        }


        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String dateString = format.format(comments.get(position).getCreatedAt());
        tv_create_date.setText(dateString);

        return view;
    }


}
