package de.pixyel.dhbw.pixyel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jan-Laptop on 02.11.2016.
 */

public class ImageCard {
    public String pic_id;
    public String pic_date;
    public String pic_upvotes;
    public String pic_downvotes;
    public String pic_votedByUser;
    public String pic_rank;
    public String pic_url;

    public ImageCard(String id, String date, String upvotes, String downvotes, String votedByUser, String rank){
        this.pic_id = id;
        this.pic_upvotes = upvotes;
        this.pic_downvotes = downvotes;
        this.pic_date = date;
        this.pic_votedByUser = votedByUser;
        this.pic_rank = rank;
        this.pic_url = MainActivity.cacheFolder.toString() + "/" + pic_id + ".jpg";
        System.out.println(this.pic_url);

    }
    public ImageCard(String url){
        this.pic_url = url;

    }
}
