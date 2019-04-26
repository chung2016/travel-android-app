package com.example.myapplication.ListView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlaceListAdapter extends BaseAdapter {
    private Context mContext;
    private List<PlaceList> placeLists;

    MainActivity mainActivity;

    public PlaceListAdapter(MainActivity mainActivity, List<PlaceList> placeLists) {
        this.mainActivity = mainActivity;
        this.mContext = mainActivity.getApplicationContext();
        this.placeLists = placeLists;
    }

    @Override
    public int getCount() {
        return placeLists.size();
    }

    @Override
    public Object getItem(int position) {
        return placeLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.place_list, null);
        ImageView ivPlacePhoto = view.findViewById(R.id.iv_place_photo);
        TextView tvPlaceName = view.findViewById(R.id.tv_place_name);
        TextView tvPlaceLocation = view.findViewById(R.id.tv_place_location);
        TextView tvPlaceDate = view.findViewById(R.id.tv_place_date);


        tvPlaceName.setText(placeLists.get(position).getName());
        tvPlaceLocation.setText(placeLists.get(position).getLocation());
        Picasso.get().load(placeLists.get(position).getPhoto()).fit().into(ivPlacePhoto);

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String dateString = format.format(placeLists.get(position).getCreatedAt());
        tvPlaceDate.setText(dateString);

        return view;
    }


}
