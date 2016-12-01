package de.pixyel.dhbw.pixyel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class activity_BigPicture extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__big_picture);


        final ImageButton Back = (ImageButton) findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(activity_BigPicture.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
