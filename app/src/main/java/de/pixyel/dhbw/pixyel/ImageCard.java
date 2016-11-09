package de.pixyel.dhbw.pixyel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jan-Laptop on 02.11.2016.
 */

public class ImageCard {
    int likes;
    int dislikes;
    String url;

    ImageCard(String url){
        this.url = url;
    }

    public void like(){
        this.likes++;
    }

    public void dislike(){
        this.dislikes++;
    }

    public int getLikes(){
        return this.likes-this.dislikes;
    }

}
