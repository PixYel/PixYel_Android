package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

//import de.pixyel.dhbw.pixyel.ConnectionManager.Compression;
import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;
//import de.pixyel.dhbw.pixyel.ConnectionManager.Encryption;
import de.pixyel.dhbw.pixyel.ConnectionManager.XML;

public class TestActivity extends AppCompatActivity {
    private ConnectionManager manager;
    private EditText toSend;
    public String dir;

    private Uri photoUri;
    private File photo;
    private static final int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        toSend = (EditText) findViewById(R.id.toSend);

        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/PixYel/";
        File newdir = new File(dir);
        newdir.mkdirs();

    }

    public void buttonConnect(View view) {
        TextView toSend = (TextView) view.findViewById(R.id.toSend);
        TextView answer = (TextView) view.findViewById(R.id.answer);
        String string = (String) toSend.getText();
        manager.sendToServer(XML.createNewXML(string));

    }


    public void openCamera(View view){
        String fileName = dir+ DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";
        photo = new File(fileName);
        photoUri = Uri.fromFile(photo);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TAKE_PICTURE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                FileTransmitter.send(photo);
                NewFragment.addPhoto(photoUri);
            }
        }

    }
}
