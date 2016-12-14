package de.pixyel.dhbw.pixyel.ConnectionManager;

import java.io.File;

import de.pixyel.dhbw.pixyel.MainActivity;

/**
 * Created by Jan-Laptop on 14.12.2016.
 */

public class Caching {
    private static File dir = MainActivity.cacheFolder;

    public static void deleteOldPictures(){
        File files[] = Caching.dir.listFiles();

        if(files.length > 100){
            for(int i = 100; i < files.length; i++){
                files[i].delete();
            }
        }
    }
}
