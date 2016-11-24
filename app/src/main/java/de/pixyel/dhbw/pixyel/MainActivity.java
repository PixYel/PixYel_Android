package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Executors;

import de.pixyel.dhbw.pixyel.ConnectionManager.ConnectionManager;

public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    public File folder;
    private Uri photoUri;
    private static final int TAKE_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *Setup the DrawerLayout and NavigationView
         */

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

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                if (menuItem.getItemId() == R.id.nav_item_likes){
                    Log.d("test","test");
                    Intent intent = new Intent(MainActivity.this, TestActivity.class);
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



        final ImageButton upload = (ImageButton) findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                String fileName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";
                folder = getFile(fileName);
                photoUri = Uri.fromFile(folder);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TAKE_PICTURE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                FileTransmitter.send(folder);
                NewFragment.addPhoto(photoUri);
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
}