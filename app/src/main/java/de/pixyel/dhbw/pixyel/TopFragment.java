package de.pixyel.dhbw.pixyel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionListener;
import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

public class TopFragment extends Fragment {
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static SwipeRefreshLayout mSwipeRefreshLayout;

    private static ByteArrayOutputStream stream;
    private static byte[] imgByte;

    public static LinkedList<ImageCard> imageList = new LinkedList<>();
    public static LinkedList<Picture> pictureList = new LinkedList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.primary_layout,null);
        imageList = new LinkedList<ImageCard>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        BitmapFactory.Options options = new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
        options.inJustDecodeBounds = true;

        //Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.jet);
//        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //System.out.println("Bytes:" + image.getByteCount());
        //imgByte = stream.toByteArray();




        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(imageList, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("Refresh");
                refreshItems();
            }
        });

        return rootView;
    }

    public static void refreshItems(){
        imageList.clear();
        mAdapter.notifyDataSetChanged();
        String longitude= MyLocationListener.getLongi();
        String latitude = MyLocationListener.getLati();
        XML xml = XML.createNewXML("getItemList");
        xml.addChildren("location");
        xml.getFirstChild("location").addChildren("long","lat");
        xml.getFirstChild("location").getFirstChild("long").setContent(longitude);
        xml.getFirstChild("location").getFirstChild("lat").setContent(latitude);
        MainActivity.requestFlag = "Top";
        ConnectionManager.sendToServer(xml);
        onItemsLoadComplete();
    }

    public static void onItemsLoadComplete(){
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public static void refreshItem(int position){
        mAdapter.notifyItemChanged(position);
    }

    public static void addPhoto(String id, String date, String upvotes, String downvotes, String votedByUser, String rank){
        imageList.add(new ImageCard(id, date, upvotes, downvotes, votedByUser, rank));
        onItemsLoadComplete();
    }


}