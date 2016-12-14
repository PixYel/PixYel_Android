package de.pixyel.dhbw.pixyel;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.LinkedList;

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.CardViewHolder> {
    private LinkedList<ImageCard> mDataset;
    private Activity mActivity;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImage;
        public TextView mLikes;
        public ImageButton mUp;
        public ImageButton mDown;
        public CardViewHolder(View v) {
            super(v);
            mImage = (ImageView) v.findViewById(R.id.CardImage);
            mLikes = (TextView) v.findViewById(R.id.CardLikes);
            mUp = (ImageButton) v.findViewById(R.id.CardUp);
            mDown = (ImageButton) v.findViewById(R.id.CardDown);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<ImageCard> myDataset, Activity myActivity) {
        mDataset = myDataset;
        mActivity = myActivity;
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
    public void onBindViewHolder(final CardViewHolder holder, final int index) {

        Glide.with(mActivity).load(mDataset.get(index).pic_url).into(holder.mImage);
        String upvotes = mDataset.get(index).pic_upvotes;
        String downvotes = mDataset.get(index).pic_downvotes;
        String Likes = "0";
        if (upvotes != null && downvotes != null){
            Likes= ""+(Integer.valueOf(upvotes) - Integer.valueOf(downvotes));
        }
        holder.mLikes.setText("Likes: " + Likes);
        final String ID = mDataset.get(index).pic_id;


        //setzt den upvoteknopf auf clicklistener
        holder.mUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                XML vote = XML.createNewXML("request").addChild("vote");
                vote.addChild("id").setContent(ID);
                vote.addChild("upvote").setContent("1");
                String s = vote.toString();
                System.out.println(s);
                ConnectionManager.sendToServer(vote);

            }
        });
        //setzt den downvoteknopf auf clicklistener
        holder.mDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                XML vote = XML.createNewXML("request").addChild("vote");
                vote.addChild("id").setContent(ID);
                vote.addChild("upvote").setContent("-1");
                String s = vote.toString();
                ConnectionManager.sendToServer(vote);
            }
        });

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String uri = mDataset.get(index).pic_url;
                        String id = mDataset.get(index).pic_id;
                        Intent intent = new Intent(MainActivity.activity, activity_BigPicture.class);
                        intent.putExtra(activity_BigPicture.KEY, id);
                        MainActivity.activity.startActivity(intent);
                    }
                });
            }
        });




    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}