package de.pixyel.dhbw.pixyel;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ich on 01.12.2016.
 */

public class Picture {
    public int id;
    public java.util.Date date;
    public int upvotes;
    public int downvotes;
    public int votedByUser;
    public int rank;
    public URI path;
    ArrayList<PicComment> picturecomments;

}
