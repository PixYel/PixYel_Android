package de.pixyel.dhbw.pixyel;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;

import de.pixyel.dhbw.pixyel.ConnectionManager.Caching;
import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;


public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    public File folder;
    public static File cacheFolder;
    private Uri photoUri;
    private Uri galerieUri;
    private static final int TAKE_PICTURE = 1;
    private static final int UPLOAD_PICTURE = 2;

    public static String requestFlag = "";
    public static Context context;
    public static Activity activity;
    public static String deviceID ="";


    public void createDeviceID() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
         deviceID = deviceUuid.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        createDeviceID();

        LocationListener listener = new MyLocationListener(MainActivity.this); //ein neuer LocationListener wird erstellt
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //ein LocationMAnager wird initialisiert
        //wenn die Erlaubnis zur Location-Nutzung in den Einstellungen noch nicht erteilt wurde, wird der User mit einem PopUp so lange darauf hingewiesen bis er das ändert
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PopUp popup =new PopUp();
            popup.PopUp(MainActivity.this, "Erlaubnis zur GPS-Nutzung fehlt", "Bitte Berechtigung zur Standorterkennung für PixYel in den Einstellungen im Andwendungsmanager geben");
            return;
        }
        //wenn es einen NETWORK_PROVIDER gibt soll dieser verwendet werden
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, listener);
        }
        //ansonsten soll die Ortung über GPS_PROVIDER erfolgen
        else {locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, listener);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            PopUp popup = new PopUp();
            popup.PopUp(MainActivity.this, "Erlaubnis für Speicherzugriff oder Kamera fehlen", "Bitte Berechtigung für PixYel in den Einstellungen im Andwendungsmanager geben");
            return;
        }

        cacheFolder = new File(Environment.getExternalStorageDirectory(), "Android/data/de.pixyel.dhbw.pixyel/cache/");
        if (!cacheFolder.exists()){
            System.out.println("create folder");
            cacheFolder.mkdirs();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.drawer) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */

        /** Menüpunkte */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
            mDrawerLayout.closeDrawers();

            /** Eigene Uploads und Likes. Comments bei Bedarf in "TabFragmentOwn.java" anlegen */
            if (menuItem.getItemId() == R.id.nav_item_own){
                //Log.d("test","test");
                Intent intent = new Intent(MainActivity.this, OwnActivity.class);
                startActivity(intent);
            }

            /** Einstellungen */
            if (menuItem.getItemId() == R.id.nav_item_settings){
                //Log.d("test","test");
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }

            /** Hilfe & Impressum */
            if (menuItem.getItemId() == R.id.nav_item_help){
                //Log.d("test","test");
                Intent intent = new Intent(MainActivity.this, Help.class);
                startActivity(intent);
            }

            return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        // Connection to Server
        Executors.newFixedThreadPool(1).submit(new ConnectionManager());

        // Click on Photo Button, Start Camera
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


        // Click on Upload Button, Starts Galerie
        // Function: Upload from Galerie to Server
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

        Caching.deleteOldPictures();
    }

    // Get Results
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
                File file = new File(getRealPathFromURI(galerieUri));
                FileTransmitter.send(file);
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    // Folder Check
    public File getFile(String fileName){
    File folder = new File("sdcard/DCIM/PixYel");

    if (!folder.exists()){
        folder.mkdir();
    }

    File image_file = new File(folder, fileName);
        return image_file;
    }

}

