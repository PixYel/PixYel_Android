package de.pixyel.dhbw.pixyel;

import android.net.Uri;

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
        byte[] contents = new byte[1024];
        try {
            BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file));
            bi.read(contents);
            int i = 0;
            while(bi.read(contents)!= -1){
                imageString += contents.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        XML xml = XML.createNewXML("request").addChild("upload");
        XML upload = xml.getFirstChild().addChildren("data","long","lat");
        upload.getFirstChild("data").setContent(imageString);
        upload.getFirstChild("long").setContent("2345");
        upload.getFirstChild("lat").setContent("23452345");


        System.out.println("Connected");
        ConnectionManager.sendToServer(xml.toString());
        System.out.println("Gesendet");
    }
}
