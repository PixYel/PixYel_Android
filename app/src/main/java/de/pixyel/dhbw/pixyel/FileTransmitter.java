package de.pixyel.dhbw.pixyel;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

/**
 * Created by Jan-Laptop on 17.11.2016.
 */

public class FileTransmitter {
    public static void send(File file){
        String imageString = null;
        BufferedInputStream buf = null;
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String longitude= MyLocationListener.getLongi();
        String latitude = MyLocationListener.getLati();
        imageString = Base64.encodeToString(bytes, Base64.NO_WRAP);
        XML xml = XML.createNewXML("upload");
        xml.addChildren("data","long","lat");
        xml.getFirstChild("data").setContent(imageString);
        xml.getFirstChild("long").setContent(longitude);
        xml.getFirstChild("lat").setContent(latitude);

        System.out.println("Bild: "+imageString);
        ConnectionManager.sendToServerUnencrypted(xml);
        System.out.println("Gesendet");
    }
}
