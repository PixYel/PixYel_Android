package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.util.Date;

public class OwnActivity extends AppCompatActivity {

    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    public File folder;
    private Uri photoUri;
    private Uri galerieUri;
    private static final int TAKE_PICTURE = 1;
    private static final int UPLOAD_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragmentOwn()).commit();

        final ImageButton Back = (ImageButton) findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(OwnActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton pictures = (ImageButton) findViewById(R.id.pictures);
        pictures.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                String fileName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";
                folder = getFile(fileName);
                photoUri = Uri.fromFile(folder);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });

        final ImageButton upload = (ImageButton) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");

                /** Kopieren nach sdcard/DCIM/PixYel (für eigene Uploads) */
                String fileName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";
                folder = getFile(fileName);
                photoUri = Uri.fromFile(folder);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(intent, UPLOAD_PICTURE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK) {
            // Make sure the request was successful
            if (requestCode == TAKE_PICTURE) {
                FileTransmitter.send(folder);
            }
            else if (requestCode == UPLOAD_PICTURE){
                galerieUri = data.getData();
                FileTransmitter.send(folder);
            }
        }
    }

    public File getFile(String fileName){
        File folder = new File("sdcard/DCIM/PixYel");

        if (!folder.exists()){
            folder.mkdir();
        }

        File image_file = new File(folder, fileName);
        return image_file;
    }

    public void ImageClick(View view){
        Intent intent = new Intent(OwnActivity.this, activity_BigPicture.class);
        startActivity(intent);
    }
}