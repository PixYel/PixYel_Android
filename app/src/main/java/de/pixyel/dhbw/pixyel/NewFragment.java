package de.pixyel.dhbw.pixyel;

import android.net.Uri;

public class NewFragment extends CardFragment {
    public static void addPhoto(Uri uri){
        imageList.add(new ImageCard(uri.toString()));
        onItemsLoadComplete();
    }

}