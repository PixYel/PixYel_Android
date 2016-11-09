package de.pixyel.dhbw.pixyel;

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

import static android.support.v7.recyclerview.R.styleable.RecyclerView;

public class CardFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinkedList<ImageCard> imageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.primary_layout,null);
        imageList = new LinkedList<ImageCard>();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);


        imageList.add(new ImageCard("http://img.pr0gramm.com/2016/11/09/d4ed7fbd761dcfd9.jpg"));


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
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

    private void refreshItems(){
        imageList.add(new ImageCard("http://img.pr0gramm.com/2016/11/09/d4ed7fbd761dcfd9.jpg"));
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete(){
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

}