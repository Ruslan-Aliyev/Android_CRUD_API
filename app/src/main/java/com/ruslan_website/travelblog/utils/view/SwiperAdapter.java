package com.ruslan_website.travelblog.utils.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruslan_website.travelblog.R;
import com.ruslan_website.travelblog.utils.database.Entry;
import com.ruslan_website.travelblog.utils.database.EntryDAO;

import java.io.File;
import java.util.List;

public class SwiperAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private List<Entry> dbentries;

    public SwiperAdapter(Context context, EntryDAO db){
        mContext = context;
        dbentries = db.getAllEntries();
    }

    @Override
    public int getCount() {
        return dbentries.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View entriesSwiper = inflater.inflate(R.layout.entries_swiper, container, false);

        TextView dateWho = (TextView) entriesSwiper.findViewById(R.id.dateWho);
        TextView placeName = (TextView) entriesSwiper.findViewById(R.id.placeName);
        ImageView placeImg = (ImageView) entriesSwiper.findViewById(R.id.placeImg);
        TextView placeComments = (TextView) entriesSwiper.findViewById(R.id.placeComments);

        Entry entry = dbentries.get(position);

        dateWho.setText("By: " + entry.getUsername() + ". On: " + entry.getDate() );
        placeName.setText(entry.getPlace());
        placeComments.setText(entry.getComments());

        File imgFile = new File(entry.getImageUrl());
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        placeImg.setImageBitmap(bitmap);

        // For Logs
//        for (Entry en : dbentries) {
//            String log = "Id: "+en.getId()+" ,Name: " + en.getUsername()
//                    + " ,Place: " + en.getPlace()
//                    + ", Time: " + en.getPlace() + ",Date: " + en.getDate()
//                    + ",Comments: " + en.getComments() + ",ImgUrl: "+en.getImageUrl();
//            Log.i("DBTEST: ", log);
//        }

        container.addView(entriesSwiper);
        return entriesSwiper;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
