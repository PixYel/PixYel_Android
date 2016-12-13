package de.pixyel.dhbw.pixyel;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ich on 01.12.2016.
 */

public class Picture {
    public String pic_id;
    public String pic_date;
    public String pic_upvotes;
    public String pic_downvotes;
    public String pic_votedByUser;
    public String pic_rank;
    public String pic_longitude;
    public String pic_latitude;
    public URI pic_path;
    ArrayList<PicComment> pic_comments;

    public Picture(String id, String date, String upvotes, String downvotes, String votedByUser, String rank){
        this.pic_id = id;
        this.pic_upvotes = upvotes;
        this.pic_downvotes = downvotes;
        this.pic_date = date;
        this.pic_votedByUser = votedByUser;
        this.pic_rank = rank;

    }

}
