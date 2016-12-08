package de.pixyel.dhbw.pixyel;

import android.net.Uri;
/**
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 */

public class UploadsFragment extends CardFragment {
    public static void addPhoto(Uri uri){
        imageList.add(new ImageCard(uri.toString()));
        onItemsLoadComplete();
    }
}