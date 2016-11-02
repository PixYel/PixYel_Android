package de.pixyel.dhbw.pixyel;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CardViewHolder> {
    private LinkedList<ImageCard> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public TextView mLikes;
        public Button mUp;
        public Button mDown;
        public CardViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.CardImage);
            mLikes = (TextView) v.findViewById(R.id.CardLikes);
            mUp = (Button) v.findViewById(R.id.CardUp);
            mDown = (Button) v.findViewById(R.id.CardDown);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<ImageCard> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.CardViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CardViewHolder vh = new CardViewHolder(v);  //You need a cast here
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewHolder holder, int index) {


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}